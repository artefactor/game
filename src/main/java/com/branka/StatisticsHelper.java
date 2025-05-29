package com.branka;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;

public class StatisticsHelper {

    static void countUnique(List<WordCard> cards) {
        var uniqueCardCounts = new ArrayList<>(cards.stream()
            .collect(Collectors.groupingBy(
                StatisticsHelper::group,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> new CardCount(list.get(0), list.size())
                )
            ))
            .values());

        for (var uniqueCardCount : uniqueCardCounts) {
            System.out.println(uniqueCardCount.getCount());
        }
    }

    private static CardKey group(WordCard card) {
        return new CardKey(card.getTone(), card.getType(), card.getOption(), card.getAdditions(),
            card.getMultiAddition());
    }

    @Data
    @AllArgsConstructor
    static class CardCount {

        private WordCard card;
        private long count;
    }

    @Data
    @AllArgsConstructor
    static class CardKey {

        private WordCard.Tone tone;
        private WordCard.Type type;
        private Integer option;
        private List<WordCard.Addition> additions;
        private WordCard.Addition multiAddition;
    }
}
