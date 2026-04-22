package project20280.hashtable;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Q6: Collision analysis for different hash functions applied to words.txt
 *
 * A "collision" occurs when two distinct words produce the same raw hash value
 * (before mod-ing into a table). We use a frequency map: if a slot gets N words,
 * it contributes (N - 1) collisions.
 *
 * Parts:
 *   (a) Polynomial accumulation, a = 41
 *   (b) Polynomial accumulation, a = 17
 *   (c) Cyclic shift, shift = 7
 *   (d) Cyclic shift for all shifts 0..31 — find the best
 *   (e) Old Java hash code function
 */
public class HashCollisions {

    // -----------------------------------------------------------------------
    // Hash functions
    // -----------------------------------------------------------------------

    /** Polynomial hash:  h = sum( s[i] * a^(n-i-1) ) */
    public static int hashPoly(String s, int a) {
        long h = 0;
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            h += c * (long) Math.pow(a, n - i - 1);
        }
        // Allow negative via cast — will still be a consistent hash
        return (int) h;
    }

    /** Cyclic shift hash */
    public static int hashCyclic(String s, int shift) {
        int h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = (h << shift) | (h >>> (32 - shift));
            h += s.charAt(i);
        }
        return h;
    }

    /** Old Java hash code (pre-JDK 1.2) */
    public static int hashOldJava(String s) {
        int hash = 0;
        int skip = Math.max(1, s.length() / 8);
        for (int i = 0; i < s.length(); i += skip) {
            hash = (hash * 37) + s.charAt(i);
        }
        return hash;
    }

    // -----------------------------------------------------------------------
    // Collision counter
    // -----------------------------------------------------------------------

    /**
     * Counts collisions: for each raw hash value that appears N times,
     * there are (N - 1) collisions.
     */
    public static long countCollisions(List<String> words, java.util.function.Function<String, Integer> hashFn) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (String w : words) {
            int h = hashFn.apply(w);
            freq.merge(h, 1, Integer::sum);
        }
        long collisions = 0;
        for (int count : freq.values()) {
            if (count > 1) collisions += (count - 1);
        }
        return collisions;
    }

    // -----------------------------------------------------------------------
    // Main
    // -----------------------------------------------------------------------

    public static void main(String[] args) throws FileNotFoundException {
        // Load all words from the dictionary file
        List<String> words = new ArrayList<>();
        File f = new File("src/project20280/hashtable/words.txt"); // adjust path as needed
        Scanner scanner = new Scanner(f);
        while (scanner.hasNext()) {
            words.add(scanner.next().trim());
        }
        scanner.close();

        System.out.printf("Loaded %,d words from words.txt%n%n", words.size());

        // (a) Polynomial, a = 41
        long colA = countCollisions(words, w -> hashPoly(w, 41));
        System.out.printf("(a) Polynomial (a=41):          %,d collisions%n", colA);

        // (b) Polynomial, a = 17
        long colB = countCollisions(words, w -> hashPoly(w, 17));
        System.out.printf("(b) Polynomial (a=17):          %,d collisions%n", colB);

        // (c) Cyclic shift, shift = 7
        long colC = countCollisions(words, w -> hashCyclic(w, 7));
        System.out.printf("(c) Cyclic shift (shift=7):     %,d collisions%n", colC);

        // (d) Cyclic shift for all shifts 0..31
        System.out.println("\n(d) Cyclic shift collision count for shifts 0..31:");
        System.out.printf("    %-8s %s%n", "Shift", "Collisions");
        System.out.println("    " + "----------------------------");

        long bestCount = Long.MAX_VALUE;
        int bestShift = -1;

        for (int shift = 0; shift <= 31; shift++) {
            final int s = shift;
            long col = countCollisions(words, w -> hashCyclic(w, s));
            System.out.printf("    %-8d %,d%n", shift, col);
            if (col < bestCount) {
                bestCount = col;
                bestShift = shift;
            }
        }
        System.out.printf("%n    Best shift value: %d  (%,d collisions)%n", bestShift, bestCount);

        // (e) Old Java hash code
        long colE = countCollisions(words, HashCollisions::hashOldJava);
        System.out.printf("%n(e) Old Java hash code:         %,d collisions%n", colE);

        // Summary comparison
        System.out.println("\nSummary:");
        System.out.printf("Polynomial a=41:    %,d%n", colA);
        System.out.printf("Polynomial a=17:    %,d%n", colB);
        System.out.printf("Cyclic shift=7:     %,d%n", colC);
        System.out.printf("Cyclic best(s=%d):  %,d%n", bestShift, bestCount);
        System.out.printf("Old Java hash:      %,d%n", colE);
    }
}