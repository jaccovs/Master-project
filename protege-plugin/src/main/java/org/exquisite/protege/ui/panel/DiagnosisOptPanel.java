package org.exquisite.protege.ui.panel;

import org.exquisite.protege.model.configuration.SearchConfiguration;

import javax.swing.*;
import java.awt.*;

public class DiagnosisOptPanel extends AbstractOptPanel {

    public static final int MAX_LEADING_DIAGS = 18;

    private JSpinner numofLeadingDiagsField = new JSpinner(new SpinnerNumberModel(9, 1, MAX_LEADING_DIAGS + 1, 1));

    private JCheckBox test_incoherency_inconsistency_Checkbox = new JCheckBox("reduce incoherency to inconsistency ", false);

    private JCheckBox extactModule_Checkbox = new JCheckBox("extract *star* modules of unsatisfiable classes it the ontology is consistent, but its terminology is incoherent", false);

    private JComboBox engineType = new JComboBox();

    public DiagnosisOptPanel(SearchConfiguration configuration, SearchConfiguration newConfiguration) {
        super(configuration, newConfiguration);

        for (SearchConfiguration.DiagnosisEngineType type : SearchConfiguration.DiagnosisEngineType.values())
            engineType.addItem(type);

        loadConfiguration();
        createPanel();

    }

    protected void createPanel() {
        setLayout(new BorderLayout());
        Box holder = Box.createVerticalBox();

        OptionGroupBox holderSearch = new OptionGroupBox("Engine Type");
        holderSearch.addOptionBox(new OptionBox("enginetype",getListener(),new JLabel("Diagnosis Engine: "), engineType));

        OptionGroupBox holderCalculation = new OptionGroupBox("Diagnoses Calculation");
        holderCalculation.addOptionBox(new OptionBox("numofleadingdiags",getListener(),new JLabel("NumOfLeadingDiag: "), numofLeadingDiagsField));
        holderCalculation.addOptionBox(new OptionBox("testincoherencyinconsistency",getListener(),test_incoherency_inconsistency_Checkbox));
        holderCalculation.addOptionBox(new OptionBox("extractModules",getListener(),extactModule_Checkbox));

        holder.add(holderSearch);
        holder.add(holderCalculation);

        add(holder, BorderLayout.NORTH);
        add(getHelpAreaPane(),BorderLayout.CENTER);

    }

    protected void loadConfiguration() {
        engineType.setSelectedItem(getConfiguration().engineType);
        numofLeadingDiagsField.setValue(getConfiguration().numOfLeadingDiags);
        test_incoherency_inconsistency_Checkbox.setSelected(getConfiguration().reduceIncoherency);
        extactModule_Checkbox.setSelected(getConfiguration().extractModules);
    }

    @Override
    public void saveChanges() {
        getNewConfiguration().reduceIncoherency = test_incoherency_inconsistency_Checkbox.isSelected();
        getNewConfiguration().extractModules = extactModule_Checkbox.isSelected();
        getNewConfiguration().numOfLeadingDiags = (Integer) numofLeadingDiagsField.getValue();
        getNewConfiguration().engineType = (SearchConfiguration.DiagnosisEngineType) engineType.getSelectedItem();

    }

}
