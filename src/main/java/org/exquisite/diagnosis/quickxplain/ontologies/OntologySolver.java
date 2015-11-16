package org.exquisite.diagnosis.quickxplain.ontologies;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.core.ISolver;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.*;

import java.util.*;

/**
 * The ontology solver used by the ontologies benchmark.
 *
 * @author Schmitz
 */
public class OntologySolver implements ISolver<OWLLogicalAxiom> {

    protected static final OWLClass TOP_CLASS = OWLManager.getOWLDataFactory().getOWLThing();
    protected static final boolean INCLUDE_AXIOMS_REFERENCING_THING = false;
    protected static boolean USE_HERMIT_REASONER = true;
    //	private static Set<OWLAnnotation> empty = new HashSet<OWLAnnotation>();
//	private static final Lock _mutex = new ReentrantLock(true);
    private static IRI iri = IRI.create("T");
    OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
    OWLOntology ontology;
    //	PelletReasoner reasoner = null;
    OWLReasoner reasoner = null;
    List<OWLLogicalAxiom> notEntailedExamples = null;

    public OWLOntology getOntology() {
        return ontology;
    }

    public OWLReasoner getReasoner() {
        return reasoner;
    }

    @Override
    public void createModel(QuickXPlain<OWLLogicalAxiom> qx, List<OWLLogicalAxiom> constraints) {
        try {
//			_mutex.lock();
            // We need to use a custom IRI, because the automatic generation of IRIs can result in endless loops in multithreaded environments.
            ontology = ontologyManager.createOntology(iri);
//			_mutex.unlock();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }


        for (OWLLogicalAxiom axiom : constraints) {
            //OWLAxiom axiom = ((AxiomConstraint) c).getAxiom();
            ontologyManager.addAxiom(ontology, axiom);
        }

        if (qx != null && qx.currentDiagnosisModel != null) {
            notEntailedExamples = qx.currentDiagnosisModel.getNotEntailedExamples();
        }
    }

    protected OWLReasoner createReasoner(OWLOntology ontology) {
        if (USE_HERMIT_REASONER) {
            return new Reasoner(ontology);
        } else {
            return new PelletReasoner(ontology, BufferingMode.NON_BUFFERING);
        }
    }

    @Override
    public boolean isFeasible(IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine) {
//		_mutex.lock();

        reasoner = createReasoner(ontology);

        boolean isConsistent = reasoner.isConsistent();
//		_mutex.unlock();

        if (!isConsistent) {
            return false;
        }

        // check that none of the "not entailed examples" is entailed
        if (notEntailedExamples != null) {
            for (OWLLogicalAxiom axiom : notEntailedExamples) {
                if (reasoner.isEntailed(axiom)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean isEntailed(IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine, Set<OWLLogicalAxiom> entailments) {
        reasoner = createReasoner(ontology);

        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLLogicalAxiom axiom : entailments) {
            // OWLAxiom axiom = ((AxiomConstraint) c).getAxiom();
            axioms.add(axiom);
        }
        boolean isEntailed = reasoner.isEntailed(axioms);
        return isEntailed;
    }

    @Override
    public Set<OWLLogicalAxiom> calculateEntailments() {
        InferenceType[] infType = new InferenceType[]{InferenceType.CLASS_HIERARCHY, InferenceType.CLASS_ASSERTIONS,
                InferenceType.DISJOINT_CLASSES, InferenceType.DIFFERENT_INDIVIDUALS, InferenceType.SAME_INDIVIDUAL};
        List<InferredAxiomGenerator<? extends OWLLogicalAxiom>> axiomGenerators = new ArrayList<InferredAxiomGenerator<? extends OWLLogicalAxiom>>();
        axiomGenerators.add(new InferredSubClassAxiomGenerator());
        axiomGenerators.add(new InferredClassAssertionAxiomGenerator());
        axiomGenerators.add(new InferredEquivalentClassAxiomGenerator());
        axiomGenerators.add(new InferredDisjointClassesAxiomGenerator());
        axiomGenerators.add(new InferredPropertyAssertionGenerator());

        reasoner = createReasoner(ontology);
        reasoner.precomputeInferences(infType);

        Set<OWLLogicalAxiom> entailments = new LinkedHashSet<>();
        for (InferredAxiomGenerator<? extends OWLLogicalAxiom> axiomGenerator : axiomGenerators) {
            for (OWLLogicalAxiom ax : axiomGenerator.createAxioms(ontology.getOWLOntologyManager(), reasoner)) {
                if (!ontology.containsAxiom(ax))
                    if (!ax.getClassesInSignature().contains(TOP_CLASS) || INCLUDE_AXIOMS_REFERENCING_THING) {
                        entailments.add(ax);
                    }

            }
        }

        return entailments;
    }

}
