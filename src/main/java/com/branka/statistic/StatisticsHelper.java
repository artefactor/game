package com.branka.statistic;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.branka.WordCard;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

public class StatisticsHelper {

    public static void countUnique(List<WordCard> cards) {
        var uniqueCardCounts = cards.stream()
            .collect(Collectors.groupingBy(
                StatisticsHelper::group,                    // Классификатор
                HashMap::new,                                         // Поставщик карты
                Collectors.toCollection(LinkedList::new)              // Коллектор для значений
            ));

        //        uniqueCardCounts.sort(new CardCountComparator());
        System.out.println("unique: " + uniqueCardCounts.size());
        for (CardKey key : uniqueCardCounts.keySet()) {
            System.out.println(uniqueCardCounts.get(key).size() + ": " + getWordCards(uniqueCardCounts, key));
        }
        System.out.println(
            "уникальных групп " + cards.stream().map(WordCard::getGroup).collect(Collectors.toSet()).size());
        System.out.println(
            "уникальных групп-тонов " + cards.stream().map(WordCard::getGroupTone).collect(Collectors.toSet()).size());
    }

    private static String getWordCards(HashMap<CardKey, LinkedList<WordCard>> uniqueCardCounts,
        CardKey key) {
        LinkedList<WordCard> wordCards1 = uniqueCardCounts.get(key);
        Optional<WordCard> first = wordCards1.stream()
            .filter(c -> !c.getGroupTone().equals(key.getGroupTone()))
            //            .filter(c -> c.getTone() != key.tone)
            //            .filter(c -> c.getType() != key.type)
            .findFirst();
        checkWordCards(wordCards1);
        if (first.isPresent()) {
            throw new RuntimeException("" + first.get());
        }
        var wordCards = wordCards1.stream()
            .map(WordCard::getGroup)
            .collect(Collectors.toList());

        var tones = wordCards1.stream()
            .map(WordCard::getTone)
            .collect(Collectors.toList());
        boolean allTonesEqual = allTonesEqual(tones);
        boolean allCharactersEqual = allCharactersEqual(wordCards);
        if (allTonesEqual) {
            return "";
        }
        if (allCharactersEqual) {
            return "";
        }
        //        return allCharactersEqual + "; " + StringUtils.join(wordCards, ",");
        return allTonesEqual + "; " + StringUtils.join(tones, ",") + " ||  " + allCharactersEqual + "; "
            + StringUtils.join(wordCards, ",");
    }

    private static void checkWordCards(LinkedList<WordCard> uniqueCardCounts) {
        for (WordCard uniqueCardCount : uniqueCardCounts) {
            if (!Objects.equals(uniqueCardCounts.get(0).getTone(), uniqueCardCount.getTone())) {
                throw new RuntimeException("not eq: " + uniqueCardCounts.get(0).getTone() + " " +  uniqueCardCount.getId());
            }
        }
        for (WordCard uniqueCardCount : uniqueCardCounts) {
            if (!Objects.equals(uniqueCardCounts.get(0).getGroupTone(), uniqueCardCount.getGroupTone())) {
                throw new RuntimeException("not eq: " + uniqueCardCounts.get(0).getGroupTone() + " " +  uniqueCardCount.getId());
            }
        }
        for (WordCard uniqueCardCount : uniqueCardCounts) {
            if (!Objects.equals(uniqueCardCounts.get(0).getType(), uniqueCardCount.getType())) {
                throw new RuntimeException("not eq: " + uniqueCardCounts.get(0).getType() + " " +  uniqueCardCount.getId());
            }
        }
    }

    private static boolean allTonesEqual(List<WordCard.Tone> uniqueCardCounts) {
        var first = uniqueCardCounts.get(0);
        return uniqueCardCounts.stream()
            .allMatch(ch -> Objects.equals(first, ch));
    }

    public static boolean allCharactersEqual(List<Character> characters) {
        if (characters == null || characters.isEmpty()) {
            return true;
        }
        Character first = characters.get(0);
        return characters.stream()
            .allMatch(ch -> Objects.equals(first, ch));
    }

    private static CardKey group(WordCard card) {
        return group(card, false);
    }

    private static CardKey groupWithoutTone(WordCard card) {
        return group(card, true);
    }

    private static CardKey group(WordCard card, boolean withoutTone) {
        return new CardKey(card.getGroupTone());
        //        return new CardKey(
        //            withoutTone ? null : card.getTone(),
        //            card.getType(), card.getOption(), card.getAdditions(),
        //            card.getMultiAddition());
    }

    @Data
    @AllArgsConstructor
    static class CardCount {

        private List<WordCard> wordCards;
        private long count;
    }

    @Data
    @AllArgsConstructor
    static class CardKey {

        private Character groupTone;
        //        private WordCard.Tone tone;
        //        private WordCard.Type type;
        //        private Integer option;
        //        private List<WordCard.Addition> additions;
        //        private WordCard.Addition multiAddition;

    }

    //    static class CardCountComparator implements Comparator<CardCount> {
    //
    //        @Override
    //        public int compare(CardCount o1, CardCount o2) {
    //            return Comparator
    //                // Шаг 1: Приоритет для Type.A
    //                .comparing((CardCount cc) -> cc.getWordCards().getType() == WordCard.Type.ГЛАГОЛ, Comparator.reverseOrder())
    //                // Шаг 2: Сортировка по type
    //                .thenComparing(cc -> cc.getWordCards().getType())
    //                // Шаг 3: Сортировка по option
    //                .thenComparing(cc -> {
    //                    Integer option = cc.getWordCards().getOption();
    //                    return option != null ? option : Integer.MIN_VALUE; // Обработка null
    //                })
    //                // Шаг 4: Сортировка по tone
    //                .thenComparing(cc -> cc.getWordCards().getTone())
    //                .compare(o1, o2);
    //        }
    //    }
}
