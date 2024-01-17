package com.game.strategy;

import com.game.model.Player;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public
class Profit {

    Player newPlayer1Status;
    Player newPlayer2Status;

    public Player getPlayer(int playerId) {
        return playerId == 1 ? newPlayer1Status : newPlayer2Status;
    }
}
