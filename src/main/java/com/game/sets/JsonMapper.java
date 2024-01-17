package com.game.sets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.GameApplication;
import com.game.model.BlueCard;
import com.game.model.GreenCard;
import com.game.model.OrangeCard;
import com.game.model.RedCard;

public class JsonMapper {

    enum Option {
        WRITE_BLUE_DECK,
        READ_BLUE_DECK,
        WRITE_GREEN_DECK,
        READ_GREEN_DECK,
        WRITE_ORANGE_DECK,
        READ_ORANGE_DECK,
        WRITE_RED_DECK,
        READ_RED_DECK,
    }

    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonMapper.Option option = Option.READ_GREEN_DECK;
        switch (option) {
            case WRITE_BLUE_DECK:
                write(objectMapper, BlueCardsTestSet.cardsSet, "base_blue_deck.json"); break;
            case READ_BLUE_DECK:
                readBlueDeck(objectMapper, "base_blue_deck.json"); break;
            case WRITE_GREEN_DECK:
                write(objectMapper, GreenCardsTestSet.cardsSet, "base_green_deck.json"); break;
            case READ_GREEN_DECK:
                readGreenDeck(objectMapper, "base_green_deck.json"); break;
            case WRITE_ORANGE_DECK:
                write(objectMapper, OrangeCardsTestSet.cardsSet, "base_orange_deck.json"); break;
            case READ_ORANGE_DECK:
                readOrangeDeck(objectMapper, "base_orange_deck.json"); break;
            case WRITE_RED_DECK:
                write(objectMapper, RedCardsTestSet.cardsSet, "red_deck_1.json"); break;
            case READ_RED_DECK:
                readRedDeck(objectMapper, "base_red_deck.json"); break;
        }
    }

    private static void readBlueDeck(ObjectMapper objectMapper, String jsonFileName) throws IOException {
        File orCreateFileJson = getOrCreateFileJson(jsonFileName);
        List<BlueCard> content = objectMapper.readValue(orCreateFileJson, new TypeReference<>() {});
        System.out.println(content.size());
    }
    private static void readGreenDeck(ObjectMapper objectMapper, String jsonFileName) throws IOException {
        File orCreateFileJson = getOrCreateFileJson(jsonFileName);
        List<GreenCard> content = objectMapper.readValue(orCreateFileJson, new TypeReference<>() {});
        System.out.println(content.size());
        System.out.println(GreenCardsTestSet.printInfo(content));
    }
    private static void readOrangeDeck(ObjectMapper objectMapper, String jsonFileName) throws IOException {
        File orCreateFileJson = getOrCreateFileJson(jsonFileName);
        List<OrangeCard> content = objectMapper.readValue(orCreateFileJson, new TypeReference<>() {});
        System.out.println(content.size());
    }
    private static void readRedDeck(ObjectMapper objectMapper, String jsonFileName) throws IOException {
        File orCreateFileJson = getOrCreateFileJson(jsonFileName);
        List<RedCard> content = objectMapper.readValue(orCreateFileJson, new TypeReference<>() {});
        System.out.println(content.size());
    }

    private static void write(ObjectMapper objectMapper, List cards, String jsonFileName)
        throws JsonProcessingException {
        String content = objectMapper.writeValueAsString(cards);
        writeToFile(getOrCreateFileJson(jsonFileName), content);
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
            Path resourceDirectory = Paths.get("/Users/user/code/raw-data-bi/bi/bi-ui/src/main", "resources"
                , "game", "deck"
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
