package com.game.model;

import static java.util.function.Predicate.not;

import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class RedCard extends GameCard {

    public enum Conditions {
        none,
        /**
         * если разница удовлетворений > 10
         */
        diff_satisfaction
    }

    @JsonIgnore
    boolean isActive;

    public void initRound(GameContext gameContext) {
        calcIsActive(gameContext);
        LinkedList<OrangeCard> cards1 = gameContext.getPlayers().getPlayer(1).getOrangeCards().cards;
        LinkedList<OrangeCard> cards2 = gameContext.getPlayers().getPlayer(2).getOrangeCards().cards;

        if ("1".equals(id)) {
            /*
             * (желтая и оранжевая) - неактивны
             */
            cards1.forEach(c -> c.setActive(false));
            cards2.forEach(c -> c.setActive(false));
        }
        if ("2".equals(id)) {
            /*
             * ОБА:
             * Вскрываете 2 карты черты (желтая и оранжевая)
             */
            cards1.stream()
                .filter(OrangeCard::isOrange)
                .findFirst().ifPresent(c -> c.setActive(true));
            cards2.stream()
                .filter(OrangeCard::isOrange)
                .findFirst().ifPresent(c -> c.setActive(true));

            cards1.stream()
                .filter(not(OrangeCard::isOrange))
                .findFirst().ifPresent(c -> c.setActive(true));
            cards2.stream()
                .filter(not(OrangeCard::isOrange))
                .findFirst().ifPresent(c -> c.setActive(true));

        }
        if ("3b".equals(id)) {
            /* ОБА:
             * Вскрываете оранжевую карту черты.
             */
            cards1.stream()
                .filter(OrangeCard::isOrange)
                .findFirst().ifPresent(c -> c.setActive(true));
            cards2.stream()
                .filter(OrangeCard::isOrange)
                .findFirst().ifPresent(c -> c.setActive(true));
        }

        if ("4".equals(id)) {
            // TODO 1:
            /**
             * Теперь когда ОБА играете карты с одним названием,
             * то (по желанию) могжете трактовать это как совместное занятие.
             */
        }
        if ("5a".equals(id)) {
            // TODO 1:
            /**
             * Тот, у кого БОЛЬШЕ удовлетворения
             * За каждый вечер без партнера получаете +3 удовлетворения
             * (распространяется на синие и зеленые карты)
             */
        }
        if ("7".equals(id)) {
            /**
             * ОБА:
             * Выбираете одну черту по своему выбору и переворачиваете рубашкой вверх на текущий раунд (не активно только 1 раунд).
             */

            int theWorstWorstOrangeCard1 = gameContext.getStrategy().findTheWorstWorstOrangeCard(cards1);
            int theWorstWorstOrangeCard2 = gameContext.getStrategy().findTheWorstWorstOrangeCard(cards2);

            cards1.stream()
                .filter(card -> card.getId() == theWorstWorstOrangeCard1)
                .findFirst().ifPresent(c -> c.setActive(false, 1));
            cards2.stream()
                .filter(card -> card.getId() == theWorstWorstOrangeCard2)
                .findFirst().ifPresent(c -> c.setActive(false, 1));
        }
    }

    private void calcIsActive(GameContext context) {
        switch (conditions) {
            case diff_satisfaction:
                isActive = Math.abs(
                    context.getPlayers().getPlayer1().getSatisfaction() -
                        context.getPlayers().getPlayer2().getSatisfaction()) > 10;
                break;
            default:
                isActive = true;
        }
    }

    public void finishRound(GameContext gameContext) {
        //TODO
        if (isActive) {
            if (getRound() == 1) {
                /*
                ОБА:
                    За каждый вечер не "совместно" получаете 1 напряг.
                * */
                var separatedEvenings = gameContext.getCardsOnTable().getSeparatedEveningsCount();
                gameContext.getPlayers().getPlayer(1).addTension(separatedEvenings);
                gameContext.getPlayers().getPlayer(2).addTension(separatedEvenings);
            }
            //            Player player1 = context.getPlayer1();
            //            player1.setTension(player1.getTension() + 1);

            if ("6".equals(id)) {

            }
            if ("8".equals(id)) {

            }
        }
    }

    public enum Usage {
        do_action_before_round,
        calc_after_round_the_actions_during_round,
        end_of_round
    }

    @NonNull
    private int round;
    @NonNull
    private String id;
    @NonNull
    private String name;
    @NonNull boolean oneRound;
    @NonNull
    private Conditions conditions;
    @NonNull
    private Usage usage;
}
