package org.exquisite.core.querycomputation.hsdag.kl;

import org.exquisite.core.conflictsearch.QuickXPlain;
import org.exquisite.core.engines.HSDAGEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.querycomputation.AbstractTestDiagnosisEngineAndKLBasedHeuristicQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

/**
 * @author wolfi
 */
public class TestHSDAGEngineWithQuickXPlainAndHeuristicQueryComputation extends AbstractTestDiagnosisEngineAndKLBasedHeuristicQueryComputation {
    @Override
    protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ExquisiteOWLReasoner reasoner) {
        return new HSDAGEngine<>(reasoner, new QuickXPlain<>(reasoner));
    }
}
