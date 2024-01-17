package com.game;

import static com.game.sets.JsonMapper.getOrCreateFileJson;
import static com.game.sets.OrangeCardsTestSet.findO;
import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.model.BlueCard;
import com.game.model.BlueDeck;
import com.game.model.BlueDecks;
import com.game.model.BlueGreenCard;
import com.game.model.FavouriteAction;
import com.game.model.GameContext;
import com.game.model.GreenCard;
import com.game.model.GreenDeck;
import com.game.model.OrangeCard;
import com.game.model.OrangeDeck;
import com.game.model.Player;
import com.game.model.Players;
import com.game.model.RedCard;
import com.game.model.RedDeck;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameSimulationSuiteFactory {

    private final String appProperties;

    public static void main(String[] args) throws IOException {
        String appProperties = "application.properties";
        var iterator =
            new GameSimulationSuiteFactory(appProperties)
                .initAndGetIterator();

        int k = 1;
        while (iterator.hasNext()) {
            GameContext context = iterator.next();
            BlueDecks blueDecks = context.getBlueDecks();
//            System.out.println("\n" + k++ + "." + blueDecks);
        }
    }

    public Iterator<GameContext> initAndGetIterator() throws IOException {
        // read properties
        Properties properties = getGameProperties(appProperties);
        properties.list(System.out);
        // load decks
        ObjectMapper objectMapper = new ObjectMapper();

        // red deck
        List<RedCard> redCards =
            objectMapper.readValue(getOrCreateFileJson(properties.getProperty("red.deck")), new TypeReference<>() {});
        // orange deck
        List<OrangeCard> orangeCards =
            objectMapper.readValue(getOrCreateFileJson(properties.getProperty("orange.deck")),
                new TypeReference<>() {});
        // green deck
        List<GreenCard> greenCards = getGreenCards(properties, objectMapper);
        System.out.println("green deck size: " + greenCards.size());

        boolean isEnableGreenDeck = Boolean.parseBoolean(properties.getProperty("green.deck.enabled"));
        var shufflesFile1 = properties.getProperty("green.events.cubes.shuffle");
        List<List<Integer>> eventsCubesShuffles =
            objectMapper.readValue(getOrCreateFileJson(shufflesFile1), new TypeReference<>() {});


        // blue decks
        List<BlueCard> blueCards = getBlueCards(properties, objectMapper);

        // + Count
        int blueCardsAmount = blueCards.size();
        System.out.println("blue deck size: " + blueCardsAmount);
        //
        //        Player player = new Player(3, 4, "O");
        //        player.initRound();
        //        var sorted = cards.stream()
        //            .sorted((b1, b2) -> GameStrategy.compare(b1, b2,
        //                player))
        //            .collect(Collectors.toList());

        // load shuffles
        var shufflesFile = properties.getProperty("blue.deck.shuffle");
        List<List<Integer>> shuffles =
            objectMapper.readValue(getOrCreateFileJson(shufflesFile), new TypeReference<>() {});
//        System.out.println(shuffles);
        // customize shuffles
        shuffles.forEach(integers -> integers.removeIf(index -> index >= blueCardsAmount));
//        System.out.println(shuffles);
        var progressDeckCardIds = asList(properties.getProperty("blue.deck.progressDeck").split(","));

        // generate decks/ split decks
        var iterator = shuffles.iterator();
        return new Iterator<>() {
            int iter;

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public GameContext next() {
                iter ++;
                List<Integer> integers = iterator.next();
                BlueDecks blueDecks = generateDecks(integers, blueCards, progressDeckCardIds, blueCardsAmount);
                return createGameContext(iter, blueDecks,
                    new GreenDeck(new LinkedList<>(greenCards), eventsCubesShuffles, isEnableGreenDeck, false),
                    new OrangeDeck(orangeCards),
                    new RedDeck(redCards));
            }
        };
    }

    private List<BlueCard> getBlueCards(Properties properties, ObjectMapper objectMapper) throws IOException {
        int i = 1;
        var cards = new ArrayList<BlueCard>();
        while (properties.getProperty("blue.deck." + i) != null) {
            String property = properties.getProperty("blue.deck." + i);
            File orCreateFileJson = getOrCreateFileJson(property);
            List<BlueCard> content = objectMapper.readValue(orCreateFileJson, new TypeReference<>() {});
            cards.addAll(content);
            i++;
        }
        // check unique cards
        validateThatNoDuplicated(cards);
        validateIds(cards);
        // remove cards
        var removeCardIds = asList(properties.getProperty("blue.deck.remove_cards").split(","));
        cards.removeIf(card -> removeCardIds.contains(card.getId()));
        return cards;
    }

    private List<GreenCard> getGreenCards(Properties properties, ObjectMapper objectMapper) throws IOException {
        int i = 1;
        var cards = new ArrayList<GreenCard>();
        while (properties.getProperty("green.deck." + i) != null) {
            String property = properties.getProperty("green.deck." + i);
            File orCreateFileJson = getOrCreateFileJson(property);
            List<GreenCard> content = objectMapper.readValue(orCreateFileJson, new TypeReference<>() {});
            cards.addAll(content);
            i++;
        }
        // check unique cards
        // remove cards
        var removeCardIds = asList(properties.getProperty("green.deck.remove_cards").split(","));
        cards.removeIf(card -> removeCardIds.contains(card.getId()));
        return cards;
    }

    private static BlueDecks generateDecks(List<Integer> shuffle, List<BlueCard> cards,
        List<String> progressDeckCardIds, int cardsAmount) {
        BlueDeck routineDeck = new BlueDeck(new LinkedList<>());
        BlueDeck progressDeck = new BlueDeck(new LinkedList<>());

        for (Integer index : shuffle) {
            BlueCard blueCard = cards.get(index);
            if (progressDeckCardIds.contains(blueCard.getId())) {
                progressDeck.getCards().add(blueCard);
            } else {
                routineDeck.getCards().add(blueCard);
            }
        }

        // тоже не совсем понятно, если запускать в цикле, то тогда нужно обнулять счетчик при тестах и ране
        List<Integer> baseCurrentShuffle = List.of(
            1, 2, 3, 4, 2, 3, 1, 4, 5, 5, 5, 3, 5, 3, 6, 3, 4, 4, 3, 1, 2, 1, 1, 1, 3, 6, 5, 5, 6, 1, 2, 1, 2, 5, 4, 2, 3, 6, 3, 1, 6, 1, 4, 5, 6, 4, 4, 2, 3, 4, 1, 5, 6, 3, 6, 3, 6, 2, 4, 4, 4, 5, 6, 2, 1, 3, 5, 6, 2, 2, 1, 3, 1, 5, 6, 6, 1, 3, 1, 4, 1, 2, 2, 6, 6, 5, 1, 2, 1, 4, 4, 4, 6, 3, 5, 4, 1, 2, 2, 2
        );

        Map<String, List<Integer>> blueCardsCubesShuffles = Map.of(
            "base", baseCurrentShuffle
        );
        BlueDecks blueDecks = new BlueDecks(routineDeck, progressDeck,
            new BlueDeck(new LinkedList<>()),
            new BlueDeck(new LinkedList<>()),
            new BlueDeck(new LinkedList<>()),
            new BlueDeck(new LinkedList<>()),
            blueCardsCubesShuffles,
            new HashMap<>(),
            false
        );
        if (cardsAmount != blueDecks.getCount()) {
            throw new Error(String.format("Invalid initialization, cardsAmount = %d,blueDecks = %d ", cardsAmount,
                blueDecks.getCount()));
        }
        return blueDecks;
    }

    GameContext createGameContext(int iterationNumber, BlueDecks shuffledBlueDecks,
        GreenDeck eventDeck, OrangeDeck orangeDeck, RedDeck relationshipDeck) {
        // todo 4: customize players
        var player1 = new Player(1, 7, 4, FavouriteAction.O);
        var player2 = new Player(2, 8, 2, FavouriteAction.R);
        player1.getOrangeCards().cards.addAll(findO(26));
        player2.getOrangeCards().cards.addAll(findO(28));

        var gameContext = new GameContext(shuffledBlueDecks, eventDeck, orangeDeck, relationshipDeck, new Players(player1, player2));
        gameContext.setCardsAmount(shuffledBlueDecks.getCount());
        gameContext.setIterationNumber(iterationNumber);
        return gameContext;
    }

    static void validateThatNoDuplicated(List<BlueCard> cards) {
        var idsCount = cards.stream().map(BlueGreenCard::getId).distinct().count();
        if (idsCount != cards.size()) {
            throw new IllegalArgumentException("Wrong input: there are " + (cards.size() - idsCount) + " duplicates!");
        }
    }

    static void validateIds(List<BlueCard> cards) {
        var none = cards.stream().map(BlueGreenCard::getActionType).filter(Predicate.not(BlueCard.ALLOWED_IDS::contains)).count();
        if (none > 0) {
            throw new IllegalArgumentException("Wrong input: there are " + none + " cards with wrong ids!");
        }
    }

    //    List<int[]> shuffles = List.of(
    //        new int[] {14, 12, 4, 1, 0, 11, 2, 10, 9, 3, 7, 6, 13, 5, 8},
    //        new int[] {0, 1, 11, 6, 7, 12, 13, 2, 5, 3, 8, 10, 4, 9, 14},
    //        new int[] {9, 11, 10, 5, 4, 0, 2, 13, 8, 3, 1, 12, 7, 14, 6}
    //    );

    private static Properties getGameProperties(String appProperties) throws IOException {
        InputStream fileAsIOStream = getFileAsIOStream("game/" + appProperties);
        Properties properties = new Properties();
        properties.load(fileAsIOStream);
        return properties;
    }

    static InputStream getFileAsIOStream(final String fileName) {
        InputStream ioStream = GameApplication.class
            .getClassLoader()
            .getResourceAsStream(fileName);

        if (ioStream == null) {
            throw new IllegalArgumentException(fileName + " is not found");
        }
        return ioStream;
    }


}
