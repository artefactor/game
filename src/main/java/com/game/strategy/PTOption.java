package com.game.strategy;

import static com.game.strategy.GameStrategy.STUPID;
import static java.util.function.Predicate.not;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.game.model.BlueCard;
import com.game.model.BlueGreenCard;
import com.game.model.BlueGreenCardWithLocationOption;
import com.game.model.CardSet;
import com.game.model.GameContext;
import com.game.model.GreenCard;
import com.game.model.Player;
import com.game.model.Players;
import com.game.model.Util;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract class PTOption {

    final PlayersTurnEnum2 value;
    final GameContext gameContext;
    final PT pt;
    final int playerId;
    final int partnerId;
    Profit totalProfit;

    abstract boolean noCards();

    //    public Stream<PTOption> getSubOptions() {
    //        return null;
    //    }
    abstract PTOption findTheBestOption();

    abstract PTOption calcTheBestOption();

    abstract String printTheBestOption(boolean simple);

    protected abstract void playCards();
    protected abstract String playCardsInfo();

    public void playTurn(GreenCard card1, GreenCard card2) {
        // if no events - play the plan
        // if cancelable events - check with plan
        // if profit of event is better (ex. birthday party) - play event
        if (card1 == null && card2 == null) {
            playCards();
        }
        LinkedList<GreenCard> greenCards = gameContext.getTurnGreenCards().cards;
        if (card1 != null && card2 == null) {
            greenCards.add(card1);
            // if no cancelable events - change the plan and play events/cards
            card1.revealEvent(gameContext.getPlayer(1));
            playOneGreenCard(card1, 1);
        }
        if (card1 == null && card2 != null) {
            greenCards.add(card2);
            // if no cancelable events - change the plan and play events/cards
            card2.revealEvent(gameContext.getPlayer(2));
            playOneGreenCard(card2, 2);
        }

        if (card1 != null && card2 != null) {
            card1.revealEvent(gameContext.getPlayer(1));
            card2.revealEvent(gameContext.getPlayer(2));
            greenCards.add(card1);
            greenCards.add(card2);
            // if no cancelable both events - play events;
            playBothGreenCardsMain(card1, card2);
        }
        //        if (card1 != null && greenCards.contains(card1)) {
        //            card1.affectPlayerRefusal(gameContext, gameContext.getPlayers().getPlayer1());
        //        }
        //        if (card2 != null && greenCards.contains(card2)) {
        //            card2.affectPlayerRefusal(gameContext, gameContext.getPlayers().getPlayer2());
        //        }
        if (greenCards.stream().anyMatch(GreenCard::is_REFUSAL_IMPOSSIBLE)) {
            throw new IllegalStateException(
                "логическая ошибка или не хэндлю карты. Зеленые карты должны были разыграться: " + Util.printCards(
                    greenCards));
        }
        greenCards.clear();
    }

    protected abstract List<String> playOneGreenCard(GreenCard card1, int playerId);

    final void playBothGreenCardsMain(GreenCard card1, GreenCard card2) {
        boolean cardActivePlayer = playerId == 1;
        GreenCard greenCard1 = cardActivePlayer ? card1 : card2;
        GreenCard greenCard2 = cardActivePlayer ? card2 : card1;

        if (greenCard1.is_REFUSAL_IMPOSSIBLE()
            && greenCard1.isNotIllnes()
            && greenCard2.is_REFUSAL_IMPOSSIBLE()
            && greenCard2.isNotIllnes()
        ) {
            playCards(Optional.of(new BlueGreenCardWithLocationOption(greenCard1)), Optional.of(new BlueGreenCardWithLocationOption(greenCard2)));
            return;
        }
        playSpecificBothGreenCards(greenCard1, greenCard2);
    }

    protected abstract void playSpecificBothGreenCards(GreenCard card1, GreenCard card2);

    void playCards(Optional<BlueGreenCardWithLocationOption> currentPlayer1ToSelfCard,
        Optional<BlueGreenCardWithLocationOption> currentPlayer2ToSelfCard) {
        playCard(gameContext, currentPlayer1ToSelfCard, gameContext.getPlayerHand(playerId),
            gameContext.getPlayer(playerId),
            gameContext.getPlayer(playerId));
        playCard(gameContext, currentPlayer2ToSelfCard, gameContext.getPlayerHand(partnerId),
            gameContext.getPlayer(partnerId),
            gameContext.getPlayer(partnerId));
    }

    protected void playCard(GameContext gameContext,
        Optional<BlueGreenCardWithLocationOption> currentPlayerCard, CardSet getPlayerHand, Player owner, Player actorPlayer) {
        currentPlayerCard.ifPresent(card -> {
            BlueGreenCard cardBase = card.getCardBase();
            boolean removed = getPlayerHand.cards.remove(cardBase);
            // тут может у другого игрока находиться
            if (cardBase instanceof BlueCard && !removed){
                removed = gameContext.getPlayerHand(1).cards.remove(cardBase);
                removed = gameContext.getPlayerHand(2).cards.remove(cardBase);
                // вот тут сложный случай, у кого она должна находится
            }
            //            gameContext.getCurrentDay()
            gameContext.getTurnGreenCards().cards.remove(cardBase);
            gameContext.getCardsOnTable().playCard(card, gameContext, owner.id, actorPlayer.getId());
            card.affectPlayer(gameContext, owner.id, actorPlayer.getId(), gameContext.getPlayers());
            card.playCard(gameContext, owner.id, actorPlayer.getId());
            if (cardBase.getId().equals("H_01")){
                int t = 3;
            }
        });
    }

    protected void playCardBoth(GameContext gameContext,
        Optional<BlueGreenCardWithLocationOption> currentPlayerCard, CardSet getPlayerHand, int ownerId) {
        currentPlayerCard.ifPresent(card -> {
            BlueGreenCard cardBase = card.getCardBase();
            if (cardBase.getId().equals("H_01")){
                int t = 3;
            }
            boolean b = getPlayerHand.cards.remove(cardBase);
            if (!b && cardBase instanceof BlueCard) {
                int i = 3;
            }
            boolean b2 = gameContext.getTurnGreenCards().cards.remove(cardBase);
            if (!b2 && cardBase instanceof GreenCard) {
                int i = 3;
            }
            gameContext.getCardsOnTable().playCardBoth(card, gameContext, ownerId);
            card.affectBothPlayers(gameContext, ownerId, gameContext.getPlayers());
            card.playCardBoth(gameContext, ownerId, partnerId);
        });
    }

    final int LAST_POSSIBLE_REVERSED = 1;
    final int FIRST_POSSIBLE_REVERSED = 4;
    final int CLEVER_STRATEGY = 5;
    int sw = STUPID ? LAST_POSSIBLE_REVERSED : CLEVER_STRATEGY;

    protected Optional<BlueGreenCardWithLocationOption> findSuitableCardInHand(GameContext gameContext, int playerId,
        BlueGreenCardWithLocationOption exclude, Players copy, boolean borrowFromPartner) {
        CardSet<BlueGreenCard> playerHand = gameContext.getPlayerHand(playerId);
        switch (sw) {
            case LAST_POSSIBLE_REVERSED:
                for (int i = playerHand.cards.size() - 1; i >= 0; i--) {
                    var card = playerHand.cards.get(i);
                    if (card.equals(exclude == null ? null : exclude.getCardBase())) {
                        continue;
                    }
                    if (card.canBePlayedBy(gameContext, playerId, playerId, copy, borrowFromPartner)) {
                        return Optional.of(new BlueGreenCardWithLocationOption(card, card.getLocation()));
                    }
                }
                return Optional.empty();
            case FIRST_POSSIBLE_REVERSED:
                return playerHand.cards.stream().
                    filter(card -> card.canBePlayedBy(gameContext, playerId, playerId, copy, borrowFromPartner))
                    .filter(not(card -> card.equals(exclude == null ? null : exclude.getCardBase())))
                    .flatMap(card -> card.getOptionsLocations().stream())
                    .findFirst();
            default:
                return playerHand.cards.stream().
                    filter(card -> card.canBePlayedBy(gameContext, playerId, playerId, copy, borrowFromPartner))
                    .filter(not(card -> card.equals(exclude == null ? null : exclude.getCardBase())))
                    .flatMap(card -> card.getOptionsLocations().stream())
                    .min(new StrategyComparator(gameContext, playerId, playerId));
        }
    }

    protected Optional<BlueGreenCardWithLocationOption> findSuitableCardInHandForPartner(GameContext gameContext, int ownerId,
        BlueGreenCardWithLocationOption exclude, Players copy, boolean borrowFromPartner) {
        CardSet<BlueGreenCard> playerHand = gameContext.getPlayerHand(ownerId);
        int actorId = copy.getPlayerPartner(ownerId).getId();
        switch (sw) {
            case LAST_POSSIBLE_REVERSED:
            case FIRST_POSSIBLE_REVERSED:
                return playerHand.cards.stream()
                    .filter(card -> card.canBePlayedBy(gameContext, ownerId, actorId, copy, borrowFromPartner))
                    .filter(not(card -> card.equals(exclude == null ? null : exclude.getCardBase())))
                    .flatMap(card -> card.getOptionsLocations().stream())
                    .findFirst();
            default:
                // dont use that satisfaction goes to different players
                return playerHand.cards.stream()
                    .filter(card -> card.canBePlayedBy(gameContext, ownerId, actorId, copy, borrowFromPartner))
                    .filter(card -> !card.equals(exclude == null ? null : exclude.getCardBase()))
                    .flatMap(card -> card.getOptionsLocations().stream())
                    .min(new StrategyComparator(gameContext, ownerId, actorId));
        }
    }

    protected Optional<BlueGreenCardWithLocationOption> findSuitableBothCardInHand(GameContext gameContext, int ownerId) {
        CardSet<BlueGreenCard> playerHand = gameContext.getPlayerHand(ownerId);
        Players copy = gameContext.getPlayers().copy();
        switch (sw) {
            case LAST_POSSIBLE_REVERSED:
            case FIRST_POSSIBLE_REVERSED:
                return playerHand.cards.stream().
                    filter(card -> card.canBePlayedBoth(gameContext, ownerId, copy, false))
                    .flatMap(card -> card.getOptionsLocations().stream())
                    .findFirst();
            default:
                // doesn't consider that 2 different players
                return playerHand.cards.stream().
                    filter(card -> card.canBePlayedBoth(gameContext, ownerId, copy, false))
                    .flatMap(card -> card.getOptionsLocations().stream())
                    .min(new StrategyComparator(gameContext, ownerId, ownerId));
        }
    }

    List<PTOption> compareOptions(Optional<BlueGreenCardWithLocationOption> cardForPlayer1, Optional<BlueGreenCardWithLocationOption> cardForPlayer2,
        boolean refusal_impossible, int playerId) {
        List<PTOption> options = new ArrayList<>();
        SelfSelf2 altOptionWithGreen = new SelfSelf2(this.pt, gameContext, playerId);
        altOptionWithGreen.setCurrentPlayer1ToSelfCard(cardForPlayer1);
        altOptionWithGreen.setCurrentPlayer2ToSelfCard(cardForPlayer2);
        altOptionWithGreen.calcTheBestOption();
        options.add(altOptionWithGreen);

        if (!refusal_impossible) {
            options.add(this);
        }
        return options;
    }

    List<PTOption> compareOptionsBoth(GreenCard card1, Optional<BlueGreenCardWithLocationOption> card2,
        boolean refusal_impossible, int playerId) {
        List<PTOption> options = new ArrayList<>();

        boolean canBePlayed =
            card1.canBePlayedBy(gameContext, playerId, playerId, gameContext.getPlayers().copy(playerId), false);
        // по идее еще можно отдолжить
        if (canBePlayed) {
            SelfSelf2 altOption = new SelfSelf2(this.pt, gameContext, playerId);  // TODO 0: playerId or 1
            altOption.setCurrentPlayer1ToSelfCard(Optional.of(new BlueGreenCardWithLocationOption(card1)));
            altOption.setCurrentPlayer2ToSelfCard(card2);
            altOption.calcTheBestOption();
            options.add(altOption);
        }

        if (card1.canBePlayedBoth(gameContext, playerId, gameContext.getPlayers().copy(playerId), false)) {
            Both1 alt2Option = new Both1(this.pt, gameContext, playerId);
            alt2Option.currentPlayerBothCard = Optional.of(new BlueGreenCardWithLocationOption(card1));
            alt2Option.calcTheBestOption();
            options.add(alt2Option);
        }
        if (!refusal_impossible) {
            options.add(this);
        }
        return options;
    }

    public String getCardIds() {
        return printTheBestOption(true);
    }

}
