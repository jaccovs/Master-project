package org.exquisite.core.engines;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.DiagnosisRuntimeException;
import org.exquisite.core.ExquisiteProgressMonitor;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ISolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    private int diagsFound;

    public InverseDiagnosisEngine(ISolver<F> solver) {
        super(solver);
    }

    public InverseDiagnosisEngine(ISolver<F> solver, ExquisiteProgressMonitor monitor) {
        super(solver, monitor);
    }

    @Override
    public Set<Diagnosis<F>> calculateDiagnoses() throws DiagnosisException {
        start(TIMER_INVERSE_DIAGNOSES);

        this.diagsFound = 0;
        if (getMonitor() != null)
            getMonitor().taskStarted(ExquisiteProgressMonitor.DIAGNOSES_CALCULATION); // progress

        try {
            InverseQuickXPlain<F> inverseQuickXPlain = new InverseQuickXPlain<>(this.getSolver());

            final List<F> correctFormulasCopy = new ArrayList<>(getSolver().getDiagnosisModel().getCorrectFormulas());
            final List<F> possiblyFaultyFormulasCopy = new ArrayList<>(getSolver().getDiagnosisModel().getPossiblyFaultyFormulas());

            Set<Diagnosis<F>> diagnoses = recDepthFirstSearch(inverseQuickXPlain, this.getDiagnoses());

            // method recDepthFirstSearch() manipulates as side effect the order of correctFormulas and possiblyFaultyFormulas
            // therefore we restore the original order.

            assert correctFormulasCopy.size() == getSolver().getDiagnosisModel().getCorrectFormulas().size();
            assert possiblyFaultyFormulasCopy.size() == getSolver().getDiagnosisModel().getPossiblyFaultyFormulas().size();

            getSolver().getDiagnosisModel().setPossiblyFaultyFormulas(possiblyFaultyFormulasCopy);
            getSolver().getDiagnosisModel().setCorrectFormulas(correctFormulasCopy);

            incrementCounter(COUNTER_INVERSE_DIAGNOSES);
            return diagnoses;
        } finally {
            if (getMonitor() != null) getMonitor().taskStopped(); // progress
            stop(TIMER_INVERSE_DIAGNOSES);
        }
    }

    private Set<Diagnosis<F>> recDepthFirstSearch(InverseQuickXPlain<F> inverseQuickXPlain, Set<Diagnosis<F>> diagnoses) throws DiagnosisException {
        // progress monitor
        if (getMonitor()!=null && diagnoses.size() > diagsFound) {
            diagsFound = diagnoses.size();
            getMonitor().taskProgressChanged(diagsFound + " of " + getMaxNumberOfDiagnoses() + " diagnoses found.",diagnoses.size(), getMaxNumberOfDiagnoses());
        }

        // terminate computations if the required number of diagnoses is reached
        if (diagnoses.size() >= getMaxNumberOfDiagnoses()) {
            return diagnoses;
        }

        Set<Set<F>> newDiagnoses = inverseQuickXPlain.findConflicts(this.getDiagnosisModel().getPossiblyFaultyFormulas());
        assert newDiagnoses.size() <= 1; // InverseQuickXPlain just finds zero or one diagnosis

        for (Set<F> diagnosis : newDiagnoses) {
            diagnoses.add(new Diagnosis<>(diagnosis));

            for (F formula : diagnosis) {
                DiagnosisModel<F> model = getSolver().getDiagnosisModel();
                model.getPossiblyFaultyFormulas().remove(formula);
                try {
                    model.getCorrectFormulas().add(formula);
                    diagnoses = recDepthFirstSearch(inverseQuickXPlain, diagnoses);
                } catch (DiagnosisRuntimeException | DiagnosisException e) {
                    // this exception occurs only if we added an inconsistent set of formulas to the CorrectFormulas
                    // checkInput() may throw this DiagnosisException
                } finally {
                    model.getCorrectFormulas().remove(formula);
                    model.getPossiblyFaultyFormulas().add(formula);
                }
            }
        }
        return diagnoses;
    }

    @Override
    public String toString() {
        return "InverseDiagnosisEngine";
    }
}
