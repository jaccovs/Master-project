package org.exquisite.core.querycomputation.hstree.spl;

import org.exquisite.core.conflictsearch.MergeXPlain;
import org.exquisite.core.engines.HSTreeEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.querycomputation.AbstractTestDiagnosisEngineAndSPLBasedHeuristicQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

/**
 * @author wolfi
 */
public class TestHSTreeEngineWithMergeXPlainAndHeuristicQueryComputation extends AbstractTestDiagnosisEngineAndSPLBasedHeuristicQueryComputation {
    @Override
    protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ExquisiteOWLReasoner reasoner) {
        return new HSTreeEngine<>(reasoner, new MergeXPlain<>(reasoner));
    }
}
