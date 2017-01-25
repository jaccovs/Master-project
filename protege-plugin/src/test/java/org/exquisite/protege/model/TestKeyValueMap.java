package org.exquisite.protege.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * A test class for org.exquisite.protege.model.KeyValueMap.
 *
 * @author wolfi
 * @see KeyValueMap
 */
public class TestKeyValueMap {

    private final static int START = 1;
    private final static int ELEMENTS = 10;
    private final static int NO_ELEMENT = 11;

    private Map<String, Integer> map = new KeyValueMap<>();

    @Before
    public void setUp() throws Exception {
        assertSize(0);

        for (int i = START; i <= ELEMENTS; i++) {
            final String key = createKey(i);
            assertNull(map.put(key, createValue(key)));
        }

        assertSize(ELEMENTS);
    }

    @After
    public void tearDown() throws Exception {
        map.clear();
        assertSize(0);
    }

    @Test
    public void testSize() throws Exception {
        assertSize(ELEMENTS);
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertFalse(map.isEmpty());
    }

    @Test
    public void testContainsKey() throws Exception {
        try {
            map.containsKey(null);
            fail();
        } catch (NullPointerException ex) {
            // exception expected - null arguments are not supported
        }

        for (int i = START; i <= ELEMENTS; i++) {
            assertTrue(map.containsKey(createKey(i)));
        }
        assertFalse(map.containsKey(createKey(NO_ELEMENT)));
    }

    @Test
    public void testContainsValue() throws Exception {
        try {
            map.containsValue(null);
            fail();
        } catch (NullPointerException ex) {
            // exception expected - null arguments are not supported
        }

        for (int i = START; i <= ELEMENTS; i++) {
            assertTrue(map.containsValue(createValue(createKey(i))));
        }
        assertFalse(map.containsValue(createValue(createKey(NO_ELEMENT))));
    }

    @Test
    public void testGet() throws Exception {
        try {
            map.get(null);
            fail();
        } catch (NullPointerException ex) {
            // exception expected - null arguments are not supported
        }

        for (int i = START; i <= ELEMENTS; i++) {
            assertNotNull(map.get(createKey(i)));
        }
        assertNull(map.get(createKey(NO_ELEMENT)));
    }

    @Test
    public void testPut() throws Exception {
        try {
            map.put(null, null);
            fail();
        } catch (NullPointerException ex) {
            // exception expected - null arguments are not supported
        }

        assertSize(ELEMENTS);
        for (int i = START; i <= ELEMENTS; i++) {
            final String key = createKey(i);
            final int value = createValue(key)<<1;

            assertTrue(map.containsKey(key));
            assertFalse(map.containsValue(value));
            final int prevValue = map.put(key, value);
            assertTrue(map.containsKey(key));
            assertTrue(map.containsValue(value));
            assertFalse(map.containsValue(prevValue));
            assertNotNull(prevValue);
            assertNotEquals(prevValue, value);
        }
        assertSize(ELEMENTS);

        final String newKey = createKey(NO_ELEMENT);
        final int newValue = createValue(newKey);

        assertFalse(map.containsValue(newValue));
        assertFalse(map.containsKey(newKey));
        assertNull(map.put(newKey, newValue));
        assertTrue(map.containsValue(newValue));
        assertTrue(map.containsKey(newKey));

        assertSize(ELEMENTS+1);
    }

    @Test
    public void testRemove() throws Exception {
        try {
            map.remove(null);
            fail();
        } catch (NullPointerException ex) {
            // exception expected - null arguments are not supported
        }

        assertSize(ELEMENTS);
        assertNull(map.remove(createKey(NO_ELEMENT)));
        assertSize(ELEMENTS);

        for (int i = START; i <= ELEMENTS; i++) {
            final String key = createKey(i);
            final Integer expectedValue = createValue(key);
            assertTrue(map.containsKey(key));
            assertTrue(map.containsValue(expectedValue));
            final Integer prevValue = map.remove(key);
            assertNotNull(prevValue);
            assertEquals(expectedValue, prevValue);
            assertFalse(map.containsKey(key));
            assertFalse(map.containsValue(expectedValue));
            assertSize(ELEMENTS - i);
            assertNull(map.remove(key));
            assertSize(ELEMENTS - i);
        }

        assertTrue(map.isEmpty());
        assertSize(0);
    }

    @Test
    public void testPutAll() throws Exception {
        try {
            map.putAll(null);
            fail();
        } catch (UnsupportedOperationException ex) {
            // expected exception
        }
    }

    @Test
    public void testClear() throws Exception {
        map.clear();
        assertSize(0);

        final String key = createKey(1);
        map.put(key, createValue(key));

        assertSize(1);

        map.clear();
        assertSize(0);
        map.clear();
        assertSize(0);
    }

    @Test
    public void testKeySet() throws Exception {
        final Set<String> keySet = map.keySet();
        assertEquals(ELEMENTS, keySet.size());

        for (int i = START; i <= ELEMENTS; i++) {
            assertTrue(keySet.contains(createKey(i)));
        }

        assertFalse(keySet.contains(createKey(NO_ELEMENT)));
    }

    @Test
    public void testValues() throws Exception {
        final Collection<Integer> values = map.values();
        assertEquals(ELEMENTS, values.size());

        for (int i = START; i <= ELEMENTS; i++) {
            assertTrue(values.contains(createValue(createKey(i))));
        }

        assertFalse(values.contains(createValue(createKey(NO_ELEMENT))));
    }

    @Test
    public void testEntrySet() throws Exception {
        final Set<Map.Entry<String, Integer>> entries = map.entrySet();
        assertEquals(ELEMENTS, entries.size());

        for (Map.Entry<String, Integer> entry : entries) {
            assertTrue(map.containsKey(entry.getKey()));
            assertTrue(map.containsValue(entry.getValue()));
        }

    }

    /// ******************** private methods ********************* ///

    private String createKey(int i) {
        return "" + i;
    }

    private int createValue(String key) {
        return key.hashCode();
    }

    private void assertSize(int expectedSize) throws Exception {
        assertEquals(expectedSize, map.size());
        assertEquals(expectedSize, map.keySet().size());
        assertEquals(expectedSize, map.values().size());
    }

}