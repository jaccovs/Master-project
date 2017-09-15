package org.exquisite.protege.ui.list;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.Debugger;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.ui.buttons.RepairDiagnosisButton;
import org.exquisite.protege.ui.list.header.DiagnosisListHeader;
import org.exquisite.protege.ui.list.item.AxiomListItem;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DiagnosisAxiomList extends AbstractAxiomList {

    public DiagnosisAxiomList(OWLEditorKit editorKit) {
        super(editorKit);
    }

    public void updateList(Set<Diagnosis<OWLLogicalAxiom>> diagnoses, OWLOntology ontology) {
        List<Object> items = new ArrayList<>();
        int cnt = 0;

        final int foundDiagnoses = diagnoses.size();

        for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
            items.add(new DiagnosisListHeader(diagnosis,createHeaderName(++cnt, foundDiagnoses, diagnosis)));
            items.addAll(diagnosis.getFormulas().stream().map(axiom -> new AxiomListItem(axiom, ontology)).collect(Collectors.toList()));
            items.add(" ");
        }
        if (items.size()>0)
            items.remove(items.size()-1);

        setListData(items.toArray());
    }

    private String createHeaderName(final int position, final int foundDiagnoses, final Diagnosis<OWLLogicalAxiom> diagnosis) {
        if (foundDiagnoses == 1) {
            final int nrOfAxioms = diagnosis.getFormulas().size();
            if (nrOfAxioms > 1) {
                return "According to your given answers these " + nrOfAxioms + " axioms are faulty. Press Repair to fix them.";
            } else {
                return "According to your given answers this axiom is faulty. Press Repair to fix them.";
            }

        } else {
            final String roundedMeas = diagnosis.getMeasure().round(new java.math.MathContext(6)).toEngineeringString();
            return "Repair #" + (position) + " (Axioms: " + diagnosis.getFormulas().size() + ", Preference Value: " + roundedMeas + ')';
        }
    }

    @Override
    protected List<MListButton> getButtons(Object value) {
        if (value instanceof DiagnosisListHeader) {
            final DiagnosisListHeader diagnosisHeader = (DiagnosisListHeader)value;
            List<MListButton> buttons = new ArrayList<>();
            buttons.addAll(super.getButtons(value));
            buttons.add(0, new RepairDiagnosisButton(e -> {
                final EditorKitHook editorKitHook = (EditorKitHook) getEditorKit().get("org.exquisite.protege.EditorKitHook");
                final Debugger debugger = editorKitHook.getActiveOntologyDebugger();
                debugger.doStartRepair(diagnosisHeader.getDiagnosis());
            }));
            return buttons;
        } else {
            return super.getButtons(value);
        }
    }
}
