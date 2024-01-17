package com.game.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RedDeck {
    private List<RedCard> cards;
}
