package org.exquisite.core;

/**
 * <p>
 * An interface to monitor the progress of diagnosis- and query-calculation as well as other time consuming operations.
 * The interface should be implemented by clients that wish to monitor the progress of such time consuming operations.
 * </p>
 * <p>Tasks are executed sequentially. Nested tasks are not supported.</p>
 * <p>
 * The contract is that class(es) responsible for time consuming operations (such as Diagnosis and QueryComputation)
 * will call {@link #taskStarted(String)} then call either {@link #taskBusy(String)} or {@link #taskProgressChanged(String, int, int)} any
 * number of times and finally call {@link #taskStopped()} when the task ends or has been interrupted. This cycle may
 * be repeated.
 * </p>
 * @author wolfi
 */
public interface IExquisiteProgressMonitor {

    /** A standard name for the task of consistency check. */
    String CONSISTENCY_CHECK = "Checking consistency";

    /** A standard name for the task of consistency and coherency check. */
    String CONSISTENCY_COHERENCY_CHECK = "Checking consistency & coherency";

    /** A standard name for the task of calculation of the diagnoses. */
    String DIAGNOSES_CALCULATION = "Searching diagnoses";

    /** A standard name for the taks of generation of queries. */
    String QUERY_COMPUTATION = "Generating queries";

    /**
     * Indicates that some long lasting task, for example consistency checking, diagnosis calculation or query generation
     * has started. When the task has finished the {@link #taskStopped()} method will be called.
     * Once this method has been called it will not be called again unless the {@link #taskStopped()} method has been
     * called. The notion of subtasks is not supported.
     * <br><br>
     * Note that this method may be called from a thread that is not the event dispatch thread.
     * @param taskName The name of the task.
     */
    void taskStarted(String taskName);

    /**
     * Indicates that the Exquisite component (for instance a debugger) is busy performing a task whose size cannot be determined.
     * This method will only be called after the {@link #taskStarted(String)} method has been called.
     * It will not be called after the {@link #taskStopped()} method has been called.
     * <br><br>
     * Note that this method may be called from a thread that is not the event dispatch thread.
     * @param message An optional message. Can be <code>null</code>.
     */
    void taskBusy(String message);

    /**
     * Indicates that Exquisite component (for instance a debugger) is part way through a particular task,
     * for example consistency checking diagnosis calculation or query generation.
     * This method will only be called after the {@link #taskStarted(String)} method has been called.
     * It will not be called after the {@link #taskStopped()} method has been called.
     * <br><br>
     * Note that this method may be called from a thread that is not the event dispatch thread.
     * @param message An optional message. Can be <code>null</code>.
     * @param value The value or portion of the task completed.
     * @param max The total size of the task.
     */
    void taskProgressChanged(String message, int value, int max);

    /**
     * Indicates that a previously started task has now stopped.
     * This method will only be called after the {@link #taskStarted(String)} method has been called.
     * The notion of subtasks is not supported.
     * <br><br>
     *  Note that this method may be called from a thread that is not the event dispatch thread.
     */
    void taskStopped();

    /**
     * This method should be called by the time consuming operation when it is possible to cancel the operation.
     *
     * @param isEnabled <code>true</code> enables the cancel feature, <code>false</code> disables the cancel feature.
     */
    void setCancel(boolean isEnabled);

    /**
     * The time consuming operation can check with this method if the user wishes to cancel the long lasting operation.
     *
     * @return returns <code>true</code> if the user cancelled the time consuming operation.
     */
    boolean isCancelled();
}
