package org.exquisite.protege.model.configuration;

import org.exquisite.core.DiagnosisRuntimeException;
import org.exquisite.core.engines.HSDAGEngine;
import org.exquisite.core.engines.HSTreeEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.slf4j.LoggerFactory;

import java.util.TreeSet;

import static org.exquisite.protege.model.OntologyDiagnosisSearcher.SessionStopReason;

public class DiagnosisEngineFactory {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(DiagnosisEngineFactory.class.getName());

    private IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine;

    private OntologyDiagnosisSearcher ods;

    private OWLOntology ontology;

    private SearchConfiguration config;

    private OWLReasonerManager reasonerMan;

    public DiagnosisEngineFactory(OntologyDiagnosisSearcher ods, OWLOntology ontology, OWLReasonerManager reasonerMan) {
        this.ods = ods;
        this.ontology = ontology;
        this.reasonerMan = reasonerMan;
        readConfiguration();
    }

    public SearchConfiguration getSearchConfiguration() {
        return config;
    }

    private void readConfiguration() {
        config = ConfigFileManager.readConfiguration();
    }

    public void updateConfig(SearchConfiguration newConfiguration) {
        if (config.hasConfigurationChanged(newConfiguration)) {
            // as soon as the configuration has changed we do stop any currently running session
            ConfigFileManager.writeConfiguration(newConfiguration);
            if (ods.isSessionRunning()) {
                ods.doStopDebugging(SessionStopReason.PREFERENCES_CHANGED);
            }
        }
    }

    public void reset() {
        readConfiguration();
        this.diagnosisEngine = createDiagnosisEngine();
    }

    public IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine() {
        if (diagnosisEngine == null)
            diagnosisEngine = createDiagnosisEngine();
        return diagnosisEngine;
    }

    private IDiagnosisEngine<OWLLogicalAxiom> createDiagnosisEngine() throws DiagnosisRuntimeException {

        IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = null;

        try {
            final OWLReasonerFactory reasonerFactory = this.reasonerMan.getCurrentReasonerFactory().getReasonerFactory();
            DiagnosisModel<OWLLogicalAxiom> diagnosisModel = ExquisiteOWLReasoner.generateDiagnosisModel(ontology, reasonerFactory, config.extractModules, config.reduceIncoherency);

            for (OWLIndividual ind : ontology.getIndividualsInSignature()) {
                diagnosisModel.getCorrectFormulas().addAll(ontology.getClassAssertionAxioms(ind));
                diagnosisModel.getCorrectFormulas().addAll(ontology.getObjectPropertyAssertionAxioms(ind));
            }
            diagnosisModel.getPossiblyFaultyFormulas().removeAll(diagnosisModel.getCorrectFormulas());

            // make a snapshot of the 'original' entailed and non-entailed test cases
            ods.getTestcases().setOriginalEntailedTestcases(new TreeSet<>(diagnosisModel.getEntailedExamples()));
            ods.getTestcases().setOriginalNonEntailedTestcases(new TreeSet<>(diagnosisModel.getNotEntailedExamples()));

            ExquisiteOWLReasoner reasoner = new ExquisiteOWLReasoner(diagnosisModel, ontology.getOWLOntologyManager(), reasonerFactory);

            reasoner.setEntailmentTypes(config.getEntailmentTypes());

            switch (config.engineType) {
                case HSDAG:
                    diagnosisEngine = new HSDAGEngine<>(reasoner);
                    break;
                case HSTree:
                    diagnosisEngine = new HSTreeEngine<>(reasoner);
                    break;
                case Inverse:
                    diagnosisEngine = new InverseDiagnosisEngine<>(reasoner);
                    break;
                default:

                    break;
            }

            diagnosisEngine.setMaxNumberOfDiagnoses(config.numOfLeadingDiags);
            logger.debug("created diagnosisEngine with calculation of maximal " + config.numOfLeadingDiags + " diagnoses using " + diagnosisEngine + " with reasoner " + reasoner + " and diagnosisModel " + diagnosisModel);
            return diagnosisEngine;

        } catch (OWLOntologyCreationException e) {
            logger.error(e.getMessage(), e);
            throw new DiagnosisRuntimeException("An error occurred during creation of a diagnosis engine", e);
        }
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public OWLReasonerManager getReasonerManager() {
        return reasonerMan;
    }

    @Override
    public String toString() {
        return "DiagnosisEngineFactory{" + "engine=" + diagnosisEngine +
                ", ontology=" + ontology +
                ", config=" + config +
                ", reasonerMan=" + reasonerMan +
                '}';
    }

}
