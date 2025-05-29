package com.branka.draft;

import static java.lang.Math.round;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import lombok.EqualsAndHashCode;

public class CardChainProbabilityCalculatorV0 {

    @EqualsAndHashCode
    // Класс для представления карты
    static class Card {

        String letter;
        List<String> canBeComposedFrom;

        Card(String letter, List<String> canBeComposedFrom) {
            this.letter = letter;
            this.canBeComposedFrom = canBeComposedFrom;
        }

        @Override
        public String toString() {
            return letter;
        }
    }

    @EqualsAndHashCode
    // Класс для представления цепочки из карт
    static class Chain {

        List<Card> cards;

        Chain(List<Card> cards) {
            this.cards = new ArrayList<>(cards);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cards.size(); i++) {
                sb.append(cards.get(i).letter);
                if (i < cards.size() - 1) {
                    sb.append(" -> ");
                }
            }
            return sb.toString();
        }
    }

    // Метод для генерации всех комбинаций из X карт
    public static List<List<Card>> generateCombinations(List<Card> deck, int X) {
        List<List<Card>> combinations = new ArrayList<>();
        generateCombinationsHelper(deck, X, 0, new ArrayList<>(), combinations);
        return combinations;
    }

    private static void generateCombinationsHelper(List<Card> deck, int X, int start,
        List<Card> current, List<List<Card>> combinations) {
        if (current.size() == X) {
            combinations.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < deck.size(); i++) {
            current.add(deck.get(i));
            generateCombinationsHelper(deck, X, i + 1, current, combinations);
            current.remove(current.size() - 1);
        }
    }

    // Метод для поиска всех цепочек длины K в комбинации карт
    private static LinkedHashSet<Chain> findAllChains(List<Card> cards, int K) {
        LinkedHashSet<Chain> allChains = new LinkedHashSet<>();
        // Построим карту из буквы к карте для быстрого доступа
        Map<String, Card> letterToCard = new HashMap<>();
        for (Card card : cards) {
            letterToCard.put(card.letter, card);
        }
        // Построим граф переходов
        Map<Card, List<Card>> graph = new HashMap<>();
        for (Card card : cards) {
            graph.put(card, new ArrayList<>());
            for (String targetLetter : card.canBeComposedFrom) {
                Card targetCard = letterToCard.get(targetLetter);
                if (targetCard != null) {
                    graph.get(card).add(targetCard);
                }
            }
        }
        // Ищем все цепочки длины K с использованием DFS
        for (Card start : cards) {
            List<Card> path = new ArrayList<>();
            path.add(start);
            dfsFindChains(graph, start, K - 1, path, allChains);
        }
        return allChains;
    }

    // Рекурсивный метод для поиска цепочек
    private static void dfsFindChains(Map<Card, List<Card>> graph, Card current, int stepsRemaining,
        List<Card> path, Set<Chain> allChains) {
        if (stepsRemaining == 0) {
            allChains.add(new Chain(path));
            return;
        }
        for (Card neighbor : graph.get(current)) {
            if (!path.contains(neighbor)) { // Избегаем циклов
                path.add(neighbor);
                dfsFindChains(graph, neighbor, stepsRemaining - 1, path, allChains);
                path.remove(path.size() - 1);
            }
        }
    }

    // Метод для вычисления вероятности и сбора цепочек
    public static void calculateProbabilityAndChains(List<Card> deck, int X, int K) {
        long totalCombinations = combination(deck.size(), X);
        System.out.println("Общее количество комбинаций " + X + " from " + deck.size() + ": " + totalCombinations);
        AtomicLong favorableCombinationCount = new AtomicLong(0);
        AtomicLong totalChainCount = new AtomicLong(0);
        AtomicLong combinationsWithChains = new AtomicLong(0);
        // Генерация и обработка комбинаций
        List<List<Card>> allCombinations = generateCombinations(deck, X);
        long processed = 0;
        long startTime = System.currentTimeMillis();
        for (List<Card> combination : allCombinations) {
            LinkedHashSet<Chain> chains = findAllChains(combination, K);
            if (!chains.isEmpty()) {
                favorableCombinationCount.incrementAndGet();
                combinationsWithChains.incrementAndGet();
                totalChainCount.addAndGet(chains.size());
                // Опционально: Вывод цепочек
                /**/
                System.out.println("Комбинация: " + combination);
                for (Chain chain : chains) {
                    System.out.println("  Цепочка: " + chain);
                }
                /**/
            }
            processed++;
            if (processed % 1000000 == 0) {
                System.out.println("Обработано комбинаций: " + processed);
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Время обработки: " + (endTime - startTime) / 1000.0 + " секунд");
        double probability = round((double) combinationsWithChains.get() / totalCombinations *100);
        double averageChainsPerCombination = combinationsWithChains.get() > 0 ?
            (double) totalChainCount.get() / combinationsWithChains.get() : 0.0;
        System.out.println(
            "Количество комбинаций с как минимум одной цепочкой длины " + K + ": " + combinationsWithChains.get());
        System.out.println("Общее количество цепочек: " + totalChainCount.get());
        System.out.println("Среднее количество цепочек на одну комбинацию: " + averageChainsPerCombination);
        System.out.println("Вероятность составить цепочку длины " + K + ": " + probability + "%");
    }

    // Метод для вычисления биномиального коэффициента C(n, k)
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

    // Пример использования
    public static void main(String[] args) {
        // Инициализация колоды из 70 карт
        List<Card> deck = initializeDeck();
        int X = 4; // Количество случайных карт
        int K = 3; // Требуемая длина цепочки
        calculateProbabilityAndChains(deck, X, K);
    }

    // Метод для инициализации колоды (примерная реализация)
    private static List<Card> initializeDeck() {
        List<Card> deck = new ArrayList<>();
        // Здесь вы должны добавить все 70 карт с соответствующими свойствами
        // Приведу пример с несколькими картами. Вы должны расширить этот список до 70.
        deck.add(new Card("A", Arrays.asList("B", "C")));
        deck.add(new Card("A", Arrays.asList("B", "C")));
        deck.add(new Card("B", Arrays.asList("C", "D")));
        deck.add(new Card("C", Arrays.asList()));
        deck.add(new Card("D", Arrays.asList("E")));
        deck.add(new Card("E", Arrays.asList("F")));
        deck.add(new Card("F", Arrays.asList()));
        return deck;
    }
}
