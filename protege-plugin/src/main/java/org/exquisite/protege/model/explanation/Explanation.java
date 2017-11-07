package org.exquisite.protege.model.explanation;

import org.exquisite.core.DiagnosisRuntimeException;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.RepairOWLReasoner;
import org.exquisite.protege.Debugger;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.explanation.JustificationBasedExplanationServiceImpl;
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
 * The explanation for an axiom. Each repair list item holds a reference to one instance of this class.
 *
 * @author wolfi
 */
public class Explanation {

    private Logger logger = LoggerFactory.getLogger(Explanation.class.getCanonicalName());

    private RepairOWLReasoner reasoner;

    private List<OWLLogicalAxiom> notEntailedExamples;

    private OWLEditorKit editorKit;

    private RepairDiagnosisPanel panel;

    private ExplanationResult explanation = null;

    public Explanation(Diagnosis<OWLLogicalAxiom> diagnosis, RepairDiagnosisPanel panel, OWLLogicalAxiom axiom, OWLEditorKit editorKit, Debugger debugger) throws OWLOntologyCreationException {
        this(diagnosis, panel, axiom, debugger.getDiagnosisModel(), editorKit, debugger.getDiagnosisEngineFactory().getReasonerFactory(), debugger.getDiagnosisEngineFactory().getDebuggerConfiguration());
    }

    private Explanation(final Diagnosis<OWLLogicalAxiom> diagnosis, RepairDiagnosisPanel panel, OWLLogicalAxiom axiom, DiagnosisModel<OWLLogicalAxiom> originalDiagnosisModel, OWLEditorKit editorKit, OWLReasonerFactory reasonerFactory, DebuggerConfiguration config) throws OWLOntologyCreationException {
        this.editorKit = editorKit;
        this.panel = panel;
        this.notEntailedExamples = originalDiagnosisModel.getNotEntailedExamples();

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

        this.reasoner = new RepairOWLReasoner(diagnosisModel, reasonerFactory, this.editorKit.getOWLModelManager().getActiveOntology().getOWLOntologyManager(), config.getEntailmentTypes());
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
        if (!panel.isExplanationEnabled()) {
            showNoExplanation(null);// TODO deactivate this and activate the next statement
        } else {
            verifyActiveOntology();
            final boolean isOntologyConsistent = isOntologyConsistent();

            logger.debug("Is ontology consistent? -> " + isOntologyConsistent);

            if (!isOntologyConsistent) {
                logger.debug("Explaining inconsistency");
                showExplanationForInconsistency();
            } else {
                final List<OWLLogicalAxiom> entailedTestCases = getEntailedTestCases();
                if (entailedTestCases.size() > 0) {
                    for (OWLLogicalAxiom entailment : entailedTestCases) {
                        logger.debug("Explaining entailment " + entailment);
                        showExplanationForEntailment(entailment, "<html>The selected axiom is responsible for the <b>entailment</b> of <font color=\"blue\">" + entailment + "</font></html>");
                    }
                } else {
                    showConsistencyExplanation();
                }
            }
        }
    }

    public void showNoExplanation(final String label) {
        verifyActiveOntology();

        // clean up dangling resources
        if (this.explanation != null)
            this.explanation.dispose();

        this.explanation = new NoExplanationResult();
        panel.setExplanation(this.explanation, label);
    }

    public void showConsistencyExplanation() {
        verifyActiveOntology();

        // clean up dangling resources
        if (this.explanation != null)
            this.explanation.dispose();

        this.explanation = new NoExplanationResult();
        panel.setExplanation(this.explanation, "The selected axiom is consistent!");
    }

    public OWLOntology getOntology() {
        return reasoner.getDebuggingOntology();
    }

    public DiagnosisModel<OWLLogicalAxiom> getDiagnosisModel() {
        return reasoner.getDiagnosisModel();
    }

    public boolean modifyAxiom(OWLLogicalAxiom newAxiom, OWLLogicalAxiom oldAxiom) {
        assert getDiagnosisModel().getPossiblyFaultyFormulas().contains(oldAxiom);

        final boolean hasBeenModified = reasoner.modifyAxiom(newAxiom, oldAxiom);
        if (hasBeenModified) {
            try {
                getDiagnosisModel().getPossiblyFaultyFormulas().remove(oldAxiom);
            } catch (DiagnosisRuntimeException e) {
                // the diagnosis model may become inconsistent!
            }

            try {
                getDiagnosisModel().getPossiblyFaultyFormulas().add(newAxiom);
            } catch (DiagnosisRuntimeException e) {
                // the diagnosis model may become inconsistent!
            }

            getDebugger().setDiagnosisModel(getDiagnosisModel());
            showExplanation();
        }
        return hasBeenModified;
    }

    public boolean deleteAxiom(OWLLogicalAxiom axiom) {
        assert getDiagnosisModel().getPossiblyFaultyFormulas().contains(axiom);

        final boolean isDeleted = reasoner.deleteAxiom(axiom);
        if (isDeleted) {
            try {
                getDiagnosisModel().getPossiblyFaultyFormulas().remove(axiom);
            } catch (DiagnosisRuntimeException e) {
                // the diagnosis model can become inconsistent!
            }
            getDebugger().setDiagnosisModel(getDiagnosisModel());
            showNoExplanation(null);
        }
        return isDeleted;
    }

    public boolean restoreAxiom(OWLLogicalAxiom axiom) {
        final boolean hasBeenRestored = reasoner.restoreAxiom(axiom);

        if (hasBeenRestored) {
            try {
                getDiagnosisModel().getPossiblyFaultyFormulas().add(axiom);
            } catch (DiagnosisRuntimeException e) {
                // the diagnosis model can become inconsistent!
            }

            getDebugger().setDiagnosisModel(getDiagnosisModel());
            showExplanation();
        }
        return hasBeenRestored;
    }

    public OWLEditorKit getOWLEditorKit() {
        return editorKit;
    }

    private Debugger getDebugger() {
        return ((EditorKitHook) editorKit.get("org.exquisite.protege.EditorKitHook")).getActiveOntologyDebugger();
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
        logger.debug("\n\n\n\n" + reasonerManager.getReasonerStatus() + "\n\n\n\n");
        final boolean b = reasonerManager.classifyAsynchronously(precompute);
    }

    private void showExplanationForEntailment(final OWLAxiom entailment, final String label) {
        synchronizeReasoner();

        final ExplanationService explanationService = new JustificationBasedExplanationServiceImpl(getOWLEditorKit());
        if (explanationService.hasExplanation(entailment)) {
            if (this.explanation!=null) this.explanation.dispose(); // dispose the previous explanation
            this.explanation = explanationService.explain(entailment);
            panel.setExplanation(this.explanation, label);
        } else {
            showNoExplanation(null);
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

}
