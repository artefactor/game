package com.game.strategy;

import java.util.Optional;

import com.game.model.BlueGreenCardWithLocationOption;
import com.game.model.GameContext;
import com.game.model.Players;
import com.game.model.Util;

class PartnerPartner2 extends TwoCards {

    Optional<BlueGreenCardWithLocationOption> currentPlayer1ToPartnerCard = Optional.empty();
    Optional<BlueGreenCardWithLocationOption> currentPlayer2ToPartnerCard = Optional.empty();

    public PartnerPartner2(PT pt, GameContext gameContext, int playerId) {
        super(PlayersTurnEnum2.PARTNER_PARTNER_2, gameContext, pt, playerId);
    }

    @Override
    Optional<BlueGreenCardWithLocationOption> getFirstCard(int pid) {
        return pid == 1 ? currentPlayer2ToPartnerCard : currentPlayer1ToPartnerCard;
//        return currentPlayer2ToPartnerCard;
    }

    @Override
    Optional<BlueGreenCardWithLocationOption> getSecondCard(int pid) {
        return pid == 1 ? currentPlayer1ToPartnerCard : currentPlayer2ToPartnerCard;
//        return currentPlayer1ToPartnerCard;
    }

    @Override
    PTOption findTheBestOption() {
        // min money
        int minMoneyPlayerId = gameContext.getMinMoneyPlayer();
        Players copy = gameContext.getPlayers().copy();

        if (minMoneyPlayerId == playerId) {
            currentPlayer2ToPartnerCard = findSuitableCardInHandForPartner(gameContext, partnerId, null, copy, false);
            if (currentPlayer2ToPartnerCard.isEmpty()) {
                currentPlayer2ToPartnerCard =
                    findSuitableCardInHandForPartner(gameContext, partnerId, null, copy, true);
            }

            currentPlayer1ToPartnerCard = findSuitableCardInHandForPartner(gameContext, playerId, null, copy, false);
        } else {
            currentPlayer1ToPartnerCard = findSuitableCardInHandForPartner(gameContext, playerId, null, copy, false);
            if (currentPlayer1ToPartnerCard.isEmpty()) {
                currentPlayer1ToPartnerCard = findSuitableCardInHandForPartner(gameContext, playerId, null, copy, true);
            }
            currentPlayer2ToPartnerCard = findSuitableCardInHandForPartner(gameContext, partnerId, null, copy, false);
        }

        return this;
    }

    @Override
    PTOption calcTheBestOption() {
        Players copy = gameContext.getPlayers().copy();
        currentPlayer1ToPartnerCard.ifPresent(card -> card.affectPlayer(gameContext,
            playerId, partnerId, gameContext.getPlayers().copy()));
        currentPlayer2ToPartnerCard.ifPresent(card -> card.affectPlayer(gameContext,
            partnerId, playerId, gameContext.getPlayers().copy()));
        totalProfit = new Profit(copy.getPlayer(playerId), copy.getPlayer(partnerId));
        return this;

    }

    @Override
    String printTheBestOption(boolean simple) {
        StringBuilder sb = new StringBuilder();
        currentPlayer1ToPartnerCard
            .ifPresent(p -> sb.append("P1->P2: ").append(simple ? Util.shortInfo(p.getCardBase()) : p).append("\n"));
        currentPlayer2ToPartnerCard
            .ifPresent(p -> sb.append("P2->P1: ").append(simple ? Util.shortInfo(p.getCardBase()) : p).append("\n"));
        return sb.toString();
    }

    @Override
    protected void playCards() {
        playCard(gameContext, currentPlayer1ToPartnerCard, gameContext.getPlayerHand(playerId), gameContext.getPlayer(playerId),
            gameContext.getPlayer(partnerId));
        playCard(gameContext, currentPlayer2ToPartnerCard, gameContext.getPlayerHand(partnerId), gameContext.getPlayer(partnerId),
            gameContext.getPlayer(playerId));
    }

    @Override
    protected String playCardsInfo() {
        return (currentPlayer1ToPartnerCard.get().getCardBase().getId() + " for " +
            partnerId)
            + "\t"+
        (currentPlayer2ToPartnerCard.get().getCardBase().getId() + " for " +
            playerId);
    }


}
