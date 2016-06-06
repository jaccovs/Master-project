package org.exquisite.protege.ui.panel;

import org.exquisite.protege.model.EditorKitHook;
import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.exquisite.protege.model.configuration.SearchConfiguration;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

public class QueryDebuggerPreferencesPanel extends OWLPreferencesPanel {

    public void initialise() throws Exception {
        EditorKitHook editorKitHook = (EditorKitHook)
                getOWLEditorKit().get("org.exquisite.protege.EditorKitHook");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.ontologyDiagnosisSearcher = editorKitHook.getActiveOntologyDiagnosisSearcher();
        SearchConfiguration configuration = this.ontologyDiagnosisSearcher.getDiagnosisEngineFactory().getSearchConfiguration();
        newConfiguration = new SearchConfiguration();
        JTabbedPane tabbedPane = new JTabbedPane();

        addPane(tabbedPane,"Diagnosis",new DiagnosisOptPanel(configuration,newConfiguration), KeyEvent.VK_D);
        addPane(tabbedPane,"Query",new QueryOptPanel(configuration,newConfiguration),KeyEvent.VK_Q);
        addPane(tabbedPane,"Probabilities",new ProbabPanel(configuration,newConfiguration,editorKitHook),KeyEvent.VK_P);

        add(tabbedPane);

    }

    public void applyChanges() {
        for (AbstractOptPanel panel : panes)
            panel.saveChanges();

        ontologyDiagnosisSearcher.updateConfig(newConfiguration);

        for (AbstractOptPanel panel : panes)
            if (panel instanceof ProbabPanel)
                ontologyDiagnosisSearcher.updateProbab(((ProbabPanel)panel).getMap());
    }

    public void dispose() throws Exception {
    }

    protected void addPane(JTabbedPane tabbedPane, String title, AbstractOptPanel panel, int mnemonic) {
        tabbedPane.addTab(title, panel);
        tabbedPane.setMnemonicAt(tabbedPane.getTabCount()-1, mnemonic);
        panes.add(panel);
    }

    private OntologyDiagnosisSearcher ontologyDiagnosisSearcher;

    private SearchConfiguration newConfiguration;

    private List<AbstractOptPanel> panes = new LinkedList<>();

}