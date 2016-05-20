package org.exquisite.protege.model.configuration;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 04.09.12
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
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

    public SearchConfiguration getConfig() {
        return config;
    }

    private void readConfiguration() {
        config = ConfigFileManager.readConfiguration();
    }

    public void updateConfig(SearchConfiguration newConfiguration) {
        ConfigFileManager.writeConfiguration(newConfiguration);
        reset();
    }

    public void reset() {

        readConfiguration();
        /*
        OWLTheory theoryOld = (OWLTheory) getSearch().getSearchable();

        search = null;
        OWLTheory theory = (OWLTheory) getSearch().getSearchable();
        copyTestcases(theoryOld,theory);
        */
        createDiagnosisEngine(); // TODO check if this works
    }

    public IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine() {
        if (diagnosisEngine == null)
            createDiagnosisEngine();
        return diagnosisEngine;
    }

    private void createDiagnosisEngine() {

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

            diagnosisEngine = new InverseDiagnosisEngine<>(reasoner);

            diagnosisEngine.setMaxNumberOfDiagnoses(config.numOfLeadingDiags);
            logger.debug("created diagnosisEngine with calculation of maximal " + config.numOfLeadingDiags + " diagnoses using " + diagnosisEngine + " with reasoner " + reasoner  + " and diagnosisModel " + diagnosisModel);

        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        } catch (DiagnosisException e) {
            e.printStackTrace();
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
