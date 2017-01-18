package org.exquisite.core.querycomputation.hstree.ent;

import org.exquisite.core.conflictsearch.QuickXPlain;
import org.exquisite.core.engines.HSTreeEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.querycomputation.AbstractTestDiagnosisEngineAndEntropyBasedHeuristicQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

/**
 * @author wolfi
 */
public class TestHSTreeEngineWithQuickXPlainAndHeuristicQueryComputation extends AbstractTestDiagnosisEngineAndEntropyBasedHeuristicQueryComputation {
    @Override
    protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ExquisiteOWLReasoner reasoner) {
        return new HSTreeEngine<>(reasoner, new QuickXPlain<>(reasoner));
    }
}
