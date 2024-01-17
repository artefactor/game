package com.game.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.game.strategy.GameStrategy;
import com.game.strategy.ProfitComparator;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
public class GameContext {

    int iterationNumber;
    int routinePosition;
    int progressPosition;
    int afterPosition;
    int shufflesCount;
    private final BlueDecks blueDecks;
    private final GreenDeck eventDeck;
    private final OrangeDeck orangeDeck;
    private final RedDeck relationshipDeck;
    private final Players players;

    public boolean isCarInService;
    public boolean firstTimeTrainingSkillDone;

    CardSet<BlueGreenCard> player1Hand = new CardSet<>();
    CardSet<BlueGreenCard> player2Hand = new CardSet<>();
    CardsOnTable cardsOnTable = new CardsOnTable();
    CardSet<GreenCard> turnGreenCards = new CardSet<>();
    private int currentDay;
    private int currentRound;
    private RedCard currentRedCard;
    private ErrorDetails error;
    private int cardsAmount;
    private boolean discount50;
    private GameStrategy strategy;
    private boolean use_extra_tension;

    public void initRound(int round, RedCard redCard) {
        currentRound = round;
        currentRedCard = redCard;
        //apply red Card if meet conditions
        /**
         * а) вводятся если условие на начало раунда
         * б) работают, если условие на конец раунда
         * я задумывал вариант а) но можно и вариант б)
         */
        redCard.initRound(this);

        // init stats
        players.player1.initRound();
        players.player2.initRound();
    }

    public void drawInitialCards() {
        // get cards
        for (int i = 0; i < 6; i++) {
            player1Hand.cards.add(getNextRoutine());
            player2Hand.cards.add(getNextRoutine());
        }
    }

    public void drawCardsDuringDay() {
        // get cards
        for (int i = 0; i < 3; i++) {
            player1Hand.cards.add(getNextRoutine());
            player2Hand.cards.add(getNextRoutine());
        }
    }

    public BlueCard getNextPersonal(BlueDeck playerDiscardDeck) {
        Queue<BlueCard> cards = playerDiscardDeck.getCards();
        if (!cards.isEmpty()) {
            return cards.poll();
        }
        return getNextRoutine();

    }

    public BlueCard getNextProgress() {
        Queue<BlueCard> cards = blueDecks.progressDeck.getCards();
        if (!cards.isEmpty()) {
            progressPosition++;
            return cards.poll();
        }
        cards = blueDecks.discardedShuffledDeck.getCards();
        if (cards.isEmpty()) {
            // TODO 4: +shuffle discard deck
            afterPosition = 1;
            shufflesCount++;
            cards.addAll(blueDecks.discardDeck.getCards());
            blueDecks.discardDeck.getCards().clear();
        }
        afterPosition++;
        if (cards.isEmpty()) {
            // may be different logic
            return getNextRoutine();
        }
        return cards.poll();

    }

    public BlueCard getNextRoutine() {
        Queue<BlueCard> cards = blueDecks.routineDeck.getCards();
        if (!cards.isEmpty()) {
            routinePosition++;
            return cards.poll();
        }
        cards = blueDecks.discardedShuffledDeck.getCards();
        if (cards.isEmpty()) {
            // TODO 4: +shuffle discard deck
            afterPosition = 1;
            shufflesCount++;
            cards.addAll(blueDecks.discardDeck.getCards());
            blueDecks.discardDeck.getCards().clear();
        }
        afterPosition++;
        if (cards.isEmpty()) {
            throw new IllegalStateException("not enough cards in game during routine!");
        }
        return cards.poll();
    }

    public void calcRedAndOrangeCardsAtTheEndOfTheRound(RedCard redCard) {
        // calc red card
        // calc orange cards
        redCard.finishRound(this);
        players.player1.getOrangeCards().cards.forEach(card -> card.finish(this, players.player1.id));
        players.player2.getOrangeCards().cards.forEach(card -> card.finish(this, players.player2.id));
        players.player1.finishWeek();
        players.player2.finishWeek();
    }

    public void clearCards() {
        //clear cards
        for (BlueGreenCardWithLocationOption blueGreenCard : getCardsOnTable().cardsOnTable.cards) {
            BlueGreenCard cardBase = blueGreenCard.getCardBase();
            if (cardBase instanceof BlueCard) {
                getBlueDecks().getDiscardDeck().getCards().add((BlueCard) cardBase);
            }
        }
        getCardsOnTable().clear();
    }

    public String getRoundDayString() {
        String s = "[" + getCurrentRound() +
            ", " + getCurrentDay() + "]";
        return s;
    }

    public static String getPlayerInfo(CardSet playerHand, Player player) {
        return " Hand " + player.id + ": " + playerHand
            + "\n Person " + player.id + " resources: " + player.getMoneyEnergyString() + "\n";
    }

    public int assertCountAllBlueCardsInGame() {
        List<BlueGreenCardWithLocationOption> blueCardsOnTable = cardsOnTable.cardsOnTable.cards
            .stream().filter(c -> c.getCardBase() instanceof BlueCard)
            .collect(Collectors.toList());
        boolean DEBUG_AMMOUNT_CARDS = true;
        if (DEBUG_AMMOUNT_CARDS) {
            System.out.println("\n "
                + " iteration : " + iterationNumber
                + " round, day : " + getRoundDayString());
            System.out.println(blueDecks.printExtendedCount());
            System.out.println("player1Hand.cards, [" + player1Hand.cards.size() + "]: " + player1Hand.idNames());
            System.out.println("player2Hand.cards, [" + player2Hand.cards.size() + "]: " + player2Hand.idNames());
            System.out.println(
                "cardsOnTable.cardsOnTable, [" + blueCardsOnTable.size() + "]: " + cardsOnTable.cardsOnTable.ids());
        }
        int count = (int) (blueDecks.getCount() +
                    player1Hand.cards.size() +
                    player2Hand.cards.size() +
                    blueCardsOnTable.size());
        if (cardsAmount != count) {
            throw new Error(String.format( "Invalid calculation at iteration %d, %s, should be cardsAmount = %d, but count = %d ",iterationNumber, getRoundDayString(), cardsAmount, count));
        }
        return count;

    }

    public void discardCard(BlueCard blueGreenCard) {
        this.blueDecks.getDiscardDeck().getCards().add(blueGreenCard);
    }

    public Player getPlayer(int i) {
        return players.getPlayer(i);
    }
    public Player getPlayerPartner(int i) {
        return players.getPlayerPartner(i);
    }
    public CardSet<BlueGreenCard> getPlayerHand(int i) {
        return i == 1 ? player1Hand : player2Hand;
    }

    Map<String, Map<String, AtomicInteger>> statistics = new LinkedHashMap<>();

    public void addStatistics(String key, Object value) {
        statistics.putIfAbsent(key, new LinkedHashMap<>());
        var map = statistics.get(key);
        map.putIfAbsent(value.toString(), new AtomicInteger());
        map.get(value.toString()).incrementAndGet();

    }


    public int discountable(int costMoney) {
        if (!isDiscount50()) {
            return costMoney;
        }
        return (costMoney + 1) / 2;
    }

    public int getMinMoneyPlayer() {
        return players.getMinMoneyPlayer();
    }

    public ProfitComparator profitComparator() {
        return new ProfitComparator(use_extra_tension || isFirstRound());
    }

    public boolean isFirstRound() {
        // не совсем правильно, делаю проверку на null только для тестов
        boolean firstRound = currentRedCard!=null && currentRedCard.getRound() == 1;
        return firstRound;
    }

    public void setUse_extra_tension(boolean use_extra_tension) {
        this.use_extra_tension = use_extra_tension;
    }

    public boolean isUse_extra_tension() {
        return use_extra_tension;
    }

    public void addDay() {
        players.player1.state.addDay();
        players.player2.state.addDay();
    }

    public void playScandal(boolean alreadyAffected, int ownerId, int partnerId) {
        // чтобы два раза не учитывать  эффект, который был
        if (alreadyAffected) {
            // TODO 2: при карте "устойчивость от сканадалов" нужно этот эффект отыграть
        } else {
            players.getPlayer(ownerId).addTension(1);
            if (partnerId > 0) {
                players.getPlayer(partnerId).addTension(1);
            }
        }
    }
}
