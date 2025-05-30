package com.branka;

import static com.branka.CombinationChecker.choicesCountMap;
import static com.branka.CombinationChecker.printCountMapDividedInPercent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainCombinationIterator {

    public static void main(String[] args) throws IOException {
        int N = 8;
        List<Card> deck = initializeDeck(N);
        var substrings = CombinationChecker.loadSubstringsFromFile("substrings.txt");
        CombinationChecker.initializeCountMap(substrings);
        int K = 6; // Размер комбинации
        System.out.println("Начало подсчета валидных комбинаций...");
        long startTime = System.currentTimeMillis();
        long validCombinations = countValidCombinations(deck, K, substrings);
        long endTime = System.currentTimeMillis();
        double elapsedSeconds = (endTime - startTime) / 1000.0;
        System.out.println("Подсчет завершен.");

        long totalCombinations = combination(N, K);
        System.out.println("Всего комбинаций: " + totalCombinations);
        System.out.println("Валидных комбинаций: " + validCombinations);
        System.out.println("Вероятность наличия вариантов комбинаций: ");
        printCountMapDividedInPercent(CombinationChecker.choicesCountMap, totalCombinations);
        var sum = choicesCountMap.values().stream().reduce((a, b) -> new AtomicInteger(a.addAndGet(b.intValue()))).get()
            .longValue();
        assert sum == totalCombinations;
        System.out.println("");
        System.out.printf("Время выполнения: %.2f секунд%n", elapsedSeconds);

        var allCombos = CombinationChecker.substringsCountMap.values().stream().reduce((a, b)-> a+ b).get();
        printCountMapDividedInPercent(CombinationChecker.substringsCountMap, allCombos);
    }

    /**
     * Метод для подсчета количества валидных комбинаций.
     *
     * @param deck       Список из 50 карточек.
     * @param K          Размер комбинации (в данном случае 6).
     * @param substrings
     * @return Количество комбинаций, прошедших проверку.
     */
    public static long countValidCombinations(List<Card> deck, int K, List<String> substrings) {
        long validCount = 0;
        int N = deck.size();
        // Индексы для генерации комбинаций
        int[] indices = new int[K];
        for (int i = 0; i < K; i++) {
            indices[i] = i;
        }
        long total = combination(N, K);
        long processed = 0;
        long lastReport = 0;
        while (indices[0] <= N - K) {
            // Собираем текущую комбинацию
            List<Character> currentCombination = new ArrayList<>();
            for (int index : indices) {
                currentCombination.add(deck.get(index).letter);
            }
            // Сортируем буквы по алфавиту
            Collections.sort(currentCombination);
            // Вызываем функцию проверки
            if (checkCombination(currentCombination, substrings)) {
                validCount++;
            }
            processed++;
            // Отчет каждые 10 миллион комбинаций
            if (processed - lastReport >= 10_000_000) {
                lastReport = processed;
                double progress = (double) processed / total * 100;
                System.out.printf("Обработано комбинаций: %d (%.2f%%)%n", processed, progress);
            }
            // Генерация следующей комбинации
            // Алгоритм генерации следующей комбинации (лексикографический порядок)
            int t = K - 1;
            while (t != -1 && indices[t] == N - K + t) {
                t--;
            }
            if (t == -1) {
                break;
            }
            indices[t]++;
            for (int i = t + 1; i < K; i++) {
                indices[i] = indices[i - 1] + 1;
            }
        }
        return validCount;
    }

    /**
     * Биномиальный коэффициент C(n, k).
     *
     * @param n Общее количество элементов.
     * @param k Размер выборки.
     * @return Значение C(n, k).
     */
    private static long combination(int n, int k) {
        if (k > n) {
            return 0;
        }
        if (k == 0 || k == n) {
            return 1;
        }
        long result = 1;
        for (int i = 1; i <= k; i++) {
            result *= (n - (k - i));
            result /= i;
        }
        return result;
    }

    /**
     * Функция проверки комбинации. Здесь вы реализуете логику проверки.
     *
     * @param combination Отсортированная по алфавиту комбинация букв.
     * @param substrings
     * @return true, если комбинация валидна, иначе false.
     */
    private static boolean checkCombination(List<Character> combination, List<String> substrings) {
        return CombinationChecker.checkCombination(combination, substrings);
    }

    /**
     * Метод для инициализации колоды из карточек. Вы можете настроить буквы по своему усмотрению.
     *
     * @return Список из  карточек.
     */
    private static List<Card> initializeDeck(int count) {
        List<Card> deck = new ArrayList<>();
        // Пример: добавим 50 карточек с буквами от 'A' до 'Z' и далее повторим
        char[] letters = "ABCDDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for (int i = 0; i < count; i++) {
            char letter = letters[i % letters.length];
            deck.add(new Card(letter));
        }
        return deck;
    }
}
