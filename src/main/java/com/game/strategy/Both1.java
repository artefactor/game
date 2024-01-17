package com.game.strategy;

import static com.game.model.Participants.BOTH;
import static com.game.model.Participants.PARTNER;
import static com.game.model.Participants.SELF;
import static com.game.model.Participants.SELF_OR_BOTH;
import static com.game.strategy.PlayersTurnEnum2.BOTH_1;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.game.model.BlueGreenCardWithLocationOption;
import com.game.model.GameContext;
import com.game.model.GreenCard;
import com.game.model.Participants;
import com.game.model.Players;
import com.game.model.Util;

class Both1 extends PTOption {

    public Both1(PT pt, GameContext gameContext, int playerId) {
        super(BOTH_1, gameContext, pt, playerId, 3 - playerId);
    }

    Optional<BlueGreenCardWithLocationOption> currentPlayerBothCard = Optional.empty();

    @Override
    boolean noCards() {
        return
            currentPlayerBothCard.isEmpty();
    }

    @Override
    String printTheBestOption(boolean simple) {
        StringBuilder sb = new StringBuilder();

        currentPlayerBothCard
            .ifPresent(
                p -> sb.append("P" + playerId + "->both" + ": ").append(simple ? Util.shortInfo(p.getCardBase()) : p).append("\n"));
        return sb.toString();
    }

    @Override
    PTOption findTheBestOption() {
        currentPlayerBothCard = findSuitableBothCardInHand(gameContext, playerId);
        return this;
    }

    @Override
    PTOption calcTheBestOption() {
        Players copy = gameContext.getPlayers().copy();
        currentPlayerBothCard.ifPresent(card -> card.affectBothPlayers(gameContext, playerId, copy));
        totalProfit = new Profit(copy.getPlayer(playerId), copy.getPlayer(partnerId));
        return this;
    }

    @Override
    protected void playCards() {
        playCardBoth(gameContext, currentPlayerBothCard, gameContext.getPlayerHand(playerId), playerId);
    }

    @Override
    protected String playCardsInfo() {
        String x = currentPlayerBothCard.get().getCardBase().getId() + " for " +
            playerId;
        return x;
    }

    @Override
    protected List<String> playOneGreenCard(GreenCard card1, int playerId) {
        boolean cardActivePlayer = playerId == this.playerId;   //  при таком варианте 1-я карта будет всегда идти на 1-го  игрока, но играть будут правильно
        //      boolean cardActivePlayer = playerId == this.playerId;   // при таком варианте 1-я карта будет всегда идти на активного игрока, и тоже играть будут правильно
        return List.of(playActivePlayerGreenCard(card1, cardActivePlayer));
    }

    private String playActivePlayerGreenCard(GreenCard card, boolean cardActivePlayer) {
        Optional<BlueGreenCardWithLocationOption> cardGreen = Optional.of(new BlueGreenCardWithLocationOption(card));
        Participants participants = card.getParticipants();
        String returnMessage;
        if (cardActivePlayer) {
            returnMessage = "active" + playerId;
        } else {
            returnMessage = "partner" + partnerId;
        }

        switch (participants) {
            case SELF:
            case PARTNER:
            case SELF_OR_BOTH:
                break;
            case ANY_PERSON_OR_BOTH: // скидки
                if (!card.getName().equalsIgnoreCase("Скидки")) {
                    throw new IllegalArgumentException("Это не скидки!");
                }// скидки 50%, но это можно кастомизировать
                //  сравнить, что лучше, с учетом скидок. Полностью пересчитать все.
                this.pt.recalculateOptionsWithDiscount().playCards();
                gameContext.setDiscount50(false);
                return returnMessage;
            default:
                throw new IllegalArgumentException("IS NOT HANDLED green type! " + participants);
        }

        boolean activeSelfBoth = SELF_OR_BOTH.equals(participants) && cardActivePlayer;
        boolean activeSelf = SELF.equals(participants) && cardActivePlayer;
        boolean activePartner = PARTNER.equals(participants) && cardActivePlayer;

        boolean partnerSelf = SELF.equals(participants) && !cardActivePlayer;
        boolean partnerSelfBoth = SELF_OR_BOTH.equals(participants) && !cardActivePlayer;
        boolean partnerPartner = PARTNER.equals(participants) && !cardActivePlayer;

        boolean activeIsActor = activeSelf || activeSelfBoth || partnerPartner;
        boolean partnerIsActor = partnerSelf || partnerSelfBoth || activePartner;
        int actorId = activeIsActor ? playerId : partnerId;
        if (activeIsActor == partnerIsActor){
            throw new IllegalStateException("Невозможная логика");
        }

        boolean refusal_impossible = card.is_REFUSAL_IMPOSSIBLE();
        List<PTOption> options = new ArrayList<>();
        /* варианты:
                 1) если зеленая отменяемая то также
                - опции: текущая (оба)
        */
        if (!refusal_impossible) {
            card.affectPlayerRefusal(gameContext, totalProfit, actorId);
            options.add(this);
        }
        /*       2) если зеленая также может играться на двоих, то
               - зеленая на двоих
        */
        if (SELF_OR_BOTH.equals(participants)) {
            // нужно понять на кого она выпала но еще
            /// конечно если она вообще может играться
            boolean canBePlayed = card.canBePlayedBoth(gameContext, playerId,  gameContext.getPlayers().copy(), false);
            if (canBePlayed) {
                Both1 alt2Option = new Both1(this.pt, gameContext, actorId);
                alt2Option.currentPlayerBothCard = cardGreen;
                alt2Option.calcTheBestOption();
                options.add(alt2Option);
            }
        }
        /*
         3) если зеленая не отменяемая и текущая играется только на 2 (интим например),

           тот, кому выпало - играет ее, а другому нужно поменять карту
              -- играет - меняет
              -    зеленая на себя - а на партнера - поискать  - сам ищет
              -    зеленая на себя - а на партнера - поискать - ему поискать
*/
        Players copy = gameContext.getPlayers().copy();
        // -- карту играет тот на кого она, другой - меняет
        if (activeIsActor) {
            addOptionsWithThisCardAndAnotherCard(playerId, cardGreen, partnerId, copy, options);
        }
        if (partnerIsActor) {
            addOptionsWithThisCardAndAnotherCard(partnerId, cardGreen, playerId, copy, options);
        }
        /*
        4) если зеленая не отменяемая и текущая может играться на одного (себя или any), то
             -- играет - эту же текущую
              -  зеленая на себя , а партнер - на эту карту (текущую)
         */
        if (!currentPlayerBothCard.get().getCardBase().getParticipants().equals(BOTH)) {
            // то второй может ее использовать - такое правило сейчас
            boolean canBePlayed = card.canBePlayedBy(gameContext, actorId, actorId, gameContext.getPlayers().copy(), false);
            if (!canBePlayed){
                throw new IllegalStateException("что-то я просчитал с логикой. Если они вдвоем уже молги сыграть эту карту, значит он и один может. Хотя - там нюанс, может у партнера понадобятся денег отдолжить" );
            }
            // это не SelfPartner, потому что деньги уже платит партнер и удовлетворение получает он же. Но карта уже не в его руке.
            SelfSelf2 altOptionReuseCard = new SelfSelf2(pt, gameContext, actorId);
            altOptionReuseCard.setCurrentPlayer1ToSelfCard(cardGreen);
            altOptionReuseCard.setCurrentPlayer2ToSelfCard(this.currentPlayerBothCard);
            altOptionReuseCard.calcTheBestOption();
            options.add(altOptionReuseCard);
        }

        PTOption winner = gameContext.profitComparator().findWinner(options);
        winner.playCards();
        return returnMessage;
    }

    private void addOptionsWithThisCardAndAnotherCard(int playerId, Optional<BlueGreenCardWithLocationOption> cardGreen, int partnerId, Players copy,
        List<PTOption> options) {
        SelfSelf2 altOptionSam = new SelfSelf2(pt, gameContext, playerId);
        altOptionSam.setCurrentPlayer1ToSelfCard(cardGreen);
        altOptionSam.setCurrentPlayer2ToSelfCard(findSuitableCardInHand(gameContext, partnerId, null, copy, false));

        altOptionSam.calcTheBestOption();
        options.add(altOptionSam);

        SelfSelf2 altOption = new SelfSelf2(pt, gameContext, playerId);
        altOption.setCurrentPlayer1ToSelfCard(cardGreen);
        altOption.setCurrentPlayer2ToSelfCard(
            findSuitableCardInHandForPartner(gameContext, playerId, null, copy, false));

        altOption.calcTheBestOption();
        options.add(altOption);
    }

    @Override
    protected void playSpecificBothGreenCards(GreenCard card1, GreenCard card2) {
        // todo 1:
        // вариант 1: карту нельзя отменить - играет ее, а второй - свою
    }


}
