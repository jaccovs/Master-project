package org.exquisite.protege.ui.list.item;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.model.QueryExplanation;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Collections;
import java.util.Set;

/**
 * <p>
 *     An axiom list item with look and feel tooltip information about asserted and inferred axioms.
 * </p>
 *
 * @author wolfi
 */
public class AssertedOrInferredAxiomListItem extends AxiomListItem {

    protected Debugger debugger;

    public AssertedOrInferredAxiomListItem(OWLLogicalAxiom axiom, OWLOntology ontology, Debugger debugger) {
        super(axiom, ontology);
        this.debugger = debugger;
    }

    @Override
    public String getTooltip() {
        if (QueryExplanation.isAxiomInferredFromDebugger(debugger, axiom)) {
            return "Inferred";
        } else {
            final OWLOntology ontology = debugger.getDiagnosisEngineFactory().getOntology();
            if (ontology.containsAxiom(axiom)) {
                return createAssertedTooltip();
            }
        }

        return super.getTooltip();
    }

    /**
     * A helper method to pretty print a tooltip for asserted axioms.
     * The implementation is a carbon copy from org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow
     * @return asserted or inferred tooltip
     * @see org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow
     */
    private String createAssertedTooltip() {
        UIHelper helper = new UIHelper(debugger.getEditorKit());
        StringBuilder sb = new StringBuilder("<html>\n\t<body>\n\t\tAsserted in: ");
        sb.append(helper.getHTMLOntologyList(Collections.singleton(ontology)));
        Set<OWLAnnotation> annotations = getAxiom().getAnnotations();
        if (!annotations.isEmpty()) {
            OWLModelManager protegeManager = debugger.getEditorKit().getModelManager();
            sb.append("\n\t\t<p>Annotations:");
            sb.append("\n\t\t<dl>");
            for (OWLAnnotation annotation : annotations) {
                sb.append("\n\t\t\t<dt>");
                sb.append(protegeManager.getRendering(annotation.getProperty()));
                sb.append("</dt>\n\t\t\t<dd>");
                sb.append(protegeManager.getRendering(annotation.getValue()));
                sb.append("</dd>");
            }
            sb.append("\n\t\t</dl>\n\t</p>\n");
        }
        sb.append("\t</body>\n</html>");
        return sb.toString();
    }
}
