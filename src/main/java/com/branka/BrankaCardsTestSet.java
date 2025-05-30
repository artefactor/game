package com.branka;

import static com.branka.WordCard.Addition.add;
import static com.branka.WordCard.MultiAddition.addMulti;
import static java.util.List.of;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.branka.WordCard.Tone;
import com.branka.WordCard.Type;


public class BrankaCardsTestSet {

    public static List<WordCard> findById(Integer... id) {
        var c = Arrays.stream(id).map(s -> cardsSet.stream()
            .filter(card -> Objects.equals(card.getId(), s))
            .findFirst().get()).collect(Collectors.toCollection(LinkedList::new));
        return c;
    }

    public static List<WordCard> cardsSet = of(
        new WordCard(1, 'A','A',Tone.RED, Type.ГЛАГОЛ.name(), 1, "name", of(
            add(Type.НАРЕЧИЕ),
            add(Type.КОГО, Type.ЧТО),
            add(Type.ЧЕМ)
        ), addMulti(Type.ОБСТОЯТЕЛЬСТВО)
        ),
        new WordCard(1,'A','B', Tone.RED, Type.ГЛАГОЛ.name(), 1, "name", of(
            add(Type.НАРЕЧИЕ),
            add(Type.НАД_КЕМ, Type.НАД_ЧЕМ),
            add(Type.ЧЕМ)
        ), addMulti(Type.ОБСТОЯТЕЛЬСТВО)
        ),
        new WordCard(1,'A','C', Tone.RED, Type.ГЛАГОЛ.name(), 1, "name", of(
            add(Type.НАРЕЧИЕ),
            add(Type.КОМУ, Type.КАКОМУ),
            add(Type.ЧЕМ)
        ), addMulti(Type.ОБСТОЯТЕЛЬСТВО)

        )
    );


}
