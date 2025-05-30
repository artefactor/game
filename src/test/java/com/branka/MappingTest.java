package com.branka;

import static com.branka.BrankaJsonMapper.readBrankaDeck;

import java.io.IOException;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

@Deprecated
public class MappingTest {

    @Test
    void testSubstringsAreValid() throws IOException {
        // read cards
        var objectMapper = new ObjectMapper();
        var readDeck = readBrankaDeck(objectMapper, "branka_deck.json");
        var convertedDeck = readDeck.stream().map(ToCardConverter.typeConvert()).collect(Collectors.toList());

        // read combinations
        var substrings = CombinationChecker.loadSubstringsFromFile("substrings.txt");
        CombinationChecker.initializeCountMap(substrings);
        for (String substring : substrings) {

        }

        // check combinations
    }
}
