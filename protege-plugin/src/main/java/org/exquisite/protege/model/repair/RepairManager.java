package org.exquisite.protege.model.repair;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.Debugger;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.util.Collections;
import java.util.Set;

/**
 * @author wolfi
 */
public class RepairManager {

    private Debugger debugger;

    private RepairSettings settings;

    private Set<Diagnosis<OWLLogicalAxiom>> originalDiagnoses;

    public RepairManager(Debugger debugger) {
        this.debugger = debugger;
        this.settings = new RepairSettings();
        this.originalDiagnoses = Collections.unmodifiableSet(debugger.getDiagnoses());
    }

    public void handleEdit() {

    }

    public void handleDelete() {

    }

    public void handleReset() {

    }

    public void handleResetAll() {

    }

}
