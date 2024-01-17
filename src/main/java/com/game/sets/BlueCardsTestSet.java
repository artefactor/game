package com.game.sets;

import static com.game.model.BlueCard.SpecialBlueCards.book;
import static com.game.model.BlueCard.SpecialBlueCards.choose_person;
import static com.game.model.BlueCard.SpecialBlueCards.energy_bonus;
import static com.game.model.BlueCard.SpecialBlueCards.increase_stamina_56;
import static com.game.model.BlueCard.SpecialBlueCards.increase_stamina_6;
import static com.game.model.BlueCard.SpecialBlueCards.money_bonus;
import static com.game.model.BlueCard.SpecialBlueCards.none;
import static com.game.model.BlueCard.SpecialBlueCards.partner_cube;
import static com.game.model.BlueCard.SpecialBlueCards.psycho_training;
import static com.game.model.BlueCard.SpecialBlueCards.scandal;
import static com.game.model.BlueCard.SpecialBlueCards.shopping;
import static com.game.model.BlueCard.SpecialBlueCards.shopping_clothes;
import static com.game.model.BlueCard.SpecialBlueCards.therapy;
import static com.game.model.BlueCard.SpecialBlueCards.training_skill;
import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.game.model.BlueCard;
import com.game.model.BlueGreenCard;
import com.game.model.Location;
import com.game.model.Participants;
import com.game.model.Restriction;

public class BlueCardsTestSet {

    public static BlueGreenCard find(String id) {
        return cardsSet.stream()
            .filter(card -> card.getId().equals(id))
            .findFirst().get();
    }

    public static List<BlueGreenCard> findB(String... id) {
        List<String> strings = asList(id);
        var c = cardsSet.stream().filter(c1 -> strings.contains(c1.getId())).map(c1->(BlueGreenCard)c1)
        .collect(Collectors.toCollection(LinkedList::new));
        return c;
    }

    public static List<BlueCard> cardsSet = List.of(
        //        special bonus for T1-T3: кубик 5/6: +1 к вынос +
        new BlueCard("T_01", "Тренажерка", 2, 2, 2, 0, Location.OUTSIDE, Participants.SELF, Restriction.NOT_TWO_IN_A_ROW, increase_stamina_56),
        new BlueCard("T_02", "Тренажерка", 2, 2, 2, 0, Location.OUTSIDE, Participants.SELF, Restriction.NOT_TWO_IN_A_ROW, increase_stamina_56),
        new BlueCard("T_03", "Тренажерка", 2, 2, 2, 0, Location.OUTSIDE, Participants.SELF, Restriction.NOT_TWO_IN_A_ROW, increase_stamina_56),

        //        special bonus for T4-T6: кубик 6: +1 к вынос +
        new BlueCard("T_04", "Сходить в бассейн", 2, 1, 2, 0, Location.OUTSIDE, Participants.SELF, Restriction.NOT_TWO_IN_A_ROW, increase_stamina_6),
        new BlueCard("T_05", "Сходить в бассейн", 2, 1, 2, 0, Location.OUTSIDE, Participants.SELF, Restriction.NOT_TWO_IN_A_ROW, increase_stamina_6),
        new BlueCard("T_06", "Сходить в бассейн", 2, 1, 2, 0, Location.OUTSIDE, Participants.SELF, Restriction.NOT_TWO_IN_A_ROW, increase_stamina_6),

        //        bonus  к силе +
        new BlueCard("T_07", "Поход на массаж", 2, 0, 3, 0, Location.OUTSIDE, Participants.SELF, Restriction.NOT_TWO_IN_A_ROW, energy_bonus),
        new BlueCard("T_08", "Поход на массаж", 2, 0, 3, 0, Location.OUTSIDE, Participants.SELF, Restriction.NOT_TWO_IN_A_ROW, energy_bonus),

        // bonus партнёр - кубик +
        new BlueCard("T_09", "Массаж партнёру", 0, 1, 0, 0, Location.HOME, Participants.BOTH, null, partner_cube),

        new BlueCard("T_10", "Пойти в поход", 0, 0, 5, 0, Location.OUTSIDE, Participants.SELF_OR_BOTH, Restriction.WEEKEND, none),
        // цена: пер персон, но бонус только тому кто играет TODO 1:
        new BlueCard("T_11", "Йога", 2, 0, 0, -1, Location.OUTSIDE, Participants.SELF_OR_BOTH, null, none),
        new BlueCard("T_12", "Пойти в салон красоты", 4, 0, 5, 0, Location.OUTSIDE, Participants.SELF, Restriction.NOT_TWO_IN_A_ROW, none),
        new BlueCard("T_13", "Пойти в салон красоты", 4, 0, 5, 0, Location.OUTSIDE, Participants.PARTNER, Restriction.NOT_TWO_IN_A_ROW, none),

        new BlueCard("B_01", "Приготовить обед", 0, 0, 3, 0, Location.HOME, Participants.SELF, null, none),
        new BlueCard("B_02", "Приготовить ужин", 0, 0, 3, 0, Location.HOME, Participants.ANY_PERSON, null, none),
        new BlueCard("B_03", "Приготовить ужин", 0, 0, 3, 0, Location.HOME, Participants.ANY_PERSON, null, none),
        new BlueCard("B_04", "Приготовить ужин", 0, 0, 3, 0, Location.HOME, Participants.ANY_PERSON, null, none),
        new BlueCard("B_05", "Приготовить торт", 0, 0, 4, 0, Location.HOME, Participants.PARTNER, null, none),
        new BlueCard("B_06", "Приготовить торт", 0, 0, 4, 0, Location.HOME, Participants.PARTNER, null, none),
        new BlueCard("B_07", "Приготовить торт", 0, 0, 4, 0, Location.HOME, Participants.PARTNER, null, none),

        new BlueCard("B_08", "Убрать квартиру", 0, 1, 1, 0, Location.HOME, Participants.ANY_PERSON_OR_BOTH, null, none),
        new BlueCard("B_09", "Убрать квартиру", 0, 1, 1, 0, Location.HOME, Participants.ANY_PERSON_OR_BOTH, null, none),

        new BlueCard("B_10", "Генеральная уборка", 0, 1, 2, 0, Location.HOME, Participants.PARTNER_OR_BOTH, Restriction.MUST_BE_PLAYED, none),

        new BlueCard("B_11", "Разморозить холодильник", 0, 0, 1, 0, Location.HOME, Participants.SELF_OR_BOTH, Restriction.MUST_BE_PLAYED_ONE_TIME_ACTION, none),

        // 4  - шопинги +
        new BlueCard("BB_12", "Купить новый телефон", 4, 0, 5, 0, Location.OUTSIDE, Participants.ANY_PERSON_OR_BOTH,
            Restriction.MUST_BE_PLAYED_ONE_TIME_ACTION, none),

        // цена 30 за покупку. (+3,  партнёр - кубик/покупку) +
        new BlueCard("BB_13", "Купить новую одежду", 3, 0, 3, 0, Location.OUTSIDE, Participants.ANY_PERSON_OR_BOTH, null, shopping_clothes),
        // цена и эффект за покупку
        new BlueCard("BB_14", "Пойти за покупками", 1, 0, 2, 0, Location.OUTSIDE, Participants.ANY_PERSON_OR_BOTH, Restriction.MULTIPLE_PURCHASE, shopping),
        new BlueCard("BB_15", "Шопинг", 2, 0, 1, 0, Location.OUTSIDE, Participants.ANY_PERSON_OR_BOTH, Restriction.MULTIPLE_PURCHASE, shopping),

        new BlueCard("O_01", "Встреча с друзьями", 0, 0, 2, 0, Location.OUTSIDE, Participants.SELF_OR_BOTH, null, none),
        new BlueCard("O_02", "Потусить с подругами", 2, 0, 3, 0, Location.OUTSIDE, Participants.SELF_OR_BOTH, null, none),
        new BlueCard("O_03", "Встретиться с подругами", 1, 0, 3, 0, Location.OUTSIDE, Participants.SELF, null, none),
        new BlueCard("O_04", "Навестить друга", 0, 0, 2, 0, Location.HOME, Participants.SELF_OR_BOTH, null, none),
        new BlueCard("O_05", "Выговориться другу", 0, 0, 0, -1, Location.HOME, Participants.SELF, null, none),

        //- накладывать
        new BlueCard("O_06", "Скандал", 0, 0, 0, 1, Location.HOME, Participants.BOTH, Restriction.SCANDAL, scandal),
        new BlueCard("O_07", "Скандал", 0, 0, 0, 1, Location.HOME, Participants.BOTH, Restriction.SCANDAL, scandal),
        new BlueCard("O_08", "Скандал", 0, 0, 0, 1, Location.OUTSIDE, Participants.BOTH, Restriction.SCANDAL, scandal),
        new BlueCard("O_09", "Скандал", 0, 0, 0, 1, Location.OUTSIDE, Participants.BOTH, Restriction.SCANDAL, scandal),

        // напряг чел на выбор +
        new BlueCard("O_10", "Душевный разговор", 0, 0, 0, -2, Location.ANY, Participants.BOTH, Restriction.MAY_BE_UNITED, choose_person),
        new BlueCard("O_11", "Душевный разговор", 0, 0, 0, -1, Location.ANY, Participants.BOTH, Restriction.MAY_BE_UNITED, choose_person),
        new BlueCard("O_12", "Душевный разговор", 0, 0, 0, -1, Location.ANY, Participants.BOTH, Restriction.MAY_BE_UNITED, choose_person),

        //+1 к физ силы +
        new BlueCard("N_01", "Отоспаться дома", 0, 0, 1, 0, Location.HOME, Participants.SELF, null, energy_bonus),
        new BlueCard("N_02", "Отоспаться дома", 0, 0, 1, 0, Location.HOME, Participants.SELF, null, energy_bonus),
        new BlueCard("N_03", "Отоспаться дома", 0, 0, 1, 0, Location.HOME, Participants.SELF, null, energy_bonus),
        //        +5, а партнер - кубик +
        new BlueCard("N_04", "Интимный вечер", 0, 1, 5, 0, Location.HOME, Participants.BOTH, null, partner_cube),
        //        +5, а партнер - кубик +
        new BlueCard("N_05", "Интимный вечер", 0, 1, 5, 0, Location.HOME, Participants.BOTH, null, partner_cube),
        //        оба - кубик +
        new BlueCard("N_06", "Интимный вечер", 0, 1, 3.5, 0, Location.HOME, Participants.BOTH, null, partner_cube),

        new BlueCard("H_01", "Рисование", 2, 0, 3, 0, Location.OUTSIDE, Participants.SELF_OR_BOTH, null, none),
        new BlueCard("H_02", "Рисование", 2, 0, 3, 0, Location.OUTSIDE, Participants.SELF_OR_BOTH, null, none),
        new BlueCard("H_03", "Рисование", 2, 0, 3, 0, Location.OUTSIDE, Participants.SELF_OR_BOTH, null, none),

        new BlueCard("H_04", "Сочинять песню", 0, 0, 3, 0, Location.HOME, Participants.SELF_OR_BOTH, null, none),
        new BlueCard("H_05", "Сочинять песню", 0, 0, 3, 0, Location.HOME, Participants.SELF_OR_BOTH, null, none),
        new BlueCard("H_06", "Собрать паззл", 0, 0, 3, 0, Location.HOME, Participants.SELF_OR_BOTH, null, none),

        new BlueCard("H_07", "Пойти на рыбалку", 0, 0, 5, 0, Location.OUTSIDE, Participants.SELF, Restriction.WEEKEND, none),
        new BlueCard("H_08", "Пойти на рыбалку", 0, 0, 5, 0, Location.OUTSIDE, Participants.SELF, Restriction.WEEKEND, none),
        new BlueCard("H_09", "Пойти на рыбалку", 0, 0, 5, 0, Location.OUTSIDE, Participants.SELF, Restriction.WEEKEND, none),

        // +2 к деньгам +
        new BlueCard("U_01", "Подработка", -2, 1, 1, 0, Location.OUTSIDE, Participants.SELF, null, money_bonus),
        new BlueCard("U_02", "Задержаться на работе", 0, 1, 1, 0, Location.OUTSIDE, Participants.SELF, null, none),
        //        Цена 4 за вечер (2 вечера) +
        //        Эффект +способность на выбор или ЗП +20
        new BlueCard("U_03", "Тренинг навыка", 4, 0, 2, 0, Location.OUTSIDE, Participants.SELF, Restriction.TWO_TIMES_ACTION, training_skill),
        // кубик 6: убрать черту на выбор +
        new BlueCard("U_04", "Психотерапия", 4, 0, 0, 0, Location.OUTSIDE, Participants.SELF_OR_BOTH, null, therapy),
        // эффект + способность случайно +
        new BlueCard("U_05", "Психотренинг", 8, 0, 2, 0, Location.OUTSIDE, Participants.SELF, Restriction.WEEKEND_ONE_TIME_ACTION, psycho_training),
        //        Бонус кубик 6: убрать черту на выбор +
        new BlueCard("U_06", "Книга по отношениям", 0, 0, 0, 0, Location.HOME, Participants.ANY_PERSON, null, book),

        new BlueCard("R_01", "Кинотеатр", 1, 0, 2, 0, Location.OUTSIDE, Participants.SELF_OR_BOTH, null, none),
        new BlueCard("R_02", "Кинотеатр", 1, 0, 2, 0, Location.OUTSIDE, Participants.SELF_OR_BOTH, null, none),
        new BlueCard("R_03", "Кинотеатр", 1, 0, 2, 0, Location.OUTSIDE, Participants.SELF_OR_BOTH, null, none),

        new BlueCard("R_04", "Посмотреть сериал", 0, 0, 1, 0, Location.HOME, Participants.SELF_OR_BOTH, null, none),
        new BlueCard("R_05", "Посмотреть сериал", 0, 0, 1, 0, Location.HOME, Participants.SELF_OR_BOTH, null, none),
        new BlueCard("R_06", "Посмотреть сериал", 0, 0, 1, 0, Location.HOME, Participants.SELF_OR_BOTH, null, none),
        // оба: кубик +
        new BlueCard("R_07", "Прыгнуть с парашютом", 3, 0, 3.5, 0, Location.OUTSIDE, Participants.SELF_OR_BOTH, null, partner_cube),
        // оба: кубик +
        new BlueCard("R_08", "Прыгнуть с парашютом", 3, 0, 3.5, 0, Location.OUTSIDE, Participants.SELF_OR_BOTH, null, partner_cube),
        new BlueCard("R_09", "Танцы", 3, 0, 3, 0, Location.OUTSIDE, Participants.SELF_OR_BOTH, null, none)
    );


}
