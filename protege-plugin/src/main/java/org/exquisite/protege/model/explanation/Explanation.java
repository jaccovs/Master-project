package org.exquisite.protege.model.explanation;

import org.exquisite.core.DiagnosisRuntimeException;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.RepairOWLReasoner;
import org.exquisite.protege.model.preferences.DebuggerConfiguration;
import org.exquisite.protege.ui.panel.explanation.NoExplanationResult;
import org.exquisite.protege.ui.panel.repair.RepairDiagnosisPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.explanation.ExplanationManager;
import org.protege.editor.owl.ui.explanation.ExplanationResult;
import org.protege.editor.owl.ui.explanation.ExplanationService;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author wolfi
 */
public class Explanation {

    private Logger logger = LoggerFactory.getLogger(Explanation.class.getCanonicalName());

    private RepairOWLReasoner reasoner;

    private DiagnosisModel<OWLLogicalAxiom> diagnosisModel;

    private List<OWLLogicalAxiom> notEntailedExamples;

    private OWLLogicalAxiom originalAxiom;

    private OWLEditorKit editorKit;

    public Explanation(OWLLogicalAxiom axiom, DiagnosisModel<OWLLogicalAxiom> dm, OWLEditorKit editorKit, OWLReasonerFactory reasonerFactory, DebuggerConfiguration config) throws OWLOntologyCreationException {
        this.originalAxiom = axiom;
        this.editorKit = editorKit;

        this.notEntailedExamples = dm.getNotEntailedExamples();

        this.diagnosisModel = new DiagnosisModel<>();
        this.diagnosisModel.setCorrectFormulas(dm.getCorrectFormulas());
        this.diagnosisModel.getCorrectFormulas().add(axiom);
        this.diagnosisModel.setEntailedExamples(dm.getEntailedExamples());

        this.reasoner = new RepairOWLReasoner(this.diagnosisModel, reasonerFactory, this.editorKit.getOWLModelManager().getActiveOntology().getOWLOntologyManager());
        this.reasoner.setEntailmentTypes(config.getEntailmentTypes());
    }

    public void dispose()  {
        final OWLOntology debuggingOntology = reasoner.getDebuggingOntology();
        final boolean removedOntology = editorKit.getOWLModelManager().removeOntology(debuggingOntology);
        if (!removedOntology) logger.warn("Ontology " + debuggingOntology + " could not be removed!");
        reasoner.dispose();
    }

    public void explain(OWLLogicalAxiom axiom, RepairDiagnosisPanel panel) {

        // we must check if the originalAxiom has changed meanwhile
        if (!axiom.equals(this.originalAxiom)) {
            // if the originalAxiom has changed then we must change the diagnosis model too
            diagnosisModel.getCorrectFormulas().remove(this.originalAxiom);
            diagnosisModel.getCorrectFormulas().add(axiom);
            this.originalAxiom = axiom;
        }

        final boolean isConsistent = isConsistent();
        logger.debug("Is ontology with " + axiom + " consistent? -> " + isConsistent);

        if (!isConsistent) {
            logger.debug("Explaining inconsistency");
            explainInconsistency(panel);
        } else {
            final List<OWLLogicalAxiom> entailedTestCases = getEntailedTestCases();
            if (entailedTestCases.size() > 0 ) {
                for (OWLLogicalAxiom entailment : entailedTestCases) {
                    logger.debug("Explaining entailment " + entailment);
                    explainEntailment(entailment, panel);
                }
            } else {
                // show no explanation (todo)
                panel.setExplanation(new NoExplanationResult());
            }
        }
    }

    private ExplanationManager getExplanationManager() {
        return editorKit.getModelManager().getExplanationManager();
    }

    private void explainInconsistency(RepairDiagnosisPanel panel) {
        OWLModelManager owlModelManager = editorKit.getOWLModelManager();
        OWLDataFactory df = owlModelManager.getOWLDataFactory();
        OWLSubClassOfAxiom ax = df.getOWLSubClassOfAxiom(df.getOWLThing(), df.getOWLNothing());
        this.explainEntailment(ax, panel);
    }

    private void explainEntailment(OWLAxiom entailment, RepairDiagnosisPanel panel) {
        //final OWLOntology activeOntology = editorKit.getOWLModelManager().getActiveOntology();
        final OWLModelManager modelManager = editorKit.getModelManager();

        try {
            // change active ontology to the the repair debugging ontology
            final OWLOntology debuggingOntology = getOntology();
            modelManager.setActiveOntology(debuggingOntology);
            Collection<ExplanationService> teachers = getExplanationManager().getTeachers(entailment);
            if (teachers.size() >= 1) {
                final ExplanationService explanationService = teachers.iterator().next();
                final ExplanationResult explanation = explanationService.explain(entailment);
                panel.setExplanation(explanation);
            }
        } finally {
            // change active ontology to the the original ontology
            //modelManager.setActiveOntology(activeOntology);
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

}
