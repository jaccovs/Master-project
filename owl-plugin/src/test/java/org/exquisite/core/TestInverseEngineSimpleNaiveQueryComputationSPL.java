package org.exquisite.core;

import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.query.querycomputation.SimpleNaiveQueryComputation;
import org.exquisite.core.query.scoring.SplitInHalf1QSS;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

/**
 * @author wolfi
 */
public class TestInverseEngineSimpleNaiveQueryComputationSPL extends AbstractTestQueryComputation {

    @Override
    protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ExquisiteOWLReasoner reasoner) {
        return new InverseDiagnosisEngine<>(reasoner);
    }

    @Override
    protected IQueryComputation<OWLLogicalAxiom> getQueryComputation(IDiagnosisEngine engine) {
        return new SimpleNaiveQueryComputation<>(engine, new SplitInHalf1QSS<>());            // SPL
    }
}
