package org.exquisite.core;

/**
 * An interface to monitor the progress of diagnosis- and query-calculation as well as other time consuming operations.
 * The interface should be implemented by clients that wish to monitor the progress of such time consuming operations.
 *
 * The contract is that class(es) responsible for time consuming operations (such as Diagnosis and QueryComputation)
 * will call {@link #taskStarted(String)} then call either {@link #taskBusy(String)} or {@link #taskProgressChanged(String, int, int)} any
 * number of times and finally call {@link #taskStopped()} when the task ends or has been interrupted. This cycle may
 * be repeated.
 *
 * @author wolfi
 */
public interface ExquisiteProgressMonitor {

    void taskStarted(String taskName);

    void taskBusy(String message);

    void taskProgressChanged(String message, int value, int max);

    void taskStopped();

}
