package org.exquisite.core.querycomputation.hsdag.ent;

import org.exquisite.core.conflictsearch.MergeXPlain;
import org.exquisite.core.engines.HSDAGEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.querycomputation.AbstractTestDiagnosisEngineAndEntropyBasedHeuristicQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

/**
 * @author wolfi
 */
public class TestHSDAGEngineWithMergeXPlainAndHeuristicQueryComputation extends AbstractTestDiagnosisEngineAndEntropyBasedHeuristicQueryComputation {
    @Override
    protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ExquisiteOWLReasoner reasoner) {
        return new HSDAGEngine<>(reasoner, new MergeXPlain<>(reasoner));
    }
}
