package org.exquisite.diagnosis.interactivity;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.interactivity.partitioning.Partition;
import org.exquisite.diagnosis.interactivity.partitioning.Partitioning;
import org.exquisite.diagnosis.interactivity.partitioning.costestimators.CostsEstimator;
import org.exquisite.diagnosis.interactivity.partitioning.scoring.Rounding;
import org.exquisite.diagnosis.models.DAGNode;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A tests.diagnosis engine that will simulate user interactivity to always return the one correct tests.diagnosis.
 *
 * @author Schmitz
 */
public class InteractivityDiagnosisEngine<Formula> extends AbstractHSDagBuilder<Formula> {

    private static final Logger log = Logger.getLogger(InteractivityDiagnosisEngine.class.getSimpleName());

    public static boolean ADD_EXPLICIT_STATEMENTS_AS_ENTAILMENTS = true;
    public static boolean ADD_IMPLICIT_STATEMENTS_AS_ENTAILMENTS = false;
    public static boolean REUSE_KNOWN_CONFLICTS = true;

    /**
     * Inner engine is used to calculate the tests.diagnosis candidates.
     */
    IDiagnosisEngine<Formula> innerEngine;

    Partitioning<Formula> partitioning;

    Diagnosis<Formula> correctDiagnosis;

    CostsEstimator<Formula> costsEstimator = null;

    int numberOfQueries = 0;
    int numberOfQueriedStatements = 0;
    int numberOfDiagnosisRuns = 0;
    long diagnosisNanos = 0;

    float diagnosisTime;
    float userInteractionTime;

    public InteractivityDiagnosisEngine(ExquisiteSession<Formula> sessionData, IDiagnosisEngine<Formula> innerEngine,
                                        int diagnosesPerQuery, Partitioning<Formula> partitioning,
                                        Diagnosis<Formula> correctDiagnosis) {
        super(sessionData);

        this.innerEngine = innerEngine;
        this.partitioning = partitioning;
        this.correctDiagnosis = correctDiagnosis;

        sessionData.config.maxDiagnoses = diagnosesPerQuery;
    }

    public int getNumberOfQueries() {
        return numberOfQueries;
    }

    public int getNumberOfQueriedStatements() {
        return numberOfQueriedStatements;
    }

    public int getNumberOfDiagnosisRuns() {
        return numberOfDiagnosisRuns;
    }

    public float getDiagnosisTime() {
        return diagnosisTime;
    }

    public float getUserInteractionTime() {
        return userInteractionTime;
    }

    public CostsEstimator<Formula> getCostsEstimator() {
        return costsEstimator;
    }

    public void setCostsEstimator(CostsEstimator<Formula> costsEstimator) {
        this.costsEstimator = costsEstimator;
    }

    @Override
    public List<Diagnosis<Formula>> calculateDiagnoses() throws DiagnosisException {
        long startTime = System.nanoTime();

        AbstractHSDagBuilder<Formula> innerHSDagEngine = null;
        if (innerEngine instanceof AbstractHSDagBuilder) {
            innerHSDagEngine = (AbstractHSDagBuilder<Formula>) innerEngine;
        }

        List<List<Formula>> conflicts = new ArrayList<>();

        List<Diagnosis<Formula>> diagnoses;

        do {
            innerEngine.resetEngine();
            innerEngine.getSessionData().diagnosisModel = innerEngine.getModel();
            if (REUSE_KNOWN_CONFLICTS && innerHSDagEngine != null) {
                innerHSDagEngine.knownConflicts.addAll(conflicts);
            }
            numberOfDiagnosisRuns++;
            log.info("Searching for diagnoses");
            long innerStart = System.nanoTime();
            diagnoses = innerEngine.calculateDiagnoses();
            long innerEnd = System.nanoTime();
            diagnosisNanos += innerEnd - innerStart;

            List<Diagnosis<Formula>> diagnosesWithCorrect = new ArrayList<>(diagnoses);
            diagnosesWithCorrect.add(correctDiagnosis);

            if (diagnoses.size() > 1) {
                if (log.isLoggable(Level.INFO)) {
                    Set<Formula> constraintSet = new LinkedHashSet<>();
                    for (Diagnosis<Formula> diag : diagnoses) {
                        constraintSet.addAll(diag.getElements());
                    }
                    log.info("Found " + diagnoses.size() + " diagnoses with " + constraintSet
                            .size() + " different constraints.");
                }

                calculateEntailments(diagnosesWithCorrect);
                if (costsEstimator == null) {
                    calculateMeasuresUniform(diagnoses);
                } else {
                    calculateMeasures(diagnoses);
                }

                Set<Diagnosis<Formula>> diagSet = new LinkedHashSet<Diagnosis<Formula>>(diagnoses);
                Partition partition = partitioning.generatePartition(diagSet);

                // List<IUserQuery> possibleQueries = userInteraction.calculatePossibleQueries(diagnoses);
                // if (possibleQueries.size() == 0) {
                // System.out.println("No query options found!");
                // }
                // IUserQuery bestQuery = bestQueryFinder.findBestQuery(possibleQueries, diagnoses);

                // Update known conflicts
                if (REUSE_KNOWN_CONFLICTS && innerHSDagEngine != null) {
                    conflicts.clear();
                    conflicts.addAll(innerHSDagEngine.knownConflicts.getCollection());
                }

                simulateUserInteraction(partition, conflicts);
            }
        } while (diagnoses.size() > 1);

        finishedTime = System.nanoTime();

        diagnosisTime = diagnosisNanos / 1000000f;
        userInteractionTime = (finishedTime - startTime - diagnosisNanos) / 1000000f;

        return diagnoses;
    }

    /**
     * Calculates and sets the measures (probabilities of the given diagnoses using the costsEstimator.
     *
     * @param diagnoses
     */
    private void calculateMeasures(List<Diagnosis<Formula>> diagnoses) {
        for (Diagnosis<Formula> diag : diagnoses) {
//			List<Formula> correctStatements = new ArrayList<Formula>(sessionData.diagnosisModel.getPossiblyFaultyStatements());
//			correctStatements.removeAll(diag.getElements());
//			diag.setMeasure(costsEstimator.getFormulaSetCosts(correctStatements));
            diag.setMeasure(costsEstimator.getFormulaSetCosts(diag.getElements()));
        }
    }

    /**
     * Calculates and sets the measures (probabilities) of the given diagnoses.
     *
     * @param diagnoses
     */
    private void calculateMeasuresUniform(List<Diagnosis<Formula>> diagnoses) {
        int constraints = innerEngine.getModel().getPossiblyFaultyStatements().size() + innerEngine.getModel()
                .getCertainlyFaultyStatements().size();
        BigDecimal constraintProbability = BigDecimal.ONE
                .divide(BigDecimal.valueOf(constraints), Rounding.PRECISION, Rounding.ROUNDING_MODE);
        for (Diagnosis<Formula> diag : diagnoses) {
            int diagSize = diag.getElements().size();

            BigDecimal result = constraintProbability.pow(diagSize).multiply(
                    BigDecimal.ONE.subtract(constraintProbability).pow(constraints - diagSize));
            diag.setMeasure(result);
        }
    }

    /**
     * Calculates and sets the entailments of the given diagnoses.
     *
     * @param diagnoses
     */
    private void calculateEntailments(List<Diagnosis<Formula>> diagnoses) {
        for (Diagnosis<Formula> diag : diagnoses) {
            Set<Formula> entailments = new LinkedHashSet<>();

            if (ADD_EXPLICIT_STATEMENTS_AS_ENTAILMENTS) {
                entailments.addAll(innerEngine.getModel().getPossiblyFaultyStatements());
                entailments.removeAll(diag.getElements());
            }

            if (ADD_IMPLICIT_STATEMENTS_AS_ENTAILMENTS) {
                Set<Formula> implicitEntailments;
                QuickXPlain<Formula> qx = new QuickXPlain<>(getSessionData(), this);
                List<Formula> constraints = new ArrayList<>(getModel().getPossiblyFaultyStatements());
                constraints.removeAll(diag.getElements());
                constraints.addAll(getModel().getCorrectStatements());
                implicitEntailments = qx.calculateEntailments(constraints);
                entailments.addAll(implicitEntailments);
            }

            diag.changeEntailments(entailments);
        }
    }

    /**
     * Checks the constraints of the partition for correctness with regard to the correctDiagnosis and updates the tests.diagnosis model and the known
     * conflicts accordingly.
     *
     * @param partition
     */
    private void simulateUserInteraction(Partition<Formula> partition, List<List<Formula>> conflicts) {
        numberOfQueries++;
        if (log.isLoggable(Level.INFO)) {
            log.info("Asking for correctness of " + partition.partition.size() + " constraints with score "
                    + String.format("%.2f", partition.score.doubleValue()) + ": " + partition.toString());
        }
        DiagnosisModel<Formula> model = innerEngine.getModel();

        int nrFaultyExp = 0;
        int nrFaultyImp = 0;
        int nrCorrectExp = 0;
        int nrCorrectImp = 0;
        for (Formula c : partition.partition) {
            numberOfQueriedStatements++;

            if (correctDiagnosis.getElements().contains(c)) {
                // If the constraint is part of the correct tests.diagnosis, it is faulty
                model.getCertainlyFaultyStatements().add(c);
                nrFaultyExp++;

                // Remove all conflicts that contain this constraint, as they are already resolved by this certainly faulty constraint
                for (int i = 0; i < conflicts.size(); i++) {
                    List<Formula> conflict = conflicts.get(i);
                    if (conflict.contains(c)) {
                        conflicts.remove(conflict);
                        i--;
                    }
                }
            } else if (model.getPossiblyFaultyStatements().contains(c)) {
                // If the constraint is not part of the correct tests.diagnosis, but it is an explicit constraint of the possibly faulty statements, it is
                // correct
                model.getCorrectStatements().add(c);
                nrCorrectExp++;
            } else {
                // Else it is an implicit constraint determined by the tests.diagnosis engine, so we have to check if the constraint is entailed by the
                // correct tests.diagnosis

                if (correctDiagnosis.getEntailments().contains(c)) {
                    // entailed testcases
                    model.getCorrectStatements().add(c);
                    nrCorrectImp++;
                } else {
                    // If the faulty statement is an implicit statement, we cannot add it to the certainly faulty statements, as it shoudnt be part
                    // of the tests.diagnosis, so we add it to the not entailed examples
                    model.getNotEntailedExamples().add(c);
                    nrFaultyImp++;
                }
            }

            model.getPossiblyFaultyStatements().remove(c);
        }

        // Remove the asked statements from all conflicts
        for (int i = 0; i < conflicts.size(); i++) {
            List<Formula> conflict = conflicts.get(i);
            conflict.removeAll(partition.partition);
            if (conflict.size() == 0) {
                conflicts.remove(conflict);
                i--;
            }
        }

        if (log.isLoggable(Level.INFO)) {
            log.info(
                    nrCorrectExp + "/" + nrCorrectImp + " exp/imp are correct and " + nrFaultyExp + "/" + nrFaultyImp + " exp/imp are faulty.");
        }
    }

    @Override
    public void expandNodes(List<DAGNode<Formula>> nodesToExpand) throws DomainSizeException {
        // Nothing to do here

    }

}
