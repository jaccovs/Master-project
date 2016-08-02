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

        final QueryOptPanel queryOptPanel = new QueryOptPanel(configuration, newConfiguration);
        addPane(tabbedPane,"Fault Localization",new DiagnosisOptPanel(configuration,newConfiguration, queryOptPanel), KeyEvent.VK_D);
        addPane(tabbedPane,"Query Computation", queryOptPanel,KeyEvent.VK_Q);
        //addPane(tabbedPane,"Preference Measures",new PrefMeasureOptPanel(configuration,newConfiguration,editorKitHook),KeyEvent.VK_P);

        add(tabbedPane);

    }

    public void applyChanges() {
        panes.forEach(AbstractOptPanel::saveChanges);
        ontologyDiagnosisSearcher.updateConfig(newConfiguration);
        panes.stream().filter(panel -> panel instanceof ProbabPanel).forEach(panel -> ontologyDiagnosisSearcher.updateProbab(((ProbabPanel) panel).getMap()));
    }

    public void dispose() throws Exception {
    }

    private void addPane(JTabbedPane tabbedPane, String title, AbstractOptPanel panel, int mnemonic) {
        tabbedPane.addTab(title, panel);
        tabbedPane.setMnemonicAt(tabbedPane.getTabCount()-1, mnemonic);
        panes.add(panel);
    }

    private OntologyDiagnosisSearcher ontologyDiagnosisSearcher;

    private SearchConfiguration newConfiguration;

    private List<AbstractOptPanel> panes = new LinkedList<>();

}
