package com.game.sets;

import static com.game.model.RedCard.Conditions.diff_satisfaction;
import static com.game.model.RedCard.Conditions.none;
import static com.game.model.RedCard.Usage.do_action_before_round;
import static com.game.model.RedCard.Usage.calc_after_round_the_actions_during_round;
import static com.game.model.RedCard.Usage.end_of_round;

import java.util.List;

import com.game.model.RedCard;

public class RedCardsTestSet {

    public static List<RedCard> cardsSet = List.of(
        /**
         * ОБА:
         * За каждый вечер не "совместно" получаете 1 напряг.
         */
        new RedCard(1, "1", "Влюбленность", true, none, calc_after_round_the_actions_during_round),
        /**
         * ОБА:
         * Вскрываете 2 карты черты (желтая и оранжевая)
         */
        new RedCard(2, "2", "Проявление характера", false, none, do_action_before_round),
        /**
         *
         Тот, у кого меньше напряга:
         (если одинаково - то оба) Вскрываете оранжевую карту черты.
         */
//        new RedCard(3, "3a", "Раскрытие характера", false, none, do_action_before_round),
        /**
         * ОБА:
         * Вскрываете оранжевую карту черты.
         */
        new RedCard(3, "3b", "Раскрытие характера", false, none, do_action_before_round),
        /**
         * Теперь когда ОБА играете карты с одним названием,
         * то (по желанию) могжете трактовать это как совместное занятие.
         */
        new RedCard(4, "4", "Сближение", false, none, do_action_before_round),
        /**
         * Тот, у кого БОЛЬШЕ удовлетворения
         * За каждый вечер без партнера получаете +3 удовлетворения
         * (распространяется на синие и зеленые карты)
         */
        new RedCard(5, "5a", "Стремление к независимости", true, diff_satisfaction,
            calc_after_round_the_actions_during_round),
        /**
         * Тот, у кого МЕНЬШЕ удовлетворения
         * За каждый вечер без партнера получаете 1 напряг (распространяется на синие и зеленые карты)
         */
//        new RedCard(5, "5b", "Чувство неуверенности", true, diff_satisfaction,
//            calc_after_round_the_actions_during_round),
        /**
         * Тот, у кого МЕНЬШЕ удовлетворения
         * Совместный выход: получаете 2 напряга, Партнер выходит один: получаете 4 напряга (распространяется на синие и зеленые карты)
         */
//        new RedCard(5, "5c", "Сильная ревность", true, diff_satisfaction, calc_after_round_the_actions_during_round),
        /**
         * Тот, у кого меньше удовлетворения (если одинаково - то ни на кого):
         * Всякий раз когда партнер проводит время не дома и не с вами (т.е. выходит один - синие и зеленые карты), то получаете 3 единицы напряга
         */
//        new RedCard(5, "5d", "Ревность", true, none, calc_after_round_the_actions_during_round),
        new RedCard(6, "6", "Развитие отношений", false, none, end_of_round),
        /**
         * ОБА:
         * Выбираете одну черту по своему выбору и переворачиваете рубашкой вверх на текущий раунд (не активно только 1 раунд).
         */
        new RedCard(7, "7", "Серьезный разговор", true, none, do_action_before_round),
        new RedCard(8, "8", "Свадьба или расставание", false, none, end_of_round)
    );




}
