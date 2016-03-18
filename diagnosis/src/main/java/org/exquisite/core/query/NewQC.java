package org.exquisite.core.query;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Framework for New Query Computation Algorithm for interactive debugging.
 *
 * Created by pr8 and wolfi on 10.03.2015.
 */
public class NewQC<Formula> implements NewQueryComputation<Formula> {

    private static Logger logger = LoggerFactory.getLogger(NewQC.class);

    private QPartitionQualityMeasure qPartitionQualityMeasure;

    private QPartition<Formula> qPartition = null;

    private AbstractDiagnosisEngine<Formula> diagnosisEngine;

    private DiagnosisModel<Formula>  diagnosisModel;

    public NewQC() {
    }

    public NewQC(QPartitionQualityMeasure qPartitionQualityMeasure, AbstractDiagnosisEngine<Formula> diagnosisEngine) {
        this.qPartitionQualityMeasure = qPartitionQualityMeasure;
        this.diagnosisEngine = diagnosisEngine;
        this.diagnosisModel = diagnosisEngine.getSolver().getDiagnosisModel();
    }

    @Override
    public void initialize(Set<Diagnosis<Formula>> diagnoses)
            throws DiagnosisException {


        List<Formula> kb = diagnosisModel.getPossiblyFaultyStatements();

        qPartition = findQPartition(diagnoses, diagnosisModel.getStatementWeights(), qPartitionQualityMeasure); // (2)

        selectQueryForQPartition(diagnoses, qPartition); // (3)

        enrichQuery(qPartition, diagnosisModel); // (4)

        Query<Formula> q = optimizeQuery(qPartition); // (5)

    }

    @Override
    public Set<Formula> next() {
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
     * @param statementWeights TODO documentation
     * @param qPartitionQualityMeasure TODO documentation
     * @return TODO documentation
     */
    private QPartition<Formula> findQPartition(Set<Diagnosis<Formula>> diagnoses, Map statementWeights, QPartitionQualityMeasure qPartitionQualityMeasure) {

        QPartition<Formula> partition = new QPartition<>(new HashSet<>(), diagnoses, new HashSet<>(), diagnosisEngine.getCostsEstimator());

        // TODO implement (2) of Main Algorithm


        return null;
    }

    /**
     * TODO documentation
     *
     * @param diagnoses TODO documentation
     * @param qPartition TODO documentation
     */
    private void selectQueryForQPartition(Set<Diagnosis<Formula>> diagnoses, QPartition<Formula> qPartition) {
        // TODO implement (3) of main algorithm
    }

    /**
     * TODO documentation
     *
     * @param qPartition TODO documentation
     * @param diagnosisModel TODO documentation
     */
    private void enrichQuery(QPartition<Formula> qPartition, DiagnosisModel diagnosisModel) {
        // TODO implement (4) of main algorithm
    }

    /**
     * TODO documentation
     *
     * @param qPartition TODO documentation
     * @return TODO documentation
     */
    private Query<Formula> optimizeQuery(QPartition<Formula> qPartition) {
        // TODO implement (5) of main algorithm
        return null;
    }

}
