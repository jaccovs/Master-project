package core.perfmeasures;

/**
 * Created by kostya on 23.11.2015.
 */
public abstract class PerfMeasurement implements Comparable<PerfMeasurement> {

    protected final String name;

    PerfMeasurement(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(PerfMeasurement o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
