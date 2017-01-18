package org.exquisite.core.querycomputation.hsdag.kl;

import org.exquisite.core.conflictsearch.Progression;
import org.exquisite.core.engines.HSDAGEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.querycomputation.AbstractTestDiagnosisEngineAndKLBasedHeuristicQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

/**
 * @author wolfi
 */
public class TestHSDAGEngineWithProgressionAndHeuristicQueryComputation extends AbstractTestDiagnosisEngineAndKLBasedHeuristicQueryComputation {
    @Override
    protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ExquisiteOWLReasoner reasoner) {
        return new HSDAGEngine<>(reasoner, new Progression<>(reasoner));
    }
}
