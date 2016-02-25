package org.exquisite.core.engines.query;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.model.Diagnosis;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: kostya
 * Date: 02.05.11
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 */
public interface QueryComputation<Formula> {

    void initialize(Set<Diagnosis<Formula>> hittingSets)
            throws DiagnosisException;

    Query<Formula> next();

    boolean hasNext();

    void reset();
}
