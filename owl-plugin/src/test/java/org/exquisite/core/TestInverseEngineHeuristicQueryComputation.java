package org.exquisite.core;

import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.EntropyBasedMeasure;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.RiskOptimizationMeasure;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.SplitInHalfMeasure;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.math.BigDecimal;

/**
 * @author wolfi
 */
public class TestInverseEngineHeuristicQueryComputation extends AbstractTestQueryComputation {

    @Override
    protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ExquisiteOWLReasoner reasoner) {
        return new InverseDiagnosisEngine<>(reasoner);
    }

    @Override
    protected IQueryComputation<OWLLogicalAxiom> getQueryComputation(IDiagnosisEngine engine) {
        HeuristicConfiguration config = new HeuristicConfiguration<OWLLogicalAxiom>((AbstractDiagnosisEngine)engine);

        config.setRm(new RiskOptimizationMeasure<>(new BigDecimal("0.05"),new BigDecimal("0"),new BigDecimal("0.3")));  // RIO
        // config.setRm(new SplitInHalfMeasure<>(BigDecimal.ZERO));                                                     // SPL
        // config.setRm(new EntropyBasedMeasure<>(new BigDecimal("0.05")));                                             // ENT
        return new HeuristicQueryComputation<>(config);
    }
}
