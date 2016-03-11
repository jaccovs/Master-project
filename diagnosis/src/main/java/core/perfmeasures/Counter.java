package core.perfmeasures;

/**
 * Simple counter used for performance perfmeasures
 */
public class Counter extends PerfMeasurement {

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
