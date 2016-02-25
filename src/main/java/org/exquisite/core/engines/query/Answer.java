package org.exquisite.core.engines.query;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kostya on 04.12.2015.
 */
public class Answer<F> {

    /**
     * Formulas that are supported by the answer
     */
    public Set<F> positive = new HashSet<>();

    /**
     * Formulas that are not supported by the answer
     */
    public Set<F> negative = new HashSet<>();
    /**
     * Formulas that are unaffected by the answer
     */
    public Set<F> undefined = new HashSet<>();
}
