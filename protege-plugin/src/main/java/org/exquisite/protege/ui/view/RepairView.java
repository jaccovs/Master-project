package org.exquisite.protege.ui.view;

import org.exquisite.protege.ui.panel.repair.RepairDiagnosisPanel;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import java.awt.*;

/**
 * @author wolfi
 */
public class RepairView extends AbstractOWLViewComponent {

    RepairDiagnosisPanel panel;

    @Override
    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout(6, 6));

        panel = new RepairDiagnosisPanel(getOWLEditorKit());
        add(panel, BorderLayout.CENTER);
    }

    @Override
    protected void disposeOWLView() {
        panel.dispose();
    }

}
