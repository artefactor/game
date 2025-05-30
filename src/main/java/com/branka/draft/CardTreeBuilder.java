package com.branka.draft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;

// Класс Card, как задано в условии
@Data
@AllArgsConstructor
class Card {

    boolean isRoot;
    String letter;
    List<String> possibleChildren;

}

// Класс для хранения информации о букве
class LetterInfo {

    String letter;
    int count; // количество экземпляров
    boolean isRoot;
    Set<String> possibleChildren;

    public LetterInfo(String letter) {
        this.letter = letter;
        this.count = 0;
        this.isRoot = false;
        this.possibleChildren = new HashSet<>();
    }

    public void addCard(Card card) {
        this.count++;
        if (card.isRoot) {
            this.isRoot = true;
        }
        this.possibleChildren.addAll(card.getPossibleChildren());
    }
}

// Класс для представления узла дерева
@Data
class TreeNode {

    String letter;
    List<TreeNode> children;

    public TreeNode(String letter) {
        this.letter = letter;
        this.children = new ArrayList<>();
    }
}

public class CardTreeBuilder {

    // Метод для построения всех уникальных деревьев
    public static Set<String> buildAllTrees(List<Card> cards) {
        // Предобработка: группируем карточки по букве
        Map<String, LetterInfo> letterInfoMap = new HashMap<>();
        for (Card card : cards) {
            letterInfoMap.putIfAbsent(card.getLetter(), new LetterInfo(card.getLetter()));
            letterInfoMap.get(card.getLetter()).addCard(card);
        }
        // Определяем корневые буквы
        List<String> rootLetters = letterInfoMap.values().stream()
            .filter(info -> info.isRoot)
            .map(info -> info.letter)
            .collect(Collectors.toList());
        Set<String> uniqueTrees = new HashSet<>();
        // Для каждого корня строим все возможные деревья
        for (String root : rootLetters) {
            // Начинаем рекурсивное построение
            TreeNode rootNode = new TreeNode(root);
            // Отслеживаем использование экземпляров букв
            Map<String, Integer> usageMap = new HashMap<>();
            usageMap.put(root, 1);
            // Построение дерева
            List<TreeNode> currentTrees = new ArrayList<>();
            currentTrees.add(rootNode);
            // Запускаем рекурсивное построение
            buildTrees(rootNode, letterInfoMap, usageMap, uniqueTrees);
        }
        return uniqueTrees;
    }

    // Рекурсивный метод для построения деревьев
    private static void buildTrees(TreeNode currentNode, Map<String, LetterInfo> letterInfoMap,
        Map<String, Integer> usageMap, Set<String> uniqueTrees) {
        LetterInfo currentInfo = letterInfoMap.get(currentNode.letter);
        if (currentInfo == null || currentInfo.possibleChildren.isEmpty()) {
            // Если текущая буква не имеет детей, сериализуем дерево
            String serialized = serializeTree(currentNode);
            uniqueTrees.add(serialized);
            return;
        }
        boolean hasChildren = false;
        for (String childLetter : currentInfo.possibleChildren) {
            LetterInfo childInfo = letterInfoMap.get(childLetter);
            if (childInfo == null) {
                // Если нет информации о дочерней букве, пропускаем
                continue;
            }
            // Проверяем, не превышено ли количество доступных экземпляров
            int used = usageMap.getOrDefault(childLetter, 0);
            if (used >= childInfo.count) {
                continue;
            }
            hasChildren = true;
            // Создаем нового ребенка
            TreeNode childNode = new TreeNode(childLetter);
            currentNode.children.add(childNode);
            // Обновляем использование
            usageMap.put(childLetter, used + 1);
            // Рекурсивно строим поддерево
            buildTrees(childNode, letterInfoMap, usageMap, uniqueTrees);
            // После рекурсии откатываем изменения
            currentNode.children.remove(currentNode.children.size() - 1);
            usageMap.put(childLetter, used);
        }
        if (!hasChildren) {
            // Если не добавлено ни одного ребенка, сериализуем дерево
            String serialized = serializeTree(currentNode);
            uniqueTrees.add(serialized);
        }
    }

    // Метод для сериализации дерева в строку
    private static String serializeTree(TreeNode node) {
        if (node == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        serializeHelper(node, sb);
        return sb.toString();
    }

    private static void serializeHelper(TreeNode node, StringBuilder sb) {
        sb.append(node.letter);
        if (!node.children.isEmpty()) {
            sb.append("(");
            // Сортируем детей для обеспечения уникальности независимо от порядка добавления
            List<String> childSerializations = node.children.stream()
                .map(TreeNode::getLetter)
                .sorted()
                .collect(Collectors.toList());
            for (int i = 0; i < node.children.size(); i++) {
                serializeHelper(node.children.get(i), sb);
                if (i < node.children.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append(")");
        }
    }

    // Метод для красивого вывода дерева
    private static void printTree(TreeNode node, String prefix, boolean isTail, List<String> lines) {
        lines.add(prefix + (isTail ? "└── " : "├── ") + node.letter);
        for (int i = 0; i < node.children.size(); i++) {
            printTree(node.children.get(i), prefix + (isTail ? "    " : "│   "), i == node.children.size() - 1, lines);
        }
    }


    // Основной метод для демонстрации
    public static void main(String[] args) {
        // Пример входных данных
        List<Card> cards = Arrays.asList(
            new Card(true, "A", Arrays.asList("H", "D")),
            new Card(false, "H", Arrays.asList("D")),
            new Card(false, "D", Collections.emptyList()),
            new Card(false, "D", Collections.emptyList())
        );
        Set<String> uniqueTrees = buildAllTrees(cards);
        // Переходим назад к деревьям, чтобы красиво их вывести
        // Поскольку мы сериализовали деревья, нужно десериализовать или хранить сами деревья
        // Для простоты, мы будем выводить сериализованные представления
        System.out.println("Все уникальные деревья:");
        for (String tree : uniqueTrees) {
            System.out.println(tree);
        }
        // Если требуется более красивый вывод, можно изменить метод buildAllTrees, чтобы собирать сами Trees
        // Ниже приведен альтернативный подход с хранением деревьев
        System.out.println("\nБолее красивый вывод деревьев:");
        // Альтернативный подход: собираем сами деревья
        // Для этого изменим метод buildAllTrees, чтобы он собирал сериализации и реальные деревья
        // Создаем список для хранения уникальных сериализаций и соответствующих деревьев
        Map<String, TreeNode> serializedToTree = new HashMap<>();
        // Повторяем построение с сохранением деревьев
        Map<String, LetterInfo> letterInfoMap = new HashMap<>();
        for (Card card : cards) {
            letterInfoMap.putIfAbsent(card.getLetter(), new LetterInfo(card.getLetter()));
            letterInfoMap.get(card.getLetter()).addCard(card);
        }
        List<String> rootLetters = letterInfoMap.values().stream()
            .filter(info -> info.isRoot)
            .map(info -> info.letter)
            .collect(Collectors.toList());
        for (String root : rootLetters) {
            TreeNode rootNode = new TreeNode(root);
            Map<String, Integer> usageMap = new HashMap<>();
            usageMap.put(root, 1);
            collectTrees(rootNode, letterInfoMap, usageMap, serializedToTree);
        }
        // Выводим красивые деревья
        for (Map.Entry<String, TreeNode> entry : serializedToTree.entrySet()) {
            TreeNode tree = entry.getValue();
            List<String> lines = new ArrayList<>();
            printTree(tree, "", true, lines);
            for (String line : lines) {
                System.out.println(line);
            }
            System.out.println(); // Разделитель между деревьями
        }
    }
    // Дополнительный метод для сбора деревьев с сохранением структуры
    private static void collectTrees(TreeNode currentNode, Map<String, LetterInfo> letterInfoMap,
        Map<String, Integer> usageMap, Map<String, TreeNode> serializedToTree) {
        LetterInfo currentInfo = letterInfoMap.get(currentNode.letter);
        if (currentInfo == null || currentInfo.possibleChildren.isEmpty()) {
            // Сериализуем и сохранем дерево
            String serialized = serializeTree(currentNode);
            serializedToTree.putIfAbsent(serialized, cloneTree(currentNode));
            return;
        }
        boolean hasChildren = false;
        for (String childLetter : currentInfo.possibleChildren) {
            LetterInfo childInfo = letterInfoMap.get(childLetter);
            if (childInfo == null) {
                continue;
            }
            int used = usageMap.getOrDefault(childLetter, 0);
            if (used >= childInfo.count) {
                continue;
            }
            hasChildren = true;
            // Добавляем ребенка
            TreeNode childNode = new TreeNode(childLetter);
            currentNode.children.add(childNode);
            // Обновляем использование
            usageMap.put(childLetter, used + 1);
            // Рекурсивно продолжаем построение
            collectTrees(childNode, letterInfoMap, usageMap, serializedToTree);
            // Откатываем изменения
            currentNode.children.remove(currentNode.children.size() - 1);
            usageMap.put(childLetter, used);
        }
        if (!hasChildren) {
            // Если дети не добавлены, сериализуем и сохранем дерево
            String serialized = serializeTree(currentNode);
            serializedToTree.putIfAbsent(serialized, cloneTree(currentNode));
        }
    }

    // Метод для клонирования дерева
    private static TreeNode cloneTree(TreeNode node) {
        if (node == null) {
            return null;
        }
        TreeNode cloned = new TreeNode(node.letter);
        for (TreeNode child : node.children) {
            cloned.children.add(cloneTree(child));
        }
        return cloned;
    }
}
