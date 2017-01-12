package org.exquisite.core.engines;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.DiagnosisRuntimeException;
import org.exquisite.core.IExquisiteProgressMonitor;
import org.exquisite.core.Utils;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ISolver;

import java.util.*;

import static org.exquisite.core.perfmeasures.PerfMeasurementManager.*;

/**
 * Inverse diagnosis engine using InverseQuickXPlain.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author kostya
 * @author patrick
 * @author wolfi
 */
public class InverseDiagnosisEngine<F> extends AbstractDiagnosisEngine<F> {

    private int sizeAlreadyFoundDiagnoses; // we save the number of already found diagnoses to notify the progress

    public InverseDiagnosisEngine(ISolver<F> solver) {
        super(solver);
    }

    public InverseDiagnosisEngine(ISolver<F> solver, IExquisiteProgressMonitor monitor) {
        super(solver, monitor);
    }

    @Override
    public Set<Diagnosis<F>> calculateDiagnoses() throws DiagnosisException {
        start(TIMER_INVERSE_DIAGNOSES);

        this.sizeAlreadyFoundDiagnoses = 0;
        notifyTaskStarted(); // progress

        try {
            InverseQuickXPlain<F> inverseQuickXPlain = new InverseQuickXPlain<>(this.getSolver());

            final List<F> correctFormulasCopy = new ArrayList<>(getSolver().getDiagnosisModel().getCorrectFormulas());
            final List<F> possiblyFaultyFormulasCopy = new ArrayList<>(getSolver().getDiagnosisModel().getPossiblyFaultyFormulas());

            Set<Diagnosis<F>> diagnoses = recDepthFirstSearch(inverseQuickXPlain, this.getDiagnoses(), new ArrayList<>());

            // method recDepthFirstSearch() manipulates as side effect the order of correctFormulas and possiblyFaultyFormulas
            // therefore we restore the original order.

            assert correctFormulasCopy.size() == getSolver().getDiagnosisModel().getCorrectFormulas().size();
            assert possiblyFaultyFormulasCopy.size() == getSolver().getDiagnosisModel().getPossiblyFaultyFormulas().size();

            getSolver().getDiagnosisModel().setPossiblyFaultyFormulas(possiblyFaultyFormulasCopy);
            getSolver().getDiagnosisModel().setCorrectFormulas(correctFormulasCopy);

            incrementCounter(COUNTER_INVERSE_DIAGNOSES);
            return diagnoses;
        } finally {
            notifyTaskStopped(); // progress
            stop(TIMER_INVERSE_DIAGNOSES);
        }
    }

    private Set<Diagnosis<F>> recDepthFirstSearch(InverseQuickXPlain<F> inverseQuickXPlain, Set<Diagnosis<F>> diagnoses, List<F> path) throws DiagnosisException {
        final int diagsSize = diagnoses.size();
        if (diagsSize > sizeAlreadyFoundDiagnoses) {
            sizeAlreadyFoundDiagnoses = diagsSize;
            notifyTaskProgress(sizeAlreadyFoundDiagnoses); // progress
        }

        // terminate computations if the required number of diagnoses is reached
        if (diagsSize >= getMaxNumberOfDiagnoses() || isCancelled()) {
            return diagnoses;
        }

        Set<Set<F>> newDiagnoses = getReusableDiagnosis(diagnoses, path);
        if (newDiagnoses == null)
            newDiagnoses = inverseQuickXPlain.findConflicts(this.getDiagnosisModel().getPossiblyFaultyFormulas()); // findConflicts() throws a DiagnosisException if conflict finding is not possible!

        assert newDiagnoses.size() <= 1; // InverseQuickXPlain just finds zero or one diagnosis

        for (Set<F> diagnosis : newDiagnoses) {

            diagnoses.add(new Diagnosis<>(diagnosis));

            for (F formula : diagnosis) {
                DiagnosisModel<F> model = getSolver().getDiagnosisModel();
                model.getPossiblyFaultyFormulas().remove(formula);
                try {
                    path.add(formula);
                    model.getCorrectFormulas().add(formula);
                    diagnoses = recDepthFirstSearch(inverseQuickXPlain, diagnoses, path); // findConflicts() in recDepthFirstSearch may throw DiagnosisException
                } catch (DiagnosisRuntimeException | DiagnosisException e) {
                    // this exception occurs only if we added an inconsistent set of formulas to the CorrectFormulas
                    // checkInput() may throw this DiagnosisException
                } finally {
                    model.getCorrectFormulas().remove(formula);
                    path.remove(formula);
                    model.getPossiblyFaultyFormulas().add(formula);
                }
            }
        }
        return diagnoses;
    }

    private Set<Set<F>> getReusableDiagnosis(Set<Diagnosis<F>> diagnoses, List<F> path) {
        for (Diagnosis<F> diagnosis : diagnoses)
            if (!Utils.hasIntersection(diagnosis.getFormulas(), path))
                return Collections.singleton(diagnosis.getFormulas());
        return null;
    }

    @Override
    public String toString() {
        return "InverseDiagnosisEngine";
    }
}
