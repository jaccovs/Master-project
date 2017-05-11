package org.exquisite.core.parser;

import org.exquisite.core.AbstractTest;
import org.exquisite.core.DiagnosisException;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.exquisite.core.utils.OWLUtils;
import org.junit.Test;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntax;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * @author wolfi
 */
public class TestOWLAxiomKeywordCounter extends AbstractTest {

    @Test
    public void testOntologyEcai2010() throws OWLOntologyCreationException, DiagnosisException {
        testOntology("ontologies/ecai2010.owl");
    }

    @Test
    public void testOntologyEconomySDA() throws OWLOntologyCreationException, DiagnosisException {
        testOntology("ontologies/Economy-SDA.owl");
    }

    @Test
    public void testOntologyKoala() throws OWLOntologyCreationException, DiagnosisException {
        testOntology("ontologies/koala.owl");
    }

    @Test
    public void testOntologyMiniTambis() throws OWLOntologyCreationException, DiagnosisException {
        testOntology("ontologies/miniTambis.owl");
    }

    @Test
    public void testOntologyRunningExample() throws OWLOntologyCreationException, DiagnosisException {
        testOntology("ontologies/running_example_annotated.owl");
    }

    @Test
    public void testOntologyTransportation() throws OWLOntologyCreationException, DiagnosisException {
        testOntology("ontologies/Transportation-SDA.owl");
    }

    @Test
    public void testOntologyUniversity() throws OWLOntologyCreationException, DiagnosisException {
        testOntology("ontologies/University.owl");
    }

    @Test
    public void testOntologyW3CPrimer() throws OWLOntologyCreationException, DiagnosisException {
        // the additional mapping is necessary to correctly import the local ontology families.owl
        String importedOntology = ClassLoader.getSystemResource("ontologies/families.owl").getFile();
        OWLOntologyIRIMapper[] mappers = {new SimpleIRIMapper(IRI.create("http://example.org/otherOntologies/families.owl"),IRI.create("file://"+importedOntology))};

        testOntology("ontologies/primer.owl", mappers);
    }

    private void testOntology(String ontology, OWLOntologyIRIMapper... ontologyIRIMappers) throws OWLOntologyCreationException, DiagnosisException {
        ExquisiteOWLReasoner reasoner = loadOntology(ontology, ontologyIRIMappers);

        for (OWLLogicalAxiom axiom : reasoner.getDiagnosisModel().getPossiblyFaultyFormulas()) {
            testOWLAxiomKeywordCounter(axiom);
        }
    }

    private void testOntology(String ontology) throws OWLOntologyCreationException, DiagnosisException {
        ExquisiteOWLReasoner reasoner = loadOntology(ontology);

        for (OWLLogicalAxiom axiom : reasoner.getDiagnosisModel().getPossiblyFaultyFormulas()) {
            testOWLAxiomKeywordCounter(axiom);
        }
    }

    private void testOWLAxiomKeywordCounter(OWLLogicalAxiom axiom) {
        OWLAxiomKeywordCounter visitor = new OWLAxiomKeywordCounter();
        axiom.accept(visitor);

        if (axiom instanceof OWLDatatypeDefinitionAxiom) {
            // for OWLDatatypeDefinitionAxioms the string representation is ""

            // but there MUST BE AT LEAST ONE equivalent_to
            assertTrue("At least one " + ManchesterOWLSyntax.EQUIVALENT_TO + " keyword expected in datatype definition axiom " + axiom, visitor.getOccurrences(ManchesterOWLSyntax.EQUIVALENT_TO) >= 1);

        } else {

            String axiomString = OWLUtils.getManchesterSyntaxString(axiom);

            for (ManchesterOWLSyntax keyword : visitor) {
                assertNotNull(keyword);

                final int numberOfOccurrences = visitor.getOccurrences(keyword);
                assertTrue(numberOfOccurrences > 0);

                final String keywordString = keyword.toString();

                int idx = -1;
                // checks that AT LEAST the number of occurrences of keyword is found in the axiom string
                for (int i = 0; i < numberOfOccurrences; i++) {
                    idx = axiomString.indexOf(keywordString, idx + 1);
                    assertTrue("No keyword \"" + keywordString + "\" found in \"" + axiomString + "\" after position " + idx, idx > -1);
                }

                // TODO implement the opposite check that not more than number occurrences of keyword appear in the axiom (problem with substrings like "and" in "grandfather")
                //idx = axiomString.indexOf(keywordString, idx + 1);
                //assertTrue("More than " + numberOfOccurrences + " occurrences of keyword \"" + keywordString + "\" found in \"" + axiomString + "\"", idx == -1);
            }
        }

    }
}
