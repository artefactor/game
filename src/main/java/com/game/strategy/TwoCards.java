package com.game.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.game.model.BlueGreenCard;
import com.game.model.BlueGreenCardWithLocationOption;
import com.game.model.GameContext;
import com.game.model.GreenCard;
import com.game.model.Participants;

abstract class TwoCards extends PTOption {

    public TwoCards(PlayersTurnEnum2 value, GameContext gameContext, PT pt, int playerId) {
        super(value, gameContext, pt, playerId, 3 - playerId);
    }

    abstract Optional<BlueGreenCardWithLocationOption> getFirstCard(int pld);

    abstract Optional<BlueGreenCardWithLocationOption> getSecondCard(int pId);

    @Override
    final boolean noCards() {
        return getFirstCard(playerId).isEmpty() || getSecondCard(playerId).isEmpty();
    }

    @Override
    protected List<String> playOneGreenCard(GreenCard card, int playerId) {
        List<String> info = new ArrayList<>();
        Optional<BlueGreenCard> greenCard = Optional.of(card);
        Participants participants = card.getParticipants();
        boolean refusalImpossible = card.is_REFUSAL_IMPOSSIBLE();
        int affectedPlayer = playerId;
        boolean self = participants == Participants.SELF || participants == Participants.SELF_OR_BOTH;
        boolean partner = participants == Participants.PARTNER;

        if (participants == Participants.SELF) {
            card.affectPlayerRefusal(gameContext, totalProfit, playerId);
            // карту можно на выбор: сравнить, что лучше
            boolean canBePlayed =
                card.canBePlayedBy(gameContext, playerId, playerId, gameContext.getPlayers().copy(), false);
            if (!canBePlayed) {
                this.playCards();
                info.add(this.playCardsInfo());
                return info;
            } else {
                var options =
                    compareOptions(greenCard.map(BlueGreenCardWithLocationOption::new), getSecondCard(playerId),
                        refusalImpossible, playerId);
                PTOption winner = gameContext.profitComparator().findWinner(options);
                winner.playCards();
                options.forEach(op-> info.add(op.playCardsInfo()));
                return info;
            }
        } else if (participants == Participants.SELF_OR_BOTH) {// если специальная карта, то все таки как селф
            // карту можно на выбор: сравнить, что лучше:
            // 1 оригинал  или 2 карта на себя - playCards
            // 3 - карта на двоих - playBothCards();
            card.affectPlayerRefusal(gameContext, totalProfit, playerId);
            var options = compareOptionsBoth(card, getSecondCard(playerId), refusalImpossible, playerId);
            PTOption winner = gameContext.profitComparator().findWinner(options);
            winner.playCards();
            options.forEach(op-> info.add(op.playCardsInfo()));
            return info;
        } else if (participants == Participants.PARTNER) {
            card.affectPlayerRefusal(gameContext, totalProfit, playerId);
            // карту можно на выбор: сравнить, что лучше
            var options =
                compareOptions(getFirstCard(playerId), greenCard.map(BlueGreenCardWithLocationOption::new),
                    refusalImpossible, playerId);
            PTOption winner = gameContext.profitComparator().findWinner(options);
            winner.playCards();
            options.forEach(op-> info.add(op.playCardsInfo()));
            return info;
        } else if (participants == Participants.ANY_PERSON_OR_BOTH) { // скидки
            if (!card.getName().equalsIgnoreCase("Скидки")) {
                throw new IllegalArgumentException("Это не скидки!");
            }// скидки 50%, но это можно кастомизировать
            //  сравнить, что лучше, с учетом скидок. Полностью пересчитать все.
            PTOption ptOption1 = this.pt.recalculateOptionsWithDiscount();
            ptOption1.playCards();
            gameContext.setDiscount50(false);
            info.add(ptOption1.playCardsInfo());
            return info;
        }
        throw new IllegalArgumentException("IS NOT HANDLED green " + this.playerId + " type! " + participants);
    }



    @Override
    protected void playSpecificBothGreenCards(GreenCard card1, GreenCard card2) {
        // todo 1
        /**
         я не буду реализоваывать все кейсы - это сложно. Вместо этого я сделаю такие комбинации в шафлах, чтобы таких карт не было
         или выберу 1-2 комбинации и их здесь обыграю. Если будет не совпадать - выкину эксепшен
         - todo 1: нужно добавить проверку на шаффл, чтобы не было комбинаций
         - todo 2: добавить сюда 2-3 комбинации. и эксепшен на если не та
         - todo 3: каким-то образом эти комбинации сюда сделать возможными
         */
        // вариант 1: карту нельзя отменить - играет ее, а второй - свою
        // вариант 2: карту можно на выбор: сравнить, что лучше
    }
}
