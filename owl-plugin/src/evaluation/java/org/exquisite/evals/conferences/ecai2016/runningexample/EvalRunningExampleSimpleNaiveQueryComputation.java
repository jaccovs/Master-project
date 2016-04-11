package org.exquisite.evals.conferences.ecai2016.runningexample;

import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

/**
 * @author wolfi
 */
public class EvalRunningExampleSimpleNaiveQueryComputation extends AbstractEvalRunningExample {
    @Override
    protected IQueryComputation<OWLLogicalAxiom> getQueryComputation(IDiagnosisEngine engine) {
        return null;
    }
}
