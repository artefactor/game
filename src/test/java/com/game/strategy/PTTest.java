package com.game.strategy;

import static com.game.model.FavouriteAction.B;
import static com.game.model.FavouriteAction.H;
import static com.game.model.FavouriteAction.O;
import static com.game.model.FavouriteAction.R;
import static com.game.model.FavouriteAction.T;
import static com.game.model.FavouriteAction.U;
import static com.game.sets.BlueCardsTestSet.findB;
import static com.game.sets.GreenCardsTestSet.findG;
import static com.game.sets.OrangeCardsTestSet.findO;
import static com.game.strategy.PT.ALL_OPTIONS;
import static com.game.strategy.PT.BOTH_1_OPT;
import static com.game.strategy.PT.BOTH_2_OPT;
import static com.game.strategy.PT.PART_PART_OPT;
import static com.game.strategy.PT.SELF_AS_BOTH_2_OPT;
import static com.game.strategy.PT.SELF_PART_1_OPT;
import static com.game.strategy.PT.SELF_PART_2_OPT;
import static com.game.strategy.PT.SELF_SELF_OPT;
import static com.game.strategy.PlayersTurnEnum2.BOTH_1;
import static com.game.strategy.PlayersTurnEnum2.EMPTY;
import static com.game.strategy.PlayersTurnEnum2.PARTNER_PARTNER_2;
import static com.game.strategy.PlayersTurnEnum2.SELF_PARTNER_2;
import static com.game.strategy.PlayersTurnEnum2.SELF_PARTNER_2_SUNDAY;
import static com.game.strategy.PlayersTurnEnum2.SELF_SELF_2;
import static com.game.strategy.PlayersTurnEnum2.SELF_SELF_2_SUNDAY;
import static com.game.strategy.PlayersTurnEnum2.SELF_SELF_AS_BOTH_2;
import static java.util.List.of;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.game.model.BlueCard;
import com.game.model.BlueDecks;
import com.game.model.BlueGreenCard;
import com.game.model.BlueGreenCardWithLocationOption;
import com.game.model.CardsOnTable;
import com.game.model.FavouriteAction;
import com.game.model.GameContext;
import com.game.model.GreenCard;
import com.game.model.GreenDeck;
import com.game.model.OrangeDeck;
import com.game.model.Player;
import com.game.model.Players;
import com.game.model.RedDeck;
import com.game.model.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PTTest {

    protected static final Player PR1_u = p1(7, 4, U);
    protected static final Player PR2_h = p2(8, 2, H);

    protected static final Player PR3_o = p1(2, 0, O);
    protected static final Player PR3_o_st = withStats(p1(2, 2, O), null, 0, null, 1);
    protected static final Player PR4_b = p2(5, 2, B);
    protected static final Player PR4_t = p2(5, 2, T);
    protected static final String EMPPPPP = "        ";

    @Test
    void playTwoCards() {
            Player player1 = new Player(1, 10, 10, O).initRound();
            Player player2 = new Player(2, 20, 20, R).initRound();
            var g = new GameContext(null, null, null, null,
                new Players(player1, player2));
            PT pt1 = new PT(g, false);
            final List<TwoCards> optionList = new ArrayList<>();

            var a1 = Optional.of(new BlueGreenCardWithLocationOption(findB("N_01").get(0)));
            var b1 = Optional.of(new BlueGreenCardWithLocationOption(findB("N_02").get(0)));
            var selfSelf1 = new SelfSelf2(pt1, g, 1); optionList.add(selfSelf1);
            selfSelf1.setCurrentPlayer1ToSelfCard(a1);
            selfSelf1.setCurrentPlayer2ToSelfCard(b1);


            var a2 = Optional.of(new BlueGreenCardWithLocationOption(findB("B_05").get(0)));
            var b2 = Optional.of(new BlueGreenCardWithLocationOption(findB("T_13").get(0)));
            var partnerPartner1 = new PartnerPartner2(pt1, g, 1); optionList.add(partnerPartner1);
            partnerPartner1.currentPlayer1ToPartnerCard = a2;
            partnerPartner1.currentPlayer2ToPartnerCard = b2;

            var a3 = Optional.of(new BlueGreenCardWithLocationOption(findB("O_03").get(0)));
            var b3 = Optional.of(new BlueGreenCardWithLocationOption(findB("B_06").get(0)));
            var selfPartner1 = new SelfPartner1(pt1, g, 1); optionList.add(selfPartner1);
            selfPartner1.currentPlayerToSelfCard = a3;
            selfPartner1.currentPlayerToPartnerCard = b3;

            var a4 = Optional.of(new BlueGreenCardWithLocationOption(findB("T_07").get(0)));
            var b4 = Optional.of(new BlueGreenCardWithLocationOption(findB("B_07").get(0)));
            var selfPartner2 = new SelfPartner1(pt1, g, 2); optionList.add(selfPartner2);
            selfPartner2.currentPlayerToSelfCard = a4;
            selfPartner2.currentPlayerToPartnerCard = b4;

            GreenCard g_SELF = findG("1");
            GreenCard g_PARTNER = findG("13");
            GreenCard g_SELF_OR_BOTH = findG("9");
            g.setCurrentRound(1);
            int currentDay = 1;

            var exp = new HashMap<TwoCards, Map<String, List<String>>>();
        exp.put(selfSelf1, Map.of(
            "normal play cards",             List.of("N_01 for 1\tN_02 for 2"),
            "options if card 1 self",        List.of("1 for 1	N_02 for 2", "N_01 for 1	N_02 for 2"),
            "options if card 1 self_both",   List.of("9 for 1	N_02 for 2", " 9 for 1", " N_01 for 1	N_02 for 2"),
            "options if card 1 partner",     List.of("N_01 for 1	13 for 2", " N_01 for 1	N_02 for 2"),
            "options if card 2 self",        List.of("1 for 2	N_01 for 1", " N_01 for 1	N_02 for 2"),
            "options if card 2 self_both", List.of("9 for 2	N_01 for 1", " 9 for 2", " N_01 for 1	N_02 for 2"),
            "options if card 2 partner", List.of("N_02 for 2	13 for 1", " N_01 for 1	N_02 for 2")
                ));
            exp.put(partnerPartner1, Map.of(
                "normal play cards",  List.of("B_05 for 2\tT_13 for 1"),
        "options if card 1 self", List.of("1 for 1	B_05 for 2"," B_05 for 2	T_13 for 1"),
        "options if card 1 self_both", List.of("9 for 1	B_05 for 2"," 9 for 1"," B_05 for 2	T_13 for 1"),
        "options if card 1 partner", List.of("T_13 for 1	13 for 2"," B_05 for 2	T_13 for 1"),
        "options if card 2 self", List.of("1 for 2	T_13 for 1"," B_05 for 2	T_13 for 1"),
        "options if card 2 self_both", List.of("9 for 2	T_13 for 1"," 9 for 2"," B_05 for 2	T_13 for 1"),
        "options if card 2 partner", List.of("B_05 for 2	13 for 1"," B_05 for 2	T_13 for 1")
            ));
            exp.put(selfPartner1, Map.of(
                "normal play cards",  List.of("O_03 for 1\tB_06 for 2"),
        "options if card 1 self", List.of("1 for 1	B_06 for 2"," O_03 for 1	B_06 for 2"),
        "options if card 1 self_both", List.of("9 for 1	B_06 for 2"," 9 for 1"," O_03 for 1	B_06 for 2"),
        "options if card 1 partner", List.of("O_03 for 1	13 for 2"," O_03 for 1	B_06 for 2"),
        "options if card 2 self", List.of("1 for 2	O_03 for 1"," O_03 for 1	B_06 for 2"),
        "options if card 2 self_both", List.of("9 for 2	O_03 for 1"," 9 for 2"," O_03 for 1	B_06 for 2"),
        "options if card 2 partner", List.of("B_06 for 2	13 for 1"," O_03 for 1	B_06 for 2")
            ));
            exp.put(selfPartner2, Map.of(
                "normal play cards",  List.of("T_07 for 2\tB_07 for 1"),
        "options if card 1 self", List.of("1 for 1	T_07 for 2"," T_07 for 2	B_07 for 1"),
        "options if card 1 self_both", List.of("9 for 1	T_07 for 2"," 9 for 1"," T_07 for 2	B_07 for 1"),
        "options if card 1 partner", List.of("B_07 for 1	13 for 2"," T_07 for 2	B_07 for 1"),
        "options if card 2 self", List.of("1 for 2	B_07 for 1"," T_07 for 2	B_07 for 1"),
        "options if card 2 self_both", List.of("9 for 2	B_07 for 1"," 9 for 2"," T_07 for 2	B_07 for 1"),
        "options if card 2 partner", List.of("T_07 for 2	13 for 1"," T_07 for 2	B_07 for 1")
            ));
        List<String> assertMessages = new ArrayList<>();
            for (TwoCards option : optionList) {
                System.out.println("" +currentDay + " "+ option.value + " A: " + option.playerId + " B: " + option.partnerId);
                g.setCurrentDay(currentDay++);
                Map<String, List<String>> expListMap = exp.get(option);
                // normal play cards
                assertWithPrint("normal play cards", List.of(option.playCardsInfo()), expListMap);
                option.calcTheBestOption();
                System.out.println();
                System.out.println("-");

                assertWithPrint("options if card 1 self", option.playOneGreenCard(g_SELF, 1), expListMap).ifPresent(assertMessages::add);
                // options if card 1 self_both
                assertWithPrint("options if card 1 self_both", option.playOneGreenCard(g_SELF_OR_BOTH, 1), expListMap).ifPresent(assertMessages::add);
                // options if card 1 partner
                assertWithPrint("options if card 1 partner", option.playOneGreenCard(g_PARTNER, 1), expListMap).ifPresent(assertMessages::add);

                // options if card 2 self
                assertWithPrint("options if card 2 self", option.playOneGreenCard(g_SELF, 2), expListMap).ifPresent(assertMessages::add);
                // options if card 2 self_both
                assertWithPrint("options if card 2 self_both", option.playOneGreenCard(g_SELF_OR_BOTH, 2), expListMap).ifPresent(assertMessages::add);
                // options if card 2 partner
                assertWithPrint("options if card 2 partner", option.playOneGreenCard(g_PARTNER, 2), expListMap).ifPresent(assertMessages::add);

                System.out.println(option.getFirstCard(1).get().getCardBase().getId());
                System.out.println(option.getSecondCard(2).get().getCardBase().getId());
                System.out.println("-------------------");
        }
        assertTrue(assertMessages.isEmpty(), () -> "wrong :" + assertMessages.size() +"\n"+ String.join("\n", assertMessages));
    }

    private Optional<String> assertWithPrint(String description, List<String> actual,
        Map<String, List<String>> expListMap) {
        System.out.print(description + "\t");
        System.out.println(actual);
        String joinExp = expListMap.get(description).stream().map(String::trim).collect(joining("\n"));
        String joinAct = actual.stream().map(String::trim).collect(joining("\n"));
        try {
            assertEquals(joinExp, joinAct);
        } catch (Error e) {
            return Optional.of(e.getMessage());
        }
        return Optional.empty();
    }

    @Test
    void playBoth() {
        GameContext gameContext = initGameContext(findB("B_07"), findB("T_13"), PR1_u.copy(), PR2_h.copy(), 1);
        var b1 = new Both1(new PT(gameContext), gameContext, 1);
        b1.currentPlayerBothCard = Optional.of(new BlueGreenCardWithLocationOption(findB("N_04").get(0)));
        b1.calcTheBestOption();
        GreenCard first = findG("22");
        GreenCard second = findG("20");
        String s1 = b1.playOneGreenCard(first, b1.playerId).get(0);
        String s2 = b1.playOneGreenCard(second, b1.partnerId).get(0);

        gameContext = initGameContext(findB("B_07"), findB("T_13"), PR1_u.copy(), PR2_h.copy(), 1);
        var b2 = new Both1(new PT(gameContext), gameContext, 2);
        b2.currentPlayerBothCard = Optional.of(new BlueGreenCardWithLocationOption(findB("N_04").get(0)));
        b2.calcTheBestOption();
        String s3 = b2.playOneGreenCard(first, b2.playerId).get(0);
        String s4 = b2.playOneGreenCard(second, b2.partnerId).get(0);
        assertEquals("active1", s1);
        assertEquals("partner2", s2);
        assertEquals("active2", s3);
        assertEquals("partner1", s4);
    }

    @ParameterizedTest
    @MethodSource(value = {
                "playGreenCardSelfSelf",
                "playGreenCardPartnerPartner",
                "playGreenCardSelfPartner",
                "playGreenCardBoth",
                "playGreenCardBothObchenie",
                "playBlueCardWeekend",
                "playGreenIllness",
                "playBlueCardSelfSelf_traits"
    })
    void playGreenCard(
        List<BlueGreenCard> c1, List<BlueGreenCard> c2,
        Player player1, Player player2,
        List<Integer> option,
        GreenCard greenCard1, GreenCard greenCard2,
        PlayersTurnEnum2 expChoice,
        List<String> expCardsIds,
        Player expPlayer1,
        Player expPlayer2,
        Map<String, Object> gameContextOptions
    ) {
        testGreenCards(c1, c2, player1, player2, option, greenCard1, greenCard2, expChoice, expCardsIds, expPlayer1, expPlayer2, gameContextOptions);
    }


    @ParameterizedTest(name = "greenInvertedW-{index}")
    @MethodSource(value = {
        "playBlueCardSelfSelf_traits",
    })
    void playGreenCardInvertedAtWeekend(
        List<BlueGreenCard> c2, List<BlueGreenCard> c1,
        Player player2, Player player1, List<Integer> option,
        GreenCard greenCard2, GreenCard greenCard1,
        PlayersTurnEnum2 expChoice,
        List<String> expCardsIds,
        Player expPlayer2, Player expPlayer1
    ) {
        testGreenCards(c1, c2, invert(player1), invert(player2), invertOptions(option), greenCard1, greenCard2,
            expChoice, expCardsIds, invert(expPlayer1),
            invert(expPlayer2), Map.of("day", 6));
    }

    @ParameterizedTest(name = "greenInverted-{index}")
    @MethodSource(value = {
        "playGreenCardSelfSelf",
        "playGreenCardPartnerPartner",
        "playGreenCardSelfPartner",
        "playGreenCardBoth",
        "playGreenCardBothObchenie",
//        "playBlueCardWeekend",
        "playBlueCardSelfSelf_traits",
        "playGreenIllness"
    })
    void playGreenCardInverted(
        List<BlueGreenCard> c2, List<BlueGreenCard> c1,
        Player player2, Player player1, List<Integer> option,
        GreenCard greenCard2, GreenCard greenCard1,
        PlayersTurnEnum2 expChoice,
        List<String> expCardsIds,
        Player expPlayer2, Player expPlayer1,
        Map<String, Object> objectMap
    ) {
        testGreenCards(c1, c2, invert(player1), invert(player2), invertOptions(option), greenCard1, greenCard2,
            expChoice, expCardsIds, invert(expPlayer1),
            invert(expPlayer2), objectMap);
    }

    private Player invert(Player player) {
        player.setId(3 - player.id);
        return player;
    }

    static List<Integer> invertOptions(List<Integer> option) {
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < option.size(); i++) {
            Integer integer = option.get(i);
            switch (integer) {
                //                    SELF_SELF_OPT = 1;
                //                    PART_PART_OPT = 2;
                case SELF_PART_1_OPT:
                    integer = SELF_PART_2_OPT;
                    break;
                case SELF_PART_2_OPT:
                    integer = SELF_PART_1_OPT;
                    break;
                case BOTH_1_OPT:
                    integer = BOTH_2_OPT;
                    break;
                case BOTH_2_OPT:
                    integer = BOTH_1_OPT;
                    break;
            }
            integers.add(integer);
        }
        return integers;
    }

    @Test
    void customTest() {
//        int indexInParametrizedList = (int) (playGreenIllness().count());
        int indexInParametrizedList = 3;
        System.out.println("playGreenCardBoth");
        System.out.println(": " + indexInParametrizedList);
        Stream<Arguments> argumentsStream = playGreenCardBoth();
        var distinct =
            argumentsStream.skip(indexInParametrizedList - 1).limit(1).distinct().collect(Collectors.toList()).get(0)
                .get();
        testGreenCards(
            (List<BlueGreenCard>) distinct[0],
            (List<BlueGreenCard>) distinct[1],
            (Player) distinct[2],
            (Player) distinct[3],
            (List<Integer>) distinct[4],
            (GreenCard) distinct[5],
            (GreenCard) distinct[6],
            (PlayersTurnEnum2) distinct[7],
            (List<String>) distinct[8],
            (Player) distinct[9],
            (Player) distinct[10],
            (Map<String, Object>) distinct[11]);
    }

    @Test
    void customTestInverted() {
        int indexInParametrizedList = 6;
        System.out.println("playGreenCardBothObchenie");
        System.out.println(": " + indexInParametrizedList);
        Stream<Arguments> argumentsStream = playGreenCardBothObchenie();
        var distinct =
            argumentsStream.skip(indexInParametrizedList - 1).limit(1).distinct().collect(Collectors.toList()).get(0)
                .get();
        testGreenCards(
            (List<BlueGreenCard>) distinct[1],
            (List<BlueGreenCard>) distinct[0],
            invert((Player) distinct[3]),
            invert((Player) distinct[2]),
            invertOptions((List<Integer>) distinct[4]),
            (GreenCard) distinct[6],
            (GreenCard) distinct[5],
            (PlayersTurnEnum2) distinct[7],
            (List<String>) distinct[8],
            invert((Player) distinct[10]),
            invert((Player) distinct[9]),
            Map.of("day", 1));
    }

    private void testGreenCards(List<BlueGreenCard> c1, List<BlueGreenCard> c2, Player player1, Player player2,
        List<Integer> option,
        GreenCard greenCard1, GreenCard greenCard2, PlayersTurnEnum2 expChoice, List<String> expCardsIds,
        Player expPlayer1, Player expPlayer2, Map<String, Object> objectMap) {
        GameContext gameContext = initGameContext(c1, c2, player1, player2, (Integer) objectMap.getOrDefault("day", 1));

        boolean use_extra_tension = (boolean) objectMap.getOrDefault("USE_EXTRA_TENSION", true);
        gameContext.setUse_extra_tension(use_extra_tension);

        int cardsOnTableCount = 0;
        var cardsOnTable = objectMap.getOrDefault("cardsOnTable", null);
        if (cardsOnTable != null) {
            gameContext.setCardsOnTable((CardsOnTable) cardsOnTable);
            cardsOnTableCount = ((CardsOnTable) cardsOnTable).cardsOnTable.cards.size();
        }

        gameContext.setCardsAmount(c1.size() + c2.size()+ cardsOnTableCount );

        String s1 = Util.shortInfo(c1);
        String s2 = Util.shortInfo(c2);
        String s1a = Util.shortInfo(greenCard1);
        String s2a = Util.shortInfo(greenCard2);
        int len = Math.max(s1.length(), s1a.length()) + 2;

        String y = range(0, len)
            .mapToObj(i -> "-").collect(joining()) + "" +
            "  DATA  " +
            range(0, len)
                .mapToObj(i -> "-").collect(joining());
        System.out.println(y);
        printLine(
            "------- P1 " + player1.getFavouriteActionType() + " ",
            "------- P2 " + player2.getFavouriteActionType() + " ",
             len);
        String x = range(0, len)
            .mapToObj(i -> "-").collect(joining()) + "" +
            EMPPPPP+
            range(0, len)
                .mapToObj(i -> "-").collect(joining());
        printLine(s1, s2, len);
        printLine(s1a, s2a, len);
        System.out.println(x);
        System.out.println("----------------- " + expCardsIds + " ----------------");

        PT pt = new PT(gameContext, option, true);
        PTOption max = pt.max;
        pt.options = ALL_OPTIONS;
        boolean noCards = max.noCards();
        //        System.out.println(pt.collectTurnChoiceInformation());
        assertFalse(noCards);
        assertEquals(expChoice, max.value);

        max.playTurn(greenCard1, greenCard2);

        List<String> actualCardsOnTable = gameContext.getCardsOnTable().cardsOnTable.cards
            .stream()
            .map(BlueGreenCardWithLocationOption::getCardBase)
            .map(BlueGreenCard::getId).
            collect(Collectors.toList());
        String message = "actual: " + actualCardsOnTable + ", vs exp: " + expCardsIds;
        assertTrue(actualCardsOnTable.containsAll(expCardsIds), message);
        assertTrue(expCardsIds.containsAll(actualCardsOnTable), message);

        assertEquals(expPlayer1, player1);
        assertEquals(expPlayer2, player2);
        System.out.println("all cards:" + gameContext.assertCountAllBlueCardsInGame());
        // нужны карты на руках.
        // игроки имеют один вариант их сыграть
        // выпадает зеленая /зеленые
        // как они сыграли - какую зеленую да, какую нет. как изменились их статы
    }

    private void printLine(String s1, String s2, int len) {
        System.out.print("" + s1);
        for (int i = s1.length(); i <= len; i++) {
            System.out.print("-");
        }
        System.out.print(EMPPPPP);
        System.out.print("" + s2);
        for (int i = s2.length(); i < len; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

    static Stream<Arguments> playGreenCardSelfSelf() {
        return Stream.of(
            // массаж восстанавливает, как и сон, но не более стамины
            /**/   arg(
                new Object[] {
                    findB("T_07"), findB("T_08"),
                    withStats(PR1_u.copy(), null, 0, null, 1),
                    PR2_h.copy(),
                    List.of(SELF_SELF_OPT)
                },
                null, null,
                SELF_SELF_2,
                of("T_07", "T_08"),
                withStats(PR1_u, 0, 3, PR1_u.getMoney() - 2, 2),
                withStats(PR2_h, 0, 3, PR2_h.getMoney() - 2, null)
            ),
            // массаж восстанавливает, как и сон, но не более стамины
            /**/   arg(
                new Object[] {
                    findB("N_01"), findB("N_02"),
                    withStats(PR1_u.copy(), null, 0, null, 1),
                    PR2_h.copy(),
                    List.of(SELF_SELF_OPT)
                },
                null, null,
                SELF_SELF_2,
                of("N_01", "N_02"),
                withStats(PR1_u, 0, 1, null, 2),
                withStats(PR2_h, 0, 1, null, null)
            ),
            /**/   arg(
                createTestSet1(),
                null, null,
                SELF_SELF_2,
                of("T_01", "T_06"),
                withStats(PR1_u, 0, 2, 5, 2),
                withStats(PR2_h, 0, 2, 6, 1)
            )
            ,
            /**/
            arg(
                createTestSet1(),
                findG("1"), null,
                SELF_SELF_2,
                of("1", "T_06"),
                withStats(PR1_u, 0, 0, 7, 4),
                withStats(PR2_h, 0, 2, 6, 1)
            ),
            arg(
                createTestSet1(),
                findG("13"), null,
                SELF_SELF_2,
                of("T_01", "13"),
                withStats(PR1_u, 0, 2, 5, 2),
                withStats(PR2_h, 0, 0, 8, 2)
            )/**/,
            // карты от которых нельзя отказаться
            arg(
                createTestSet1(),
                findG("22"), null,
                SELF_SELF_2,
                of("22", "T_06"),
                withStats(PR1_u, 0, 0, 7, 3),
                withStats(PR2_h, 0, 2, 6, 1)
            ),
            // бонус
            arg(
                createTestSet1(),
                findG("7"), null,
                SELF_SELF_2,
                of("7"),
                withStats(PR1_u, 0, 3.5, 6, null),
                withStats(PR2_h, 0, 3.5, 6, null)
            ),   //  но зп не хватает, не сможет пойти на ДР
            arg(
                //                createTestSet1Money(2),    - здесь могут скинуться и пойти, почему нет
                createTestSet1Money(2),
                findG("7"), null,
                SELF_SELF_2,
                of("T_01", "T_06"),
                withStats(PR1_u, 0, 2, 0, 2),
                withStats(PR2_h, 0, 2, 0, 1)
            ),
            // если уровень удовлетворенности партнера больше 30
            arg(
                createTestSet1(),
                findG("20"), null,
                SELF_SELF_2,
                of("20", "T_06"),
                withStats(PR1_u, 0, 10, 5, 4),
                withStats(PR2_h, 0, 2, 6, 1)
            ),
            // c парнером  10
            arg(
                createTestSet1(31),
                findG("20"), null,
                SELF_SELF_2,
                of("20"),
                withStats(PR1_u, 0, 31 + 10, 5, 4),
                withStats(PR2_h, 0, 31 + 3.5, 6, 2)
            ),
            // лоттерея одному  11
            arg(
                new Object[] {
                    findB("T_01"), findB("B_04"),
                    PR1_u.copy(),
                    PR2_h.copy(),
                    List.of(SELF_SELF_OPT)
                },
                findG("9"), null,
                SELF_SELF_2,
                of("9", "B_04"),
                withStats(PR1_u, 0, 0, PR1_u.getMoney() + 4, null),
                withStats(PR2_h, 0, 3, null, null),
                Map.of("USE_EXTRA_TENSION", false)
            ),
            // лоттерея с партнером  12
            arg(
                createTestSet1Money(2),
                findG("9"), null,
                SELF_SELF_2,
                of("9"),
                withStats(PR1_u, 0, 0, 2 + 4, null),
                withStats(PR2_h, 0, 0, 2, null)
            )

            // карты от которых нельзя отказаться
            // 4 - труба
            // 5 - машина

            // карты с партнером
            // 17 - крестник (по желанию)
            // 6 - шкаф

            // 16 - психотер

            // болезни
            // 10
            // 11
            // 12

            //15 скидки

        );
    }

    static Stream<Arguments> playGreenCardPartnerPartner() {
        return Stream.of(
            /**/   arg(
                createTestSet2(),
                null, null,
                PARTNER_PARTNER_2,
                of("B_07", "T_13"),
                withStats(PR3_o, 0, 4, 0, 0),
                withStats(PR4_b, 0, 7, 3, 2)
            ),
            /**/   arg(
                createTestSet3(),
                null, null,
                PARTNER_PARTNER_2,
                of("B_08", "B_03"),
                withStats(PR3_o, 0, 1, null, 0),
                withStats(PR4_t, 0, 3, null, 1)
            )
        );
    }

    static Stream<Arguments> playGreenCardSelfPartner() {
        Object[] args = new Object[] {
            List.of(), findB("H_01", "B_04"),
            PR3_o.copy(),
            PR4_b.copy(),
            List.of(SELF_PART_1_OPT, SELF_PART_2_OPT)
        };
        Object[] argsInverted = new Object[] {
            findB("H_01", "B_04"), List.of(),
            PR3_o.copy(),
            PR4_t.copy(),
            List.of(SELF_PART_1_OPT, SELF_PART_2_OPT)
        };
        return Stream.of(
            /**/   arg(
                createTestSet4(),
                null, null,
                SELF_PARTNER_2,
                of("B_08", "B_03"),
                withStats(PR3_o, 0, 4, null, 0),
                withStats(PR4_b, 0, 2, null, 1)
            ),
            /**/   arg(
                args,
                findG("5"), null,
                SELF_PARTNER_2,
                of("5", "H_01"),
                withStats(stateCar(PR3_o.copy(), true), 1, 0, null, null),
                withStats(PR4_b, 0, 3, PR4_b.getMoney() - 2, null),
                Map.of("USE_EXTRA_TENSION", false)
            ),

            // the same but inverted

            /**/   arg(
                argsInverted,
                null, findG("5"),
                SELF_PARTNER_2,
                of("5", "H_01"),
                withStats(PR3_o, 0, 3, PR3_o.getMoney() - 2, null),
                withStats(stateCar(PR4_t.copy(), true), 1, 0, null, null),
                Map.of("USE_EXTRA_TENSION", false)
            )
        );
    }

    static Stream<Arguments> playBlueCardWeekend() {
        // рыбалка
        BlueGreenCardWithLocationOption card = new BlueGreenCardWithLocationOption(findB("H_07").get(0));
        BlueGreenCardWithLocationOption cardПоход = new BlueGreenCardWithLocationOption(findB("T_10").get(0));

        return Stream.of(
            /**/   arg(
                // нужно чтобы был выходной - -
                new Object[] {
                    findB("U_05"), findB("H_05"),
                    withStats(PR1_u.copy(), null, 0, 7, null),
                    PR2_h.copy(),
                    List.of(SELF_SELF_OPT)
                },
                null, null,
                SELF_SELF_2_SUNDAY,
                of(
                    "H_07"  // c субботы
                    , "H_05"),
                withStats(PR1_u, null, 0, null, null),
                withStats(PR2_h, null, 5, null, null),
                cardsOnSaturday(card, 1, 1)
            ),

            /**/   arg(
                // нужно чтобы был выходной - -
                new Object[] {
                    findB("H_05"), findB("U_05"),
                    withStats(PR1_u.copy(), null, 0, 7, null),
                    PR2_h.copy(),
                    List.of(SELF_SELF_OPT)
                },
                null, null,
                SELF_SELF_2_SUNDAY,
                of(
                    "H_07"  // c субботы
                    , "H_05"),
                    withStats(PR1_u, null, 3, null, null),
                    withStats(PR2_h, null, 0, null, null),
                cardsOnSaturday(card, 2, 2)
            )
            // partner
            ,
            /**/   arg(
                // нужно чтобы был выходной - -
                new Object[] {
                    findB("B_10"), findB("U_05"),
                    withStats(PR1_u.copy(), null, 0, 7, null),
                    PR2_h.copy(),
                    List.of(SELF_PART_1_OPT)
                },
                null, null,
                SELF_PARTNER_2_SUNDAY,
                of(
                    "H_07"  // c субботы
                    , "B_10"),
                withStats(PR1_u, null, 2, null, null),
                withStats(PR2_h, null, 0, null, 1),
                cardsOnSaturday(card, 1, 1)
            ),

            // both
            /**/   arg(
                // нужно чтобы был выходной - -
                new Object[] {
                    findB("B_10"), findB("U_05"),
                    withStats(PR1_u.copy(), null, 0, 7, null),
                    PR2_h.copy(),
                    ALL_OPTIONS
                },
                null, null,
                EMPTY,
                of(
                    "T_10"  // c субботы
                    ),
                withStats(PR1_u, null, 0, null, null),
                withStats(PR2_h, null, 0, null, null),
                cardsOnSaturdayBoth(cardПоход, 2)
            )

        );

    }

    private static Map<String, Object> cardsOnSaturday(BlueGreenCardWithLocationOption card, int ownerId, int playerId) {
        GameContext mock = mock(GameContext.class);
        when(mock.getCurrentDay()).thenReturn(6);
        CardsOnTable cardsOnTable = new CardsOnTable();
        cardsOnTable.playCard(card, mock, ownerId, playerId);
        Map<String, Object> gameContextOptions = Map.of(
            "day", 7,
            "cardsOnTable", cardsOnTable
        );
        return gameContextOptions;
    }

    private static Map<String, Object> cardsOnSaturdayBoth(BlueGreenCardWithLocationOption card, int ownerId) {
        GameContext mock = mock(GameContext.class);
        when(mock.getCurrentDay()).thenReturn(6);
        CardsOnTable cardsOnTable = new CardsOnTable();
        cardsOnTable.playCardBoth(card, mock, ownerId);
        Map<String, Object> gameContextOptions = Map.of(
            "day", 7,
            "cardsOnTable", cardsOnTable
        );
        return gameContextOptions;
    }


        static Stream<Arguments> playBlueCardSelfSelf_traits() {
        Map<String, Object> gameContextOptions = Map.of("day", 6);

        return Stream.of(
            /**/   arg(
                // нужно чтобы был выходной - -
                new Object[] {
                    findB("U_05"), findB("U_06"),   //   Психотренинг  и книга
                    addOrange(withStats(PR1_u.copy(), null, 0, 7, null), 5, 26),
                    addOrange(PR2_h.copy(), 28, 14),
                    List.of(SELF_SELF_OPT)
                },
                null, null,
                SELF_SELF_2,
                of("U_05", "U_06"),
                addOrange(withStats(PR1_u, null, 4, 0, null), 26, 5, 49),
                addOrange(withStats(PR2_h, null, 0, PR2_h.getMoney() - 1, null), 28),
                gameContextOptions
            ),
            /**/   arg(
                new Object[] {
                    findB("U_01"), findB("U_04"),   //   психотерапия
                    addOrange(withStats(PR1_u.copy(), null, 0, 7, null), 5, 26),
                    addOrange(PR2_h.copy(), 28, 14),
                    List.of(SELF_SELF_OPT)
                },
                null, null,
                SELF_SELF_2,
                of("U_01","U_04"),
                addOrange(withStats(PR1_u, null, 3, 9, PR1_u.getEnergy() - 1), 5, 26),
                addOrange(withStats(PR2_h, null, 0, PR2_h.getMoney() - 4, null), 28),
                gameContextOptions
            )
            ,
            /**/   arg(
                new Object[] {
                    findB(), findB("U_04"),   //   психотерапия на двоих
                    addOrange(withStats(PR1_u.copy(), null, 0, 7, null), 5, 26),
                    addOrange(PR2_h.copy(), 28, 14),
                    List.of(BOTH_1_OPT, BOTH_2_OPT)
                },
                null, null,
                BOTH_1,
                of("U_04"),
                addOrange(withStats(PR1_u, null, 2, PR1_u.getMoney() - 4 , null), 26),
                addOrange(withStats(PR2_h, null, 0, PR2_h.getMoney() - 4, null), 28),
                gameContextOptions
            )

            // TODO 0: тренинг навыка на два вечера  "U_03"
        );

    }

    private static Player addOrange(Player player, Integer... i) {
        Player copy = player.copy();
        copy.getOrangeCards().cards.addAll(findO(i));
        return copy;
    }

    static Stream<Arguments> playGreenCardBothObchenie() {
        return Stream.of(
            // 5a
            /**/   arg(
                new Object[] {
                    List.of(), findB("O_06"),
                    PR3_o.copy(),
                    PR4_b.copy(),
                    List.of(BOTH_1_OPT, BOTH_2_OPT, SELF_AS_BOTH_2_OPT)
                },
                null, null,
                BOTH_1,
                of("O_06"),
                withStats(PR3_o, 1, 2, null, null),
                withStats(PR4_b, 1, 0, null, null)
            )
            // 5b
            ,/**/   arg(
                new Object[] {
                    findB("O_06"), findB("O_07"),
                    PR3_o.copy(),
                    PR4_b.copy(),
                    List.of(SELF_SELF_OPT, BOTH_1_OPT, BOTH_2_OPT, SELF_AS_BOTH_2_OPT)
                },
                null, null,
                SELF_SELF_AS_BOTH_2,
                of("O_06", "O_07"),
                withStats(PR3_o, 2, 4, null, null),
                withStats(PR4_b, 2, 0, null, null)
            )
            //  тесты на "душевный разговор"
            //  душевный разговор ничего не снимает, если напряжения нет
            // 5c
            ,/**/   arg(
                new Object[] {
                    List.of(), findB("O_10"),
                    PR3_o.copy(),
                    PR4_b.copy(),
                    List.of(BOTH_1_OPT, BOTH_2_OPT, SELF_AS_BOTH_2_OPT)
                },
                null, null,
                BOTH_1,
                of("O_10"),
                withStats(PR3_o, 0, 2, null, null),
                withStats(PR4_b, 0, 0, null, null)
            )
            // 5c
            // душевный разговор по умолчанию снимает у того, у кого больше напряжения.
            // если равенство - то у того, у кого меньше удовлетворения (можно у автора карты, можно еще какой-то критерий)
            ,/**/   arg(
                new Object[] {
                    List.of(), findB("O_10"),
                    withStats(PR3_o.copy(), 1, 0, null, null),
                    withStats(PR4_b.copy(), 1, 0, null, null),
                    List.of(BOTH_1_OPT, BOTH_2_OPT, SELF_AS_BOTH_2_OPT)
                },
                null, null,
                BOTH_1,
                of("O_10"),
                withStats(PR3_o, 1, 2, null, null),
                withStats(PR4_b, 0, 0, null, null)
            )
            // душевный разговор по умолчанию снимает у того, у кого больше напряжения.
            // если равенство - то у того, у кого меньше удовлетворения (можно у автора карты, можно еще какой-то критерий)
            // 5c
            ,/**/   arg(
                new Object[] {
                    List.of(), findB("O_11"),
                    withStats(PR3_o.copy(), 5, 0, null, null),
                    withStats(PR4_b.copy(), 1, 0, null, null),
                    List.of(BOTH_1_OPT, BOTH_2_OPT, SELF_AS_BOTH_2_OPT)
                },
                null, null,
                BOTH_1,
                of("O_11"),
                withStats(PR3_o, 4, 2, null, null),
                withStats(PR4_b, 1, 0, null, null)
            )
            // 5d
            ,
            /**/   arg(
                new Object[] {
                    findB("O_11"), findB("O_12"),
                    withStats(PR3_o.copy(), 1, 0, null, null),
                    withStats(PR4_b.copy(), 1, 0, null, null),
                    List.of(SELF_SELF_OPT, BOTH_1_OPT, BOTH_2_OPT, SELF_AS_BOTH_2_OPT)
                },
                null, null,
                SELF_SELF_AS_BOTH_2,
                of("O_11", "O_12"),
                withStats(PR3_o, 0, 4, null, null),
                withStats(PR4_b, 0, 0, null, null)
            )
        );
    }

    static Stream<Arguments> playGreenIllness() {
        return Stream.of(
            // 10 заболел до конца недели
            /**/   arg(
                new Object[] {
                    findB("O_11"), findB("B_02"),
                    PR1_u.copy(),
                    PR4_b.copy(),
                    List.of(BOTH_1_OPT, BOTH_2_OPT)
                },
                findG("10"), null,
                BOTH_1,
                of("10", "B_02"),
                unwell(withStats(PR1_u, 0, 0, null, null), true, false, 0),
                withStats(PR4_b, 0, 5, null, null)
            ),
            // 12 заболел на 7 дней
            /**/   arg(
                new Object[] {
                    findB("O_11"), findB("B_02"),
                    PR1_u.copy(),
                    PR4_b.copy(),
                    List.of(BOTH_1_OPT, BOTH_2_OPT)
                },
                findG("12"), null,
                BOTH_1,
                of("12", "B_02"),
                unwell(withStats(PR1_u, 0, 0, null, null), false, true, 7),
                withStats(PR4_b, 0, 5, null, null)
            ),

            // 5 Сломалась машина
            /**/   arg(
                new Object[] {
                    findB("O_11"), findB("B_02"),
                    PR1_u.copy(),
                    PR4_b.copy(),
                    List.of(BOTH_1_OPT, BOTH_2_OPT)
                },
                findG("5"), null,
                BOTH_1,
                of("5", "B_02"),
                withStats(stateCar(PR1_u.copy(), true), 1, 0, null, null),
                withStats(PR4_b, 0, 5, null, null),
                Map.of("USE_EXTRA_TENSION", false)
            )
        );
    }

    private static Player unwell(Player player, boolean isHit, boolean isIll, int days) {
        player.getState().setHit(isHit);
        player.getState().setIll(isIll);
        player.getState().setIllnessDays(days);
        return player;
    }

    private static Player stateCar(Player player, boolean brokenCar) {
        player.getState().setHasBrokenCar(brokenCar);
        return player;
    }


    static Stream<Arguments> playGreenCardBoth() {
        return Stream.of(
            /**/   arg(
                new Object[] {
                    findB("N_06"), findB("N_02"),
                    PR3_o_st.copy(),
                    PR4_b.copy(),
                    List.of(BOTH_1_OPT, BOTH_2_OPT)
                },
                findG("1"), null,
                BOTH_1,
                of("1", "N_02"),
                withStats(PR3_o_st, 0, 2, null, null),
                withStats(PR4_b, 0, 1, null, null)
            ),
            /**/   arg(
                new Object[] {
                    findB("N_06", "N_02"), List.of(),
                    PR3_o_st.copy(),
                    PR4_b.copy(),
                    List.of(BOTH_1_OPT, BOTH_2_OPT)
                },
                null, findG("1"),
                BOTH_1,
                of("1", "N_02"),
                withStats(PR3_o_st, 0, 1, null, 2),
                withStats(PR4_b, 0, 0, null, null)
            )
            ,/**/   arg(
                new Object[] {
                    findB("H_02"), List.of(),
                    PR3_o_st.copy(),
                    PR4_b.copy(),
                    List.of(BOTH_1_OPT, BOTH_2_OPT)
                },
                findG("1"), null,
                BOTH_1,
                of("1", "H_02"),
                withStats(PR3_o_st, 0, 2, null, null),
                withStats(PR4_b, 0, 3, PR4_b.getMoney() - 2, null)
            )
            ,/**/   arg(
                new Object[] {
                    findB("H_02"), List.of(),
                    PR3_o.copy(),
                    PR4_b.copy(),
                    List.of(BOTH_1_OPT, BOTH_2_OPT)
                },
                null, findG("1"),
                BOTH_1,
                of("1", "H_02"),
                withStats(PR3_o, 0, 3, PR3_o.getMoney() - 2, null),
                withStats(PR4_b, 0, 0, null, null)
            )  //7
            ,/**/   arg(
                new Object[] {
                    findB("O_11"), findB("B_02"),
                    PR1_u.copy(),
                    PR4_b.copy(),
                    List.of(BOTH_1_OPT, BOTH_2_OPT)
                },
                findG("22"), null,
                BOTH_1,
                of("22", "B_02"),
                withStats(PR1_u, 0, 0, null, PR1_u.getEnergy() - 1),
                withStats(PR4_b, 0, 5, null, null)
            )  //7a
            ,/**/   arg(
                new Object[] {
                    findB("O_11"), findB("B_02"),
                    PR3_o.copy(),
                    PR4_b.copy(),
                    List.of(BOTH_1_OPT, BOTH_2_OPT)
                },
                findG("22"), null,
                BOTH_1,
                of("22", "B_02"),
                withStats(PR3_o, 1, 0, null, null),
                withStats(PR4_b, 0, 5, null, null)
            )
            //4 Прорвало трубу
            ,/**/   arg(
                new Object[] {
                    findB("O_11"), findB("B_02"),
                    PR1_u.copy(),
                    PR4_b.copy(),
                    List.of(BOTH_1_OPT, BOTH_2_OPT)
                },
                findG("4"), null,
                BOTH_1,
                of("4"),
                withStats(PR1_u, 0, 5, null, null),
                withStats(PR4_b, 0, 2, null, null)
            )
            ,/**/   arg(
                new Object[] {
                    findB("O_11"), findB("B_02"),
                    PR1_u.copy(),
                    PR2_h.copy(),
                    List.of(BOTH_1_OPT, BOTH_2_OPT)
                },
                findG("22"), findG("4"),
                BOTH_1,
                of("22", "4"),
                withStats(PR1_u, 0, 0, null, PR1_u.getEnergy() - 1),
                withStats(PR2_h, 2, 0, null, null)
            )


            //6 Передвинуть шкаф
            ,/**/   arg(
                new Object[] {
                    findB("O_11"), findB("B_02"),
                    PR1_u.copy(),
                    PR4_b.copy(),
                    List.of(BOTH_1_OPT, BOTH_2_OPT)
                },
                findG("6"), null,
                BOTH_1,
                of("6"),
                withStats(PR1_u, 0, 0, null, null),
                withStats(PR4_b, 0, 2, null, PR4_b.copy().getEnergy() - 2)
            )

        );
    }

    private static Object[] createTestSet1() {
        Object[] args = new Object[] {
            findB("T_01"), findB("T_06"),
            PR1_u.copy(),
            PR2_h.copy(),
            List.of(SELF_SELF_OPT)
        };
        return args;
    }

    private static Object[] createTestSet1Money(int money) {
        Object[] testSet1 = createTestSet1();
        ((Player) testSet1[2]).setMoney(money);
        ((Player) testSet1[3]).setMoney(money);
        return testSet1;
    }

    private static Object[] createTestSet1(int sat) {
        Object[] testSet1 = createTestSet1();
        ((Player) testSet1[2]).setSatisfaction(sat);
        ((Player) testSet1[3]).setSatisfaction(sat);
        return testSet1;
    }

    private static Object[] createTestSet2() {
        Object[] args = new Object[] {
            findB("B_07"), findB("T_13"),
            // можно повторить тест но с большей зп.
            PR3_o.copy(),
            PR4_b.copy(),
            List.of(PART_PART_OPT)
        };
        return args;
    }

    private static Object[] createTestSet3() {
        Object[] args = new Object[] {
            findB("B_08"), findB("B_03"),
            PR3_o.copy(),
            PR4_t.copy(),
            List.of(PART_PART_OPT)
        };
        return args;
    }

    private static Object[] createTestSet4() {
        Object[] args = new Object[] {
            findB("B_08", "B_03"), List.of(),
            PR3_o.copy(),
            PR4_b.copy(),
            List.of(SELF_PART_1_OPT, SELF_PART_2_OPT)
        };
        return args;
    }

    private static Arguments arg(List<BlueCard> c, List<BlueCard> c1,
        Player player1, Player player2, List<Integer> option,
        GreenCard greenCard1, GreenCard greenCard2,
        PlayersTurnEnum2 expChoice,
        List<String> expCardsIds,
        Player expPlayer1, Player expPlayer2) {
        return Arguments.of(c, c1, player1, player2, option, greenCard1, greenCard2, expChoice,
            expCardsIds, expPlayer1, expPlayer2, Map.of());
    }

//    private static Arguments arg(List<BlueCard> c, List<BlueCard> c1,
//        Player player1, Player player2, List<Integer> option,
//        GreenCard greenCard1, GreenCard greenCard2,
//        PlayersTurnEnum2 expChoice,
//        List<String> expCardsIds,
//        Player expPlayer1, Player expPlayer2,
//        Map<String, Object> gameContextOptions) {
//        return Arguments.of(c, c1, player1, player2, option, greenCard1, greenCard2, expChoice,
//            expCardsIds, expPlayer1, expPlayer2, gameContextOptions);
//    }

    private static Arguments arg(Object[] args,
        GreenCard greenCard1, GreenCard greenCard2,
        PlayersTurnEnum2 expChoice,
        List<String> expCardsIds,
        Player expPlayer1, Player expPlayer2) {
        return Arguments.of(args[0], args[1], args[2], args[3],
            args[4], greenCard1, greenCard2, expChoice,
            expCardsIds, expPlayer1, expPlayer2, Map.of());
    }

    private static Arguments arg(Object[] args,
        GreenCard greenCard1, GreenCard greenCard2,
        PlayersTurnEnum2 expChoice,
        List<String> expCardsIds,
        Player expPlayer1, Player expPlayer2,
        Map<String, Object> gameContextOptions) {
        return Arguments.of(args[0], args[1], args[2], args[3],
            args[4], greenCard1, greenCard2, expChoice,
            expCardsIds, expPlayer1, expPlayer2, gameContextOptions);
    }

    //    @ParameterizedTest
    @MethodSource
    void findTheBestOption(List<BlueGreenCard> c, List<BlueGreenCard> c1,
        Player player1, Player player2,
        PlayersTurnEnum2 expChoice, List<String> expCardsIds) {
        GameContext gameContext = initGameContext(c, c1, player1, player2, 1);
        //        1,2,3,4,5,
        PT pt = new PT(gameContext);
        PTOption max = pt.max;

        boolean noCards = max.noCards();
        //        System.out.println(pt.collectTurnChoiceInformation());

        assertFalse(noCards);
        assertEquals(expChoice, max.value);
        for (String card : expCardsIds) {
            assertTrue(max.printTheBestOption(false).contains(card), "" + card);
        }
    }

    // TODO 1: test on real cards
    // TODO 2: implement cards some specific.
    // TODO 2: Exclude cards that don't implemented for now
    private static Stream<Arguments> findTheBestOption() {
        return Stream.of(
            Arguments.of(
                findB("T_01", "T_02"), findB("T_04", "T_05"),
                PR1_u, PR2_h,
                SELF_SELF_2,
                List.of("T_01", "T_04")
            ),
            Arguments.of(
                findB("O_03", "T_12", "U_02", "T_06", "T_10"), findB(
                    "B_06", "T_01", "H_08", "N_02", "N_03"),
                PR1_u, PR2_h,
                SELF_SELF_2,
                List.of("O_03", "N_02")
            ),
            Arguments.of(
                findB("B_07", "B_06", "H_09", "T_12", "H_08"), findB(
                    "T_03", "U_02", "H_02", "T_07", "BB_13", "T_06", "N_02"),
                p1(1, 3, O), p2(2, 1, R),
                SELF_SELF_2,
                List.of("O_03", "N_02")
            )

        );
    }

    @ParameterizedTest
    @MethodSource
    void testSaveCards(List<BlueGreenCard> p1Cards, List<BlueGreenCard> p2Cards,
        List<BlueGreenCard> cardOnTable,
        List<BlueGreenCard> expP1Cards, List<BlueGreenCard> expP2Cards
    ) {
        GameContext gameContext = initGameContext(p1Cards, p2Cards, PR1_u.copy(), PR2_h.copy(), 1);
        CardsOnTable cardsOnTable1 = new CardsOnTable();
        cardsOnTable1.cardsOnTable.cards.addAll(cardOnTable.stream().map(BlueGreenCardWithLocationOption::new).collect(Collectors.toList()));
        new GameStrategy(gameContext).saveCards();
        // карты на руках карты на столе
        // ассерт карт, которые остаются на руках
        // машина
        // todo 1:
    }

    private static Stream<Arguments> testSaveCards() {
        return Stream.of(
            Arguments.of(
                findB("T_01", "T_02"), findB("T_04", "T_05"),
                findB("T_01", "T_02"),
                findB("T_01", "T_02"), findB("T_04", "T_05")
            )
        );
    }

    private GameContext initGameContext(List<BlueGreenCard> player1Hand, List<BlueGreenCard> player2Hand, Player player1, Player player2, int day) {
        Map<String, List<Integer>> blueCardsCubesShuffles = initBlueCardsCubesShuffles();
        BlueDecks blueDecks = new BlueDecks();
        blueDecks.setCounterMap(new HashMap<>());
        blueDecks.setBlueCardsCubesShuffles(blueCardsCubesShuffles);
        blueDecks.setTest(true);
        GameContext gameContext = new GameContext(
            blueDecks, new GreenDeck(new LinkedList<>(), of(of(1), of(1)), false, true)
            , new OrangeDeck(new LinkedList<>()), new RedDeck(new LinkedList<>()), new Players(player1, player2));

        new GameStrategy(gameContext);
        gameContext.getPlayer1Hand().cards.addAll(player1Hand);
        gameContext.getPlayer2Hand().cards.addAll(player2Hand);
        gameContext.setCurrentDay(day);
        gameContext.getBlueDecks().resetCounters();
        gameContext.getEventDeck().resetCounters();
        return gameContext;
    }

    private Map<String, List<Integer>> initBlueCardsCubesShuffles() {
        // тоже не совсем понятно, если запускать в цикле, то тогда нужно обнулять счетчик при тестах и ране
        List<Integer> currentShuffle1 = List.of(
            1, 2, 3, 4, 2, 3, 1, 4, 5, 5, 5, 3, 5, 3, 6, 3, 4, 4, 3, 1, 2, 1, 1, 1, 3, 6, 5, 5, 6, 1, 2, 1, 2, 5, 4, 2, 3, 6, 3, 1, 6, 1, 4, 5, 6, 4, 4, 2, 3, 4, 1, 5, 6, 3, 6, 3, 6, 2, 4, 4, 4, 5, 6, 2, 1, 3, 5, 6, 2, 2, 1, 3, 1, 5, 6, 6, 1, 3, 1, 4, 1, 2, 2, 6, 6, 5, 1, 2, 1, 4, 4, 4, 6, 3, 5, 4, 1, 2, 2, 2
        );

        Map<String, List<Integer>> blueCardsCubesShuffles = Map.of(
            "base", currentShuffle1,
            "T_04", List.of(6,6,6,6,6,6,6),
            "U_04", List.of(6,6,6,6,6,6,6),
            "U_05", List.of(6,6,6,6,6,6,6),
            "U_06", List.of(6,6,6,6,6,6,6)
        );
        return blueCardsCubesShuffles;
    }

    private static Player withStats(Player player, Integer tension, double satisfaction, Integer money,
        Integer energy) {
        Player copy = player.copy();
        if (tension != null) {
            copy.setTension(tension);
        }
        copy.setSatisfaction(satisfaction);
        if (money != null) {
            copy.setMoney(money);
        }
        if (energy != null) {
            copy.setEnergy(energy);
        }
        return copy;
    }

    static Player p1(int salary1, int stamina1, FavouriteAction o) {
        return new Player(1, salary1, stamina1, o).initRound();
    }

    static Player p2(int salary2, int stamina2, FavouriteAction r) {
        return new Player(2, salary2, stamina2, r).initRound();
    }
}