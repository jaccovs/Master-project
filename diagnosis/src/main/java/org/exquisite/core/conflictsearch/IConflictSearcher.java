package org.exquisite.core.conflictsearch;

import org.exquisite.core.DiagnosisException;

import java.util.Collection;
import java.util.Set;

/**
 * A general interface for conflict searchers, like QuickXPlain, MergeXPlain, InverseQuickXPlain or Progression.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author kostya
 */
public interface IConflictSearcher<F> {
    Set<Set<F>> findConflicts(Collection<F> formulas) throws DiagnosisException;
}
