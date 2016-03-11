package org.exquisite.core.query;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.model.Diagnosis;

import java.util.Set;

/**
 * Copy of QueryComputation and slightly adapted for requirements of NewQC.
 *
 * Created by pr8 and wolfi on 10.03.2015.
 *
 * @param <Formula>
 */
public interface NewQueryComputation<Formula> {

    void initialize(Set<Diagnosis<Formula>> diagnoses)
            throws DiagnosisException;

    Set<Formula> next();

    boolean hasNext();

    void reset();
}
