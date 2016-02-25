package org.exquisite.core.measurements;

/**
 * Simple counter used for performance measurements
 */
public class Counter extends Measurement {

    private long value = 0;

    Counter(String name) {
        super(name);
    }

    public long value() {
        return this.value;
    }

    public long increment(int step) {
        this.value = this.value + step;
        return value();
    }
}
