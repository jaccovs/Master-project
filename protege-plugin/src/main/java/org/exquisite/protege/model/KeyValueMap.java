package org.exquisite.protege.model;

import java.util.*;

/**
 * A <code>java.util.Map</code> implementation that saves the key/value pairs in two separate lists.
 * Depending on the index of a key this map looks up the mapped value from a the appropriate index from the values list.
 *
 * <p>
 *     This map has been implemented as data structure to resolve
 *     <a href="https://git-ainf.aau.at/interactive-KB-debugging/debugger/issues/69">issue #69</a>.
 * </p>
 * <p>
 *     This map does not supports <code>null</code> values as keys and values and throws an
 *     <code>NullPointerException</code> in such cases.
 * </p>
 *
 * <p>
 *     {@link #putAll(Map)} is <strong>not supported</strong> by this implementation.
 * </p>
 *
 * @author wolfi
 * @see NullPointerException
 */
public class KeyValueMap<K, V> implements Map<K, V> {

    private List<K> keys = new ArrayList<>();

    private List<V> values = new ArrayList<>();

    @Override
    public int size() {
        assert this.keys.size() == this.values.size();

        return keys.size();
    }

    @Override
    public boolean isEmpty() {
        assert this.keys.size() == this.values.size();

        return keys.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        checkParameters(key);
        assert this.keys.size() == this.values.size();

        return keys.contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        checkParameters(value);
        assert this.keys.size() == this.values.size();

        return values.contains(value);
    }

    @Override
    public V get(Object key) {
        checkParameters(key);
        assert this.keys.size() == this.values.size();

        int idx = this.keys.indexOf(key);
        if (idx!=-1) return this.values.get(idx);
        return null;
    }

    @Override
    public V put(K key, V value) {
        checkParameters(key, value);
        assert this.keys.size() == this.values.size();

        int idx = this.keys.indexOf(key);
        if (idx == -1) {
            this.keys.add(key);
            this.values.add(value);
            return null;
        } else {
            final V prevValue = this.values.get(idx);
            this.values.set(idx, value);
            return prevValue;
        }
    }

    @Override
    public V remove(Object key) {
        checkParameters(key);
        assert this.keys.size() == this.values.size();

        int idx = this.keys.indexOf(key);
        if (idx == -1) {
            assert this.keys.size() == this.values.size();

            return null;
        }

        this.keys.remove(idx);
        V prevDebugger = this.values.remove(idx);

        assert this.keys.size() == this.values.size();
        return prevDebugger;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        assert this.keys.size() == this.values.size();

        this.keys.clear();
        this.values.clear();
    }

    @Override
    public Set<K> keySet() {
        assert this.keys.size() == this.values.size();

        return new HashSet<>(this.keys);
    }

    @Override
    public Collection<V> values() {
        assert this.keys.size() == this.values.size();

        return this.values.subList(0,this.values.size());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        assert this.keys.size() == this.values.size();

        Set<Entry<K, V>> s = new HashSet();
        for (int idx = 0; idx < this.keys.size(); idx++) {
            s.add(new KeyValueEntry<>(keys.get(idx), values.get(idx)));
        }
        return s;
    }

    /**
     * Checks if all args are permitted by this map, which means that the arguments are <u>not</u> <code>null</code>.
     *
     * @param args A list of method parameters.
     * @throws NullPointerException if one of the args is <code>null</code>.
     */
    private void checkParameters(Object... args) {
        for (Object arg : args)
            if (arg == null)
                throw new NullPointerException("This map implementation does not permit null keys and values!");
    }

    final class KeyValueEntry<K, V> implements Map.Entry<K, V> {

        private K k;
        private V v;

        public KeyValueEntry(K k, V d) {
            this.k = k;
            this.v = d;
        }

        @Override
        public K getKey() {
            return k;
        }

        @Override
        public V getValue() {
            return v;
        }

        @Override
        public V setValue(V value) {
            final V oldV = v;
            v = value;
            return oldV;
        }
    }
}
