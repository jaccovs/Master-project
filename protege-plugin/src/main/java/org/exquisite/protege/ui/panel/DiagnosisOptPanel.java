package org.exquisite.protege.ui.panel;

import org.exquisite.protege.model.configuration.DefaultConfiguration;
import org.exquisite.protege.model.configuration.SearchConfiguration;

import javax.swing.*;
import java.awt.*;

class DiagnosisOptPanel extends AbstractOptPanel {

    /** The maximal possible number of leading diagnoses which can be set in the preferences. */
    private static final int MAX_LEADING_DIAGS = 18;

    private JSpinner numOfLeadingDiagsSpinner = new JSpinner(new SpinnerNumberModel(DefaultConfiguration.getDefaultNumOfLeadingDiags().intValue(), 1, MAX_LEADING_DIAGS + 1, 1));

    private JCheckBox reduceIncoherencyCheckbox = new JCheckBox("reduce incoherency to inconsistency ", DefaultConfiguration.getDefaultReduceIncoherency());

    private JCheckBox extractModulesCheckbox = new JCheckBox("extract *star* modules of unsatisfiable classes it the ontology is consistent, but its terminology is incoherent", DefaultConfiguration.getDefaultExtractModules());

    private JComboBox<SearchConfiguration.DiagnosisEngineType> engineTypeCombobox = new JComboBox<>();

    DiagnosisOptPanel(SearchConfiguration configuration, SearchConfiguration newConfiguration) {
        super(configuration, newConfiguration);

        for (SearchConfiguration.DiagnosisEngineType type : SearchConfiguration.DiagnosisEngineType.values())
            engineTypeCombobox.addItem(type);

        loadConfiguration();
        createPanel();
    }

    private void createPanel() {
        setLayout(new BorderLayout());
        Box holder = Box.createVerticalBox();

        OptionGroupBox holderSearch = new OptionGroupBox("Engine Type");
        holderSearch.addOptionBox(new OptionBox("enginetype",getListener(),new JLabel("Diagnosis Engine: "), engineTypeCombobox));

        OptionGroupBox holderCalculation = new OptionGroupBox("Diagnoses Calculation");
        holderCalculation.addOptionBox(new OptionBox("numofleadingdiags",getListener(),new JLabel("NumOfLeadingDiag: "), numOfLeadingDiagsSpinner));
        holderCalculation.addOptionBox(new OptionBox("testincoherencyinconsistency",getListener(), reduceIncoherencyCheckbox));
        holderCalculation.addOptionBox(new OptionBox("extractModules",getListener(), extractModulesCheckbox));

        holder.add(holderSearch);
        holder.add(holderCalculation);

        add(holder, BorderLayout.NORTH);
        add(getHelpAreaPane(),BorderLayout.SOUTH);
    }

    private void loadConfiguration() {
        engineTypeCombobox.setSelectedItem(getConfiguration().engineType);
        numOfLeadingDiagsSpinner.setValue(getConfiguration().numOfLeadingDiags);
        reduceIncoherencyCheckbox.setSelected(getConfiguration().reduceIncoherency);
        extractModulesCheckbox.setSelected(getConfiguration().extractModules);
    }

    @Override
    public void saveChanges() {
        getNewConfiguration().reduceIncoherency = reduceIncoherencyCheckbox.isSelected();
        getNewConfiguration().extractModules = extractModulesCheckbox.isSelected();
        getNewConfiguration().numOfLeadingDiags = (Integer) numOfLeadingDiagsSpinner.getValue();
        getNewConfiguration().engineType = (SearchConfiguration.DiagnosisEngineType) engineTypeCombobox.getSelectedItem();
    }

}
