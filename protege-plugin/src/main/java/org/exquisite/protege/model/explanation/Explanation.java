package org.exquisite.protege.model.explanation;

import org.exquisite.core.DiagnosisRuntimeException;
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

    public Explanation(RepairDiagnosisPanel panel, OWLLogicalAxiom axiom, DiagnosisModel<OWLLogicalAxiom> originalDiagnosisModel, OWLEditorKit editorKit, OWLReasonerFactory reasonerFactory, DebuggerConfiguration config) throws OWLOntologyCreationException {
        this.editorKit = editorKit;
        this.panel = panel;

        this.notEntailedExamples = originalDiagnosisModel.getNotEntailedExamples();

        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = new DiagnosisModel<>();
        diagnosisModel.setCorrectFormulas(originalDiagnosisModel.getCorrectFormulas());
        diagnosisModel.getCorrectFormulas().add(axiom);
        diagnosisModel.setEntailedExamples(originalDiagnosisModel.getEntailedExamples());

        this.reasoner = new RepairOWLReasoner(diagnosisModel, reasonerFactory, this.editorKit.getOWLModelManager().getActiveOntology().getOWLOntologyManager());
        this.reasoner.setEntailmentTypes(config.getEntailmentTypes());
    }

    public void dispose()  {
        if (this.explanation != null) this.explanation.dispose();

        final OWLOntology debuggingOntology = reasoner.getDebuggingOntology();
        final boolean removedOntology = editorKit.getOWLModelManager().removeOntology(debuggingOntology);
        if (!removedOntology) logger.warn("Ontology " + debuggingOntology + " could not be removed!");
        reasoner.dispose();
    }

    public void showExplanation() {
        verifyActiveOntology();
        final boolean isConsistent = isConsistent();

        logger.debug("Is ontology consistent? -> " + isConsistent);

        if (!isConsistent) {
            logger.debug("Explaining inconsistency");
            showExplanationForInconsistency();
        } else {
            final List<OWLLogicalAxiom> entailedTestCases = getEntailedTestCases();
            if (entailedTestCases.size() > 0 ) {
                for (OWLLogicalAxiom entailment : entailedTestCases) {
                    logger.debug("Explaining entailment " + entailment);
                    showExplanationForEntailment(entailment);
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
        OWLSubClassOfAxiom ax = df.getOWLSubClassOfAxiom(df.getOWLThing(), df.getOWLNothing());

        this.showExplanationForEntailment(ax);
    }

    private void synchronizeReasoner() {
        final OWLReasonerManager reasonerManager = editorKit.getOWLModelManager().getOWLReasonerManager();
        ReasonerPreferences preferences = reasonerManager.getReasonerPreferences();
        Set<InferenceType> precompute = preferences.getPrecomputedInferences();
        System.out.println("\n\n\n\n" + reasonerManager.getReasonerStatus() + "\n\n\n\n");
        final boolean b = reasonerManager.classifyAsynchronously(precompute);
    }

    private void showExplanationForEntailment(OWLAxiom entailment) {
        synchronizeReasoner();

        if (!getExplanationManager().getExplainers().isEmpty()) {
            final ExplanationService explanationService = getExplanationManager().getExplainers().iterator().next();
            if (explanationService.hasExplanation(entailment)) {
                if (this.explanation!=null) this.explanation.dispose();
                this.explanation = explanationService.explain(entailment);
                panel.setExplanation(this.explanation);
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

    private boolean isConsistent() {
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
