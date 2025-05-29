package com.branka;

import static com.branka.CombinationChecker.printCountMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CombinationChecker1 {

    // Класс для представления карточки
    static class Card {

        char letter;

        Card(char letter) {
            this.letter = letter;
        }

        @Override
        public String toString() {
            return String.valueOf(letter);
        }
    }

    /**
     * Метод для подсчета количества валидных комбинаций.
     *
     * @param deck Список из 50 карточек.
     * @param K    Размер комбинации (в данном случае 6).
     * @return Количество комбинаций, прошедших проверку.
     */
    public static long countValidCombinations(List<Card> deck, int K) {
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
            if (checkCombination(currentCombination)) {
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
     * @return true, если комбинация валидна, иначе false.
     */
    private static boolean checkCombination(List<Character> combination) {
        return CombinationChecker.checkCombination(combination);
    }

    public static void main(String[] args) {
        int N = 7;
        List<Card> deck = initializeDeck(N);
        int K = 6; // Размер комбинации
        System.out.println("Начало подсчета валидных комбинаций...");
        long startTime = System.currentTimeMillis();
        long validCombinations = countValidCombinations(deck, K);
        long endTime = System.currentTimeMillis();
        double elapsedSeconds = (endTime - startTime) / 1000.0;
        System.out.println("Подсчет завершен.");

        long totalCombinations = combination(N, K);
        System.out.println("Всего комбинаций: " + totalCombinations);
        System.out.println("Валидных комбинаций: " + validCombinations);
        System.out.printf("Время выполнения: %.2f секунд%n", elapsedSeconds);

        printCountMap();
    }

    /**
     * Метод для инициализации колоды из карточек. Вы можете настроить буквы по своему усмотрению.
     *
     * @return Список из  карточек.
     */
    private static List<Card> initializeDeck(int count) {
        List<Card> deck = new ArrayList<>();
        // Пример: добавим 50 карточек с буквами от 'A' до 'Z' и далее повторим
        char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for (int i = 0; i < count; i++) {
            char letter = letters[i % letters.length];
            deck.add(new Card(letter));
        }
        return deck;
    }
}
