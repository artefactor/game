package com.game.model;

import static com.game.model.Util.shortInfo;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class CardSet<T extends GameCard> {

    public LinkedList<T> cards = new LinkedList<>();

    @Override
    public String toString() {
        return Util.printCards(cards);
    }

    public String ids() {
        return cards.stream().map(c->String.valueOf(c.getId())).collect(Collectors.joining(", "));
    }

    public String idNames() {
        return cards.stream().map(c->shortInfo((BlueGreenCard) c)).collect(Collectors.joining(", "));
    }
}
