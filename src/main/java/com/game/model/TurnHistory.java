package com.game.model;

import java.util.HashMap;

import lombok.Data;

@Data
public class TurnHistory {

    private HashMap<String, BlueGreenCardWithLocationOption> map = new HashMap<>();

    public void set(int ownerId, Participants participants1, BlueGreenCardWithLocationOption card) {
        String key = ownerId + "" + participants1;
        map.put(key, card);
    }

    public BlueGreenCardWithLocationOption get(int ownerId, Participants participants1) {
        String key = ownerId + "" + participants1;
        return map.get(key);
    }
}
