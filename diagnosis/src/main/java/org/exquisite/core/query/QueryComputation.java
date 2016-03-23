package org.exquisite.core.query;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.DiagnosisException;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: kostya
 * Date: 02.05.11
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 */
public interface QueryComputation<F> {

    void initialize(Set<Diagnosis<F>> diagnoses) throws DiagnosisException;

    Query<F> next();

    boolean hasNext();

    void reset();
}
