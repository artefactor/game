package com.branka;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombinationChecker {

    // Предопределённый список подстрок
    private static final List<String> SUBSTRINGS = Arrays.asList("ABH", "A", "AD", "ADD", "AH");
    // Карта для подсчёта количества способов для каждой подстроки
    private static final Map<String, Integer> countMap = new HashMap<>();

    static {
        // Инициализируем карту нулями для каждой подстроки
        for (String substr : SUBSTRINGS) {
            countMap.put(substr, 0);
        }
    }

    /**
     * Проверяет комбинацию и обновляет countMap.
     *
     * @param combination отсортированный и валидный список символов
     */
    static boolean checkCombination(List<Character> combination) {
        // Подсчитываем частоту каждого символа в комбинации
        Map<Character, Integer> combinationCount = countCharacters(combination);
        boolean anyOption = false;
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
                anyOption = true;
                // Вычисляем количество способов
                long ways = 1;
                for (Map.Entry<Character, Integer> entry : substrCount.entrySet()) {
                    char ch = entry.getKey();
                    int required = entry.getValue();
                    int available = combinationCount.getOrDefault(ch, 0);
                    ways *= combinationCount(available, required);
                }
                // Обновляем countMap, приводя к int (предполагается, что значение не превышает Integer.MAX_VALUE)
                countMap.put(substr, countMap.get(substr) + (int) ways);
            }
        }
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
    static void printCountMap() {
        for (String substr : SUBSTRINGS) {
            System.out.println(substr + " - " + countMap.get(substr));
        }
    }

    // Пример использования
    public static void main(String[] args) {
        // Пример 1
        List<Character> combination1 = Arrays.asList('A', 'C', 'D', 'D', 'E');
        checkCombination(combination1);
        System.out.println("После первой комбинации:");
        printCountMap();
        // Сброс карты для следующего примера
        resetCountMap();
        // Пример 2
        List<Character> combination2 = Arrays.asList('A', 'D', 'D', 'D', 'E', 'H', 'K');
        checkCombination(combination2);
        System.out.println("\nПосле второй комбинации:");
        printCountMap();
    }

    /**
     * Сбрасывает countMap к нулевым значениям.
     */
    private static void resetCountMap() {
        for (String substr : SUBSTRINGS) {
            countMap.put(substr, 0);
        }
    }
}