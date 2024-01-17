package com.game.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;

@Data
public class PlayerState {

    // это до конца недели
    boolean isHit;
    // это - на количество illnessDays , которое сначала = 7, потом уменьшается
    boolean isIll;
    int illnessDays;
    boolean hasBrokenCar;

    public PlayerState copy() {
        PlayerState playerState = new PlayerState();
        playerState.isIll = this.isIll;
        playerState.isHit = this.isHit;
        playerState.illnessDays = this.illnessDays;
        playerState.hasBrokenCar = this.hasBrokenCar;
        Map<Integer, AtomicInteger> newTemporaryCards = new HashMap<>(this.temporaryCards);
        playerState.temporaryCards = newTemporaryCards;  // deep copy
        return playerState;
    }

    public boolean isUnwell() {
        return isIll || isHit;
    }

    public void gotIll() {
        isIll = true;
        illnessDays = 7;
    }

    public void addDay() {
        if (isIll) {
            illnessDays--;
        }
        if (illnessDays <= 0) {
            isIll = false;
        }
    }

    Map<Integer, AtomicInteger> temporaryCards = new HashMap<>();

    public void setTemporaryOrangeCardForPlayer(int cardId, int rounds) {
        temporaryCards.put(cardId, new AtomicInteger(rounds));
    }
}
