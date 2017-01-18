package org.exquisite.core.querycomputation.inverse.rio;

import org.exquisite.core.conflictsearch.QuickXPlain;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.querycomputation.AbstractTestDiagnosisEngineAndRIOBasedHeuristicQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

/**
 * @author wolfi
 */
public class TestInverseEngineWithQuickXPlainAndHeuristicQueryComputation extends AbstractTestDiagnosisEngineAndRIOBasedHeuristicQueryComputation {
    @Override
    protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ExquisiteOWLReasoner reasoner) {
        return new InverseDiagnosisEngine<>(reasoner, new QuickXPlain<>(reasoner));
    }
}
