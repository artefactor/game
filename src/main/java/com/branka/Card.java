package com.branka;

import lombok.Data;

// Класс для представления карточки
@Data
class Card {

    char letter;

    Card(char letter) {
        this.letter = letter;
    }

    @Override
    public String toString() {
        return String.valueOf(letter);
    }
}
