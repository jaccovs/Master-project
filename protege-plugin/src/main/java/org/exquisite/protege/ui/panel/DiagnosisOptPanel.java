package org.exquisite.protege.ui.panel;

import org.exquisite.protege.model.configuration.SearchConfiguration;

import javax.swing.*;
import java.awt.*;


public class DiagnosisOptPanel extends AbstractOptPanel {

    public static final int maxLeadingDiags = 18;

    private JCheckBox test_Tbox_Checkbox = new JCheckBox("include TBox in Background Knowledge", false);

    private JCheckBox test_Abox_Checkbox = new JCheckBox("include ABox in Background Knowledge", true);

    private JSpinner numofLeadingDiagsField = new JSpinner(new SpinnerNumberModel(9, 1, maxLeadingDiags + 1, 1));

    private JCheckBox test_incoherency_inconsistency_Checkbox = new JCheckBox("reduce incoherency to inconsistency ", false);

    private JCheckBox extactModule_Checkbox = new JCheckBox("extract *star* modules of unsatisfiable classes it the ontology is consistent, but its terminology is incoherent", false);

    private JCheckBox calcAllDiags_checkbox = new JCheckBox("calc all diagnoses ", true);

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


        OptionGroupBox holderBoxes = new OptionGroupBox("Background Axioms");
        holderBoxes.addOptionBox(new OptionBox("abox",getListener(),test_Abox_Checkbox));
        holderBoxes.addOptionBox(new OptionBox("tbox",getListener(),test_Tbox_Checkbox));

        OptionGroupBox holderSearch = new OptionGroupBox("Engine Type");
        holderSearch.addOptionBox(new OptionBox("enginetype",getListener(),new JLabel("Engine Type: "), engineType));

        OptionGroupBox holderCalculation = new OptionGroupBox("Diagnoses Calculation");
        holderCalculation.addOptionBox(new OptionBox("numofleadingdiags",getListener(),new JLabel("NumOfLeadingDiag: "), numofLeadingDiagsField));
        holderCalculation.addOptionBox(new OptionBox("calcalldiags",getListener(),calcAllDiags_checkbox));
        holderCalculation.addOptionBox(new OptionBox("testincoherencyinconsistency",getListener(),test_incoherency_inconsistency_Checkbox));
        holderCalculation.addOptionBox(new OptionBox("extractModules",getListener(),extactModule_Checkbox));

        holder.add(holderBoxes);
        holder.add(holderSearch);
        holder.add(holderCalculation);

        add(holder, BorderLayout.NORTH);
        add(getHelpAreaPane(),BorderLayout.CENTER);

    }

    protected void loadConfiguration() {
        test_Tbox_Checkbox.setSelected(getConfiguration().tBoxInBG);
        test_Abox_Checkbox.setSelected(getConfiguration().aBoxInBG);

        test_incoherency_inconsistency_Checkbox.setSelected(getConfiguration().reduceIncoherency);
        extactModule_Checkbox.setSelected(getConfiguration().extractModules);
        calcAllDiags_checkbox.setSelected(getConfiguration().calcAllDiags);

        numofLeadingDiagsField.setValue(getConfiguration().numOfLeadingDiags);
        engineType.setSelectedItem(getConfiguration().engineType);
    }

    @Override
    public void saveChanges() {

        getNewConfiguration().tBoxInBG = test_Tbox_Checkbox.isSelected();
        getNewConfiguration().aBoxInBG = test_Abox_Checkbox.isSelected();

        getNewConfiguration().reduceIncoherency = test_incoherency_inconsistency_Checkbox.isSelected();
        getNewConfiguration().extractModules = extactModule_Checkbox.isSelected();
        getNewConfiguration().calcAllDiags = calcAllDiags_checkbox.isSelected();

        getNewConfiguration().numOfLeadingDiags = (Integer) numofLeadingDiagsField.getValue();
        getNewConfiguration().engineType = (SearchConfiguration.DiagnosisEngineType) engineType.getSelectedItem();

    }

}
