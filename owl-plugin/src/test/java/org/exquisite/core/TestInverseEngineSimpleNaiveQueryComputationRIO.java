package org.exquisite.core;

import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.query.querycomputation.SimpleNaiveQueryComputation;
import org.exquisite.core.query.scoring.RIOQSS;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.math.BigDecimal;

/**
 * @author wolfi
 */
public class TestInverseEngineSimpleNaiveQueryComputationRIO extends AbstractTestQueryComputation {

    @Override
    protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ExquisiteOWLReasoner reasoner) {
        return new InverseDiagnosisEngine<>(reasoner);
    }

    @Override
    protected IQueryComputation<OWLLogicalAxiom> getQueryComputation(IDiagnosisEngine engine) {
        return new SimpleNaiveQueryComputation<>(engine, new RIOQSS<>(new BigDecimal("0.1")));  // RIO
    }
}
