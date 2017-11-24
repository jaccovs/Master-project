package org.exquisite.protege.explanation;

import org.protege.editor.owl.ui.explanation.ExplanationResult;

import java.awt.*;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 18/03/2012
 *
 * @apiNote This is a <i>modified</i> copy from the explanation-workbench 5.0.0-beta-19
 * (Revision Number 3c2a4fa7f0591c18693d2b8a6bd0a9739dde2340) at https://github.com/protegeproject/explanation-workbench.git
 * <br>modifications: visibility changes by @author wolfi
 */
public class WorkbenchPanelExplanationResult extends ExplanationResult {

    private WorkbenchPanel workbenchPanel;

    WorkbenchPanelExplanationResult(WorkbenchPanel workbenchPanel) {
        this.workbenchPanel = workbenchPanel;
        setLayout(new BorderLayout());
        add(workbenchPanel);
    }

    @Override
    public void dispose() {
        workbenchPanel.dispose();
    }
}
