package org.exquisite.diagnosis.engines.common;

import choco.kernel.model.constraints.Constraint;

import java.util.Comparator;


/**
 * Compares two constraints. By David. Not sure we really need that.
 *
 * @author dietmar
 */
public class ConstraintComparator implements Comparator<Constraint> {

    @Override
    public int compare(Constraint o1, Constraint o2) {
        return o2.hashCode() - o1.hashCode();
    }

}

