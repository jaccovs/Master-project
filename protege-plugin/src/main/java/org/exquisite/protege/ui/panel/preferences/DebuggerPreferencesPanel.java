package org.exquisite.protege.ui.panel.preferences;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.model.preferences.DebuggerConfiguration;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * The main class for preferences used in the debugger.
 */
public class DebuggerPreferencesPanel extends OWLPreferencesPanel {

    private Debugger debugger;

    private DebuggerConfiguration newConfiguration;

    private List<AbstractDebuggerPreferencesPanel> panes = new LinkedList<>();

    @Override
    public void initialise() throws Exception {
        EditorKitHook editorKitHook = (EditorKitHook)
                getOWLEditorKit().get("org.exquisite.protege.EditorKitHook");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.debugger = editorKitHook.getActiveOntologyDebugger();
        DebuggerConfiguration configuration = this.debugger.getDiagnosisEngineFactory().getSearchConfiguration();
        newConfiguration = new DebuggerConfiguration();
        JTabbedPane tabbedPane = new JTabbedPane();

        final QueryComputationPreferencesPanel queryOptPanel = new QueryComputationPreferencesPanel(configuration, newConfiguration);
        addPane(tabbedPane,"Fault Localization",new FaultLocalizationPreferencesPanel(configuration,newConfiguration, queryOptPanel), KeyEvent.VK_F);
        addPane(tabbedPane,"Query Computation", queryOptPanel,KeyEvent.VK_Q);
        // TODO a third preferences panel for defining the keyword preferences is deactivated at the moment
        //addPane(tabbedPane,"Preference Measures",new PrefMeasureOptPanel(configuration,newConfiguration,editorKitHook),KeyEvent.VK_P);

        add(tabbedPane);
    }

    @Override
    public void applyChanges() {
        panes.forEach(AbstractDebuggerPreferencesPanel::saveChanges);
        debugger.updateConfig(newConfiguration);
        // TODO a third preferences panel for defining the keyword preferences is deactivated at the moment
        //panes.stream().filter(panel -> panel instanceof ProbabPanel).forEach(panel -> debugger.updateProbab(((ProbabPanel) panel).getMap()));
    }

    @Override
    public void dispose() throws Exception {}

    private void addPane(JTabbedPane tabbedPane, String title, AbstractDebuggerPreferencesPanel panel, int mnemonic) {
        tabbedPane.addTab(title, panel);
        tabbedPane.setMnemonicAt(tabbedPane.getTabCount()-1, mnemonic);
        panes.add(panel);
    }

}
