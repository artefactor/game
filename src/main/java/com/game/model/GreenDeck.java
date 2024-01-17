package com.game.model;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class GreenDeck {

    boolean enableDeck;

    LinkedList<GreenCard> cards;
    List<List<Integer>> eventsCubesShuffles;

    private boolean test;
    List<Integer> currentShuffle;
    int counter;
    int initSize;


    public GreenDeck(LinkedList<GreenCard> cards, List<List<Integer>> eventsCubesShuffles, boolean enableDeck,
        boolean test) {
        this.cards = cards;
        initSize = cards.size();
        this.eventsCubesShuffles = eventsCubesShuffles;
        currentShuffle = eventsCubesShuffles.get(1);
        this.enableDeck = enableDeck;
        this.test = test;
    }

    // тоже не совсем понятно, если запускать в цикле, то тогда нужно обнулять счетчик при тестах и ране
//    static List<Integer> currentShuffle1 = List.of(
//        1, 2, 3, 4, 2, 3, 1, 4, 5, 5, 5, 3, 5, 3, 6, 3, 4, 4, 3, 1, 2, 1, 1, 1, 3, 6, 5, 5, 6, 1, 2, 1, 2, 5, 4, 2, 3,
//        6, 3, 1, 6, 1, 4, 5, 6, 4, 4, 2, 3, 4, 1, 5, 6, 3, 6, 3, 6, 2, 4, 4, 4, 5, 6, 2, 1, 3, 5, 6, 2, 2, 1, 3, 1, 5,
//        6, 6, 1, 3, 1, 4, 1, 2, 2, 6, 6, 5, 1, 2, 1, 4, 4, 4, 6, 3, 5, 4, 1, 2, 2, 2
//    );
    public int counter1 = 0;

    public void resetCounters() {
        counter1 = 0;
    }

    public Integer getNextCube(String id) {
        if (test) {

        }
        return currentShuffle.get(counter1++);
    }

    public int getNext2Cubes(String id) {
        return getNextCube(id) + getNextCube(id);
    }


    public GreenCard checkOnEvent(int day) {
        if (enableDeck) {
            Integer currentCubeResult = currentShuffle.get(counter++);
            if (currentCubeResult == day) {
                GreenCard poll = cards.poll();
                //            System.out.println("GREEN CARD:" + poll);
                return poll;
            }
        }
        return null;
    }

    public int getUsedCards() {
        return initSize - cards.size();
    }
}
