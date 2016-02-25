package org.exquisite.core.measurements;

/**
 * Created by kostya on 23.11.2015.
 */
public abstract class Measurement implements Comparable<Measurement> {

    protected final String name;

    Measurement(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Measurement o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
