package com.game.strategy;

import java.util.Optional;

import com.game.model.BlueGreenCardWithLocationOption;
import com.game.model.GameContext;
import com.game.model.Players;
import com.game.model.Util;

class SelfPartner1 extends TwoCards {

    Optional<BlueGreenCardWithLocationOption> currentPlayerToSelfCard = Optional.empty();
    Optional<BlueGreenCardWithLocationOption> currentPlayerToPartnerCard = Optional.empty();
    public SelfPartner1(PT pt, GameContext gameContext, int playerId) {
        super(PlayersTurnEnum2.SELF_PARTNER_2, gameContext, pt, playerId);
    }

    @Override
    Optional<BlueGreenCardWithLocationOption> getFirstCard(int pId) {
        return playerId == pId ? currentPlayerToSelfCard : currentPlayerToPartnerCard;
    }

    @Override
    Optional<BlueGreenCardWithLocationOption> getSecondCard(int pId) {
        return playerId == pId ? currentPlayerToPartnerCard : currentPlayerToSelfCard;
    }


    @Override
    PTOption findTheBestOption() {
        int minMoneyPlayerId = gameContext.getMinMoneyPlayer();
        Players copy = gameContext.getPlayers().copy();

        currentPlayerToSelfCard = findSuitableCardInHand(gameContext, playerId, null, copy, false);
        currentPlayerToPartnerCard = findSuitableCardInHandForPartner(gameContext, playerId, currentPlayerToSelfCard.orElse(null), copy, false);

        if (noCards()) {
            currentPlayerToPartnerCard = findSuitableCardInHandForPartner(gameContext, playerId, null, copy, false);
            currentPlayerToSelfCard = findSuitableCardInHand(gameContext, playerId, currentPlayerToPartnerCard.orElse(null),
                copy, false);
        }
        if (noCards()){
            currentPlayerToSelfCard = findSuitableCardInHand(gameContext, playerId, null, copy, true);
            currentPlayerToPartnerCard = findSuitableCardInHandForPartner(gameContext, playerId, currentPlayerToSelfCard.orElse(null), copy, false);
        }
        if (noCards()) {
            currentPlayerToPartnerCard = findSuitableCardInHandForPartner(gameContext, playerId, null, copy, true);
            currentPlayerToSelfCard = findSuitableCardInHand(gameContext, playerId, currentPlayerToPartnerCard.orElse(null),
                copy, false);
        }
        return this;
    }

    @Override
    PTOption calcTheBestOption() {
        Players copy = gameContext.getPlayers().copy();

        currentPlayerToSelfCard.ifPresent(card -> card.affectPlayer(gameContext,
            playerId, playerId, copy));
        currentPlayerToPartnerCard.ifPresent(card -> card.affectPlayer(gameContext,
            playerId, partnerId, copy));

        totalProfit = new Profit(copy.getPlayer(playerId), copy.getPlayer(partnerId));
        return this;
    }

    @Override
    String printTheBestOption(boolean simple) {
        StringBuilder sb = new StringBuilder();
        currentPlayerToSelfCard
            .ifPresent(p -> sb.append("P" + playerId + "->self" + ": ").append(simple ? Util.shortInfo(p.getCardBase()) : p).append("\n"));
        currentPlayerToPartnerCard
            .ifPresent(p -> sb.append("P" + playerId + "->partner" + ": ").append(simple ? Util.shortInfo(p.getCardBase()) : p).append("\n"));
        return sb.toString();
    }

    @Override
    protected void playCards() {
        playCard(gameContext, currentPlayerToSelfCard, gameContext.getPlayerHand(playerId),
            gameContext.getPlayer(playerId),
            gameContext.getPlayer(playerId));
        playCard(gameContext, currentPlayerToPartnerCard, gameContext.getPlayerHand(playerId),
            gameContext.getPlayer(playerId),
            gameContext.getPlayerPartner(playerId));
    }

    @Override
    protected String playCardsInfo() {
        return (currentPlayerToSelfCard.get().getCardBase().getId() + " for " +
            playerId)
            +"\t"+
        (currentPlayerToPartnerCard.get().getCardBase().getId() + " for " +
            partnerId);
    }



}
