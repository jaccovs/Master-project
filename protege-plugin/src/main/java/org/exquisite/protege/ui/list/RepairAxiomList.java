package org.exquisite.protege.ui.list;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.model.repair.RepairManager;
import org.exquisite.protege.ui.buttons.ResetAxiomButton;
import org.exquisite.protege.ui.list.header.DiagnosisListHeader;
import org.exquisite.protege.ui.list.item.RepairListItem;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wolfi
 */
public class RepairAxiomList extends AbstractAxiomList {

    protected EditorKitHook editorKitHook;

    protected RepairManager repairManager;

    protected Component parent;

    public RepairAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook, RepairManager repairManager, Component parent) {
        super(editorKit);
        this.editorKitHook = editorKitHook;
        this.repairManager = repairManager;
    }

    @Override
    protected List<MListButton> getButtons(Object value) {
        if (value instanceof RepairListItem) {
            final RepairListItem listItem = (RepairListItem) value;
            List<MListButton> buttons = new ArrayList<>();
            buttons.addAll(super.getButtons(value));

            if (listItem.hasChanged()) {
                buttons.add(0, new ResetAxiomButton(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        listItem.handleReset();
                    }
                }));
            }
            return buttons;
        } else {
            return super.getButtons(value);
        }
    }

    public void updateList(Set<Diagnosis<OWLLogicalAxiom>> diagnoses, OWLOntology ontology) {
        if (diagnoses.size() == 1) {
            List<Object> items = new ArrayList<>();

            for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
                items.add(new DiagnosisListHeader(diagnosis, createHeaderName(diagnosis)));
                items.addAll(diagnosis.getFormulas().stream().map(axiom -> new RepairListItem(this, axiom, ontology, getEditorKit(), repairManager, parent)).collect(Collectors.toList()));
                items.add(" ");
            }

            if (items.size() > 0)
                items.remove(items.size() - 1);

            setListData(items.toArray());
        } else {
            setListData(new ArrayList<>().toArray());
        }

    }

    public void updateListItem(RepairListItem item) {
        final ListModel listModel = getModel();

        for (int i = 1; i < listModel.getSize(); i++) {
            final RepairListItem listModelElementAt = (RepairListItem) listModel.getElementAt(i);
            if (listModelElementAt.equals(item)) {
                System.out.println("found");
            }

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
