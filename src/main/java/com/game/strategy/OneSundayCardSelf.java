package com.game.strategy;

import static com.game.strategy.PlayersTurnEnum2.SELF_SELF_2_SUNDAY;

import java.util.List;
import java.util.Optional;

import com.game.model.BlueGreenCardWithLocationOption;
import com.game.model.GameContext;
import com.game.model.GreenCard;
import com.game.model.Players;
import com.game.model.Util;

public class OneSundayCardSelf extends PTOption {

    Optional<BlueGreenCardWithLocationOption> currentPlayerToSelfCard = Optional.empty();

    public OneSundayCardSelf(PT pt, GameContext gameContext, int playerId) {
        super(SELF_SELF_2_SUNDAY, gameContext, pt, playerId, 3 - playerId);
    }

    @Override
    boolean noCards() {
        return currentPlayerToSelfCard.isEmpty();
    }

    @Override
    PTOption findTheBestOption() {
        Players copy = gameContext.getPlayers().copy();
        currentPlayerToSelfCard = findSuitableCardInHand(gameContext, playerId, null, copy, false);
        if (currentPlayerToSelfCard.isEmpty()) {
            // попробуем отдолжить
            currentPlayerToSelfCard = findSuitableCardInHand(gameContext, playerId, null, copy, true);
        }

        return this;
    }

    @Override
    PTOption calcTheBestOption() {
        Players copy = gameContext.getPlayers().copy();
        currentPlayerToSelfCard.ifPresent(card -> card.affectPlayer(gameContext,
            playerId, playerId, copy));
        totalProfit = new Profit(copy.getPlayer(playerId), copy.getPlayer(partnerId));
        return this;
    }

    @Override
    String printTheBestOption(boolean simple) {
        StringBuilder sb = new StringBuilder();
        currentPlayerToSelfCard
            .ifPresent(
                p -> sb.append("P" + playerId + "->self" + ": ").append(simple ? Util.shortInfo(p.getCardBase()) : p)
                    .append("\n"));
        return sb.toString();
    }

    @Override
    protected void playCards() {
        playCards(currentPlayerToSelfCard, Optional.empty());
    }

    @Override
    protected String playCardsInfo() {
        return (currentPlayerToSelfCard.get().getCardBase().getId() + " for " +
            playerId);
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
