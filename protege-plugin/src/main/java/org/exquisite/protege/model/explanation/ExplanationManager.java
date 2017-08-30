package org.exquisite.protege.model.explanation;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.Debugger;
import org.exquisite.protege.ui.panel.repair.RepairDiagnosisPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wolfi
 */
public class ExplanationManager {

    private Map<Integer, Explanation> map;

    public ExplanationManager(OWLEditorKit editorKit, Debugger debugger) throws OWLOntologyCreationException {
        if (debugger.getDiagnoses().size() != 1)
            throw new UnsupportedOperationException("Unsupported size of diagnoses " + debugger.getDiagnoses().size());

        for (Diagnosis<OWLLogicalAxiom> diagnosis : debugger.getDiagnoses()) {

            this.map = new HashMap<>();
            int idx = 1;
            for (OWLLogicalAxiom axiom : diagnosis.getFormulas()) {
                Explanation model = new Explanation(axiom, debugger.getDiagnosisModel(), editorKit, debugger.getDiagnosisEngineFactory().getReasonerFactory(), debugger.getDiagnosisEngineFactory().getDebuggerConfiguration());
                map.put(idx, model);
                idx++;
            }
        }
    }

    public void dispose()  {
        for (Explanation explanation : map.values()){
            explanation.dispose();
        }
    }

    public void explain(int idx, OWLLogicalAxiom axiom, RepairDiagnosisPanel panel) {
        map.get(idx).explain(axiom, panel);
    }

    public OWLOntology getOntology(int idx) {
        final Explanation explanation = map.get(idx);
        return explanation.getOntology();
    }

}
