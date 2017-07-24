package org.exquisite.protege.ui.list;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.ui.buttons.ResetAxiomButton;
import org.exquisite.protege.ui.list.header.DiagnosisListHeader;
import org.exquisite.protege.ui.list.item.RepairListItem;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wolfi
 */
public class RepairAxiomList extends AbstractAxiomList {

    protected EditorKitHook editorKitHook;

    public RepairAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit);
        this.editorKitHook = editorKitHook;
    }

    @Override
    protected List<MListButton> getButtons(Object value) {
        if (value instanceof RepairListItem) {
            List<MListButton> buttons = new ArrayList<>();
            buttons.addAll(super.getButtons(value));
            buttons.add(0, new ResetAxiomButton(null));
            return buttons;
        } else {
            return super.getButtons(value);
        }
    }

    public void updateList(Set<Diagnosis<OWLLogicalAxiom>> diagnoses, OWLOntology ontology) {
        if (diagnoses.size() == 1) {
            List<Object> items = new ArrayList<>();
            int cnt = 0;

            for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
                items.add(new DiagnosisListHeader(diagnosis, createHeaderName(diagnosis)));
                items.addAll(diagnosis.getFormulas().stream().map(axiom -> new RepairListItem(axiom, ontology)).collect(Collectors.toList()));
                items.add(" ");
            }

            if (items.size() > 0)
                items.remove(items.size() - 1);

            setListData(items.toArray());
        } else {
            setListData(new ArrayList<>().toArray());
        }

    }

    private String createHeaderName(final Diagnosis<OWLLogicalAxiom> diagnosis) {
        final int size = diagnosis.getFormulas().size();
        final String s = (size == 1) ? "this axiom" : "these " + size + " axioms";
        return "Repair " + s + " either by deletion or by modification.";
    }

    public void dispose() {

    }

}
