package com.game.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BlueDeck {

    final LinkedList<BlueCard> cards;

    @Override
    public String toString() {
        return Util.printCards(cards);
    }

    public String ids() {
        return cards.stream().map(c->String.valueOf(c.getId())).collect(Collectors.joining(", "));
    }
}
