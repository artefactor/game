package com.game.strategy;

import static java.util.function.Predicate.not;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.game.GameSimulationSuiteFactory;
import com.game.model.CardsOnTable;
import com.game.model.GameContext;
import com.game.model.RedCard;
import com.game.model.WhenNoCards;

class PT {

    public static final int SELF_SELF_OPT = 1;
    public static final int PART_PART_OPT = 2;
    public static final int SELF_PART_1_OPT = 3;
    public static final int SELF_PART_2_OPT = 4;
    public static final int BOTH_1_OPT = 5;
    public static final int BOTH_2_OPT = 6;
    public static final int SELF_AS_BOTH_2_OPT = 7;
    public static final List<Integer> ALL_OPTIONS =
        List.of(SELF_SELF_OPT, PART_PART_OPT, SELF_PART_1_OPT, SELF_PART_2_OPT, BOTH_1_OPT,
            BOTH_2_OPT, SELF_AS_BOTH_2_OPT);
    List<Integer> options;
    private final GameContext gameContext;
    final List<PTOption> optionList;
    PTOption max;
    Comparator<PTOption> profitComparator ;

    public PT(GameContext gameContext) {
        this(gameContext, true);
    }

    public PT(GameContext gameContext, boolean init) {
        this(gameContext, ALL_OPTIONS, init);
    }

    public PT(GameContext gameContext, List<Integer> options, boolean init) {
        profitComparator = gameContext.profitComparator();

        this.options = options;
        optionList = createOptionList(gameContext);
        this.gameContext = gameContext;
        if (init) {
            init(gameContext);
        }
    }

    private void init(GameContext gameContext) {
        // prepare the best option for preplan
        // choose the best option
        List<PTOption> optionTypes = optionList.stream()
//            .flatMap(PTOption::getSubOptions)
            .map(PTOption::findTheBestOption)
            .filter(not(PTOption::noCards)).collect(Collectors.toList());
        System.out.println(" --  options count: " + optionTypes.size());  // можно в статистику считать сколько типов опций на каком ходу есть.

        max = optionTypes.stream()
            .map(PTOption::calcTheBestOption)
            .max(profitComparator).orElse(new PTOptionEmpty(this, gameContext, true));
        if (max.noCards()) {
            // if no option is applicable, handle whenNoRule
            WhenNoCards whenNoCards = WhenNoCards.THROW_EXCEPTION;
            // TODO 1: handle no cards - new max
            whenNoCards.handle(max.value);
        }

        // System.out.println(collectTurnChoiceInformation());
    }

    private List<PTOption> createOptionList(GameContext gameContext) {
        final List<PTOption> optionList = new ArrayList<>();
        if (gameContext.getCurrentDay() == 7){
            CardsOnTable cardsOnTable = gameContext.getCardsOnTable();
            boolean played1Weekend =  cardsOnTable.playedWeekend(1);
            boolean played2Weekend =  cardsOnTable.playedWeekend(2);
            boolean playedBothWeekend =  cardsOnTable.playedBothWeekend();
            if (played1Weekend && !played2Weekend){
                // 1 player sat san 2 days (weekend):
                // he  0, partner - plays  self_self(no,)
                // he  0 and for partner   self_partner(no,)
                if (options.contains(SELF_SELF_OPT)) {
                    optionList.add(new OneSundayCardSelf(this, gameContext, 2));
                }
                if (options.contains(SELF_PART_1_OPT)) {
                    optionList.add(new OneSundayCardPartner(this, gameContext, 1));
                }
                return optionList;
            }
            if (played2Weekend && !played1Weekend){
                // 2 player sat san 2 days (weekend):
                // he  0, partner - plays  self_self(,no)
                // he  0 and for partner   self_partner(,no)
                if (options.contains(SELF_SELF_OPT)) {
                    optionList.add(new OneSundayCardSelf(this, gameContext, 1));
                }
                if (options.contains(SELF_PART_2_OPT)) {
                    optionList.add(new OneSundayCardPartner(this, gameContext, 2));
                }
                return optionList;
            }
            if (played1Weekend && played2Weekend || playedBothWeekend){
                // 1 and 2 played  - no options
                // 1 plays both  - no options
                // 2 plays both  - no options, add 'empty option'
                optionList.add(new PTOptionEmpty(this, gameContext, false));
                return optionList;
            }
        }

        if (options.contains(SELF_SELF_OPT)) {
            optionList.add(new SelfSelf2(this, gameContext, 1));
        }
        if (options.contains(PART_PART_OPT)) {
            optionList.add(new PartnerPartner2(this, gameContext, 1));
        }
        if (options.contains(SELF_PART_1_OPT)) {
            optionList.add(new SelfPartner1(this, gameContext, 1));
        }
        if (options.contains(SELF_PART_2_OPT)) {
            optionList.add(new SelfPartner1(this, gameContext, 2));
        }
        if (options.contains(BOTH_1_OPT)) {
            optionList.add(new Both1(this, gameContext, 1));
        }
        if (options.contains(BOTH_2_OPT)) {
            optionList.add(new Both1(this, gameContext, 2));
        }
        if (options.contains(SELF_AS_BOTH_2_OPT)) {
            optionList.add(new Self_Self_as_Both_2(this, gameContext, 1));
        }
        return optionList;
    }

    String collectTurnChoiceInformation() {
        StringBuilder sb = new StringBuilder();
        sb.append("Player1 Hand:\n")
            .append(gameContext.getPlayer1Hand());
        sb.append("\nPlayer2 Hand:\n")
            .append(gameContext.getPlayer2Hand());
        sb.append("\n\nOptions:\n");
        for (int i = 0; i < optionList.size(); i++) {
            PTOption option = optionList.get(i);
            sb.append((i + 1) + "." + option.value + "\n" + option.printTheBestOption(false));
            sb.append("\n");
        }

        sb.append("chosen: ").append(max.value).append("\n").
            append(max.printTheBestOption(false));
        return sb.toString();
    }

    PTOption recalculateOptionsWithDiscount() {
        gameContext.setDiscount50(true);  // как сделать лучше ??
        PTOption max = createOptionList(gameContext).stream()
            // TODO 5 для статистики хотелось бы подсчитать, изменяется или нет вариант
            .map(PTOption::findTheBestOption)
            .map(PTOption::calcTheBestOption)
            .max(profitComparator).get();
        if (max.noCards()) {
            throw new IllegalStateException("ЛОгически невозможно. Уже должен быть какой-то вариант выбран");
        }
        return max;
    }


    public static void main(String[] args) throws IOException {
        String appProperties = "application.properties";
        // init game
        Iterator<GameContext> gameContextIterator = new GameSimulationSuiteFactory(appProperties).initAndGetIterator();
        GameContext gameContext = gameContextIterator.next();

        RedCard redCard = gameContext.getRelationshipDeck().getCards().get(0);
        int round = 1;
        gameContext.initRound(round, redCard);
        var strategy = new GameStrategy(gameContext);
        strategy.drawCardsAtTheBeginningOfTheRound();
        gameContext.setCurrentDay(1);

        PT pt = new PT(gameContext);

        // if no events - play the plan
        // if cancelable events - play the plan
        // if no cancelable events - change the plan and play events/cards
        pt.max.playTurn(gameContext.getEventDeck().getCards().get(0), null);
    }
}

