package project20280.hashtable;

import project20280.interfaces.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

/**
 * Q5: Word frequency counter using ChainHashMap.
 * Reads words from sample_text.txt, counts frequency of each word,
 * and reports the top 10 most frequently used words.
 */
public class WordFrequency {

    public static void main(String[] args) throws FileNotFoundException {
        File f = new File("src/project20280/hashtable/sample_text.txt"); // adjust path as needed
        ChainHashMap<String, Integer> counter = new ChainHashMap<>();

        Scanner scanner = new Scanner(f);
        while (scanner.hasNext()) {
            // normalise
            String word = scanner.next().toLowerCase().replaceAll("[^a-z0-9']", "");

            if (word.isEmpty()) continue;

            Integer count = counter.get(word);
            if (count == null) {
                counter.put(word, 1);
            } else {
                counter.put(word, count + 1);
            }
        }
        scanner.close();

        // collect all entries into a list and sort by frequency (descending)
        List<Map.Entry<String, Integer>> entries = new ArrayList<>();
        for (Entry<String, Integer> e : counter.entrySet()) {
            entries.add(new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()));
        }

        // sort entries in descending order by value.
        // (a, b) are two Map.Entry objects from the list.
        // b.getValue().compareTo(a.getValue()) reverses the usual order:
        // normally a.compareTo(b) gives ascending order,
        // but swapping to b.compareTo(a) makes larger values come first.
        entries.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        System.out.println("Top 10 most frequently used words:");
        System.out.printf("%-5s %-20s %s%n", "Rank", "Word", "Count");
        System.out.println("-----------------------------------");
        int limit = Math.min(10, entries.size());
        for (int i = 0; i < limit; i++) {
            System.out.printf("%-5d %-20s %d%n", i + 1, entries.get(i).getKey(), entries.get(i).getValue());
        }
    }
}