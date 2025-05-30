package com.branka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ToCardConverter {

    static Function<WordCard, WordCard> typeToGroupConvert(List<WordCard> readDeck) {
        Map<String, Character> typeGroup = new HashMap<>();
        for (var wordCard : readDeck) {
            typeGroup.put(wordCard.getType(), wordCard.getGroup());
        }
        return wordCard -> {
            updateWordCard(wordCard, typeGroup);
            return wordCard;
        };
    }

    private static void updateWordCard(WordCard wordCard, Map<String, Character> typeGroup) {
        wordCard.setName(null);
        var additions = wordCard.getAdditions();
        if (additions != null) {
            for (WordCard.Addition addition : additions) {
                var changedList = addition.list.stream().map(
                    r -> typeGroup.get(r).toString()
                ).collect(Collectors.toList());
                addition.setList(changedList);
            }
        }
    }

    static Function<WordCard, Card> typeConvert() {
        return wordCard -> new Card(wordCard.getGroup());
    }

    static Function<WordCard, Card> typeAndToneConvert() {
        return wordCard -> new Card(wordCard.getGroupTone());
    }
}
