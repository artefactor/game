package com.game.strategy;

import java.util.Optional;

import com.game.model.BlueGreenCardWithLocationOption;
import com.game.model.GameContext;
import com.game.model.Players;
import com.game.model.Util;

class SelfSelf2 extends TwoCards {

    private Optional<BlueGreenCardWithLocationOption> currentPlayer1ToSelfCard = Optional.empty();
    private Optional<BlueGreenCardWithLocationOption> currentPlayer2ToSelfCard = Optional.empty();

    public SelfSelf2(PT pt, GameContext gameContext, int playerId) {
        super(PlayersTurnEnum2.SELF_SELF_2, gameContext, pt, playerId);
    }

    @Override
    Optional<BlueGreenCardWithLocationOption> getFirstCard(int pid) {
        return pid == 1 ? currentPlayer1ToSelfCard : currentPlayer2ToSelfCard;
    }

    @Override
    Optional<BlueGreenCardWithLocationOption> getSecondCard(int pid) {
        return pid == 1 ? currentPlayer2ToSelfCard : currentPlayer1ToSelfCard;
    }

    public void setCurrentPlayer1ToSelfCard(
        Optional<BlueGreenCardWithLocationOption> currentPlayer1ToSelfCard) {
//        if (playerId == 1) {
            this.currentPlayer1ToSelfCard = currentPlayer1ToSelfCard;
//        } else {
//            this.currentPlayer2ToSelfCard = currentPlayer1ToSelfCard;
//        }
    }

    public void setCurrentPlayer2ToSelfCard(
        Optional<BlueGreenCardWithLocationOption> currentPlayer2ToSelfCard) {
//        if (playerId == 1){
            this.currentPlayer2ToSelfCard = currentPlayer2ToSelfCard;
//        }else{
//            this.currentPlayer1ToSelfCard = currentPlayer2ToSelfCard;
//        }
    }

//    @Override
//    public Stream<PTOption> getSubOptions() {
//        return super.getSubOptions();
//    }

    // TODO 3: нужно будет сделать возможность играть 2 тренажерки как одну - типа наследоваться от этого или как..
    @Override
    PTOption findTheBestOption() {
        // min money
        int minMoneyPlayerId = gameContext.getMinMoneyPlayer();
        Players copy = gameContext.getPlayers().copy();
        if (minMoneyPlayerId == playerId) {
            setCurrentPlayer1ToSelfCard(findSuitableCardInHand(gameContext, playerId, null, copy, false));
            if (currentPlayer1ToSelfCard.isEmpty()) {
                // попробуем отдолжить
                setCurrentPlayer1ToSelfCard(findSuitableCardInHand(gameContext, playerId, null, copy, true));
            }
            setCurrentPlayer2ToSelfCard(findSuitableCardInHand(gameContext, partnerId, null, copy, false));
        } else {
            setCurrentPlayer2ToSelfCard(findSuitableCardInHand(gameContext, partnerId, null, copy, false));
            if (currentPlayer2ToSelfCard.isEmpty()) {
                // попробуем отдолжить
                setCurrentPlayer2ToSelfCard(findSuitableCardInHand(gameContext, partnerId, null, copy, true));
            }
            setCurrentPlayer1ToSelfCard(findSuitableCardInHand(gameContext, playerId, null, copy, false));
        }
        return this;
    }

    @Override
    PTOption calcTheBestOption() {
        Players copy = gameContext.getPlayers().copy();

        currentPlayer1ToSelfCard.ifPresent(card -> card.affectPlayer(gameContext,
            playerId, playerId, copy));
        currentPlayer2ToSelfCard.ifPresent(card -> card.affectPlayer(gameContext,
            partnerId, partnerId, copy));
        totalProfit = new Profit(copy.getPlayer(playerId), copy.getPlayer(partnerId));
        return this;
    }

    @Override
    String printTheBestOption(boolean simple) {
        StringBuilder sb = new StringBuilder();
        currentPlayer1ToSelfCard
            .ifPresent(p -> sb.append("P1->self: ").append(simple ? Util.shortInfo(p.getCardBase()) : p).append("\n"));
        currentPlayer2ToSelfCard
            .ifPresent(p -> sb.append("P2->self: ").append(simple ? Util.shortInfo(p.getCardBase()) : p).append("\n"));
        return sb.toString();
    }

    @Override
    protected void playCards() {
        playCards(currentPlayer1ToSelfCard, currentPlayer2ToSelfCard);
    }

    @Override
    protected String playCardsInfo() {
        return (currentPlayer1ToSelfCard.get().getCardBase().getId() + " for " +
            playerId)+ "\t"+
        (currentPlayer2ToSelfCard.get().getCardBase().getId() + " for " +
            partnerId);
    }


}
