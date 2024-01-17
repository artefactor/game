package com.game.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlueDecks {

    BlueDeck routineDeck = new BlueDeck(new LinkedList<>());
    BlueDeck progressDeck = new BlueDeck(new LinkedList<>());
    BlueDeck discardDeck = new BlueDeck(new LinkedList<>());
    BlueDeck discardedShuffledDeck = new BlueDeck(new LinkedList<>());
    BlueDeck player1DiscardDeck = new BlueDeck(new LinkedList<>());
    BlueDeck player2DiscardDeck = new BlueDeck(new LinkedList<>());

    // для каждой карты свой шаффл - в тесте, в ране обший
    Map<String, List<Integer>> blueCardsCubesShuffles;
    Map<String, AtomicInteger> counterMap;
    private boolean test;

    public void resetCounters() {
        counterMap.clear();
    }

    public Integer getNextCube(String id) {
        int counter = counterMap.getOrDefault(id, new AtomicInteger()).getAndIncrement();
        if (test) {
            // counter per card
        } else {
            // one single counter
            counter = counterMap.getOrDefault("base", new AtomicInteger()).getAndIncrement();
        }
        List<Integer> integers = blueCardsCubesShuffles.getOrDefault(id, blueCardsCubesShuffles.get("base"));
        Integer cube = integers.get(counter);
        return cube;
        //        if (id.startsWith("T")){
        //            return 3;
        //        }
        //        return 6;
    }


    @Override
    public String toString() {
        return "BlueDecks {\n" +
            "   routineDeck = " + routineDeck +
            ",\n" +
            "   progressDeck = " + progressDeck +
            ",\n" +
            "   discardedShuffledDeck = " + discardedShuffledDeck +
            ",\n" +
            "   discardDeck = " + discardDeck +
            ",\n" +
            "   player1DiscardDeck = " + player1DiscardDeck +
            ",\n" +
            "   player2DiscardDeck = " + player2DiscardDeck +
            '}';
    }

    public int getCount() {
        return
            routineDeck.cards.size() +
                progressDeck.cards.size() +
                discardDeck.cards.size() +
                discardedShuffledDeck.cards.size() +
                player1DiscardDeck.cards.size() +
                player2DiscardDeck.cards.size();

    }

    public String printExtendedCount() {
        return
            "routineDeck, [" + routineDeck.cards.size() + "]: " + routineDeck.ids() +
                "\n" + "progressDeck, [" + progressDeck.cards.size() + "]: " + progressDeck.ids() +
                "\n" + "discardDeck, [" + discardDeck.cards.size() + "]: " + discardDeck.ids() +
                "\n" + "discardedShuffledDeck, [" + discardedShuffledDeck.cards.size() + "]: "
                + discardedShuffledDeck.ids() +
                "\n" + "player1DiscardDeck, [" + player1DiscardDeck.cards.size() + "]: " + player1DiscardDeck.ids() +
                "\n" + "player2DiscardDeck, [" + player2DiscardDeck.cards.size() + "]: " + player2DiscardDeck.ids() +
                ""
            ;

    }
}
