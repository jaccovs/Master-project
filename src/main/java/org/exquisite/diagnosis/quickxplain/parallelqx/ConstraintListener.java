package org.exquisite.diagnosis.quickxplain.parallelqx;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import choco.kernel.model.constraints.Constraint;

/**
 * Listens for new constraints that arrived..
 */
public class ConstraintListener {
	// The constraint 
	private Constraint constraint = null;

	// The lock
    protected ReentrantLock lock = null;
    // The condition
    protected Condition addedConstraint = null;
    // released?
    private boolean released = false;
	 
    /**
     * Create the listener
     * @param fairLock
     */
    public ConstraintListener(boolean fairLock) {
        this.lock = new ReentrantLock(fairLock);
        this.addedConstraint = lock.newCondition();
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
     * @return
     * @throws InterruptedException
     */
    public Constraint getFoundConstraint() throws InterruptedException {
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
        Constraint localConstraint = this.constraint;
        this.constraint = null;
        return localConstraint;
    }

    /**
     * Set a new constraint
     * @param constraint
     */
    public void setFoundConstraint(Constraint constraint) {
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
     * Create a new listener instance
     * @return
     */
    static ConstraintListener newInstance() {
    	return new ConstraintListener(true);
    }

    /**
     * Return true if released
     * @return
     */
    public boolean isReleased() {
        return this.released;
    }

    /**
     * Check if there is a constraint
     * @return
     */
    public boolean hasConstraints() {
    	return this.constraint != null;
    }
    
    
}
