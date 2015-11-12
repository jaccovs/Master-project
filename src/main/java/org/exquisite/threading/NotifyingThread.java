package org.exquisite.threading;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * Notifies observers when the runnable object running in this thread has finished its work.
 * 
 * Set run as final so it cannot be overridden (to avoid accidental loss of event propagation)
 * @author David
 * 
 * For an example use, see {@link org.exquisite.communication.protocol.ServerProtocol}
 *
 */
public class NotifyingThread extends Thread 
{
	//A collection of listeners that this thread should notify.
	private final Set<INotifyingThreadListener> listeners = new CopyOnWriteArraySet<INotifyingThreadListener>();
	
	//The runnable object that you want to run in this thread.
	volatile Runnable runner;
	
	public NotifyingThread(Runnable runner){
		this.runner = runner;
	}	
	
	public Runnable getRunner(){
		return this.runner;
	}
	
	//Adding a listener to the collection of listeners.
	public final void addListener(final INotifyingThreadListener listener){
		listeners.add(listener);
	}
	
	//Removes a listener from the collection of listeners
	public final void removeListener(final INotifyingThreadListener listener){
		listeners.remove(listener);
	}
	
	//Notifies all listeners in the collection of a change in the thread.
	private final void notifyListeners(){
		for (INotifyingThreadListener listner : listeners){
			listner.notifyOfThreadComplete(this);
		}
	}
	
	@Override
	/**
	 * Calls the run method of target object you want to run in this thread.
	 * When the runnable object is finished, it then sends out an event to 
	 * any listening objects that this work has finished.
	 */
	public final void run(){
		try {
			runner.run();
		} finally{
			notifyListeners();
		}
	}	
}
