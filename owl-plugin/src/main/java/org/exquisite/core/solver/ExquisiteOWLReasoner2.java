package org.exquisite.core.solver;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.model.DiagnosisModel;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplPlain;

import java.util.Set;

/**
 * @author wolfi
 * @author patrick
 */
public class ExquisiteOWLReasoner2 extends ExquisiteOWLReasoner {

    public ExquisiteOWLReasoner2(DiagnosisModel<OWLLogicalAxiom> dm, OWLOntologyManager manager, OWLReasonerFactory reasonerFactory) throws OWLOntologyCreationException, DiagnosisException {
        super(dm, manager, reasonerFactory);
    }

    public static DiagnosisModel<OWLLogicalAxiom> generateDiagnosisModel(OWLOntology ontology, OWLReasonerFactory reasonerFactory)
            throws OWLOntologyCreationException, DiagnosisException {
        DiagnosisModel<OWLLogicalAxiom> dm = new DiagnosisModel<>();

        for (OWLLogicalAxiom axiom : ontology.getLogicalAxioms()) {

            Set<OWLAnnotationProperty> propertiesInSignature = axiom.getAnnotationPropertiesInSignature();
            if (propertiesInSignature != null && propertiesInSignature.iterator().hasNext() ) {
                OWLAnnotationProperty property = propertiesInSignature.iterator().next();
                if (property.isComment()) {
                    OWLAnnotationValue annotationValue = axiom.getAnnotations(propertiesInSignature.iterator().next()).iterator().next().getValue();

                    String comment = (((OWLLiteralImplPlain) annotationValue).getLiteral());
                    switch (comment) {
                        case "B":
                            dm.getCorrectFormulas().add(axiom);
                            break;
                        case "P":
                            dm.getEntailedExamples().add(axiom);
                            break;
                        case "N":
                            dm.getNotEntailedExamples().add(axiom);
                            break;
                        default:
                    }
                }
            } else
                dm.getPossiblyFaultyFormulas().add(axiom);
        }

        return dm;
    }

}
