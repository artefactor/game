package com.game.model;


import com.game.strategy.PlayersTurnEnum2;

public enum WhenNoCards {
    THROW_EXCEPTION {
        public void handle(PlayersTurnEnum2 value) {
            throw new IllegalStateException(" could not find suitable card for player for " + value
                // gameContext.getRoundDayString()
                // gameContext.getPlayerInfo(2, gameContext.getPlayer2Hand(), gameContext.getPlayers().getPlayer2())
            );
        }
    },
    /**
     * Открывает все карты по очереди из любой колоды,
     * <p> пока не найдет ту, которую технически может сыграть.
     * Получает 1 напряг за каждую открытую, но не сыгранную карту.
     * <p> Все открытые - в руку? или в сброс?
     */
    FIND_UNTIL_GET {
        @Override
        public void handle(PlayersTurnEnum2 value) {
            // TODO 1: implement
        }
    };

    public abstract void handle(PlayersTurnEnum2 value);
}
