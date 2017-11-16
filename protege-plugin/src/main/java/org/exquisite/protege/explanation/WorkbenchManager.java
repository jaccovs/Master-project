package org.exquisite.protege.explanation;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.Set;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 20/03/2012
 *
 * @apiNote This is a <i>modified</i> copy from the explanation-workbench 5.0.0-beta-19
 * (Revision Number 3c2a4fa7f0591c18693d2b8a6bd0a9739dde2340) at https://github.com/protegeproject/explanation-workbench.git
 * <br>modifications: modified put method by @author wolfi, reformatting by @author wolfi
 */
public class WorkbenchManager {

    private WorkbenchSettings workbenchSettings;

    private JustificationManager justificationManager;

    private OWLAxiom entailment;

    WorkbenchManager(JustificationManager justificationManager, OWLAxiom entailment, WorkbenchSettings settings) {
        this.justificationManager = justificationManager;
        this.entailment = entailment;
        this.workbenchSettings = settings;
    }

    WorkbenchSettings getWorkbenchSettings() {
        return workbenchSettings;
    }

    public OWLAxiom getEntailment() {
        return entailment;
    }

    Set<Explanation<OWLAxiom>> getJustifications(OWLAxiom entailment) {
        JustificationType justificationType = workbenchSettings.getJustificationType();
        return justificationManager.getJustifications(entailment, justificationType);
    }

    int getJustificationCount(OWLAxiom entailment) {
        JustificationType justificationType = workbenchSettings.getJustificationType();
        return justificationManager.getComputedExplanationCount(entailment, justificationType);
    }


    JustificationManager getJustificationManager() {
        return justificationManager;
    }

    int getPopularity(OWLAxiom axiom) {
        int count = 0;
        Set<Explanation<OWLAxiom>> justifications = justificationManager.getJustifications(entailment, workbenchSettings.getJustificationType());
        for (Explanation<OWLAxiom> justification : justifications) {
            if (justification.contains(axiom)) {
                count++;
            }
        }
        return count;
    }

}
