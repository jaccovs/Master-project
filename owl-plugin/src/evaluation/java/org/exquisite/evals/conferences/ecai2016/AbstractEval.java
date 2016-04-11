package org.exquisite.evals.conferences.ecai2016;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.junit.Test;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.OWLAPIConfigProvider;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxParserImpl;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author wolfi
 */
abstract public class AbstractEval {

    private ManchesterOWLSyntaxParser parser;

    private ExquisiteOWLReasoner createReasoner(File file) throws OWLOntologyCreationException, DiagnosisException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = man.loadOntologyFromOntologyDocument(file);

        parser = new ManchesterOWLSyntaxParserImpl(new OWLAPIConfigProvider(), man.getOWLDataFactory());

        ReasonerFactory reasonerFactory = new ReasonerFactory();
        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = ExquisiteOWLReasoner.generateDiagnosisModel(ontology, reasonerFactory, false, false);

        for (OWLIndividual ind : ontology.getIndividualsInSignature()) {
            diagnosisModel.getCorrectFormulas().addAll(ontology.getClassAssertionAxioms(ind));
            diagnosisModel.getCorrectFormulas().addAll(ontology.getObjectPropertyAssertionAxioms(ind));
        }
        diagnosisModel.getPossiblyFaultyFormulas().removeAll(diagnosisModel.getCorrectFormulas());

        return new ExquisiteOWLReasoner(diagnosisModel, ontology.getOWLOntologyManager(), reasonerFactory);
    }

    @Test
    public void evalQueryComputation() throws OWLOntologyCreationException, DiagnosisException {
        File ontology = new File(ClassLoader.getSystemResource(getOntology()).getFile());
        ExquisiteOWLReasoner reasoner = createReasoner(ontology);

        IDiagnosisEngine<OWLLogicalAxiom> engine = getDiagnosisEngine(reasoner);
        engine.setMaxNumberOfDiagnoses(getMaxNumberOfDiagnoses());

        Set<Diagnosis<OWLLogicalAxiom>> diagnoses = engine.calculateDiagnoses();


        setDiagnosesMeasures(diagnoses);

        reasoner.setEntailmentTypes(InferenceType.DISJOINT_CLASSES, InferenceType.CLASS_HIERARCHY);

        // query computation
        IQueryComputation<OWLLogicalAxiom> queryComputation = getQueryComputation(engine);
        queryComputation.initialize(diagnoses);

        int i = 1;
        if (queryComputation.hasNext()) {
            Query<OWLLogicalAxiom> query = queryComputation.next();
        }

    }
    @SafeVarargs
    public static <T> HashSet<T> getSet(T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }

    private IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ExquisiteOWLReasoner reasoner) {
        return new InverseDiagnosisEngine<>(reasoner);
    }

    abstract protected IQueryComputation<OWLLogicalAxiom> getQueryComputation(IDiagnosisEngine engine);

    abstract protected void setDiagnosesMeasures(Set<Diagnosis<OWLLogicalAxiom>> diagnoses);

    abstract protected String getOntology();

    abstract protected int getMaxNumberOfDiagnoses();
}
