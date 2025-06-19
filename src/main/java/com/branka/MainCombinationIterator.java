package com.branka;

import static com.branka.BrankaJsonMapper.readBrankaDeck;
import static com.branka.CombinationChecker.choicesCountMap;
import static com.branka.CombinationChecker.countCharacters;
import static com.branka.CombinationChecker.printCountMapDividedInPercent;
import static com.branka.CombinationChecker.sortByValues;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.branka.statistic.ToneCount;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MainCombinationIterator {

    public static void main(String[] args) throws IOException {
        //        int N = 8;
        //        List<Card> deck = initializeDeck(N);

        var deck = readBrankaDeck(new ObjectMapper(), "ref_branka_deck5.json");
//        branka_converted_type_deck.json
        int N = deck.size();
        Set<Character> usedLetters = deck.stream().map(WordCard::getGroup).collect(Collectors.toSet());
        var substrings = CombinationChecker.loadSubstringsFromFile("substrings_full.txt").stream()
            .filter(line -> !line.startsWith("--"))
            .filter(line -> line.chars()
                .allMatch(c -> usedLetters.contains((char) c)))
            .map(CombinationChecker::sortString)
            .collect(Collectors.toList());
        // могут повторяться, мы сделаем уникальными
        substrings = new ArrayList<>(new TreeSet<>(substrings));
        assertEquals(substrings.size(), new HashSet<>(substrings).size(), "should be unique");
        System.out.printf("Проверяем %,d уникальных подстрок ...%n" , substrings.size());
        CombinationChecker.initializeCountMap(substrings);
        ToneCount.initToneMap(6);

        int K = 6; // Размер комбинации
        long totalCombinations = combinationCount(N, K);
        System.out.printf("Всего комбинаций из %d по %d: %,d%n", N, K, totalCombinations);

        System.out.println("Начало подсчета валидных комбинаций...");
        long startTime = System.currentTimeMillis();
        long validCombinations = countValidCombinations(deck, K, substrings);
        long endTime = System.currentTimeMillis();
        double elapsedSeconds = (endTime - startTime) / 1000.0;
        System.out.println("Подсчет завершен.");
        System.out.printf("Время выполнения: %.2f секунд%n", elapsedSeconds);
        System.out.println();

        System.out.printf("Всего комбинаций: %,d%n" , totalCombinations);
        System.out.printf("Валидных комбинаций: %,d%n" , validCombinations);
        System.out.println();
        System.out.println("Вероятность наличия вариантов в комбинациях: ");
        printCountMapDividedInPercent(CombinationChecker.choicesCountMap, totalCombinations);
        var sum = choicesCountMap.values().stream().reduce((a, b) -> new AtomicInteger(a.addAndGet(b.intValue()))).get()
            .longValue();
        assertEquals(sum, totalCombinations, "should sum equals");
        System.out.println();

        System.out.println("Вероятность наличия вариантов по тону в комбинациях: ");
        Map<Integer, Map<Set<String>, Long>> choicesToneCountExpandedMap = CombinationChecker.choicesToneCountMap;
        Map<Integer, Long> choicesToneCountMap = new HashMap<>();
        for (Map.Entry<Integer, Map<Set<String>, Long>> entry : choicesToneCountExpandedMap.entrySet()) {
            Integer key = entry.getKey();
            Map<Set<String>, Long> innerMap = entry.getValue();
            long sum1 = 0;
            for (Long value : innerMap.values()) {
                sum1 += value;
            }
            choicesToneCountMap.put(key, sum1);
        }
        printCountMapDividedInPercent(choicesToneCountMap, totalCombinations);
        System.out.println();
        System.out.println("Развернуто:");
        for (Map.Entry<Integer, Map<Set<String>, Long>> entry : choicesToneCountExpandedMap.entrySet()) {
            System.out.println(entry.getKey() + ":");
            Map<Set<String>, Long> t = entry.getValue();
            for (Map.Entry<Set<String>, Long> e : t.entrySet()) {
                System.out.println(" \t\t" + e.getKey() + " -  " + e.getValue());
            }
        }

        var allCombos = CombinationChecker.substringsCountMap.values().stream().reduce(Long::sum).get();
        System.out.printf("Распределение %,d комбинаций: %n", allCombos);
        printCountMapDividedInPercent(sortByValues(CombinationChecker.substringsCountMap), allCombos);
        System.out.printf("%n%nВероятность розыгрыша комбинации из %,d: %n", totalCombinations);
        printCountMapDividedInPercent(sortByValues(CombinationChecker.substringsCountMap), totalCombinations);

        System.out.printf("%n%nВероятность розыгрыша тонов из %,d: %n", totalCombinations);
        var sortedToneMap  = ToneCount.sort(ToneCount.toneCountMap);
        printCountMapDividedInPercent(sortedToneMap, totalCombinations);
    }

    /**
     * Метод для подсчета количества валидных комбинаций.
     *
     * @param deck       Список из 50 карточек.
     * @param K          Размер комбинации (в данном случае 6).
     * @param substrings
     * @return Количество комбинаций, прошедших проверку.
     */
    public static long countValidCombinations(List<WordCard> deck, int K, List<String> substrings) {
        Map<String, Map<Character, Integer>> mapSubstring = new HashMap<>();
        for (String substr : substrings) {
            // Подсчитываем частоту символов в подстроке
            mapSubstring.put(substr, countCharacters(substr));
        }

        long validCount = 0;
        int N = deck.size();
        // Индексы для генерации комбинаций
        int[] indices = new int[K];
        for (int i = 0; i < K; i++) {
            indices[i] = i;
        }
        long total = combinationCount(N, K);
        long processed = 0;
        long lastReport = 0;
        while (indices[0] <= N - K) {
            // Собираем текущую комбинацию
            List<Character> currentCardSetCombination = new ArrayList<>();
            List<WordCard> currentWordCardSetCombination = new ArrayList<>();
            for (int index : indices) {
                WordCard wordCard = deck.get(index);
                currentCardSetCombination.add(wordCard.getGroup());
                currentWordCardSetCombination.add(wordCard);
            }
            // Сортируем буквы по алфавиту
            Collections.sort(currentCardSetCombination);
            // Вызываем функцию проверки
            if (checkCombination(currentCardSetCombination, currentWordCardSetCombination, substrings, mapSubstring)) {
                validCount++;
            }
            processed++;
            // Отчет каждые X комбинаций
            var percentil = total / 20;
            if (processed - lastReport >= percentil) {
                lastReport = processed;
                double progress = (double) processed / total * 100;
                System.out.printf("Обработано комбинаций: %,d (%.2f%%)%n", processed, progress);
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
    private static long combinationCount(int n, int k) {
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
     * @param cardSet                       Отсортированная по алфавиту комбинация букв.
     * @param substrings
     * @return true, если комбинация валидна, иначе false.
     */
    private static boolean checkCombination(List<Character> cardSet, List<WordCard> currentWordCardSetCombination, List<String> substrings,
        Map<String, Map<Character, Integer>> mapSubstring) {
        return CombinationChecker.checkCombination(cardSet, currentWordCardSetCombination, substrings, mapSubstring);
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
