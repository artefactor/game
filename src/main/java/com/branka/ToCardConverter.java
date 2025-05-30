package com.branka;

import java.util.function.Function;

public class ToCardConverter {

    static Function<WordCard, Card> typeConvert() {
        return wordCard -> new Card(wordCard.getGroup());
    }

    static Function<WordCard, Card> typeAndToneConvert() {
        return wordCard -> new Card(wordCard.getGroupTone());
    }
}
