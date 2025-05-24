package com.branka;

import static com.branka.BrankaJsonMapper.readBrankaDeck;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Combinations {

    public static void main(String[] args) throws IOException {
        var list =
            readBrankaDeck(new ObjectMapper(), "branka_deck.json");
        int X = 5; // Количество случайных карт
        int K = 3; // Требуемая длина цепочки

    }
}
