package org.exquisite.diagnosis.quickxplain.parallelqx;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Listens for new constraints that arrived..
 */
public class FormulaListener<T> {
    // The lock
    protected ReentrantLock lock = null;
    // The condition
    protected Condition addedConstraint = null;
    // The constraint
    private T constraint = null;
    // released?
    private boolean released = false;

    /**
     * Create the listener
     *
     * @param fairLock
     */
    public FormulaListener(boolean fairLock) {
        this.lock = new ReentrantLock(fairLock);
        this.addedConstraint = lock.newCondition();
    }

    /**
     * Create a new listener instance
     *
     * @return
     */
    static <T> FormulaListener<T> newInstance() {
        return new FormulaListener<>(true);
    }

    /**
     * Release the lock and signal that something happens
     */
    public void release() {
        this.lock.lock();
        try {
            this.released = true;
            addedConstraint.signal();
        } finally {
            this.lock.unlock();
        }

    }

    /**
     * Get a copy of the constraint and remove the local pointer
     *
     * @return
     * @throws InterruptedException
     */
    public T getFoundConstraint() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();

        try {
            while (!isReleased() && this.constraint == null) {
                addedConstraint.await();
            }
        } finally {
            lock.unlock();
        }
        release();
        T localConstraint = this.constraint;
        this.constraint = null;
        return localConstraint;
    }

    /**
     * Set a new constraint
     *
     * @param constraint
     */
    public void setFoundConstraint(T constraint) {
        if (isReleased() || this.constraint != null) {
            return;
        }
        this.lock.lock();
        try {
            this.constraint = constraint;
            addedConstraint.signal();
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * Return true if released
     *
     * @return
     */
    public boolean isReleased() {
        return this.released;
    }

    /**
     * Check if there is a constraint
     *
     * @return
     */
    public boolean hasConstraints() {
        return this.constraint != null;
    }


}
