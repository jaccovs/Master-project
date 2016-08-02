package org.exquisite.protege.ui.panel;

import org.exquisite.protege.model.configuration.DefaultConfiguration;
import org.exquisite.protege.model.configuration.SearchConfiguration;

import javax.swing.*;
import java.awt.*;

class DiagnosisOptPanel extends AbstractOptPanel {

    /** The maximal possible number of leading diagnoses which can be set in the preferences. */
    private static final int MAX_LEADING_DIAGS = 20;

    private JSpinner numOfLeadingDiagsSpinner = new JSpinner(new SpinnerNumberModel(DefaultConfiguration.getDefaultNumOfLeadingDiags().intValue(), 1, MAX_LEADING_DIAGS , 1));

    private JCheckBox reduceIncoherencyCheckbox = new JCheckBox("reduce incoherency to inconsistency ", DefaultConfiguration.getDefaultReduceIncoherency());

    // private JCheckBox extractModulesCheckbox = new JCheckBox("extract *star* modules of unsatisfiable classes it the ontology is consistent, but its terminology is incoherent", DefaultConfiguration.getDefaultExtractModules());

    private JComboBox<SearchConfiguration.DiagnosisEngineType> engineTypeCombobox = new JComboBox<>();

    private JComboBox<SearchConfiguration.CostEstimator> estimatorComboBox = new JComboBox<>();

    private QueryOptPanel queryOptPanel;

    DiagnosisOptPanel(SearchConfiguration configuration, SearchConfiguration newConfiguration, QueryOptPanel queryOptPanel) {
        super(configuration, newConfiguration);

        this.queryOptPanel = queryOptPanel;

        for (SearchConfiguration.CostEstimator costEstimator : SearchConfiguration.CostEstimator.values())
            estimatorComboBox.addItem(costEstimator);

        for (SearchConfiguration.DiagnosisEngineType type : SearchConfiguration.DiagnosisEngineType.values())
            engineTypeCombobox.addItem(type);

        loadConfiguration();
        createPanel();
    }

    private void createPanel() {
        setLayout(new BorderLayout());
        Box holder = Box.createVerticalBox();

        OptionGroupBox holderEngineType = new OptionGroupBox("Engine Type");
        holderEngineType.addOptionBox(new OptionBox("enginetype",getListener(),new JLabel("Diagnosis Engine: "), engineTypeCombobox));

        OptionGroupBox optionGroupCostEstimator = new OptionGroupBox("Cost Estimation");
        optionGroupCostEstimator.addOptionBox(new OptionBox("costEstimator", getListener(), new JLabel("Preference Function: "), estimatorComboBox));

        OptionGroupBox holderCalculation = new OptionGroupBox("Diagnoses Calculation");
        holderCalculation.addOptionBox(new OptionBox("numofleadingdiags",getListener(),new JLabel("Number of Leading Diagnoses: "), numOfLeadingDiagsSpinner));
        numOfLeadingDiagsSpinner.addChangeListener(e -> queryOptPanel.updateThresholdParameter(((Integer) numOfLeadingDiagsSpinner.getValue())));
        holderCalculation.addOptionBox(new OptionBox("testincoherencyinconsistency",getListener(), reduceIncoherencyCheckbox));
        //holderCalculation.addOptionBox(new OptionBox("extractModules",getListener(), extractModulesCheckbox));

        holder.add(holderEngineType);
        holder.add(optionGroupCostEstimator);
        holder.add(holderCalculation);

        add(holder, BorderLayout.NORTH);
        add(getHelpAreaPane(),BorderLayout.SOUTH);
    }

    private void loadConfiguration() {
        engineTypeCombobox.setSelectedItem(getConfiguration().engineType);
        estimatorComboBox.setSelectedItem(getConfiguration().costEstimator);
        numOfLeadingDiagsSpinner.setValue(getConfiguration().numOfLeadingDiags);
        reduceIncoherencyCheckbox.setSelected(getConfiguration().reduceIncoherency);
        //extractModulesCheckbox.setSelected(getConfiguration().extractModules);
    }

    @Override
    public void saveChanges() {
        getNewConfiguration().engineType = (SearchConfiguration.DiagnosisEngineType) engineTypeCombobox.getSelectedItem();
        getNewConfiguration().costEstimator = (SearchConfiguration.CostEstimator) estimatorComboBox.getSelectedItem();
        getNewConfiguration().numOfLeadingDiags = (Integer) numOfLeadingDiagsSpinner.getValue();
        getNewConfiguration().reduceIncoherency = reduceIncoherencyCheckbox.isSelected();
        //getNewConfiguration().extractModules = extractModulesCheckbox.isSelected();
    }

}
