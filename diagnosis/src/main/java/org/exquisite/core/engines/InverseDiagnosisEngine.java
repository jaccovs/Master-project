package org.exquisite.core.engines;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.DiagnosisRuntimeException;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ISolver;

import java.util.Set;

/**
 * Inverse diagnosis engine using InverseQuickXPlain.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author kostya
 * @author patrick
 * @author wolfi
 */
public class InverseDiagnosisEngine<F> extends AbstractDiagnosisEngine<F> {

    public InverseDiagnosisEngine(ISolver<F> solver) {
        super(solver);
    }

    @Override
    public Set<Diagnosis<F>> calculateDiagnoses() throws DiagnosisException {
        InverseQuickXPlain<F> inverseQuickXPlain = new InverseQuickXPlain<>(this.getSolver());
        return recDepthFirstSearch(inverseQuickXPlain, this.getDiagnoses());
    }

    private Set<Diagnosis<F>> recDepthFirstSearch(InverseQuickXPlain<F> inverseQuickXPlain, Set<Diagnosis<F>> diagnoses) throws DiagnosisException {

        // terminate computations if the required number of diagnoses is reached
        if (diagnoses.size() >= getMaxNumberOfDiagnoses())
            return diagnoses;

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
}
