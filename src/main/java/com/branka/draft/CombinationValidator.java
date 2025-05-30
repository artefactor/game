package com.branka.draft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class CombinationValidator {

    // Класс для представления карточки
    static class Card {

        String letter;
        List<String> canBeComposedFrom; // Буквы, из которых можно составить эту букву

        Card(String letter, List<String> canBeComposedFrom) {
            this.letter = letter;
            this.canBeComposedFrom = new ArrayList<>();
            this.canBeComposedFrom.addAll(canBeComposedFrom);
        }
    }

    // Список подстрок, которые нужно проверить
    private static final List<String> SUBSTRINGS = Arrays.asList("AHB", "A", "AH");
    // Колода карточек
    private static List<Card> deck = new ArrayList<>();
    // Карта для быстрого доступа к карточкам по букве
    private static Map<String, Card> letterToCardMap = new HashMap<>();
    // Адъективный список для представления графа связей
    private static Map<String, List<String>> adjacencyList = new HashMap<>();

    // Метод инициализации колоды и построения графа
    private static void initializeDeckAndAdjacencyList() {
        // Пример инициализации. Вам необходимо заполнить все 50 карточек согласно вашим данным.
        deck.add(new Card("A", Arrays.asList("B", "D", "L")));
        deck.add(new Card("B", Arrays.asList("H")));
        deck.add(new Card("H", Arrays.asList("D")));
        deck.add(new Card("D", Collections.emptyList()));
        deck.add(new Card("L", Collections.emptyList()));
        // ... Добавьте остальные 45 карточек
        // Заполняем карту букв к карточкам
        for (Card card : deck) {
            letterToCardMap.put(card.letter, card);
        }
        // Строим адъективный список:
        // Если карточка 'B' может быть составлена из 'A', то 'A' может соединять 'B'.
        for (Card card : deck) {
            for (String sourceLetter : card.canBeComposedFrom) {

                adjacencyList.computeIfAbsent(sourceLetter, k -> new ArrayList<>()).add(card.letter);
            }
        }
    }

    /**
     * Метод для проверки, содержит ли комбинация хотя бы одну из заданных подстрок.
     *
     * @param combination Список символов, представляющих комбинацию букв.
     * @return true, если хотя бы одна подстрока валидна, иначе false.
     */
    private static boolean checkCombination(List<Character> combination) {
        // Конвертируем комбинацию в сет строк для быстрого поиска
        Set<String> combinationSet = new HashSet<>();
        for (char c : combination) {
            if (Character.isLetter(c)) {
                combinationSet.add(String.valueOf(Character.toUpperCase(c)));
            }
        }
        for (String substring : SUBSTRINGS) {
            // Проверяем, содержатся ли все буквы подстроки в комбинации
            boolean allLettersPresent = true;
            for (char c : substring.toCharArray()) {
                if (!combinationSet.contains(String.valueOf(Character.toUpperCase(c)))) {
                    allLettersPresent = false;
                    break;
                }
            }
            if (!allLettersPresent) {
                continue; // Переходим к следующей подстроке
            }
            // Проверяем, можно ли построить цепочку из подстроки, начиная с 'A'
            // Выполняем BFS от 'A' и ищем все достижимые буквы
            if (!combinationSet.contains("A")) {
                continue; // Невозможно начать без 'A'
            }
            Set<String> visited = new HashSet<>();
            Queue<String> queue = new LinkedList<>();
            queue.add("A");
            visited.add("A");
            while (!queue.isEmpty()) {
                String current = queue.poll();
                List<String> neighbors = adjacencyList.getOrDefault(current, Collections.emptyList());
                for (String neighbor : neighbors) {
                    if (combinationSet.contains(neighbor) && !visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
            // Проверяем, все ли буквы подстроки были достигнуты
            boolean allReachable = true;
            for (char c : substring.toCharArray()) {
                String upperC = String.valueOf(Character.toUpperCase(c));
                if (!visited.contains(upperC)) {
                    allReachable = false;
                    break;
                }
            }
            if (allReachable) {
                return true; // Найдена валидная подстрока
            }
        }
        return false; // Ни одна из подстрок невалидна для данной комбинации
    }

    // Пример использования
    public static void main(String[] args) {
        // Инициализируем колоду и граф
        initializeDeckAndAdjacencyList();
        // Примеры комбинаций для проверки
        List<Character> combination1 = Arrays.asList('A', 'H', 'B'); // Должно вернуть true для "AHB"
        List<Character> combination2 = Arrays.asList('A', 'B', 'C'); // Должно вернуть false
        List<Character> combination3 = Arrays.asList('A', 'B', 'H', 'D', 'L'); // Должно вернуть true для "AHB" и "A"
        List<Character> combination4 = Arrays.asList('C', 'D', 'E', 'F'); // Должно вернуть false
        // Проверяем комбинации
        System.out.println("Combination1 is valid: " + checkCombination(combination1)); // true
        System.out.println("Combination2 is valid: " + checkCombination(combination2)); // false
        System.out.println("Combination3 is valid: " + checkCombination(combination3)); // true
        System.out.println("Combination4 is valid: " + checkCombination(combination4)); // false
    }
}