package org.exquisite.diagnosis.quickxplain.ontologies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.quickxplain.ISolver;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDisjointClassesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;

import choco.kernel.model.constraints.Constraint;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;

/**
 * The ontology solver used by the ontologies benchmark.
 * @author Schmitz
 *
 */
public class OntologySolver implements ISolver {
	
	protected static final OWLClass TOP_CLASS = OWLManager.getOWLDataFactory().getOWLThing();
	protected static final boolean INCLUDE_AXIOMS_REFERENCING_THING = false;
	protected static boolean USE_HERMIT_REASONER = true;

	OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
	OWLOntology ontology;
	OWLReasoner reasoner = null;
//	PelletReasoner reasoner = null;
	
	List<Constraint> notEntailedExamples = null;
	
//	private static Set<OWLAnnotation> empty = new HashSet<OWLAnnotation>();
//	private static final Lock _mutex = new ReentrantLock(true);
	private static IRI iri = IRI.create("T");
	
	public OWLOntology getOntology() {
		return ontology;
	}
	
	public OWLReasoner getReasoner() {
		return reasoner;
	}
	
	@Override
	public void createModel(QuickXPlain qx, List<Constraint> constraints) {
		try {
//			_mutex.lock();
			// We need to use a custom IRI, because the automatic generation of IRIs can result in endless loops in multithreaded environments.
			ontology = ontologyManager.createOntology(iri);
//			_mutex.unlock();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		
		
		for (Constraint c: constraints) {
			OWLAxiom axiom = ((AxiomConstraint)c).getAxiom();
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
	public boolean isFeasible(IDiagnosisEngine diagnosisEngine) {
//		_mutex.lock();
		
		reasoner = createReasoner(ontology);

		boolean isConsistent = reasoner.isConsistent();
//		_mutex.unlock();
		
		if (!isConsistent) {
			return false;
		}
		
		// check that none of the "not entailed examples" is entailed
		if (notEntailedExamples != null) {
			for (Constraint c: notEntailedExamples) {
				if (reasoner.isEntailed(((AxiomConstraint)c).getAxiom())) {
					return false;
				}
			}
		}
		
		return true;
	}

	@Override
	public boolean isEntailed(IDiagnosisEngine diagnosisEngine, Set<Constraint> entailments) {
		reasoner = createReasoner(ontology);
		
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		for (Constraint c: entailments) {
			OWLAxiom axiom = ((AxiomConstraint)c).getAxiom();
			axioms.add(axiom);
		}
		boolean isEntailed = reasoner.isEntailed(axioms);
		return isEntailed;
	}

	@Override
	public Set<Constraint> calculateEntailments() {
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

        Set<Constraint> entailments = new LinkedHashSet<Constraint>();
        for (InferredAxiomGenerator<? extends OWLLogicalAxiom> axiomGenerator : axiomGenerators) {
            for (OWLLogicalAxiom ax : axiomGenerator.createAxioms(ontology.getOWLOntologyManager(), reasoner)) {
                if (!ontology.containsAxiom(ax))
                    if (!ax.getClassesInSignature().contains(TOP_CLASS) || INCLUDE_AXIOMS_REFERENCING_THING) {
                        entailments.add(new AxiomConstraint(ax));
                    }

            }
        }
        
        return entailments;
	}

}
