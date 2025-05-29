package com.branka;

import static java.util.Map.entry;

import java.util.Map;
import java.util.function.Function;

public class ToCardConverter {

    static Map<WordCard.Type, Character> typeMap = Map.ofEntries(
        entry(WordCard.Type.ГЛАГОЛ, 'G'),
        entry(WordCard.Type.КАКОМУ, 'Q'),
        entry(WordCard.Type.КОГО, 'R'),
        entry(WordCard.Type.КОМУ, 'T'),
        entry(WordCard.Type.НА_ЧТО, 'I'),
        entry(WordCard.Type.НАД_КЕМ, 'Y'),
        entry(WordCard.Type.НАД_ЧЕМ, 'U'),
        entry(WordCard.Type.НАРЕЧИЕ, 'O'),
        entry(WordCard.Type.ОБСТОЯТЕЛЬСТВО, 'A'),
        entry(WordCard.Type.СТЕПЕНЬ, 'P'),
        entry(WordCard.Type.ЧЕМ, 'L'),
        entry(WordCard.Type.ЧТО, 'K')

    );

    static Function<WordCard, Card> typeConvert() {
        //TODO with option
        return wordCard -> new Card(typeMap.get(wordCard.getType()));
    }

    static Function<WordCard, Card> typeAndToneConvert() {
        // TODO
        return wordCard -> new Card('3');
    }
}
