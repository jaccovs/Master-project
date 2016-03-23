package org.exquisite.core.query;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Framework for a heuristic Query Computation Algorithm for interactive debugging.
 *
 * Created by pr8 and wolfi on 10.03.2015.
 */
public class HeuristicQC<F> implements QueryComputation<F> {

    private static Logger logger = LoggerFactory.getLogger(HeuristicQC.class);

    private QPartitionQualityMeasure qPartitionQualityMeasure;

    private QPartition<F> qPartition = null;

    private AbstractDiagnosisEngine<F> diagnosisEngine;

    private DiagnosisModel<F>  diagnosisModel;

    public HeuristicQC(QPartitionQualityMeasure qPartitionQualityMeasure, AbstractDiagnosisEngine<F> diagnosisEngine) {
        this.qPartitionQualityMeasure = qPartitionQualityMeasure;
        this.diagnosisEngine = diagnosisEngine;
        this.diagnosisModel = diagnosisEngine.getSolver().getDiagnosisModel();
    }

    @Override
    public void initialize(Set<Diagnosis<F>> diagnoses)
            throws DiagnosisException {
        calcQuery(this.diagnosisModel, diagnoses, this.diagnosisModel.getFormulaWeights(), this.qPartitionQualityMeasure);
    }

    /**
     *
     *
     * @param diagnosisModel
     * @param leadingDiagnoses
     * @param formulaWeights
     * @param qPartitionQualityMeasure
     */
    private void calcQuery(DiagnosisModel<F>  diagnosisModel, Set<Diagnosis<F>> leadingDiagnoses, Map<F, Float> formulaWeights, QPartitionQualityMeasure qPartitionQualityMeasure) {
        List<F> kb = diagnosisModel.getPossiblyFaultyFormulas();

        qPartition = findQPartition(leadingDiagnoses, formulaWeights, qPartitionQualityMeasure); // (2)

        selectQueryForQPartition(leadingDiagnoses, qPartition); // (3)

        enrichQuery(qPartition, diagnosisModel); // (4)

        Query<F> q = optimizeQuery(qPartition); // (5)
    }

    @Override
    public Query<F> next() {
        return qPartition.getQuery();
    }

    @Override
    public boolean hasNext() {
        return qPartition != null;
    }

    @Override
    public void reset() {
        this.qPartition = null;
    }

    /**
     * TODO implement step (2) of Main Algorithm
     *
     * @param diagnoses TODO documentation
     * @param formulaWeights TODO documentation
     * @param qPartitionQualityMeasure TODO documentation
     * @return TODO documentation
     */
    private QPartition<F> findQPartition(Set<Diagnosis<F>> diagnoses, Map formulaWeights, QPartitionQualityMeasure qPartitionQualityMeasure) {

        QPartition<F> partition = new QPartition<>(new HashSet<>(), diagnoses, new HashSet<>(), diagnosisEngine.getCostsEstimator());

        // TODO implement (2) of Main Algorithm


        return null;
    }

    /**
     * TODO documentation
     *
     * @param diagnoses TODO documentation
     * @param qPartition TODO documentation
     */
    private void selectQueryForQPartition(Set<Diagnosis<F>> diagnoses, QPartition<F> qPartition) {
        // TODO implement (3) of main algorithm
    }

    /**
     * TODO documentation
     *
     * @param qPartition TODO documentation
     * @param diagnosisModel TODO documentation
     */
    private void enrichQuery(QPartition<F> qPartition, DiagnosisModel diagnosisModel) {
        // TODO implement (4) of main algorithm
    }

    /**
     * TODO documentation
     *
     * @param qPartition TODO documentation
     * @return TODO documentation
     */
    private Query<F> optimizeQuery(QPartition<F> qPartition) {
        // TODO implement (5) of main algorithm
        return null;
    }

}
