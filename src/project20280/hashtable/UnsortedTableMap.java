package project20280.hashtable;

import project20280.interfaces.AbstractMap;
import project20280.interfaces.Entry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * An implementation of a map using an unsorted table.
 */
public class UnsortedTableMap<K, V> extends AbstractMap<K, V> {

    private final ArrayList<MapEntry<K, V>> table = new ArrayList<>();

    public UnsortedTableMap() {}

    /**
     * Returns the index of an entry with equal key, or -1 if none found.
     */
    private int findIndex(K key) {
        int n = table.size();
        for (int i = 0; i < n; i++) {
            K k = table.get(i).getKey();
            if (key == null ? k == null : key.equals(k)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public V get(K key) {
        int j = findIndex(key);
        if (j == -1) {
            return null; // FIXED
        }
        return table.get(j).getValue();
    }

    @Override
    public V put(K key, V value) {
        int j = findIndex(key);
        if (j == -1) {
            table.add(new MapEntry<>(key, value));
            return null;
        } else {
            return table.get(j).setValue(value);
        }
    }

    @Override
    public V remove(K key) {
        int j = findIndex(key);
        int n = table.size();
        if (j == -1) {
            return null; // FIXED
        }
        V value = table.get(j).getValue();
        if (j != n - 1) {
            table.set(j, table.get(n - 1));
        }
        table.remove(n - 1);
        return value;
    }

    private class EntryIterator implements Iterator<Entry<K, V>> {
        private int j = 0;

        public boolean hasNext() {
            return j < table.size();
        }

        public Entry<K, V> next() {
            if (j == table.size()) {
                throw new NoSuchElementException("No further entries");
            }
            return table.get(j++);
        }

        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }
    }

    private class EntryIterable implements Iterable<Entry<K, V>> {
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }
    }

    @Override
    public Iterable<Entry<K, V>> entrySet() {
        return new EntryIterable();
    }

    public String toString() {
        return table.toString();
    }
}