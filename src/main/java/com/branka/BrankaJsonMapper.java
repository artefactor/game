package com.branka;

import static java.util.function.Predicate.not;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.branka.statistic.StatisticsHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.GameApplication;

public class BrankaJsonMapper {

    enum Option {
        WRITE_BRANKA_DECK,
        READ_BRANKA_DECK,
        READ_BRANKA_DECK_SIMPLE,
        READ_AND_CONVERT_TO_TYPE_ONLY_BRANKA_DECK,
        READ_AND_CONVERT_TO_TYPE_AND_TONES_BRANKA_DECK,
    }

    public static void main(String[] args) throws IOException {
        var objectMapper = new ObjectMapper();

        var option = Option.READ_BRANKA_DECK;
        switch (option) {
            case WRITE_BRANKA_DECK:
                write(objectMapper, BrankaCardsTestSet.cardsSet, "branka_deck.json");
                break;
            case READ_BRANKA_DECK:
                StatisticsHelper.countUnique(readBrankaDeck(objectMapper, "ref_branka_deck5_doubleE.json"));
                break;
            case READ_BRANKA_DECK_SIMPLE:
                readBrankaDeck(objectMapper, "ref_branka_deck5_doubleE.json");
                break;
            case READ_AND_CONVERT_TO_TYPE_ONLY_BRANKA_DECK: {
                //                var readDeck = BrankaCardsTestSet.cardsSet;
                var readDeck = readBrankaDeck(objectMapper, "branka_deck.json");
                var convertedDeck =
                    readDeck.stream().map(ToCardConverter.typeToGroupConvert(readDeck)).collect(Collectors.toList());
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
        content = content.stream().filter(not(WordCard::isSkip)).collect(Collectors.toList());
        System.out.println("Reading file: " + jsonFileName);
        System.out.println("Deck size: " + content.size());
        Collection<String> set = new LinkedHashSet<>();
        for (var card : content) {
            String format = "| %d| %-" + 3 + "s | %-" + 15 + "s | %" + 18 + "s |";
            set.add(String.format(format, card.getId(), card.getGroup(), card.getTone(), card.getType()));
        }
        for (var string : set) {
            System.out.println(string);
        }
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

    public static void writeToFile(File file, List<String> content) {
        try {
            var writer = new BufferedWriter(new FileWriter(file));
            for (String s : content) {
                writer.write(s);
                writer.newLine();
            }
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
            String absolutePath = ResourceDirectory.get_RESOURCE_DIRECTORY("deck").toFile().getAbsolutePath();
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
