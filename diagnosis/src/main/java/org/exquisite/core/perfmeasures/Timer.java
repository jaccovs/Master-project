package org.exquisite.core.perfmeasures;

import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * Simple class for measuring time in experiments. The timings of time measurements are stored in an array and can be
 * retrieved using getTimings method.
 */
public class Timer extends Counter {

    private LinkedList<Long> timings = new LinkedList<>();
    private long time = 0;
    private Boolean running = null;

    Timer(String name) {
        super(name);
    }

    /**
     * Start the timer.
     */
    public void start() {
        if (this.running != null && this.running) throw new IllegalStateException("The timer " + this.name + " is " +
                "already running!");
        this.running = true;
        this.time = System.nanoTime();
    }

    /**
     * Stop the timer.
     *
     * @return return the time elapsed since the start in nanoseconds.
     */
    public long stop() {
        this.time = getElapsedTime();
        this.running = false;
        this.timings.add(this.time);
        return this.time;
    }

    /**
     * @return the time elapsed since the timer is started.
     */
    public long getElapsedTime() {
        if (this.running == null || !this.running) throw new IllegalStateException("The timer " + this.name + " is " +
                "not running!");
        return System.nanoTime() - this.time;
    }

    /**
     * @return timings of the time perfmeasures
     */
    public List<Long> getTimings() {
        return unmodifiableList(this.timings);
    }

    /**
     * @return the total time that the timer was running
     */
    public long total() {
        long total = 0;
        for (long t : this.timings)
            total += t;
        return total;
    }
}
