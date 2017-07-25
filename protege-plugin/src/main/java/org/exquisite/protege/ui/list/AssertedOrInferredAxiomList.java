package org.exquisite.protege.ui.list;

import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.model.QueryExplanation;
import org.exquisite.protege.ui.list.item.AxiomListItem;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.owl.OWLEditorKit;

import java.awt.*;

import static org.protege.editor.owl.ui.framelist.OWLFrameList.INFERRED_BG_COLOR;

/**
 * List with corresponding background colors depending on asserted or inferred axioms.
 *
 * @author wolfi
 */
public class AssertedOrInferredAxiomList extends AbstractAxiomList {

    protected EditorKitHook editorKitHook;

    AssertedOrInferredAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit);
        this.editorKitHook = editorKitHook;
    }

    @Override
    protected Color getItemBackgroundColor(MListItem item) {
        if (item instanceof AxiomListItem) {
            if (QueryExplanation.isAxiomInferredFromDebugger(this.editorKitHook.getActiveOntologyDebugger(), ((AxiomListItem) item).getAxiom())) {
                return INFERRED_BG_COLOR;
            }
        }
        return super.getItemBackgroundColor(item);
    }

    public EditorKitHook getEditorKitHook() {
        return editorKitHook;
    }

}
