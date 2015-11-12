package org.exquisite.diagnosis.engines.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Where's the documentation, David?
 * 
 * A synchronized list
 * @author dietmar
 * The type
 * @param <T>
 */
public class SharedCollection<T> {
	private final Object writeLock = new Object();
	private volatile List<T> collection = Collections.synchronizedList(new ArrayList<T>());

	/**
	 * Add an item to the end of the list
	 * @param item
	 */
	public void add(T item){
		synchronized(writeLock){
			collection.add(item);
		}
	}
	
	public void addAll(Collection<T> items) {
		synchronized (writeLock) {
			collection.addAll(items);
		}
	}
	

	/**
	 * Add a set to the end of the list if it is not already contained in the list
	 * Collection must contain lists of T
	 * (set semantic: similar list means same elements and same size)
	 * @param a list of items
	 * @return the list if it is already there
	 * 
	 */
	public T addItemListNoDups(T newList){
		synchronized(writeLock){
			// Not very nice.
			List<T> newL = (List<T>) newList;
			List<T> list;
			// Check if already existing
			boolean alreadyThere = false;
			for (Object o : collection) {
				list = (List<T>) o;
				if (list.size() == newL.size() && newL.containsAll(list)) {
					alreadyThere = true;
					return (T) o;
				}
			}
			if (!alreadyThere) {
				this.collection.add(newList);
			}
			return null;
		}
	}
	
	public int size() {
		synchronized (writeLock) {
			return collection.size();
		}
	}
	
	public T get(int index) {
		synchronized (writeLock) {
			return collection.get(index);
		}
	}

	
	
	/**
	 * Get the collection
	 * @return
	 */
	public List<T> getCollection(){
		// TODO TS: This does not help a bit.
		synchronized(writeLock){
			return collection;
		}
	}
	
	public List<T> getCopy() {
		synchronized (writeLock) {
			return new ArrayList<T>(collection);
		}
	}
	
	/**
	 * Empty the collection
	 */
	public void clear(){
		collection.clear();
	}	
	
	
	/**
	 * A getter for the write lock for sub classes
	 * @return
	 */
	public Object getWriteLock() {
		return writeLock;
	}
	
	/**
	 * Returns a string representation
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (T elem: collection) {
			result.append(elem.toString()).append(" ");
		}
		return result.toString();
	}
	
}
