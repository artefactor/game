package com.branka.statistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import com.branka.WordCard.Tone;

public class ToneCombinations {

    public static void main(String[] args) {
        // Пример входных данных
        Map<Character, Integer> charUsedCount = new HashMap<>();
        charUsedCount.put('A', 3);
        charUsedCount.put('B', 3);
        charUsedCount.put('C', 3);
        charUsedCount.put('D', 2);

        Map<Character, Collection<Tone>> charToneMap = new HashMap<>();
        charToneMap.put('A', Arrays.asList(Tone.RED_OR_GREEN, Tone.GREEN, Tone.GREEN));
        charToneMap.put('B', Arrays.asList(Tone.RED, Tone.GREEN, Tone.GREEN, Tone.YELLOW, Tone.NEUTRAL));
        charToneMap.put('C', Arrays.asList(Tone.YELLOW, Tone.YELLOW, Tone.NEUTRAL));
        charToneMap.put('D', Arrays.asList(Tone.RED_OR_GREEN, Tone.RED_OR_GREEN, Tone.NEUTRAL, Tone.RED_OR_YELLOW, Tone.GREEN));

        // Генерация всех комбинаций
        List<Map<Character, List<Tone>>> allCombinations = generateAllToneCombinations(charUsedCount, charToneMap);

        // Вывод результатов
        System.out.println("Total combinations: " + allCombinations.size());
        for (int i = 0; i < allCombinations.size(); i++) {
            Map<Character, List<Tone>> combination = allCombinations.get(i);
            System.out.println((i+1) + ": " + combinationToString(combination));
        }

        System.out.println("\nCount: " + allCombinations.size());
        List<ToneCount> counted =
            countTonesInCombinations(allCombinations).stream().map(ToneCount::toToneCount).collect(Collectors.toList());

        for (int i = 0; i < counted.size(); i++) {
            var d = counted.get(i);
            System.out.println((i+1) + ": " + d);
        }
        Set<String> uniqueCombinationTones = new HashSet<>();
        counted.forEach(d -> uniqueCombinationTones.add(ToneCount.classifySingleCombination(d)));

        System.out.println("\nУникальных вариатов:" + uniqueCombinationTones.size() + "; " + uniqueCombinationTones);
    }

    public static List<Map<Tone, Integer>> countTonesInCombinations(List<Map<Character, List<Tone>>> allCombinations) {
        List<Map<Tone, Integer>> result = new ArrayList<>();

        for (Map<Character, List<Tone>> combination : allCombinations) {
            Map<Tone, Integer> toneCounts = new EnumMap<>(Tone.class);

            // Проходим по всем спискам тонов для каждого символа
            for (List<Tone> tones : combination.values()) {
                for (Tone tone : tones) {
                    toneCounts.put(tone, toneCounts.getOrDefault(tone, 0) + 1);
                }
            }

            result.add(toneCounts);
        }

        return result;
    }

    public static List<Map<Character, List<Tone>>> generateAllToneCombinations(
        Map<Character, Integer> charUsedCount,
        Map<Character, Collection<Tone>> charToneMap) {

        List<Map<Character, List<Tone>>> result = new ArrayList<>();
        result.add(new HashMap<>());

        for (Character c : charUsedCount.keySet()) {
            int count = charUsedCount.get(c);
            List<Tone> availableTones = new ArrayList<>(charToneMap.get(c));

            // Генерируем все возможные уникальные комбинации для текущего символа
            List<List<Tone>> charCombinations = generateUniqueCombinations(availableTones, count);
            charCombinations = mapDoubleTones(charCombinations);

            // Объединяем с предыдущими результатами
            List<Map<Character, List<Tone>>> newResult = new ArrayList<>();
            for (Map<Character, List<Tone>> existing : result) {
                for (List<Tone> toneCombination : charCombinations) {
                    Map<Character, List<Tone>> newMap = new HashMap<>(existing);
                    newMap.put(c, toneCombination);
                    newResult.add(newMap);
                }
            }
            result = newResult;
        }

        return result;
    }

    private static List<List<Tone>> mapDoubleTones(List<List<Tone>> charCombinations) {
        List<List<Tone>> resultCombinations = new ArrayList<>();
        int size = charCombinations.get(0).size();

        Queue<List<Tone>> queue = new LinkedList<>();
        for (List<Tone> charCombination : charCombinations) {
            if (containsDouble(charCombination)) {
                // put in queue
                queue.add(charCombination);
            } else {
                resultCombinations.add(charCombination);
            }
        }

        while (!queue.isEmpty()) {
            List<Tone> poll = queue.poll();
            // find first double and put in queue both options
            for (int i = 0; i < size; i++) {
                Tone[] doubles = poll.get(i).getDoubles();
                if (doubles == null) {
                    continue;
                }
                // put in queue
                var one = new ArrayList<Tone>();
                var two = new ArrayList<Tone>();
                for (int j = 0; j < size; j++) {
                    if (i != j) {
                        one.add(poll.get(j));
                        two.add(poll.get(j));
                    } else {
                        one.add(doubles[0]);
                        two.add(doubles[1]);
                    }
                }
                if (containsDouble(one)) {
                    // put in queue
                    queue.add(one);
                } else {
                    // if no doubles - put in list
                    resultCombinations.add(one);
                }
                if (containsDouble(two)) {
                    // put in queue
                    queue.add(two);
                } else {
                    // if no doubles - put in list
                    resultCombinations.add(two);
                }
                break;
            }

        }
        for (List<Tone> resultCombination : resultCombinations) {
            Collections.sort(resultCombination);
        }
        List<List<Tone>> t1 = new ArrayList<>(new HashSet(resultCombinations));
        return t1;
    }

    private static boolean containsDouble(List<Tone> charCombination) {
        return charCombination.stream().anyMatch(Tone::isDouble);
    }

    private static List<List<Tone>> generateUniqueCombinations(List<Tone> tones, int count) {
        List<List<Tone>> result = new ArrayList<>();
        generateUniqueCombinationsHelper(tones, count, 0, new ArrayList<>(), result);
        return result;
    }

    private static void generateUniqueCombinationsHelper(List<Tone> tones, int count, int start,
        List<Tone> current, List<List<Tone>> result) {
        if (current.size() == count) {
            result.add(new ArrayList<>(current));
            return;
        }

        // Используем Set для отслеживания уже использованных тонов на текущей позиции
        Set<Tone> used = new HashSet<>();
        for (int i = start; i < tones.size(); i++) {
            Tone tone = tones.get(i);
            if (used.contains(tone)) {
                continue;
            }
            used.add(tone);
            current.add(tone);
            generateUniqueCombinationsHelper(tones, count, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    private static String combinationToString(Map<Character, List<Tone>> combination) {
        return combination.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(e -> e.getKey() + ": " + e.getValue())
            .collect(Collectors.joining("; "));
    }
}