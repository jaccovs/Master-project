package org.exquisite.core;

import java.util.Collection;
import java.util.Set;

/**
 * Created by kostya on 26.11.2015.
 */
public interface IConflictSearcher<F> {
    Set<Set<F>> findConflicts(Collection<F> formulas) throws DiagnosisException;
}
