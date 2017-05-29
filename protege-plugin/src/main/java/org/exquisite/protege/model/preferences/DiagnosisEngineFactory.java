package org.exquisite.protege.model.preferences;

import org.exquisite.core.DiagnosisRuntimeException;
import org.exquisite.core.engines.HSDAGEngine;
import org.exquisite.core.engines.HSTreeEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.exquisite.protege.Debugger;
import org.exquisite.protege.model.exception.DiagnosisModelCreationException;
import org.protege.editor.core.log.LogBanner;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.slf4j.LoggerFactory;

import java.util.TreeSet;

import static org.exquisite.protege.Debugger.SessionStopReason;

public class DiagnosisEngineFactory {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(DiagnosisEngineFactory.class.getName());

    private IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine;

    private Debugger debugger;

    private OWLOntology ontology;

    private DebuggerConfiguration config;

    private OWLReasonerManager reasonerMan;

    public DiagnosisEngineFactory(Debugger debugger, OWLOntology ontology, OWLReasonerManager reasonerMan) {
        this.debugger = debugger;
        this.ontology = ontology;
        this.reasonerMan = reasonerMan;
        readConfiguration();
    }

    public DebuggerConfiguration getSearchConfiguration() {
        return config;
    }

    private void readConfiguration() {
        config = ConfigFileManager.readConfiguration();
    }

    public void updateConfig(DebuggerConfiguration newConfiguration) {
        if (config.hasConfigurationChanged(newConfiguration)) {
            // we need the information later if the check type has changed
            final boolean hasCheckTypeChanged = config.hasCheckTypeChanged(newConfiguration);
            // as soon as the configuration has changed we do stop any currently running session
            ConfigFileManager.writeConfiguration(newConfiguration);
            // and immediately load the new config
            readConfiguration();

            if (debugger.isSessionRunning()) {
                debugger.doStopDebugging(SessionStopReason.PREFERENCES_CHANGED);
            } else {
                debugger.syncDiagnosisModel();
            }
        }
    }

    public void dispose() {
        // always remember the last preference settings before we close the application
        ConfigFileManager.dispose(config);
        if (this.diagnosisEngine != null )
            this.diagnosisEngine.dispose();
    }

    public void reset() {
        readConfiguration();
        this.diagnosisEngine = createDiagnosisEngine();
    }

    public IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine() {
        return diagnosisEngine;
    }

    private IDiagnosisEngine<OWLLogicalAxiom> createDiagnosisEngine() throws DiagnosisRuntimeException {

        IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = null;

        try {
            final OWLReasonerFactory reasonerFactory = this.reasonerMan.getCurrentReasonerFactory().getReasonerFactory();
            ExquisiteOWLReasoner reasoner = new ExquisiteOWLReasoner(this.debugger.getDiagnosisModel(), reasonerFactory, this.debugger.getReasonerProgressMonitor());

            reasoner.setEntailmentTypes(config.getEntailmentTypes());

            switch (config.engineType) {
                case HSDAG:
                    diagnosisEngine = new HSDAGEngine<OWLLogicalAxiom>(reasoner, config.createConflictSearcher(reasoner), debugger.getExquisiteProgressMonitor());
                    break;
                case HSTree:
                    diagnosisEngine = new HSTreeEngine<OWLLogicalAxiom>(reasoner, config.createConflictSearcher(reasoner), debugger.getExquisiteProgressMonitor());
                    break;
                case Inverse:
                    diagnosisEngine = new InverseDiagnosisEngine<OWLLogicalAxiom>(reasoner, config.createConflictSearcher(reasoner), debugger.getExquisiteProgressMonitor());
                    break;
                default:

                    break;
            }

            diagnosisEngine.setMaxNumberOfDiagnoses(config.numOfLeadingDiags);
            final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = this.debugger.getDiagnosisModel();

            logger.info(LogBanner.start("Debugger Settings"));
            logger.info("Diagnosis Engine: {}", diagnosisEngine);
            logger.info("Reasoner: {}", reasoner);
            logger.info("Leading Diagnoses: " + config.numOfLeadingDiags);
            logger.info("Enrich Query: " + config.enrichQuery);
            logger.info("Thresholds: {}, {}, {}", config.entropyThreshold, config.cardinalityThreshold, config.cautiousParameter);
            logger.info("Configuration [reduceIncoherency]: {}", config.reduceIncoherency);
            logger.info("Configuration [extractModules]: {}", config.extractModules);
            logger.info("Diagnosis Model:");
            logger.info("   {} Possibly Faulty Formulas", diagnosisModel.getPossiblyFaultyFormulas().size());
            logger.info("   {} Correct Formulas", diagnosisModel.getCorrectFormulas().size());
            logger.info("   {} Entailed Examples", diagnosisModel.getEntailedExamples().size());
            logger.info("   {} Not-Entailed Examples", diagnosisModel.getNotEntailedExamples().size());
            logger.info(LogBanner.end());

            return diagnosisEngine;
        } catch (OWLOntologyCreationException e) {
            logger.error(e.getMessage(), e);
            throw new DiagnosisRuntimeException("An error occurred during creation of a diagnosis engine", e);
        }
    }

    public DiagnosisModel<OWLLogicalAxiom> createDiagnosisModel() throws DiagnosisModelCreationException {
        try {
            final long start = System.currentTimeMillis();

            DiagnosisModel<OWLLogicalAxiom> diagnosisModel = ExquisiteOWLReasoner.generateDiagnosisModel(ontology);

            logger.info(LogBanner.start("Generated Diagnosis Model"));
            logger.info("Ontology: {}", ontology.getOntologyID());
            logger.info("Generated in {} ms", (System.currentTimeMillis() - start));
            logger.info("{} Possibly Faulty Formulas", diagnosisModel.getPossiblyFaultyFormulas().size());
            logger.info("{} Correct Formulas", diagnosisModel.getCorrectFormulas().size());
            logger.info("{} Entailed Examples", diagnosisModel.getEntailedExamples().size());
            logger.info("{} Not-Entailed Examples", diagnosisModel.getNotEntailedExamples().size());
            logger.info(LogBanner.end());

            for (OWLIndividual ind : ontology.getIndividualsInSignature()) {
                diagnosisModel.getCorrectFormulas().addAll(ontology.getClassAssertionAxioms(ind));
                diagnosisModel.getCorrectFormulas().addAll(ontology.getObjectPropertyAssertionAxioms(ind));
            }
            diagnosisModel.getPossiblyFaultyFormulas().removeAll(diagnosisModel.getCorrectFormulas());

            // make a snapshot of the 'original' entailed and non-entailed test cases
            debugger.getTestcases().setOriginalEntailedTestcases(new TreeSet<>(diagnosisModel.getEntailedExamples()));
            debugger.getTestcases().setOriginalNonEntailedTestcases(new TreeSet<>(diagnosisModel.getNotEntailedExamples()));

            return diagnosisModel;
        } catch (OWLOntologyCreationException | ReasonerInternalException e) {
            throw new DiagnosisModelCreationException(e);
        }
    }

    public DiagnosisModel<OWLLogicalAxiom> consistencyCheck(DiagnosisModel<OWLLogicalAxiom> dm) throws DiagnosisModelCreationException  {
        try {
            final OWLReasonerFactory reasonerFactory = this.reasonerMan.getCurrentReasonerFactory().getReasonerFactory();
            final long start = System.currentTimeMillis();

            logger.info(LogBanner.start("Settings for Consistency Check"));
            logger.info("Ontology: {}", ontology.getOntologyID());
            logger.info("OWLReasonerFactory: {}", reasonerFactory);
            logger.info("Configuration [extractModules]: {}", config.extractModules);
            logger.info("Configuration [reduceIncoherency]: {}", config.reduceIncoherency);
            logger.info(LogBanner.end());

            final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = ExquisiteOWLReasoner.consistencyCheck(dm, ontology, reasonerFactory, config.extractModules, config.reduceIncoherency, this.debugger.getReasonerProgressMonitor(), this.debugger.getExquisiteProgressMonitor());

            logger.info(LogBanner.start("Diagnosis Model"));
            logger.info("Checked in {} ms", (System.currentTimeMillis() - start));
            logger.info("{} Possibly Faulty Formulas", dm.getPossiblyFaultyFormulas().size());
            logger.info("{} Correct Formulas", dm.getCorrectFormulas().size());
            logger.info("{} Entailed Examples", dm.getEntailedExamples().size());
            logger.info("{} Not-Entailed Examples", dm.getNotEntailedExamples().size());
            logger.info(LogBanner.end());

            return diagnosisModel;
        } catch (RuntimeException e) {
            throw new DiagnosisModelCreationException(e);
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
