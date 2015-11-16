package org.exquisite.threading;

/**
 * Any class that wants to listen for events originating from NotifyingThread needs to implement this interface.
 *
 * @author David
 *         <p>
 *         For an example use, see {@link org.exquisite.communication.protocol.ServerProtocol}
 * @see org.exquisite.threading.NotifyingThread
 */
public interface INotifyingThreadListener {
    void notifyOfThreadComplete(final NotifyingThread thread);
}
