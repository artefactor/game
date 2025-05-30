package com.branka;

import static com.branka.BrankaJsonMapper.writeToFile;
import static com.branka.CombinationChecker.sortString;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

public class SubstringMerger {

    public static void main(String[] args) throws IOException {
        //        добавить_наречие_и_степень("substrings_merged_verbs.txt");
        //        соединить_обстоятельства("substrings_merged_adverbs.txt");
        соединить_глаголы_и_обстоятельства("substrings_full.txt");
        // 236 425
    }

    private static void соединить_глаголы_и_обстоятельства(String filename) throws IOException {
        List<String> verbsList = getCleanedListFull("substrings_merged_verbs.txt");
        List<String> adverbsList = getCleanedListFull("substrings_merged_adverbs.txt");
        var resultList = new LinkedList<>(verbsList);

        for (String s1 : verbsList) {
            for (String s2 : adverbsList) {
                resultList.add(s1 + s2);
            }
        }

        finalRemove(resultList, 6);
        writeListToFile(resultList, filename);
    }

    private static void соединить_обстоятельства(String filename) throws IOException {
        List<String> cleanedList1 = getCleanedListFull("substrings_adverbs1.txt");
        List<String> cleanedList2 = getCleanedListFull("substrings_adverbs2.txt");
        List<String> cleanedList3 = getCleanedListFull("substrings_adverbs3.txt");
        List<String> cleanedList4 = getCleanedListFull("substrings_adverbs4.txt");

        var all = List.of(cleanedList1, cleanedList2, cleanedList3, cleanedList4);
        var resultList = new LinkedList<String>();

        //  1
        //  2
        //  3
        //  4
        for (var list : all) {
            resultList.addAll(list);
        }

        //  1 2
        //  1 3
        //  1 4
        //  2 3
        //  2 4
        //  3 4
        List<Pair<Integer, Integer>> pairs = List.of(
            Pair.of(1, 2),
            Pair.of(1, 3),
            Pair.of(1, 4),
            Pair.of(2, 3),
            Pair.of(2, 4),
            Pair.of(3, 4)
        );
        for (var pair : pairs) {
            List<String> list1 = all.get(pair.getLeft() - 1);
            List<String> list2 = all.get(pair.getRight() - 1);
            for (String s1 : list1) {
                for (String s2 : list2) {
                    resultList.add(s1 + s2);
                }
            }
        }

        //  1 2 3
        //  1 2 4
        //  1 3 4
        //  2 3 4
        List<Triple<Integer, Integer, Integer>> triples = List.of(
            Triple.of(1, 2, 3),
            Triple.of(1, 2, 4),
            Triple.of(1, 3, 4),
            Triple.of(2, 3, 4)
        );
        for (var triple : triples) {
            List<String> list1 = all.get(triple.getLeft() - 1);
            List<String> list2 = all.get(triple.getMiddle() - 1);
            List<String> list3 = all.get(triple.getRight() - 1);
            for (String s1 : list1) {
                for (String s2 : list2) {
                    for (String s3 : list3) {
                        resultList.add(s1 + s2 + s3);
                    }
                }
            }
        }
        //  1 2 3 4
        List<String> list1 = all.get(0);
        List<String> list2 = all.get(1);
        List<String> list3 = all.get(2);
        List<String> list4 = all.get(3);
        for (String s1 : list1) {
            for (String s2 : list2) {
                for (String s3 : list3) {
                    for (String s4 : list4) {
                        resultList.add(s1 + s2 + s3 + s4);
                    }
                }
            }
        }

        finalRemove(resultList, 6);
        writeListToFile(resultList, filename);
    }

    private static void finalRemove(LinkedList<String> resultList, int maxLength) {
        for (Iterator<String> iterator = resultList.iterator(); iterator.hasNext(); ) {
            String s = iterator.next();
            // clean XXX
            if (isCharRepeatedShort(s, 'X')) {
                iterator.remove();
                // clean length
            } else if (s.length() > maxLength) {
                iterator.remove();
            }
        }
    }

    public static boolean isCharRepeatedShort(String str, char target) {
        return str.indexOf(target) != str.lastIndexOf(target);
    }

    private static void добавить_наречие_и_степень(String filename) throws IOException {
        // read substrings and plus наречие и степень
        List<String> cleanedList = getCleanedList("substrings.txt");
        var resultList = new ArrayList<>(cleanedList);
        resultList.add(0, "-- initial");
        resultList.add("-- наречие");
        for (String substring : cleanedList) {
            if (substring.startsWith("--")) {
                resultList.add(substring);
            } else {
                resultList.add(sortString('Д' + substring));
            }
        }
        resultList.add("-- наречие и степень");

        for (String substring : cleanedList) {
            if (substring.startsWith("--")) {
                resultList.add(substring);
            } else {
                resultList.add(sortString("" + '*' + 'Д' + substring));
            }
        }

        writeListToFile(resultList, filename);
    }

    private static void writeListToFile(List<String> resultList, String filename) {
        var resourceDirectory = ResourceDirectory.get_RESOURCE_DIRECTORY();
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        var file = new File(absolutePath, filename);

        writeToFile(file, resultList);
        System.out.println("wrote " + resultList.size() + " to  " + filename);
    }

    private static ArrayList<String> getCleanedListFull(String filename) throws IOException {
        var substrings = CombinationChecker.loadSubstringsFromFile(filename);
        var cleanedList = new ArrayList<String>();
        for (String substring : substrings) {
            if (!substring.startsWith("--")) {
                cleanedList.add(substring);
            }
        }
        return cleanedList;
    }

    private static ArrayList<String> getCleanedList(String filename) throws IOException {
        var substrings = CombinationChecker.loadSubstringsFromFile(filename);
        boolean comments = true;
        var cleanedList = new ArrayList<String>();
        for (String substring : substrings) {
            if (comments && !substring.startsWith("--")) {
                comments = false;
            }
            if (!comments) {
                cleanedList.add(substring);
            }
        }
        return cleanedList;
    }
}
