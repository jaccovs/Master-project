package org.exquisite.core.querycomputation.hstree.kl;

import org.exquisite.core.conflictsearch.Progression;
import org.exquisite.core.engines.HSTreeEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.querycomputation.AbstractTestDiagnosisEngineAndKLBasedHeuristicQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

/**
 * @author wolfi
 */
public class TestHSTreeEngineWithProgressionAndHeuristicQueryComputation extends AbstractTestDiagnosisEngineAndKLBasedHeuristicQueryComputation {
    @Override
    protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ExquisiteOWLReasoner reasoner) {
        return new HSTreeEngine<>(reasoner, new Progression<>(reasoner));
    }
}
