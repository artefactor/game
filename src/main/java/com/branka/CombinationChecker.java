package com.branka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    static void loadSubstringsFromFile(String filename) throws IOException {
        Path resourceDirectory = Paths.get("/Users/user/code/game/src/main", "resources"
            , "branka"
        );
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        var file = new File(absolutePath, filename);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    // Убедимся, что подстрока отсортирована
                    String sorted = sortString(line);
                    SUBSTRINGS.add(sorted);
                }
            }
        }
    }

    /**
     * Сортирует символы строки в алфавитном порядке.
     *
     * @param s исходная строка
     * @return отсортированная строка
     */
    private static String sortString(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    // Карта для подсчёта количества способов для каждой подстроки
    static final Map<String, Integer> substringsCountMap = new HashMap<>();
    static final Map<Integer, AtomicInteger> choicesCountMap = new HashMap<>();

    // Предопределённый список подстрок
    private static final List<String> SUBSTRINGS = new ArrayList<>();

    //<editor-fold desc="predefined">
    //    private static final List<String> SUBSTRINGS = Arrays.asList("ABH", "A", "AD", "ADD", "AH");
    //    static {
    //        // Инициализируем карту нулями для каждой подстроки
    //        for (String substr : SUBSTRINGS) {
    //            countMap.put(substr, 0);
    //        }
    //    }
    //</editor-fold>


    /**
     * Инициализирует countMap нулями для каждой подстроки.
     */
    static void initializeCountMap() {
        substringsCountMap.clear();
        for (String substr : SUBSTRINGS) {
            substringsCountMap.put(substr, 0);
        }
    }

    /**
     * Проверяет комбинацию и обновляет countMap.
     *
     * @param combination отсортированный и валидный список символов
     */
    static boolean checkCombination(List<Character> combination) {
        System.out.print(combination);
        // Подсчитываем частоту каждого символа в комбинации
        Map<Character, Integer> combinationCount = countCharacters(combination);
        int choicesCount = 0;
        // Обрабатываем каждую подстроку
        for (String substr : SUBSTRINGS) {
            // Подсчитываем частоту символов в подстроке
            Map<Character, Integer> substrCount = countCharacters(substr);
            // Проверяем, можно ли составить подстроку из комбинации
            boolean canForm = true;
            for (Map.Entry<Character, Integer> entry : substrCount.entrySet()) {
                char ch = entry.getKey();
                int required = entry.getValue();
                int available = combinationCount.getOrDefault(ch, 0);
                if (available < required) {
                    canForm = false;
                    break;
                }
            }
            if (canForm) {
                choicesCount++;
                // Вычисляем количество способов
                long ways = 1;
                for (Map.Entry<Character, Integer> entry : substrCount.entrySet()) {
                    char ch = entry.getKey();
                    int required = entry.getValue();
                    int available = combinationCount.getOrDefault(ch, 0);
                    ways *= combinationCount(available, required);
                }
                System.out.printf(" '%s':%s", substr, ways);
                // Обновляем countMap, приводя к int (предполагается, что значение не превышает Integer.MAX_VALUE)
                substringsCountMap.put(substr, substringsCountMap.get(substr) + (int) ways);
            }
        }
        var res = choicesCountMap.getOrDefault(choicesCount, new AtomicInteger(0));
        choicesCountMap.put(choicesCount, res);
        res.incrementAndGet();

        boolean anyOption = choicesCount > 0;
        System.out.print(";  choices:" + choicesCount + " = " + anyOption);
        System.out.println();
        return anyOption;
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
    private static Map<Character, Integer> countCharacters(String s) {
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
            (key, value) -> System.out.println(key + " -\t" + value + "\t ( " + (value.longValue() * 100 / total) + " % )"));
    }

    // Пример использования
    public static void main(String[] args) {
        // Пример 1
        List<Character> combination1 = Arrays.asList('A', 'C', 'D', 'D', 'E');
        checkCombination(combination1);
        System.out.println("После первой комбинации:");
        printCountMap(substringsCountMap);
        // Сброс карты для следующего примера
        resetCountMap();
        // Пример 2
        List<Character> combination2 = Arrays.asList('A', 'D', 'D', 'D', 'E', 'H', 'K');
        checkCombination(combination2);
        System.out.println("\nПосле второй комбинации:");
        printCountMap(substringsCountMap);
    }

    /**
     * Сбрасывает countMap к нулевым значениям.
     */
    private static void resetCountMap() {
        for (String substr : SUBSTRINGS) {
            substringsCountMap.put(substr, 0);
        }
    }
}