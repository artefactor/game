package com.game.model;

import static java.util.stream.Collectors.joining;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Util {

    public static String shortInfo(List<BlueGreenCard> cards) {
        StringBuilder stringBuilder = new StringBuilder();
        if (cards.isEmpty()){
            return "------------";
        }
        stringBuilder.append(cards.stream()
            .map(Util::shortInfoB).collect(
                joining(",")));

        return "" + stringBuilder + "";
    }

    private static String shortInfoB(BlueGreenCard card) {
        return card.getId() + " " +
            (card.getName() + " -----------------").substring(0, 11)
            //            + (card.getRestriction() != null ? card.getRestriction() : " ")
            //            + (card.getSpecial() != none ? card.getSpecial() : "")
            ;
    }

    public static String shortInfo(BlueGreenCard card) {
        if (card == null) {
            return "-------------";
        }
        return card.getId() + " " + card.getName();
    }

    public static <T extends GameCard> String printCards(Collection<T> cards) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<T> iterator = cards.iterator();
        for (int i = 0; i < cards.size(); i++) {
            stringBuilder.append("\t\t").append(i + 1).append(".")
                .append(iterator.next()).append("\n");
        }

        return "{\n" + stringBuilder + '}';
    }

    public static String printCards(Map<?, ?> cards) {
        StringBuilder stringBuilder = new StringBuilder();
        var iterator = cards.entrySet().iterator();
        for (int i = 0; i < cards.size(); i++) {
            Map.Entry<?, ?> next = iterator.next();
            stringBuilder.append("\t\t").append(i + 1).append(".")
                .append(next.getKey())
                .append(" - ")
                .append(next.getValue())
                .append("\n");
        }

        return "{\n" + stringBuilder + '}';
    }

}
