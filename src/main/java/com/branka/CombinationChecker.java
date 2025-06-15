package com.branka;

import static com.branka.statistic.ToneCombinations.countTonesInCombinations;
import static com.branka.statistic.ToneCombinations.generateAllToneCombinations;
import static com.branka.statistic.ToneCount.classifySingleCombination;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.branka.statistic.ToneCount;
import lombok.Data;

public class CombinationChecker {

    /**
     * Загружает подстроки из файла.
     *
     * @param filename имя файла с подстроками
     * @throws IOException если файл не найден или возникает ошибка чтения
     */
    static List<String> loadSubstringsFromFile(String filename) throws IOException {
        var resourceDirectory = ResourceDirectory.get_RESOURCE_DIRECTORY();
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        var file = new File(absolutePath, filename);
        List<String> substrings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    // Убедимся, что подстрока отсортирована
                    String sorted = sortString(line);
                    substrings.add(sorted);
                }
            }
        }
        return substrings;
    }

    /**
     * Сортирует символы строки в алфавитном порядке.
     *
     * @param s исходная строка
     * @return отсортированная строка
     */
    static String sortString(String s) {
        if (s.startsWith("--")) {
            return s;
        }
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    // Карта для подсчёта количества способов для каждой подстроки
    static final Map<String, Long> substringsCountMap = new HashMap<>();
    static final Map<Integer, AtomicInteger> choicesCountMap = new HashMap<>();
    static final Map<Integer, Long> choicesToneCountMap = new HashMap<>();

    /**
     * Инициализирует countMap нулями для каждой подстроки.
     */
    static void initializeCountMap(List<String> substrings) {
        substringsCountMap.clear();
        for (String substr : substrings) {
            substringsCountMap.put(substr, 0L);
        }
        assertEquals(substrings.size(), substringsCountMap.size(), "should be the same");
    }

    static int k = 0;

    static boolean PRINT_COMBINATION = !true;
    static boolean PRINT_TONES = !true;
    static boolean PRINT_FORMED = !true;

    /**
     * Проверяет комбинацию и обновляет countMap.
     *
     * @param cardSet отсортированный и валидный список символов
     */
    static boolean checkCombination(List<Character> cardSet, List<WordCard> currentWordCardSetCombination,
        List<String> substrings,
        Map<String, Map<Character, Integer>> mapSubstring) {

        if (PRINT_COMBINATION) {
            System.out.print("№ " + k++ + ".\t");
        }
        if (PRINT_COMBINATION) {
            System.out.print(cardSet);
        }
        // Подсчитываем частоту каждого символа в комбинации
        Map<Character, Integer> combinationCount = countCharacters(cardSet);
        int choicesCount = 0;

        var tones = new HashSet<ToneCount>();

        List<String> formed = PRINT_FORMED ? new ArrayList<>() : null;
        // Обрабатываем каждую подстроку
        for (String substr : substrings) {
            if (checkIfCanForm(mapSubstring, PRINT_COMBINATION, combinationCount, substr)) {
                choicesCount++;
                if (PRINT_FORMED) {
                    formed.add(substr);
                }
                var options = findOptions(currentWordCardSetCombination, mapSubstring.get(substr));
                tones.addAll(options);
            }
        }
        // сколько вариантов можно сделать в данной комбинации (наборе) карт
        var res = choicesCountMap.getOrDefault(choicesCount, new AtomicInteger(0));
        choicesCountMap.put(choicesCount, res);
        res.incrementAndGet();

        // сколько вариантов по тонам можно сделать в данной комбинации (наборе) карт
        for (ToneCount tone : tones) {
            var resTone = ToneCount.toneCountMap.getOrDefault(tone, 0);
            ToneCount.toneCountMap.put(tone, ++resTone);
        }

        Set<String> uniqueCombinationTones = new HashSet<>();
        tones.forEach(d -> uniqueCombinationTones.add(classifySingleCombination(d)));
        int tonesOptions = uniqueCombinationTones.size();
        Long comboToneCount = choicesToneCountMap.getOrDefault(tonesOptions, 0L);
        choicesToneCountMap.put(tonesOptions, ++ comboToneCount);

        boolean anyOption = choicesCount > 0;
        if (PRINT_COMBINATION) {
            System.out.print(";  choices:" + choicesCount + " = " + anyOption);
        }
        if (PRINT_FORMED) {
            if (choicesCount > 9) {
                System.out.println();
                System.out.println("\t=================================");
                if (!PRINT_COMBINATION) {
                    System.out.println("\t\t\t" + cardSet);
                }
                for (var f : formed) {
                    System.out.println("\t-\t\t" + f);
                }
                System.out.print("\t=================================");
            }
        }
        if (PRINT_COMBINATION) {
            System.out.println();
        }
        return anyOption;
    }

    private static Collection<ToneCount> findOptions(List<WordCard> currentWordCardSetCombination,
        Map<Character, Integer> charUsedCount) {
        Map<Character, Collection<WordCard.Tone>> charToneMap = new HashMap<>(charUsedCount.size());
        if (PRINT_TONES) {
            System.out.println();
            for (WordCard wordCard : currentWordCardSetCombination) {
                System.out.print(wordCard.getGroup() + ": " + wordCard.getTone() + "; ");
            }
        }
        if (PRINT_TONES) {
            System.out.println();
            System.out.println(charUsedCount);
        }
        for (var key : charUsedCount.keySet()) {
            if (charUsedCount.get(key) == 1) {
                charToneMap.put(key, new HashSet<>());
            } else {
                charToneMap.put(key, new ArrayList<>());
            }
        }
        for (var card : currentWordCardSetCombination) {
            if (charUsedCount.containsKey(card.getGroup())) {
                charToneMap.get(card.getGroup()).add(card.getTone());
            }
        }
        List<Map<Character, List<WordCard.Tone>>> allCombinations =
            generateAllToneCombinations(charUsedCount, charToneMap);
        Collection<ToneCount> counted =
            countTonesInCombinations(allCombinations).stream().map(ToneCount::toToneCount).collect(Collectors.toSet());
        if (PRINT_TONES) {
            System.out.println("Count: " + allCombinations.size());
            int i = 0;
            for (ToneCount toneCount : counted) {
                i++;
                System.out.println(i + ": " + toneCount + " - " + classifySingleCombination(toneCount));
            }
            Set<String> uniqueCombinationTones = new HashSet<>();
            counted.forEach(d -> uniqueCombinationTones.add(classifySingleCombination(d)));
            System.out.println("Уникальных вариатов:" + uniqueCombinationTones.size() + ": " + uniqueCombinationTones
                + "\n");
        }
        return counted;
    }

    @Data
    static class UniqueCardTone {

        private final Character group;
        private final WordCard.Tone tone;
    }

    private static boolean checkIfCanForm(Map<String, Map<Character, Integer>> mapSubstring, boolean print,
        Map<Character, Integer> combinationCount, String substr) {
        // Подсчитываем частоту символов в подстроке
        var substrCount = mapSubstring.get(substr);
        // Проверяем, можно ли составить подстроку из комбинации
        for (var entry : substrCount.entrySet()) {
            char ch = entry.getKey();
            int required = entry.getValue();
            int available = combinationCount.getOrDefault(ch, 0);
            if (available < required) {
                return false;
            }
        }
        boolean NEED_CACL_WAYS = false;
        if (NEED_CACL_WAYS) {
            // Вычисляем количество способов
            int ways = 1;
            for (var entry : substrCount.entrySet()) {
                char ch = entry.getKey();
                int required = entry.getValue();
                int available = combinationCount.getOrDefault(ch, 0);
                ways *= combinationCount(available, required);
            }
            if (print) {
                System.out.printf(" '%s':%s", substr, ways);
            }
        }
        // + ways - если каждый вариант считаем в рамках одной комбинации (наборе) карт
        // + 1  - если просто в рамках одной комбинации (встречается или нет)
        substringsCountMap.put(substr, substringsCountMap.get(substr) + 1);
        return true;
    }

    /**
     * Подсчитывает частоту символов в списке символов.
     *
     * @param characters список символов
     * @return карта частот
     */
    private static Map<Character, Integer> countCharacters(List<Character> characters) {
        Map<Character, Integer> countMap = new HashMap<>();
        for (char ch : characters) {
            countMap.put(ch, countMap.getOrDefault(ch, 0) + 1);
        }
        return countMap;
    }

    /**
     * Подсчитывает частоту символов в строке.
     *
     * @param s строка
     * @return карта частот
     */
    static Map<Character, Integer> countCharacters(String s) {
        Map<Character, Integer> countMap = new HashMap<>();
        for (char ch : s.toCharArray()) {
            countMap.put(ch, countMap.getOrDefault(ch, 0) + 1);
        }
        return countMap;
    }

    /**
     * Вычисляет биномиальный коэффициент C(n, k).
     *
     * @param n общее количество
     * @param k количество выбранных
     * @return C(n, k)
     */
    private static long combinationCount(int n, int k) {
        if (k > n || k < 0) {
            return 0;
        }
        if (k == 0 || k == n) {
            return 1;
        }
        k = Math.min(k, n - k); // Оптимизация
        long result = 1;
        for (int i = 1; i <= k; i++) {
            result = result * (n - (k - i)) / i;
        }
        return result;
    }

    // Метод для отображения текущего состояния countMap
    static void printCountMap(Map substringsCountMap1) {
        substringsCountMap1.forEach((key, value) -> System.out.println(key + " - " + value));
    }

    static Map<?, ? extends Number> sortByValues(Map<String, Long> unsortedMap) {
        Map<String, Long> sortedMap = unsortedMap.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.comparingLong(value -> value)))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (oldVal, newVal) -> oldVal,
                LinkedHashMap::new
            ));
        return sortedMap;
    }

    static void printCountMapDividedInPercent(Map<?, ? extends Number> map, long total) {
        map.forEach(
            (key, value) -> {
                if (value.longValue() > 0) {
                    var printKey = key.toString();
                    String format = "| %-" + (printKey.length() > 12 ? 42 : 15) + "s | %" + 10 + "d | %" + 4 + "d %% |";
                    System.out.printf((format) + "%n", printKey, value.intValue(), (value.longValue() * 100 / total));
                }
            });
    }

    // Пример использования
    public static void main(String[] args) {
        // Предопределённый список подстрок
        final List<String> substrings = Arrays.asList("ABH", "A", "AD", "ADD", "AH");
        HashMap<String, Map<Character, Integer>> mapSubstring = new HashMap<>();

        // Инициализируем карту нулями для каждой подстроки
        for (String substr : substrings) {
            substringsCountMap.put(substr, 0L);
            // Подсчитываем частоту символов в подстроке
            mapSubstring.put(substr, countCharacters(substr));
        }

        // Пример 1
        System.out.println("Пример 1:");
        List<Character> combination1 = Arrays.asList('A', 'C', 'D', 'D', 'E');
        checkCombination(combination1, new ArrayList<>(), substrings, mapSubstring);
        printCountMap(substringsCountMap);
        // Сброс карты для следующего примера
        resetCountMap(substrings);
        System.out.println("=======================");
        // Пример 2
        System.out.println("Пример 2:");
        List<Character> combination2 = Arrays.asList('A', 'D', 'D', 'D', 'E', 'H', 'K');
        checkCombination(combination2, new ArrayList<>(), substrings, mapSubstring);
        printCountMap(substringsCountMap);
    }

    /**
     * Сбрасывает countMap к нулевым значениям.
     */
    private static void resetCountMap(List<String> substrings) {
        for (String substr : substrings) {
            substringsCountMap.put(substr, 0L);
        }
    }
}