package com.branka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
    static final Map<String, Integer> substringsCountMap = new HashMap<>();
    static final Map<Integer, AtomicInteger> choicesCountMap = new HashMap<>();

    /**
     * Инициализирует countMap нулями для каждой подстроки.
     */
    static void initializeCountMap(List<String> substrings) {
        substringsCountMap.clear();
        for (String substr : substrings) {
            substringsCountMap.put(substr, 0);
        }
    }

    static int k = 0;

    /**
     * Проверяет комбинацию и обновляет countMap.
     *
     * @param cardSet                       отсортированный и валидный список символов
     */
    static boolean checkCombination(List<Character> cardSet, List<WordCard> currentWordCardSetCombination, List<String> substrings,
        Map<String, Map<Character, Integer>> mapSubstring) {
        boolean print = !true;
        if (print)        System.out.print(k++ + "\t");
        if (print) {System.out.print(cardSet);}
        // Подсчитываем частоту каждого символа в комбинации
        Map<Character, Integer> combinationCount = countCharacters(cardSet);
        int choicesCount = 0;

        boolean PRINT_FORMED = false;
        List<String> formed = PRINT_FORMED ? new ArrayList<>() : null;
        // Обрабатываем каждую подстроку
        for (String substr : substrings) {
            if (checkIfCanForm(mapSubstring, print, combinationCount, substr)) {
                // TODO здесь нужно проверить окрасы фраз. И какого тона фразы можно сложить. И какой длины. Есть ли выбор
                choicesCount++;
                if (PRINT_FORMED) {
                    formed.add(substr);
                }
            }
        }
        // сколько вариантов можно сделать в данной комбинации (нароре) карт
        var res = choicesCountMap.getOrDefault(choicesCount, new AtomicInteger(0));
        choicesCountMap.put(choicesCount, res);
        res.incrementAndGet();

        if (PRINT_FORMED) {
            if (choicesCount > 27) {
                System.out.println("=====");
                System.out.println(cardSet);
                for (var f : formed) {
                    System.out.println(f);
                }
                System.out.println("=====");
            }
        }
        boolean anyOption = choicesCount > 0;
        if (print)        System.out.print(";  choices:" + choicesCount + " = " + anyOption);
        if (print)        System.out.println();
        return anyOption;
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
            if (print) System.out.printf(" '%s':%s", substr, ways);
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

    static void printCountMapDividedInPercent(Map<?, ? extends Number> substringsCountMap1, long total) {
        substringsCountMap1.forEach(
            (key, value) -> {
                if (value.longValue() > 0) {
                    System.out.println(
                        key + " -\t" + value + "\t ( " + (value.longValue() * 100 / total) + " % )");
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
            substringsCountMap.put(substr, 0);
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
            substringsCountMap.put(substr, 0);
        }
    }
}