package org.exquisite.protege.model.repair;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.Debugger;
import org.exquisite.protege.ui.panel.repair.RepairDiagnosisPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wolfi
 */
public class ExplanationManager {

    private Map<Integer, DiagnosisAxiomExplanationModel> map;

    public ExplanationManager(OWLEditorKit editorKit, Debugger debugger) throws OWLOntologyCreationException {
        if (debugger.getDiagnoses().size() != 1)
            throw new UnsupportedOperationException("Unsupported size of diagnoses " + debugger.getDiagnoses().size());

        for (Diagnosis<OWLLogicalAxiom> diagnosis : debugger.getDiagnoses()) {

            this.map = new HashMap<>();
            int idx = 1;
            for (OWLLogicalAxiom axiom : diagnosis.getFormulas()) {
                DiagnosisAxiomExplanationModel model = new DiagnosisAxiomExplanationModel(axiom, debugger.getDiagnosisModel(), editorKit, debugger.getDiagnosisEngineFactory().getReasonerFactory(), debugger.getDiagnosisEngineFactory().getDebuggerConfiguration());
                map.put(idx, model);
                idx++;
            }
        }
    }

    public void dispose()  {
        for (DiagnosisAxiomExplanationModel model : map.values()){
            model.dispose();
        }
    }

    public void explain(int idx, OWLLogicalAxiom axiom, RepairDiagnosisPanel panel) {
        map.get(idx).explain(axiom, panel);
    }

}
