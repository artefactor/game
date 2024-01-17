package com.game.strategy;

import static com.game.strategy.PlayersTurnEnum2.SELF_PARTNER_2_SUNDAY;

import java.util.List;
import java.util.Optional;

import com.game.model.BlueGreenCardWithLocationOption;
import com.game.model.GameContext;
import com.game.model.GreenCard;
import com.game.model.Players;
import com.game.model.Util;

public class OneSundayCardPartner extends PTOption {

    Optional<BlueGreenCardWithLocationOption> currentPlayerToPartnerCard = Optional.empty();

    public OneSundayCardPartner(PT pt, GameContext gameContext, int playerId) {
        super(SELF_PARTNER_2_SUNDAY, gameContext, pt, playerId, 3 - playerId);
    }

    @Override
    boolean noCards() {
        return currentPlayerToPartnerCard.isEmpty();
    }

    @Override
    PTOption findTheBestOption() {
        Players copy = gameContext.getPlayers().copy();
        currentPlayerToPartnerCard = findSuitableCardInHandForPartner(gameContext, playerId, null, copy, false);
        if (currentPlayerToPartnerCard.isEmpty()) {
            currentPlayerToPartnerCard =
                findSuitableCardInHandForPartner(gameContext, playerId, null, copy, true);
        }
        return this;
    }

    @Override
    PTOption calcTheBestOption() {
        Players copy = gameContext.getPlayers().copy();
        currentPlayerToPartnerCard.ifPresent(card -> card.affectPlayer(gameContext,
            playerId, partnerId, gameContext.getPlayers().copy()));
        totalProfit = new Profit(copy.getPlayer(playerId), copy.getPlayer(partnerId));
        return this;
    }

    @Override
    String printTheBestOption(boolean simple) {
        StringBuilder sb = new StringBuilder();
        currentPlayerToPartnerCard
            .ifPresent(
                p -> sb.append("P" + playerId + "->partner" + ": ").append(simple ? Util.shortInfo(p.getCardBase()) : p)
                    .append("\n"));
        return sb.toString();
    }

    @Override
    protected void playCards() {
        playCard(gameContext, currentPlayerToPartnerCard,
            gameContext.getPlayerHand(playerId),
            gameContext.getPlayer(playerId),
            gameContext.getPlayer(partnerId));
    }

    @Override
    protected String playCardsInfo() {
        return currentPlayerToPartnerCard.get().getCardBase().getId() + " for " +
            partnerId;
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
