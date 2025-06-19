package com.branka.statistic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.branka.WordCard;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ToneCount {

    protected static final String ЖЕЛТАЯ = "желтая";
    protected static final String КРАСНО_ЖЕЛТАЯ = "красно-желтая";
    protected static final String НЕЙТРАЛЬНАЯ = "нейтральная";
    protected static final String ЗЕЛЕНАЯ = "зеленая";
    protected static final String КРАСНАЯ = "красная";
    protected static final String НЕОПРЕДЕЛЕННАЯ = "неопределенная";

    int red;
    int green;
    int yellow;
    int neutral;

    public static ToneCount toToneCount(Map<WordCard.Tone, Integer> countMap) {
        int red = countMap.getOrDefault(WordCard.Tone.RED, 0);
        int green = countMap.getOrDefault(WordCard.Tone.GREEN, 0);
        int yellow = countMap.getOrDefault(WordCard.Tone.YELLOW, 0);
        int neutral = countMap.getOrDefault(WordCard.Tone.NEUTRAL, 0);
        return new ToneCount(
            red, green, yellow, neutral
        );
    }

    public static String classifySingleCombination(ToneCount countMap) {
        int red = countMap.red;
        int green = countMap.green;
        int yellow = countMap.yellow;
        int neutral = countMap.neutral;

        // Правило 4а - имеет приоритет над другими
        if (green == red && red > 0) {
            return ЖЕЛТАЯ;
        }
        // Правило 4
        if (yellow >= 2 && red == 0 && green == 0) {
            return ЖЕЛТАЯ;
        }

        // Правило 3
        if (red >= 2 && green == 0 && yellow == 0) {
            return КРАСНАЯ;
        }

        // Правило 3а
        if (red >= 2 && yellow > 0 && green == 0) {
            return КРАСНО_ЖЕЛТАЯ;
        }

        // Правило 1. Может нейтральных можно делать  и просто >0
        if (red == 0 && green == 0 && yellow == 0 && neutral >= 2) {
            return НЕЙТРАЛЬНАЯ;
        }

        // Правило 2
        if (green >= 2 && red == 0 && yellow == 0) {
            return ЗЕЛЕНАЯ;
        }

        // Если ни одно из правил не подошло
        return НЕОПРЕДЕЛЕННАЯ;
    }

    public static Map<ToneCount, Integer> sort(Map<ToneCount, Integer> unsortedMap) {
        Comparator<ToneCount> comparator =
            Comparator.comparing(ToneCount::classifySingleCombination)
                .thenComparingInt(tc -> tc.red + tc.green + tc.yellow + tc.neutral)
                .thenComparingInt(tc -> tc.red)
                .thenComparingInt(tc -> tc.green)
                .thenComparingInt(tc -> tc.yellow)
                .thenComparingInt(tc -> tc.neutral);

        Map<ToneCount, Integer> sortedMap = unsortedMap.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(comparator))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (oldVal, newVal) -> oldVal,
                LinkedHashMap::new
            ));

        return sortedMap;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s | %d | %s",
            red == 0 ? "   " : "R:" + red,
            green == 0 ? "   " : "G:" + green,
            yellow == 0 ? "   " : "Y:" + yellow,
            neutral == 0 ? "   " : "N:" + neutral,
            (red + green + yellow + neutral), classifySingleCombination(this));
    }

    public static List<ToneCount> generateAllCombinations(int total) {
        List<ToneCount> combinations = new ArrayList<>();

        for (int r = 0; r <= total; r++) {
            for (int g = 0; g <= total - r; g++) {
                for (int y = 0; y <= total - r - g; y++) {
                    int n = total - r - g - y;
                    combinations.add(new ToneCount(r, g, y, n));
                }
            }
        }

        return combinations;
    }

    public static void main(String[] args) {
        int total = 6;
        List<ToneCount> allCombinations = initToneMap(total);
        System.out.println("Все возможные комбинации для суммы = " + total + ":");
        System.out.println("Всего комбинаций: " + allCombinations.size());

        for (ToneCount tc : allCombinations) {
            System.out.println(tc);
        }
    }

    public static Map<ToneCount, Integer> toneCountMap = new LinkedHashMap<>();

    public static List<ToneCount> initToneMap(int total) {
        List<ToneCount> allCombinations = generateAllCombinations(total);
        toneCountMap.clear();
        for (ToneCount toneCount : allCombinations) {
            toneCountMap.put(toneCount, 0);
        }
        return allCombinations;
    }
}
