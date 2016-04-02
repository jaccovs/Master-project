package org.exquisite.core.query.qc.heuristic.sortcriteria;

import java.util.Comparator;

/**
 * A sorting criterion used as comparator on insertion, sorting etc of formulas.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author patrick
 * @author wolfi
 */
public interface ISortCriterion<F> extends Comparator<F> {
}
