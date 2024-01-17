package com.game.sets;

import static com.game.model.FavouriteAction.B;
import static com.game.model.FavouriteAction.N;
import static com.game.model.FavouriteAction.O;
import static com.game.model.FavouriteAction.R;
import static com.game.model.FavouriteAction.U;
import static com.game.model.GreenCard.ActionTime.MOVABLE;
import static com.game.model.GreenCard.ActionTime.TODAY;
import static com.game.model.GreenCard.ConsequencesOfRefusal.ADDITIONAL;
import static com.game.model.GreenCard.ConsequencesOfRefusal.REFUSAL_FREE;
import static com.game.model.GreenCard.ConsequencesOfRefusal.REFUSAL_IMPOSSIBLE;
import static com.game.model.GreenCard.ConsequencesOfRefusal.TENSION_1;
import static com.game.model.GreenCard.ConsequencesOfRefusal.TENSION_3;
import static com.game.model.GreenCard.ConsequencesOfRefusal.TENSION_4;
import static com.game.model.GreenCard.Type.ILLNESS;
import static com.game.model.GreenCard.Type.NORMAL;
import static com.game.model.GreenCard.Type.WORK;
import static com.game.model.Location.ANY;
import static com.game.model.Location.HOME;
import static com.game.model.Location.OUTSIDE;
import static com.game.model.Participants.ANY_PERSON_OR_BOTH;
import static com.game.model.Participants.PARTNER;
import static com.game.model.Participants.SELF;
import static com.game.model.Participants.SELF_OR_BOTH;
import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.stream.Collectors;

import com.game.model.GreenCard;
import com.game.model.GreenCard.SpecialGreenCards;
import com.game.model.Util;


public class GreenCardsTestSet {
//    {
//        1.SELF - 12
//        2.PARTNER - 1
//        3.ANY_PERSON_OR_BOTH - 2
//        4.SELF_OR_BOTH - 9
//    }

    public static List<GreenCard> cardsSet = List.of(
        new GreenCard("1","Друг проездом", NORMAL, true, OUTSIDE, SELF, O, TODAY,
            TENSION_4, SpecialGreenCards.none),
        new GreenCard("2","Поддержать друга", NORMAL, false, HOME, SELF, O, TODAY,
            TENSION_4, SpecialGreenCards.none),
        new GreenCard("3","Поддержать друга", NORMAL, false, HOME, SELF, O, TODAY,
            TENSION_4, SpecialGreenCards.none),
        new GreenCard("4","Прорвало трубу", NORMAL, false, HOME, SELF_OR_BOTH, B, TODAY,
            REFUSAL_IMPOSSIBLE, SpecialGreenCards.Прорвало_трубу),
        // Сегодня + вечер на выбор след недел
        new GreenCard("5","Сломалась машина", NORMAL, true, OUTSIDE, SELF_OR_BOTH, N, TODAY,
            REFUSAL_IMPOSSIBLE, SpecialGreenCards.Сломалась_машина),
        // вопрос по поводу локации
        new GreenCard("6","Передвинуть шкаф", NORMAL, true, HOME, SELF_OR_BOTH, B, MOVABLE,
            TENSION_4, SpecialGreenCards.Передвинуть_шкаф),
        new GreenCard("7","Пригласили на ДР", NORMAL, true, OUTSIDE, SELF_OR_BOTH, O, MOVABLE,
            REFUSAL_FREE, SpecialGreenCards.Пригласили_на_ДР),
        new GreenCard("8","Пригласили на ДР", NORMAL, true, OUTSIDE, SELF_OR_BOTH, O, TODAY,
            REFUSAL_FREE, SpecialGreenCards.Пригласили_на_ДР),
        new GreenCard("9","Выигрыш в лотерею", NORMAL, true, OUTSIDE, SELF_OR_BOTH, N, MOVABLE,
            REFUSAL_FREE, SpecialGreenCards.Выигрыш_в_лотерею),
        new GreenCard("10","Понятул спину", ILLNESS, false, HOME, SELF, N, TODAY, REFUSAL_IMPOSSIBLE, SpecialGreenCards.ушиб_TILL_END_OF_WEEK),
        new GreenCard("11","Ушиб руку", ILLNESS, false, HOME, SELF, N, TODAY, REFUSAL_IMPOSSIBLE, SpecialGreenCards.ушиб_TILL_END_OF_WEEK),
        new GreenCard("12","Заболел", ILLNESS, false, HOME, SELF, N, TODAY, REFUSAL_IMPOSSIBLE, SpecialGreenCards.заболел_SEVEN_DAYS),
        new GreenCard("13","Забрать передачку", NORMAL, true, OUTSIDE, PARTNER, N, MOVABLE,
            TENSION_3, SpecialGreenCards.none),
        new GreenCard("14","Скидки", NORMAL, false, ANY, ANY_PERSON_OR_BOTH, N, TODAY,
            ADDITIONAL, SpecialGreenCards.Скидки),
        new GreenCard("15","Скидки", NORMAL, false, ANY, ANY_PERSON_OR_BOTH, N, MOVABLE,
            ADDITIONAL, SpecialGreenCards.Скидки),
        new GreenCard("16","Временная психотерапия", NORMAL, false, HOME, SELF, U, TODAY,
            REFUSAL_FREE, SpecialGreenCards.Временная_психотерапия),
        new GreenCard("17","Посидеть с крестником", NORMAL, false, HOME, SELF_OR_BOTH, N, TODAY,
            TENSION_1, SpecialGreenCards.Посидеть_с_крестником),
        new GreenCard("18","Застрял в лифте", NORMAL, false, HOME, SELF, N, TODAY,
            REFUSAL_IMPOSSIBLE, SpecialGreenCards.none),
        // Можно пригласить партнера, если его уровень удовлетворенности >30 Тогда партнер - кубик
        new GreenCard("19","Концерт любимой группы", NORMAL, true, OUTSIDE, SELF_OR_BOTH, R, TODAY,
            REFUSAL_FREE, SpecialGreenCards.Концерт_любимой_группы),
        new GreenCard("20","Концерт любимой группы", NORMAL, true, OUTSIDE, SELF_OR_BOTH, R, TODAY,
            REFUSAL_FREE, SpecialGreenCards.Концерт_любимой_группы),
        new GreenCard("21","Собеседование", NORMAL, false, ANY, SELF, U, TODAY,
            REFUSAL_FREE, SpecialGreenCards.Собеседование),
        new GreenCard("22","Задержали на работе", WORK, false, OUTSIDE, SELF, N, MOVABLE,
            REFUSAL_IMPOSSIBLE, SpecialGreenCards.Задержали_на_работе),
        new GreenCard("23","Задержали на работе", WORK, false, OUTSIDE, SELF, N, TODAY,
            REFUSAL_IMPOSSIBLE, SpecialGreenCards.Задержали_на_работе),
        new GreenCard("24","Задержали на работе", WORK, false, OUTSIDE, SELF, N, TODAY,
            REFUSAL_IMPOSSIBLE, SpecialGreenCards.Задержали_на_работе)
    );

    public static GreenCard findG(String id) {
        var c = cardsSet.stream()
            .filter(card -> card.getId().equals(id))
            .findFirst().get();
        return c;
    }

    public static String printInfo(List<GreenCard> list) {
        var stat = list.stream()
            .collect(groupingBy(GreenCard::getParticipants, Collectors.counting()));
        return Util.printCards(stat);
    }
}
