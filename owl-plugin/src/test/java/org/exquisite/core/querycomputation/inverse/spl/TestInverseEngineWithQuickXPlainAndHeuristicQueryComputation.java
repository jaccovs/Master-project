package org.exquisite.core.querycomputation.inverse.spl;

import org.exquisite.core.conflictsearch.QuickXPlain;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.querycomputation.AbstractTestDiagnosisEngineAndSPLBasedHeuristicQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

/**
 * @author wolfi
 */
public class TestInverseEngineWithQuickXPlainAndHeuristicQueryComputation extends AbstractTestDiagnosisEngineAndSPLBasedHeuristicQueryComputation {
    @Override
    protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ExquisiteOWLReasoner reasoner) {
        return new InverseDiagnosisEngine<>(reasoner, new QuickXPlain<>(reasoner));
    }
}
