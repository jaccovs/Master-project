package org.exquisite.core.model;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * An observable list that notifies its observers as soon as elements of the list are changed. This class consideres
 * that the list is changed is some of its elements are removed or added. In all other cases the cahnges are not
 * tracked. For instance, if there is a list of String then adding or removing a stiring from the list results in a
 * notification of observers. Getting a string from the list and modifying its content by, e.g., removing some
 * symbols, is not considered as the change of the list, but as a change of the object stored in the list. The latter
 * must be considered separately.
 */
public class ObservableList<E> extends Observable implements List<E> {

    private List<E> list;

    private ObservableList(List<E> list) {
        this.list = list;
    }

    public static <E> ObservableList<E> observableList(List<E> list) {
        if (list == null) {
            throw new NullPointerException();
        }
        return list instanceof RandomAccess ? new ObservableList<E>(new ArrayList<>(list)) :
                new ObservableList<E>(new LinkedList<>(list));
    }

    public static <E> ObservableList<E> observableList(Collection<E> list, Observer obs) {
        List<E> ls;
        if (list instanceof List)
            ls = (List<E>) list;
        else
            ls = new ArrayList<>(list);

        ObservableList<E> olist = observableList(ls);
        olist.addObserver(obs);
        return olist;

    }

    public static <E> ObservableList<E> observableArrayList() {
        return observableList(new ArrayList<>());
    }

    @Override
    public Spliterator<E> spliterator() {
        return list.spliterator();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            Iterator<E> itr = list.iterator();

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
                notifyObs(true);
            }
        };
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return notifyObs(list.add(e));
    }

    private boolean notifyObs(boolean b) {
        if (b) {
            setChanged();
            notifyObservers();
        }
        return b;
    }

    @Override
    public boolean remove(Object o) {
        return notifyObs(list.remove(o));
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return notifyObs(list.addAll(c));
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return notifyObs(list.addAll(index, c));
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return notifyObs(list.removeAll(c));
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return notifyObs(list.retainAll(c));
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        list.replaceAll(operator);
        notifyObs(true);
    }

    @Override
    public void sort(Comparator<? super E> c) {
        list.sort(c);
    }

    @Override
    public void clear() {
        list.clear();
        notifyObs(true);
    }

    @Override
    public boolean equals(Object o) {
        return list.equals(o);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public E set(int index, E element) {
        E set = list.set(index, element);
        notifyObs(true);
        return set;
    }

    @Override
    public void add(int index, E element) {
        list.add(index, element);
        notifyObs(true);
    }

    @Override
    public E remove(int index) {
        E remove = list.remove(index);
        notifyObs(true);
        return remove;
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new ListIterator<E>() {
            ListIterator<E> itr = list.listIterator();

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public E next() {
                return itr.next();
            }

            @Override
            public boolean hasPrevious() {
                return itr.hasPrevious();
            }

            @Override
            public E previous() {
                return itr.previous();
            }

            @Override
            public int nextIndex() {
                return itr.nextIndex();
            }

            @Override
            public int previousIndex() {
                return itr.previousIndex();
            }

            @Override
            public void remove() {
                itr.remove();
                notifyObs(true);
            }

            @Override
            public void set(E e) {
                itr.set(e);
                notifyObs(true);
            }

            @Override
            public void add(E e) {
                itr.add(e);
                notifyObs(true);
            }
        };
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return new ObservableList<>(list.subList(fromIndex, toIndex));
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return notifyObs(list.removeIf(filter));
    }

    @Override
    public Stream<E> stream() {
        return list.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return list.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        list.forEach(action);
    }
}
