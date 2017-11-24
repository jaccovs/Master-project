package org.exquisite.protege.explanation;

import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 19/03/2012
 *
 * @apiNote This is a <i>modified</i> copy from the explanation-workbench 5.0.0-beta-19
 * (Revision Number 3c2a4fa7f0591c18693d2b8a6bd0a9739dde2340) at https://github.com/protegeproject/explanation-workbench.git
 * <br>modifications: visibility changes by @author wolfi, code changes by @author wolfi
 */
public class JustificationFrameSectionRow extends AbstractOWLFrameSectionRow<Explanation<OWLAxiom>, OWLAxiom, OWLAxiom> {

    private int depth;


    JustificationFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection<Explanation<OWLAxiom>, OWLAxiom, OWLAxiom> section, Explanation<OWLAxiom> rootObject, OWLAxiom axiom, int depth) {
        super(owlEditorKit, section, owlEditorKit.getOWLModelManager().getActiveOntology(), rootObject, axiom);
        this.depth = depth;
    }

    @Override
    public String getRendering() {
        String rendering = super.getRendering().replaceAll("\\s", " ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("        ");
        }
        sb.append(rendering);
        return sb.toString();
    }

    @Override
    public List<MListButton> getAdditionalButtons() {
        return Collections.emptyList();
    }

    @Override
    protected OWLObjectEditor<OWLAxiom> getObjectEditor() {
        return null;
    }

    @Override
    protected OWLAxiom createAxiom(OWLAxiom editedObject) {
        return null;
    }

    @Override
    public String getTooltip() {
        if (this.getOntology() != null) {
            StringBuilder sb = new StringBuilder("<html>\n\t<body>\n\t\tAsserted in test ontology: ");
            sb.append("<font color=\"0000ff\"><b>");
            sb.append(getOntology().getOntologyID());
            sb.append("</font></b>");
            Set<OWLAnnotation> annotations = getAxiom().getAnnotations();
            if (!annotations.isEmpty()) {
                OWLModelManager protegeManager = getOWLModelManager();
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
        } else {
            return "";
        }
    }

    public List<? extends OWLObject> getManipulatableObjects() {
        return Collections.singletonList(getAxiom());
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }

    @Override
    public boolean isInferred() {
        return false;
    }


}
