package org.exquisite.core.model;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An observable set that notifies its observers as soon as elements of the set are changed. This class consideres
 * that the set is changed is some of its elements are removed or added. In all other cases the cahnges are not
 * tracked. For instance, if there is a set of String then adding or removing a stiring from the set results in a
 * notification of observers. Getting a string from the set and modifying its content by, e.g., removing some
 * symbols, is not considered as the change of the set, but as a change of the object stored in the set. The latter
 * must be considered separately.
 */
public class ObservableSet<E> extends Observable implements Set<E> {

    private Set<E> set;

    private ObservableSet(Set<E> list) {
        this.set = list;
    }

    public static <E> ObservableSet<E> observableSet(Set<E> set) {
        if (set == null) {
            throw new NullPointerException();
        }
        return set instanceof SortedSet ? new ObservableSet<E>(new TreeSet<>(set)) :
                new ObservableSet<E>(new HashSet<>(set));
    }

    public static <E> ObservableSet<E> observableSet(Set<E> set, Observer obs) {
        ObservableSet<E> st = observableSet(set);
        st.addObserver(obs);
        return st;
    }

    public static <E> ObservableSet<E> observableHashSet() {
        return observableSet(new HashSet<E>());
    }

    @Override
    public Spliterator<E> spliterator() {
        return set.spliterator();
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            Iterator<E> itr = set.iterator();

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public E next() {
                return itr.next();
            }

            @Override
            public void remove() {
                itr.remove();
                setChanged();
                notifyObservers();
            }
        };
    }

    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return set.toArray(a);
    }

    @Override
    public boolean add(E e) {
        setChanged();
        notifyObservers();
        return set.add(e);
    }

    @Override
    public boolean remove(Object o) {
        setChanged();
        notifyObservers();
        return set.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return set.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        setChanged();
        notifyObservers();
        return set.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        setChanged();
        notifyObservers();
        return set.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        setChanged();
        notifyObservers();
        return set.retainAll(c);
    }


    @Override
    public void clear() {
        setChanged();
        notifyObservers();
        set.clear();
    }

    @Override
    public boolean equals(Object o) {
        return set.equals(o);
    }

    @Override
    public int hashCode() {
        return set.hashCode();
    }


    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        boolean b = set.removeIf(filter);
        if (b) {
            setChanged();
            notifyObservers();
        }
        return b;
    }

    @Override
    public Stream<E> stream() {
        return set.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return set.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        set.forEach(action);
    }
}
