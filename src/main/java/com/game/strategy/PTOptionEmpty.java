package com.game.strategy;

import java.util.List;

import com.game.model.GameContext;
import com.game.model.GreenCard;

public class PTOptionEmpty extends PTOption {

    private final boolean noCards;

    public PTOptionEmpty(PT pt, GameContext gameContext, boolean noCards) {
        super(PlayersTurnEnum2.EMPTY, gameContext, pt, 1, 3 - 2);
        this.noCards = noCards;
    }

    @Override
    boolean noCards() {
        return noCards;
    }

    @Override
    PTOption findTheBestOption() {
        return this;
    }

    @Override
    PTOption calcTheBestOption() {
        return this;
    }

    @Override
    String printTheBestOption(boolean simple) {
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }

    @Override
    protected void playCards() {

    }

    @Override
    protected String playCardsInfo() {
        return "empty";
    }

    @Override
    protected List<String> playOneGreenCard(GreenCard card1, int playerId) {
        throw new UnsupportedOperationException("не должно быть , это же воскресенье!");
    }

    @Override
    protected void playSpecificBothGreenCards(GreenCard card1, GreenCard card2) {
        throw new UnsupportedOperationException("не должно быть , это же воскресенье!");
    }
}
