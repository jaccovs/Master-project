package org.exquisite.core.querycomputation.inverse.emcb;

import org.exquisite.core.conflictsearch.Progression;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.querycomputation.AbstractTestDiagnosisEngineAndEMCbBasedHeuristicQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

/**
 * @author wolfi
 */
public class TestInverseEngineWithProgressionAndHeuristicQueryComputation extends AbstractTestDiagnosisEngineAndEMCbBasedHeuristicQueryComputation {
    @Override
    protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ExquisiteOWLReasoner reasoner) {
        return new InverseDiagnosisEngine<>(reasoner, new Progression<>(reasoner));
    }
}
