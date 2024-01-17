package com.game.sets;

import static com.game.model.OrangeCard.Sphere.D;
import static com.game.model.OrangeCard.Sphere.G;
import static com.game.model.OrangeCard.Sphere.H;
import static com.game.model.OrangeCard.Sphere.L;
import static com.game.model.OrangeCard.Sphere.T;
import static com.game.model.OrangeCard.Sphere.U;
import static com.game.model.OrangeCard.Sphere.V;
import static com.game.model.OrangeCard.Type.ORANGE;
import static com.game.model.OrangeCard.Type.YELLOW;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.game.model.OrangeCard;

public class OrangeCardsTestSet {

    public static List<OrangeCard> findO(Integer... id) {
        var c = Arrays.stream(id).map(s -> cardsSet.stream()
            .filter(card -> card.getId() == s)
            .findFirst().get()).collect(Collectors.toCollection(LinkedList::new));
        return c;
    }

    public static List<OrangeCard> cardsSet = List.of(
        new OrangeCard(YELLOW, G, 49, "Управление гневом", 2), // В конце недели уберите 2 напряга
        new OrangeCard(YELLOW, T, 51, "За компанию", null),
        new OrangeCard(YELLOW, T, 52, "Компанейский", null),
        new OrangeCard(YELLOW, V, 39, "Спонтанный", 3), // Получите + 3 бонуса с сыгранных зеленых событий
        new OrangeCard(YELLOW, V, 48, "Переговорщик", null),
        new OrangeCard(YELLOW, H, 47, "Бытовой Мастер", null),
        new OrangeCard(YELLOW, T, 42, "Темпераментный", 1), // т.е. бонус + 1
        new OrangeCard(YELLOW, H, 41, "Доктор", 2), //либо партнёр выздоравливает на 2 дня раньше
        new OrangeCard(YELLOW, G, 44, "Муза", 40),  //Работает, если Ваше удовлетворение > 40
        new OrangeCard(ORANGE, H, 5, "Нужна помощь", 2), //и дают 2 напряга если играть самому в одиночку)
        new OrangeCard(ORANGE, G, 9, "Герой", null),
        new OrangeCard(YELLOW, H, 38, "Эмпат", null),
        new OrangeCard(ORANGE, U, 21, "Не выношу скандал", 2), //получаете дополнительно 2 напряга
        new OrangeCard(ORANGE, U, 23, "Переменчивое настроение", 5), //Сумма меньше 5 – закатываете истерику
        new OrangeCard(ORANGE, T, 22, "Личное пространство", 5), // За каждый недопроведенный вечер получаете 5 единиц напряга)
        new OrangeCard(ORANGE, L, 26, "Домосед", 1), //получите 1 напряг
        new OrangeCard(ORANGE, L, 28, "Тусовщик", 3), //За каждый недопроведенный вечер получаете 3 напряга)
        new OrangeCard(ORANGE, T, 25, "Совместный вечер", 2), //Если вечер будет раздельный - получите 2 напряга
        new OrangeCard(ORANGE, D, 14, "Раздражительность", null),
        new OrangeCard(ORANGE, H, 11, "Я тебе не служанка", 2) //то получаете 2 напряга

    );


}
