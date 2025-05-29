package com.branka;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.GameApplication;

public class BrankaJsonMapper {

    enum Option {
        WRITE_BRANKA_DECK,
        READ_BRANKA_DECK,
        READ_AND_CONVERT_TO_TYPE_ONLY_BRANKA_DECK,
        READ_AND_CONVERT_TO_TYPE_AND_TONES_BRANKA_DECK,
    }

    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        Option option = Option.READ_AND_CONVERT_TO_TYPE_ONLY_BRANKA_DECK;
        switch (option) {
            case WRITE_BRANKA_DECK:
                write(objectMapper, BrankaCardsTestSet.cardsSet, "branka_deck.json");
                break;
            case READ_BRANKA_DECK:
                StatisticsHelper.countUnique(readBrankaDeck(objectMapper, "branka_deck.json"));
                break;
            case READ_AND_CONVERT_TO_TYPE_ONLY_BRANKA_DECK: {
                //                var readDeck = BrankaCardsTestSet.cardsSet;
                var readDeck = readBrankaDeck(objectMapper, "branka_deck.json");
                var convertedDeck = readDeck.stream().map(ToCardConverter.typeConvert()).collect(Collectors.toList());
                write(objectMapper, convertedDeck, "branka_converted_type_deck.json");
            }
            break;
            case READ_AND_CONVERT_TO_TYPE_AND_TONES_BRANKA_DECK: {
                //                var readDeck = BrankaCardsTestSet.cardsSet;
                var readDeck = readBrankaDeck(objectMapper, "branka_deck.json");
                var convertedDeck =
                    readDeck.stream().map(ToCardConverter.typeAndToneConvert()).collect(Collectors.toList());
                write(objectMapper, convertedDeck, "branka_converted_type_tone_deck.json");
            }
            break;

        }
    }

    static List<WordCard> readBrankaDeck(ObjectMapper objectMapper, String jsonFileName) throws IOException {
        File orCreateFileJson = getOrCreateFileJson(jsonFileName);
        List<WordCard> content = objectMapper.readValue(orCreateFileJson, new TypeReference<>() {});
        System.out.println(content.size());
        return content;
    }

    private static void write(ObjectMapper objectMapper, List cards, String jsonFileName)
        throws JsonProcessingException {
        String content = objectMapper.writeValueAsString(cards);
        writeToFile(getOrCreateFileJson(jsonFileName), content);
        System.out.println("wrote " + cards.size() + " to  " + jsonFileName);
    }

    public static void writeToFile(File file, String content) {
        try {
            var writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static InputStream getFileAsIOStream(final String fileName) {
        InputStream ioStream = GameApplication.class
            .getClassLoader()
            .getResourceAsStream(fileName);

        if (ioStream == null) {
            throw new IllegalArgumentException(fileName + " is not found");
        }
        return ioStream;
    }

    public static File getOrCreateFileJson(String jsonFileName) {
        try {
            Path resourceDirectory = Paths.get("/Users/user/code/game/src/main", "resources"
                , "branka", "deck"
            );
            String absolutePath = resourceDirectory.toFile().getAbsolutePath();
            var file = new File(absolutePath, jsonFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
