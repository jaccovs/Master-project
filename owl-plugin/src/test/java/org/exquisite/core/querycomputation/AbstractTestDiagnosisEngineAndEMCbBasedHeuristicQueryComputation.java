package org.exquisite.core.querycomputation;

import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.EMCbMeasure;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

/**
 * @author wolfi
 */
public abstract class AbstractTestDiagnosisEngineAndEMCbBasedHeuristicQueryComputation extends AbstractTestQueryComputation {
    @Override
    protected IQueryComputation<OWLLogicalAxiom> getQueryComputation(IDiagnosisEngine engine) {
        HeuristicConfiguration config = new HeuristicConfiguration<OWLLogicalAxiom>((AbstractDiagnosisEngine)engine, monitor);
        config.setRm(new EMCbMeasure());                                                  // EMCb
        return new HeuristicQueryComputation<>(config);
    }
}
