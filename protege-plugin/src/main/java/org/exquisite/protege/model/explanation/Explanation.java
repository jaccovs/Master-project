package org.exquisite.protege.model.explanation;

import org.exquisite.core.DiagnosisRuntimeException;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.RepairOWLReasoner;
import org.exquisite.protege.model.preferences.DebuggerConfiguration;
import org.exquisite.protege.ui.panel.explanation.NoExplanationResult;
import org.exquisite.protege.ui.panel.repair.RepairDiagnosisPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.model.inference.ReasonerPreferences;
import org.protege.editor.owl.ui.explanation.ExplanationManager;
import org.protege.editor.owl.ui.explanation.ExplanationResult;
import org.protege.editor.owl.ui.explanation.ExplanationService;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author wolfi
 */
public class Explanation {

    private Logger logger = LoggerFactory.getLogger(Explanation.class.getCanonicalName());

    private RepairOWLReasoner reasoner;

    private List<OWLLogicalAxiom> notEntailedExamples;

    private OWLEditorKit editorKit;

    private RepairDiagnosisPanel panel;

    private ExplanationResult explanation = null;

    public Explanation(final Diagnosis<OWLLogicalAxiom> diagnosis, RepairDiagnosisPanel panel, OWLLogicalAxiom axiom, DiagnosisModel<OWLLogicalAxiom> originalDiagnosisModel, OWLEditorKit editorKit, OWLReasonerFactory reasonerFactory, DebuggerConfiguration config) throws OWLOntologyCreationException {
        this.editorKit = editorKit;
        this.panel = panel;
        this.notEntailedExamples = originalDiagnosisModel.getNotEntailedExamples();

        // TODO: also add the axioms from the possibly faulty formulas that are not member of the diagnosis to the set of correct axioms
        // TODO: move the selected axiom to the set of possibly faulty formulas (as its only element)
        // TODO: debug and find the reasyon why the views shows another diagnosis model as it is created here (the axioms (ontology) are correct, but represented in wrong formula sets)
        List<OWLLogicalAxiom> possiblyFaultyAxiomsMinusDiagnosis = new ArrayList<>();
        possiblyFaultyAxiomsMinusDiagnosis.addAll(originalDiagnosisModel.getPossiblyFaultyFormulas());
        possiblyFaultyAxiomsMinusDiagnosis.removeAll(diagnosis.getFormulas());

        List<OWLLogicalAxiom> correctFormulas = new ArrayList<>();
        correctFormulas.addAll(originalDiagnosisModel.getCorrectFormulas());
        correctFormulas.addAll(possiblyFaultyAxiomsMinusDiagnosis);

        List<OWLLogicalAxiom> possiblyFaultyFormulas = new ArrayList<>();
        possiblyFaultyFormulas.add(axiom);

        List<OWLLogicalAxiom> entailedExamples = new ArrayList<>();
        entailedExamples.addAll(originalDiagnosisModel.getEntailedExamples());

        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = new DiagnosisModel<>();
        diagnosisModel.setCorrectFormulas(correctFormulas);
        diagnosisModel.setPossiblyFaultyFormulas(possiblyFaultyFormulas);
        diagnosisModel.setEntailedExamples(entailedExamples);

        this.reasoner = new RepairOWLReasoner(diagnosisModel, reasonerFactory, this.editorKit.getOWLModelManager().getActiveOntology().getOWLOntologyManager());
        this.reasoner.setEntailmentTypes(config.getEntailmentTypes());

        reasoner.sync(possiblyFaultyFormulas);
    }

    public void dispose()  {
        if (this.explanation != null) this.explanation.dispose();

        final OWLOntology testOntology = reasoner.getDebuggingOntology();
        final boolean removedOntology = editorKit.getOWLModelManager().removeOntology(testOntology);
        if (!removedOntology) {
            logger.warn("Test-Ontology " + testOntology + " could not be removed!");
        } else {
            logger.debug("Test-Ontology " + testOntology + " successfully removed!");
        }
        reasoner.dispose();
    }

    /**
     * Shows an explanation for the currently selected axiom of a diagnosis.
     *
     * <ul>
     *     <li>If the ontology is inconsistent, this shows the explanation, why the selected axiom is responsible for the
     *     inconsistency</li>
     *     <li>If test ontology containing the selected axiom <strong>entails</strong> one of the negative test cases,
     *     it shows the explanation for this negative test case.</li>
     *     <li>No explanation in all other cases.</li>
     * </ul>
     */
    public void showExplanation() {
        verifyActiveOntology();
        final boolean isOntologyConsistent = isOntologyConsistent();

        logger.debug("Is ontology consistent? -> " + isOntologyConsistent);

        if (!isOntologyConsistent) {
            logger.debug("Explaining inconsistency");
            showExplanationForInconsistency();
        } else {
            final List<OWLLogicalAxiom> entailedTestCases = getEntailedTestCases();
            if (entailedTestCases.size() > 0 ) {
                for (OWLLogicalAxiom entailment : entailedTestCases) {
                    logger.debug("Explaining entailment " + entailment);
                    showExplanationForEntailment(entailment, "<html>The selected axiom is responsible for the <b>entailment</b> of <font color=\"blue\">" + entailment + "</font></html>");
                }
            } else {
                showNoExplanation();
            }
        }
    }

    public void showNoExplanation() {
        verifyActiveOntology();

        // clean up dangling resources
        if (this.explanation != null)
            this.explanation.dispose();

        this.explanation = new NoExplanationResult();
        panel.setExplanation(this.explanation);
    }

    private ExplanationManager getExplanationManager() {
        return editorKit.getModelManager().getExplanationManager();
    }

    private void showExplanationForInconsistency() {
        OWLModelManager owlModelManager = editorKit.getOWLModelManager();
        OWLDataFactory df = owlModelManager.getOWLDataFactory();
        OWLSubClassOfAxiom entailment = df.getOWLSubClassOfAxiom(df.getOWLThing(), df.getOWLNothing());

        this.showExplanationForEntailment(entailment, "<html>The selected axiom is responsible for an <b>inconsistency</b></html>");
    }

    private void synchronizeReasoner() {
        final OWLReasonerManager reasonerManager = editorKit.getOWLModelManager().getOWLReasonerManager();
        ReasonerPreferences preferences = reasonerManager.getReasonerPreferences();
        Set<InferenceType> precompute = preferences.getPrecomputedInferences();
        System.out.println("\n\n\n\n" + reasonerManager.getReasonerStatus() + "\n\n\n\n");
        final boolean b = reasonerManager.classifyAsynchronously(precompute);
    }

    private void showExplanationForEntailment(final OWLAxiom entailment, final String label) {
        synchronizeReasoner();

        if (!getExplanationManager().getExplainers().isEmpty()) {
            final ExplanationService explanationService = getExplanationManager().getExplainers().iterator().next();
            if (explanationService.hasExplanation(entailment)) {
                if (this.explanation!=null) this.explanation.dispose(); // dispose the previous explanation
                this.explanation = explanationService.explain(entailment);
                panel.setExplanation(this.explanation, label);
            } else {
                showNoExplanation();
            }
        } else {
            showNoExplanation();
        }
    }

    private void verifyActiveOntology() {
        final OWLModelManager modelManager = editorKit.getModelManager();
        if (!modelManager.getActiveOntology().equals(getOntology())) {
            modelManager.setActiveOntology(getOntology());
        }
    }

    /**
     * Checks whether the debugging ontology of the reasoner is consistent or not.
     *
     * @return <code>true</code> if ontology is consistent, otherwise <code>false</code>.
     */
    private boolean isOntologyConsistent() {
        boolean isConsistent;
        try {
            isConsistent = this.reasoner.isConsistent(Collections.emptySet());
        } catch (DiagnosisRuntimeException e) {
            isConsistent = false;
        }
        return isConsistent;
    }

    private List<OWLLogicalAxiom> getEntailedTestCases() {
        final List<OWLLogicalAxiom> entailedTestCases = new ArrayList<>();
        for (OWLLogicalAxiom testcase : this.notEntailedExamples) {
            if (this.reasoner.isEntailed(testcase)) {
                entailedTestCases.add(testcase);
            }
        }
        return entailedTestCases;
    }

    public OWLOntology getOntology() {
        return reasoner.getDebuggingOntology();
    }

    public DiagnosisModel getDiagnosisModel() {
        return reasoner.getDiagnosisModel();
    }

}
