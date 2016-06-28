package org.exquisite.protege.ui.panel;

import org.exquisite.protege.model.EditorKitHook;
import org.exquisite.protege.model.configuration.SearchConfiguration;

import javax.swing.*;

import java.awt.*;

import static org.exquisite.protege.model.configuration.SearchConfiguration.CostEstimator;

class PrefMeasureOptPanel extends AbstractOptPanel {

    private JComboBox<CostEstimator> estimatorComboBox = new JComboBox<>();

    PrefMeasureOptPanel(SearchConfiguration configuration, SearchConfiguration newConfiguration, EditorKitHook editorKitHook) {
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
