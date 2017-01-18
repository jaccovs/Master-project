package org.exquisite.core.querycomputation.inverse.ent;

import org.exquisite.core.conflictsearch.QuickXPlain;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.querycomputation.AbstractTestDiagnosisEngineAndEntropyBasedHeuristicQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

/**
 * @author wolfi
 */
public class TestInverseEngineWithQuickXPlainAndHeuristicQueryComputation extends AbstractTestDiagnosisEngineAndEntropyBasedHeuristicQueryComputation {
    @Override
    protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ExquisiteOWLReasoner reasoner) {
        return new InverseDiagnosisEngine<>(reasoner, new QuickXPlain<>(reasoner));
    }
}
