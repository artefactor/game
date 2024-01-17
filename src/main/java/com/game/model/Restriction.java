package com.game.model;

 // TODO 0:
public enum Restriction {
    //      Только на оба выходных
    WEEKEND,
    //       разовая
    ONE_TIME_ACTION,
    WEEKEND_ONE_TIME_ACTION,   // TODO одну карту на 2 дня растянуть. и при выборе

    //        2 вечера, разовая
    TWO_TIMES_ACTION,
    //       Нельзя сбросить
    MUST_BE_PLAYED,
    //       Нельзя сбросить, разовая
    MUST_BE_PLAYED_ONE_TIME_ACTION,
    //       не может быть 2 дня подряд
    NOT_TWO_IN_A_ROW,

    //       от 0 до 5 покупок
    MULTIPLE_PURCHASE,

    //       "Не сбрасывается,если Напряг>=5
    //        Можно объединять от каждого"
    SCANDAL,
    //        Можно объединять от каждого"
    MAY_BE_UNITED

}
