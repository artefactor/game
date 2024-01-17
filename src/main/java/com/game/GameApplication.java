package com.game;

import static com.game.model.GameContext.getPlayerInfo;

import java.util.Iterator;
import java.util.List;

import com.game.model.ErrorDetails;
import com.game.model.GameContext;
import com.game.model.GreenCard;
import com.game.model.RedCard;
import com.game.strategy.GameStrategy;
import lombok.SneakyThrows;

public class GameApplication {

    /**
     * не хватает глобально: I имплементация всех правил 1) карты играть на совместно и на партнера. На кого аффектиться
     * бонус и цена 2) зеленые карты 3) карты характера 4) особые свойства карт
     * <p>
     * II имплементация запуска сьюта кастомизация зеленых карт кастомизация черт характера кастомизация параметров
     * игроков кастомизация стратегий игры ИИ кастомизация правил добора кастомизация правил колод кастомизация правил
     * когда нечем походить подсчет статистических параметров результата (дисперсия, макс, мин)
     * <p>
     * III стратегия учет карт характера
     *
     * ПЛАН:
     * закрыть ТОDO 0 - 8 карт зеленых и 8 синих карт
     * затем  - реализовать красные и
     * тогда смогу тестировать пару ходов.
     *
     *
     */

    @SneakyThrows
    public static void main(String[] args) {
//      String appProperties = "application_small.properties";
        String appProperties = "application.properties";

        // init game
        GameSimulationSuiteResult simulationResult = new GameSimulationSuiteResult();
        Iterator<GameContext> gameContextIterator = new GameSimulationSuiteFactory(appProperties).initAndGetIterator();
        while (gameContextIterator.hasNext()) {
            GameContext gameContext = gameContextIterator.next();
            var strategy = new GameStrategy(gameContext);
            runTheGame(gameContext, strategy);
//            System.out.println(gameContext);
            System.out.println("all cards:" + gameContext.assertCountAllBlueCardsInGame());

            // calculate result
            var result = new DetailedGameResult(gameContext);
            System.out.println(result);
            simulationResult.add(result);
        }
        System.out.println(simulationResult.summarize());
    }

    private static void runTheGame(GameContext gameContext, GameStrategy strategy) {
        gameContext.getEventDeck().resetCounters();
        gameContext.getBlueDecks().resetCounters();
        // run game
        try {
            List<RedCard> list = gameContext.getRelationshipDeck().getCards();
            for (int round = 1; round < list.size() + 1; round++) {
                // init round
                RedCard redCard = list.get(round - 1);
                gameContext.initRound(round, redCard);
                strategy.drawCardsAtTheBeginningOfTheRound();

                // Mon - Sat
                int day = 1;
                for (; day <= 6; day++) {
                    // breakPoint if needed
                    if (round == 1 && day == 6) {
                        int u = 0;
                    }
                    gameContext.setCurrentDay(day);
                    System.out.println("all cards:" + gameContext.assertCountAllBlueCardsInGame());
                    strategy.prePlan();
                    GreenCard card1 = gameContext.getEventDeck().checkOnEvent(day);
                    GreenCard card2 = gameContext.getEventDeck().checkOnEvent(day);
                    strategy.playTurn(card1, card2);
                    // версия правил такая. можно сделать другую версию
                    if (day == 3) {
                        strategy.drawCardsDuringDay();
                    }
                }
                // Sun
                gameContext.setCurrentDay(day);
                strategy.prePlan();
                strategy.playTurn(null, null);

                // end of round
                gameContext.calcRedAndOrangeCardsAtTheEndOfTheRound(redCard);

                if (round == 6 && day == 7) {
                    int u = 0;
                }
                System.out.println("all cards:" + gameContext.assertCountAllBlueCardsInGame());
                strategy.saveCards();
                System.out.println("all cards:" + gameContext.assertCountAllBlueCardsInGame());
                gameContext.clearCards();

                System.out.println("all cards:" + gameContext.assertCountAllBlueCardsInGame());

            }
        } catch (Exception e) {
            String info = gameContext.getRoundDayString() + "\n"
                + getPlayerInfo(gameContext.getPlayer1Hand(), gameContext.getPlayers().getPlayer1())
                + getPlayerInfo(gameContext.getPlayer2Hand(), gameContext.getPlayers().getPlayer2());
            System.err.println("Error at:" + e + "\n" + info);

            String message = e.getMessage();
            if (e instanceof ArrayIndexOutOfBoundsException){
                throw new Error(e);
            }
            if (e instanceof NullPointerException){
                message +=" " + e.getStackTrace()[0].toString();
            }
            gameContext.setError(new ErrorDetails(
                e,
                info,
                gameContext.getRoundDayString(),
                e.getClass().getName(),
                message));

        }
    }


}

// some strategy
//Run count: 10000
//WIN_WEDDING - 39% (3933),
//BROKE_UP_AS_FRIENDS - 45% (4547),
//BROKE_UP_AS_ENEMIES - 9% (976),
//ERROR - 5% (544)
//In average:
// Player1: {171 🎂}, [15💥]		14💰		3 💪
// Player2: {95 🎂}, [10💥]		34💰		3 💪

// random strategy
//
//    Run count: 10000
//    WIN_WEDDING - 0% (18),
//    BROKE_UP_AS_FRIENDS - 34% (3454),
//    BROKE_UP_AS_ENEMIES - 22% (2253),
//    ERROR - 42% (4275)
//    In average:
//    Player1: {171 🎂}, [15💥]		7💰		2 💪
//    Player2: {58 🎂}, [14💥]		10💰		1 💪