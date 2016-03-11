package org.exquisite.core.query;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Framework for New Query Computation Algorithm for interactive debugging.
 *
 * Created by pr8 and wolfi on 10.03.2015.
 */
public class NewQC<T> implements NewQueryComputation<T> {

    private static Logger logger = LoggerFactory.getLogger(NewQC.class);

    private final QPartitionQualityMeasure qPartitionQualityMeasure;

    private QPartition<T> qPartition = null;

    private DiagnosisModel diagnosisModel;


    public NewQC(QPartitionQualityMeasure qPartitionQualityMeasure, DiagnosisModel diagnosisModel) {
        this.qPartitionQualityMeasure = qPartitionQualityMeasure;
        this.diagnosisModel = diagnosisModel;
    }

    @Override
    public void initialize(Set<Diagnosis<T>> diagnoses)
            throws DiagnosisException {

        List<T> kb = diagnosisModel.getPossiblyFaultyStatements();

        qPartition = findQPartition(diagnoses, diagnosisModel.getStatementWeights(), qPartitionQualityMeasure); // (2)

        selectQueryForQPartition(diagnoses, qPartition); // (3)

        enrichQuery(qPartition, diagnosisModel); // (4)

        Query<T> q = optimizeQuery(qPartition); // (5)

    }

    @Override
    public Set<T> next() {
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
     * TODO documentation
     *
     * @param diagnoses TODO documentation
     * @param statementWeights TODO documentation
     * @param qPartitionQualityMeasure TODO documentation
     * @return TODO documentation
     */
    private QPartition<T> findQPartition(Set<Diagnosis<T>> diagnoses, Map statementWeights, QPartitionQualityMeasure qPartitionQualityMeasure) {

        // TODO implement (2) of Main Algorithm
        return null;
    }

    /**
     * TODO documentation
     *
     * @param diagnoses TODO documentation
     * @param qPartition TODO documentation
     */
    private void selectQueryForQPartition(Set<Diagnosis<T>> diagnoses, QPartition<T> qPartition) {
        // TODO implement (3) of main algorithm
    }

    /**
     * TODO documentation
     *
     * @param qPartition TODO documentation
     * @param diagnosisModel TODO documentation
     */
    private void enrichQuery(QPartition<T> qPartition, DiagnosisModel diagnosisModel) {
        // TODO implement (4) of main algorithm
    }

    /**
     * TODO documentation
     *
     * @param qPartition TODO documentation
     * @return TODO documentation
     */
    private Query<T> optimizeQuery(QPartition<T> qPartition) {
        // TODO implement (5) of main algorithm
        return null;
    }

}
