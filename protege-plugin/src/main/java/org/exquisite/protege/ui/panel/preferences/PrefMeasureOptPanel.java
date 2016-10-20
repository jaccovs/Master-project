package org.exquisite.protege.ui.panel.preferences;

import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.model.preferences.DebuggerConfiguration;

import javax.swing.*;

import java.awt.*;

import static org.exquisite.protege.model.preferences.DebuggerConfiguration.CostEstimator;

class PrefMeasureOptPanel extends AbstractDebuggerPreferencesPanel {

    private JComboBox<CostEstimator> estimatorComboBox = new JComboBox<>();

    PrefMeasureOptPanel(DebuggerConfiguration configuration, DebuggerConfiguration newConfiguration, EditorKitHook editorKitHook) {
        super(configuration, newConfiguration);

        for (CostEstimator costEstimator : CostEstimator.values())
            estimatorComboBox.addItem(costEstimator);

        loadConfiguration();
        createPanel();
    }

    private void createPanel() {
        setLayout(new BorderLayout());
        Box holder = Box.createVerticalBox();

        OptionGroupBox optionGroupCostEstimator = new OptionGroupBox("Cost Estimation");
        optionGroupCostEstimator.addOptionBox(new OptionBox("costEstimator", getListener(), new JLabel("Preference Function: "), estimatorComboBox));

        holder.add(optionGroupCostEstimator);

        add(holder, BorderLayout.NORTH);
        add(getHelpAreaPane(),BorderLayout.SOUTH);
    }

    private void loadConfiguration() {
        estimatorComboBox.setSelectedItem(getConfiguration().costEstimator);
    }

    @Override
    public void saveChanges() {
        getNewConfiguration().costEstimator = (CostEstimator) estimatorComboBox.getSelectedItem();
    }
}
