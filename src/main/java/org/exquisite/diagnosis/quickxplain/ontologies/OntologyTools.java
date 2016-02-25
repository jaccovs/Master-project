package org.exquisite.diagnosis.quickxplain.ontologies;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

import java.util.*;

/**
 * Class for static ontology tool functions.
 *
 * @author Schmitz
 */
public class OntologyTools {

    protected static OWLOntology createCopyForExtraction(OWLOntology ontology) {

        OWLOntology result = null;
        try {
            result = OWLManager.createOWLOntologyManager()
                    .createOntology(IRI.create("http://ainf.at/TempExtractionOntology"));
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        result.getOWLOntologyManager().addAxioms(result, ontology.getLogicalAxioms());
        return result;
    }

    public static OWLOntology getIncoherentPartAsOntology(OWLOntology ontology) {
        return extract(ontology, false, false).iterator().next();
    }

    protected static Set<OWLOntology> extract(OWLOntology ont, boolean multiple, boolean useMultiple) {

        Set<OWLEntity> signature = new LinkedHashSet<OWLEntity>();
        OWLOntology ontology = createCopyForExtraction(ont);
        OWLReasoner reasoner = new PelletReasoner(ontology, BufferingMode.NON_BUFFERING);
        Set<OWLAxiom> aBoxAxioms = null;
        boolean consistent = reasoner.isConsistent();
        if (!consistent) {
            //OWLOntologyManager man = OWLManager.createOWLOntologyManager(); //ontology.getOWLOntologyManager();
            aBoxAxioms = ontology.getABoxAxioms(false);      //true
            ontology.getOWLOntologyManager().removeAxioms(ontology, aBoxAxioms);
            reasoner.flush();
            if (!reasoner.isConsistent())
                throw new RuntimeException("The ontology without ABox is not consistent! Reasoner Flush Problem? ");
            /*for (OWLAxiom aBoxAxiom : aBoxAxioms) {
                // if contains negation
                signature.addAll(aBoxAxiom.getClassesInSignature());
            }*/
        }

        for (OWLClass entity : reasoner.getUnsatisfiableClasses().getEntities())
            signature.add(entity);

        signature.remove(OWLManager.getOWLDataFactory().getOWLNothing());


        Set<OWLOntology> result;

        SyntacticLocalityModuleExtractor sme = new SyntacticLocalityModuleExtractor(
                OWLManager.createOWLOntologyManager(), ontology, ModuleType.STAR);


        String iriString = "http://ainf.at/IncoherencyModule";
        try {
            if (!signature.isEmpty()) {
                if (multiple) {
                    result = new LinkedHashSet<OWLOntology>();
                    int cnt = 0;
                    for (OWLEntity i : signature) {
                        result.add(sme.extractAsOntology(Collections.singleton(i), IRI.create(iriString + "_" + cnt)));
                        cnt++;
                    }
                } else {
                    if (!useMultiple) {
                        result = Collections.singleton(sme.extractAsOntology(signature, IRI.create(iriString)));
                    } else {
                        result = new LinkedHashSet<OWLOntology>();
                        int cnt = 0;
                        for (OWLEntity i : signature) {
                            result.add(
                                    sme.extractAsOntology(Collections.singleton(i), IRI.create(iriString + "_" + cnt)));
                            cnt++;
                        }
                        Set<OWLLogicalAxiom> axioms = new HashSet<OWLLogicalAxiom>();
                        for (OWLOntology on : result)
                            axioms.addAll(on.getLogicalAxioms());
                        result = Collections
                                .singleton(OWLManager.createOWLOntologyManager().createOntology(IRI.create(iriString)));
                        OWLOntology on = result.iterator().next();
                        on.getOWLOntologyManager().addAxioms(on, axioms);
                    }
                }

            } else
                result = Collections
                        .singleton(OWLManager.createOWLOntologyManager().createOntology(IRI.create(iriString)));
        } catch (OWLOntologyCreationException e) {
            result = null;
        }

        if (!consistent)
            ontology.getOWLOntologyManager().addAxioms(ontology, aBoxAxioms);

        return result;

    }

    /**
     * Adds individuals for all unsatisfiable classes to make the ontology inconsistent instead of only incoherent.
     *
     * @param model
     */
    public static void reduceToUnsatisfiability(DiagnosisModel<OWLLogicalAxiom> model) {
//        LinkedHashSet<OWLLogicalAxiom> backupCachedFormulars = new LinkedHashSet<OWLLogicalAxiom>(getReasoner().getFormulasCache());
//        getReasoner().clearFormulasCache();
//        getReasoner().addFormulasToCache(getOriginalOntology().getLogicalAxioms());
        OntologySolver solver = new OntologySolver();
        List<OWLLogicalAxiom> allConstraints = new ArrayList<>(model.getCorrectStatements());
        allConstraints.addAll(model.getPossiblyFaultyStatements());
        solver.createModel(null, allConstraints);
        if (solver.isFeasible()) {
            Set<OWLClass> entities = solver.getReasoner().getUnsatisfiableClasses().getEntities();
            entities.remove(OWLManager.getOWLDataFactory().getOWLNothing());
            if (!entities.isEmpty()) {
                String iri = "http://ainf.at/testiri#";
                OWLDataFactory fac = solver.getOntology().getOWLOntologyManager().getOWLDataFactory();
                // TODO module d extraction machen
                for (OWLClass cl : entities) {
                    OWLIndividual test_individual = fac
                            .getOWLNamedIndividual(IRI.create(iri + "d_" + cl.getIRI().getFragment()));
//                    getKnowledgeBase().addBackgroundFormulas(Collections.<OWLLogicalAxiom>singleton(fac.getOWLClassAssertionAxiom(cl, test_individual)));
                    OWLLogicalAxiom ax = fac.getOWLClassAssertionAxiom(cl, test_individual);
                    model.addCorrectFormula(ax, ax.toString());
                }
            }
        }

//        getReasoner().clearFormulasCache();
//        getReasoner().addFormulasToCache(backupCachedFormulars);

    }

    public static DiagnosisModel<OWLLogicalAxiom> createDiagnosisModel(OWLOntology ontology,
                                                                       boolean setAssertionsCorrect) {
        DiagnosisModel<OWLLogicalAxiom> diagModel = new DiagnosisModel<>();

        for (OWLLogicalAxiom axiom : ontology.getLogicalAxioms()) {
            if (setAssertionsCorrect && axiom.toString().contains("Assertion")) {
                diagModel.addCorrectFormula(axiom, axiom.toString());
            } else {
                diagModel.addPossiblyFaultyConstraint(axiom, axiom.toString());
            }
        }

        diagModel.getPositiveExamples().add(new Example<>());

        return diagModel;
    }

}
