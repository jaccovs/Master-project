package org.exquisite.protege.ui.list.header;

import org.exquisite.core.model.Diagnosis;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

/**
 * <p>
 *     Section header for the diagnoses.
 * </p>
 *
 * @author wolfi
 */
public class DiagnosisListHeader implements MListSectionHeader {

    private Diagnosis<OWLLogicalAxiom> diagnosis;

    private String name;

    public DiagnosisListHeader(Diagnosis<OWLLogicalAxiom> diagnosis, String name) {
        this.diagnosis = diagnosis;
        this.name = name;
    }

    public Diagnosis<OWLLogicalAxiom> getDiagnosis() {
        return diagnosis;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean canAdd() {
        return false;
    }
}
