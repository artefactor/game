package com.game.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class MPTOption {

    final int i;

    public MPTOption(int i) {
        this.i = i;
    }

    Stream<MPTOption> getE() {
        return Stream.of(new MPTOption(i + 10), this);
    }
}

public class TestStream {

    public static void main(String[] args) {

        List<MPTOption> optionTypes = new ArrayList<>();
        optionTypes.add(new MPTOption(1));
        optionTypes.add(new MPTOption(2));
        optionTypes.stream()
            .flatMap(MPTOption::getE)
            .forEach(e -> System.out.println(e.i));
    }

}
