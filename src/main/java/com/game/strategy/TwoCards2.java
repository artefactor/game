//package com.game.strategy;
//
//import java.util.List;
//import java.util.Optional;
//
//import com.game.model.BlueGreenCard;
//import com.game.model.BlueGreenCardWithLocationOption;
//import com.game.model.GameContext;
//import com.game.model.GreenCard;
//import com.game.model.Participants;
//
//abstract class TwoCards extends PTOption {
//
//    public TwoCards(PlayersTurnEnum2 value, GameContext gameContext, PT pt, int playerId) {
//        super(value, gameContext, pt, playerId, 3 - playerId);
//    }
//
//    abstract Optional<BlueGreenCardWithLocationOption> getFirstCard(int pld);
//
//    abstract Optional<BlueGreenCardWithLocationOption> getSecondCard(int pId);
//
//    @Override
//    final boolean noCards() {
//        return getFirstCard(playerId).isEmpty() || getSecondCard(playerId).isEmpty();
//    }
//
//    @Override
//    protected String playOneGreenCard(GreenCard card, int playerId) {
//        Optional<BlueGreenCard> greenCard = Optional.of(card);
//        Participants participants = card.getParticipants();
//        boolean refusalImpossible = card.is_REFUSAL_IMPOSSIBLE();
//        switch (participants) {
//            case SELF:
//                card.affectPlayerRefusal(gameContext, totalProfit, playerId);
//                // карту можно на выбор: сравнить, что лучше
//                PTOption winner;
//                boolean canBePlayed = card.canBePlayedBy(gameContext, playerId, playerId, gameContext.getPlayers().copy(), false);
//                if (canBePlayed) {
//                    List<PTOption> options =
//                        compareOptions(greenCard.map(BlueGreenCardWithLocationOption::new), getSecondCard(playerId),
//                            refusalImpossible, playerId);
//                    winner = gameContext.profitComparator().findWinner(options);
//                } else {
//                    winner = this;
//                }
//                winner.playCards();
//                return "";
//            case PARTNER:
//                card.affectPlayerRefusal(gameContext, totalProfit, playerId);
//                // карту можно на выбор: сравнить, что лучше
//                var options =
//                    compareOptions(getFirstCard(playerId), greenCard.map(BlueGreenCardWithLocationOption::new),
//                        refusalImpossible, playerId);
//                winner = gameContext.profitComparator().findWinner(options);
//                winner.playCards();
//                return "";
//            case SELF_OR_BOTH:
//                // если специальная карта, то все таки как селф
//                // карту можно на выбор: сравнить, что лучше:
//                // 1 оригинал  или 2 карта на себя - playCards
//                // 3 - карта на двоих - playBothCards();
//                card.affectPlayerRefusal(gameContext, totalProfit, playerId);
//                options = compareOptionsBoth(card, getSecondCard(playerId), refusalImpossible, playerId);
//                gameContext.profitComparator().findWinner(options);
//                return "";
//            case ANY_PERSON_OR_BOTH: // скидки
//                if (!card.getName().equalsIgnoreCase("Скидки")) {
//                    throw new IllegalArgumentException("Это не скидки!");
//                }// скидки 50%, но это можно кастомизировать
//                //  сравнить, что лучше, с учетом скидок. Полностью пересчитать все.
//                this.pt.recalculateOptionsWithDiscount().playCards();
//                gameContext.setDiscount50(false);
//                return "";
//            default:
//                throw new IllegalArgumentException("IS NOT HANDLED green "
//                    + this.playerId
//                    + " type! " + participants);
//        }
//    }
//
//
//
//    @Override
//    protected void playSpecificBothGreenCards(GreenCard card1, GreenCard card2) {
//        // todo 1
//        /**
//         я не буду реализоваывать все кейсы - это сложно. Вместо этого я сделаю такие комбинации в шафлах, чтобы таких карт не было
//         или выберу 1-2 комбинации и их здесь обыграю. Если будет не совпадать - выкину эксепшен
//         - todo 1: нужно добавить проверку на шаффл, чтобы не было комбинаций
//         - todo 2: добавить сюда 2-3 комбинации. и эксепшен на если не та
//         - todo 3: каким-то образом эти комбинации сюда сделать возможными
//         */
//        // вариант 1: карту нельзя отменить - играет ее, а второй - свою
//        // вариант 2: карту можно на выбор: сравнить, что лучше
//    }
//}
