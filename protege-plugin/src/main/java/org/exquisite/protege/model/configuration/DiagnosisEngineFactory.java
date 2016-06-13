package org.exquisite.protege.model.configuration;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.HSDAGEngine;
import org.exquisite.core.engines.HSTreeEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.slf4j.LoggerFactory;

public class DiagnosisEngineFactory {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(DiagnosisEngineFactory.class.getName());

    private IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine;

    private OWLOntology ontology;

    private SearchConfiguration config;

    private OWLReasonerManager reasonerMan;

    public DiagnosisEngineFactory(OWLOntology ontology, OWLReasonerManager reasonerMan) {
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
            ConfigFileManager.writeConfiguration(newConfiguration);
            reset();
        }
    }

    public void reset() {
        readConfiguration();
        this. diagnosisEngine = createDiagnosisEngine();
    }

    public IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine() {
        if (diagnosisEngine == null)
            diagnosisEngine = createDiagnosisEngine();
        return diagnosisEngine;
    }

    private IDiagnosisEngine<OWLLogicalAxiom> createDiagnosisEngine() {

        IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = null;

        try {
            final OWLReasonerFactory reasonerFactory = this.reasonerMan.getCurrentReasonerFactory().getReasonerFactory();
            DiagnosisModel<OWLLogicalAxiom> diagnosisModel = ExquisiteOWLReasoner.generateDiagnosisModel(ontology, reasonerFactory, config.extractModules, config.reduceIncoherency);

            for (OWLIndividual ind : ontology.getIndividualsInSignature()) {
                diagnosisModel.getCorrectFormulas().addAll(ontology.getClassAssertionAxioms(ind));
                diagnosisModel.getCorrectFormulas().addAll(ontology.getObjectPropertyAssertionAxioms(ind));
            }
            diagnosisModel.getPossiblyFaultyFormulas().removeAll(diagnosisModel.getCorrectFormulas());

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
            logger.debug("created diagnosisEngine with calculation of maximal " + config.numOfLeadingDiags + " diagnoses using " + diagnosisEngine + " with reasoner " + reasoner  + " and diagnosisModel " + diagnosisModel);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        } catch (DiagnosisException e) {
            e.printStackTrace();
        } finally {
            return diagnosisEngine;
        }

    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DiagnosisEngineFactory{");
        sb.append("config=").append(config);
        sb.append(", diagnosisEngine=").append(diagnosisEngine);
        sb.append(", ontology=").append(ontology);
        sb.append(", reasonerMan=").append(reasonerMan);
        sb.append('}');
        return sb.toString();
    }


}
