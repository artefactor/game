package com.game.strategy;

import java.util.Optional;

import com.game.model.BlueCard;
import com.game.model.BlueGreenCard;
import com.game.model.BlueGreenCardWithLocationOption;
import com.game.model.CardSet;
import com.game.model.GameContext;
import com.game.model.Players;
import com.game.model.Restriction;
import com.game.model.Util;

/**
 * по идее это только для скандалов и для дущевных разговоров
 */
public class Self_Self_as_Both_2 extends TwoCards {

    Optional<BlueGreenCardWithLocationOption> currentPlayer1ToBothCard = Optional.empty();
    Optional<BlueGreenCardWithLocationOption> currentPlayer2ToBothCard = Optional.empty();

    public Self_Self_as_Both_2(PT pt, GameContext gameContext, int playerId) {
        super(PlayersTurnEnum2.SELF_SELF_AS_BOTH_2, gameContext, pt, playerId);
    }

    @Override
    Optional<BlueGreenCardWithLocationOption> getFirstCard(int pid) {
        return pid == 1 ? currentPlayer1ToBothCard : currentPlayer2ToBothCard;
    }

    @Override
    Optional<BlueGreenCardWithLocationOption> getSecondCard(int pid) {
        return pid == 1 ? currentPlayer2ToBothCard : currentPlayer1ToBothCard;
    }

    @Override
    PTOption findTheBestOption() {
        // по сути тут просто. сделаю так: только душевный разговор и скандал.
        // если есть оба душевных разгвора - верну душевный разговор
        // если есть оба скандала - верну скандал
        // какую предпочтительней искать - это уже потом, на стратегию
        boolean found = findSimilarInBothHands(Restriction.MAY_BE_UNITED);
        if (!found) {
            findSimilarInBothHands(Restriction.SCANDAL);
        }
        return this;
    }

    private boolean findSimilarInBothHands(Restriction restriction) {
        CardSet<BlueGreenCard> player1Hand = gameContext.getPlayerHand(playerId);
        CardSet<BlueGreenCard> player2Hand = gameContext.getPlayerHand(partnerId);
        Players copy = gameContext.getPlayers().copy();

        Optional<BlueGreenCardWithLocationOption> card1 = player1Hand.cards.stream()
            .filter(card -> card instanceof BlueCard)
            .map(card -> (BlueCard) card)
            .filter(card -> restriction.equals(card.getRestriction()))
            .map(BlueGreenCardWithLocationOption::new)
            .min(new StrategyComparator(gameContext, playerId, playerId));
        Optional<BlueGreenCardWithLocationOption> card2 = player2Hand.cards.stream()
            .filter(card -> card instanceof BlueCard)
            .map(card -> (BlueCard) card)
            .filter(card -> restriction.equals(card.getRestriction()))
            .map(BlueGreenCardWithLocationOption::new)
            .min(new StrategyComparator(gameContext, playerId, playerId));

        if (card1.isPresent() && card2.isPresent()) {
            currentPlayer1ToBothCard = card1;
            currentPlayer2ToBothCard = card2;
            return true;
        }
        return false;
    }

    @Override
    PTOption calcTheBestOption() {
        Players copy = gameContext.getPlayers().copy();
        currentPlayer1ToBothCard.ifPresent(card -> card.affectBothPlayers(gameContext, playerId, copy));
        currentPlayer2ToBothCard.ifPresent(card -> card.affectBothPlayers(gameContext, partnerId, copy));
        // нужно этот вариант сделать предпочтительней, т.к. лучше 2 раза чем один
        totalProfit = new Profit(copy.getPlayer(playerId), copy.getPlayer(partnerId));
        return this;
    }

    @Override
    String printTheBestOption(boolean simple) {
        StringBuilder sb = new StringBuilder();
        currentPlayer1ToBothCard
            .ifPresent(p -> sb.append(" P1->self-both: ").append(simple ? Util.shortInfo(p.getCardBase()) : p).append("\n"));
        currentPlayer2ToBothCard
            .ifPresent(p -> sb.append(" P2->self-both: ").append(simple ? Util.shortInfo(p.getCardBase()) : p).append("\n"));
        return sb.toString();
    }

    @Override
    protected void playCards() {
        playCardBoth(gameContext, currentPlayer1ToBothCard, gameContext.getPlayerHand(playerId), playerId);
        playCardBoth(gameContext, currentPlayer2ToBothCard, gameContext.getPlayerHand(partnerId), partnerId);
    }

    @Override
    protected String playCardsInfo() {
        return (currentPlayer1ToBothCard.get().getCardBase().getId() + " for " +
            playerId) + "\n" +
        (currentPlayer2ToBothCard.get().getCardBase().getId() + " for " +
            partnerId);
    }

}
