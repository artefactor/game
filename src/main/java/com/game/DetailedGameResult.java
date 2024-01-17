package com.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.game.model.ErrorDetails;
import com.game.model.FavouriteAction;
import com.game.model.GameContext;
import com.game.model.Player;

public class DetailedGameResult {

    int counter;
    int usedEventCards;

    enum GameResult {
        WIN_WEDDING,
        BROKE_UP_AS_FRIENDS,
        BROKE_UP_AS_ENEMIES,
        ERROR,
    }

    final GameContext gameContext;
    final GameResult result;
    String gameResultDetails;

    DetailedGameResult(GameContext gameContext) {
        this.gameContext = gameContext;
        //        gameContext.getEventDeck().getCards().clear();
        //        gameContext.getBlueDecks().getProgressDeck().getCards().clear();
        //        gameContext.getBlueDecks().getRoutineDeck().getCards().clear();
        //        gameContext.getRelationshipDeck().getCards().clear();

        Player p1 = gameContext.getPlayers().getPlayer1();
        Player p2 = gameContext.getPlayers().getPlayer2();
        double sat1 = p1.getSatisfaction();
        double sat2 = p2.getSatisfaction();
        int tens1 = p1.getTension();
        int tens2 = p2.getTension();

        boolean isWinSatCond = sat1 >= 100 && sat2 >= 100;
        boolean isWinTensCond = tens1 <= 20 && tens2 <= 20;
        ErrorDetails error = gameContext.getError();
        if (error != null) {
            result = GameResult.ERROR;
            gameResultDetails = "error at " + error.getErrorPoint();
            return;
        }
        // TODO 4: вариант если за 6 ходов
        if (isWinSatCond && isWinTensCond) {
            result = GameResult.WIN_WEDDING;
        } else if (isWinTensCond) {
            result = GameResult.BROKE_UP_AS_FRIENDS;
        } else {
            result = GameResult.BROKE_UP_AS_ENEMIES;
        }
        counter = gameContext.getEventDeck().getCounter();
        usedEventCards = gameContext.getEventDeck().getUsedCards();
        gameResultDetails = result
            + ": \n"
            + "Satisfaction Tension Money Energy \n"
            + getPlayerInfo(1, p1) + "\n"
            + getPlayerInfo(2, p2) + "\n"
            + String.format("event counter = %d, usedEventCards = %d ", counter, usedEventCards)
        ;
    }

    static String getPlayerInfo(int i, Player p) {
        return " Player" + i + ":" + p.getSatTensionString() + "\t\t" + p.getMoneyEnergyString();
    }


    @Override
    public String toString() {
        return gameResultDetails;
    }


}

class GameSimulationSuiteResult {

    List<DetailedGameResult> resultList = new ArrayList<>();

    public void add(DetailedGameResult result) {
        resultList.add(result);
    }

    public String summarize() {
        Map<String, AtomicInteger> played_optionsTotal = new HashMap<>();
        var errorResults = new LinkedHashMap<String, AtomicInteger>();

        var results = new LinkedHashMap<DetailedGameResult.GameResult, AtomicInteger>();
        for (DetailedGameResult.GameResult value : DetailedGameResult.GameResult.values()) {
            results.put(value, new AtomicInteger(0));
        }
        String a1 = resultList.get(0).gameContext.getPlayers().getPlayer1().getFavouriteActionType();
        String a2 = resultList.get(0).gameContext.getPlayers().getPlayer2().getFavouriteActionType();
        var averageP1 = new Player(1, 0, 0, FavouriteAction.valueOf(a1));
        var averageP2 = new Player(2, 0, 0, FavouriteAction.valueOf(a2));
        int notErrorResultsCount = 0;
        int sumCounter = 0;
        int sumUsedEventCards = 0;

        for (DetailedGameResult detailedGameResult : resultList) {
            DetailedGameResult.GameResult result = detailedGameResult.result;
            results.get(result).incrementAndGet();
            if (result == DetailedGameResult.GameResult.ERROR) {
                // считаем среднее по ошибке. И какую ошибку
                ErrorDetails error = detailedGameResult.gameContext.getError();
                errorResults.putIfAbsent(error.getErrorMessage(), new AtomicInteger());
                errorResults.get(error.getErrorMessage()).incrementAndGet();
                errorResults.putIfAbsent(error.getErrorPoint(), new AtomicInteger());
                errorResults.get(error.getErrorPoint()).incrementAndGet();
            } else {
                notErrorResultsCount++;
                averageP1.add(detailedGameResult.gameContext.getPlayers().getPlayer1());
                averageP2.add(detailedGameResult.gameContext.getPlayers().getPlayer2());
                sumUsedEventCards += detailedGameResult.usedEventCards;
                sumCounter += detailedGameResult.counter;

                Map<String, AtomicInteger> played_options = detailedGameResult.gameContext.getStatistics()
                    .get("played_option");

                played_options.forEach((key, value) -> {
                    played_optionsTotal.putIfAbsent(key, new AtomicInteger(0));
                    played_optionsTotal.get(key).addAndGet(value.intValue());
                });

            }
        }

        int size = resultList.size();
        averageP1.divide(notErrorResultsCount);
        averageP2.divide(notErrorResultsCount);
        int averageUsedEventCards = notErrorResultsCount > 0 ? sumUsedEventCards / notErrorResultsCount : 0;
        int averageCounter = notErrorResultsCount > 0 ? sumCounter / notErrorResultsCount : 0;
        String r = results.entrySet().stream().map(entry ->
            entry.getKey() + " - " +
                (entry.getValue().doubleValue() * 100 / size) + "%" + " (" + entry.getValue().intValue() + ")"
        ).collect(Collectors.joining(",\n"));

        return "\n===============================\n"

            + "Run count: " + size + "\n"
            + r + "\n"
            + "In average:" + "\n"
            + DetailedGameResult.getPlayerInfo(1, averageP1) + "\n"
            + DetailedGameResult.getPlayerInfo(2, averageP2) + "\n"
            + String.format("event counter = %d, usedEventCards = %d ", averageCounter, averageUsedEventCards)


            +"\n\n played options:\n"+
            played_optionsTotal.entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, AtomicInteger>, Integer>comparing(e -> e.getValue().get()).reversed())
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(",\n"))
            + (errorResults.isEmpty()? "": "\n\n error details:\n ")
            + errorResults.entrySet().stream()
            .sorted(Comparator.<Map.Entry<String, AtomicInteger>, Integer>comparing(e -> e.getValue().get()).reversed())
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.joining(",\n"))
            ;

    }

}
