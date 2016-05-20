package org.exquisite.protege.ui.panel;

import org.exquisite.protege.model.configuration.SearchConfiguration;

import javax.swing.*;
import java.awt.*;

public class QueryOptPanel extends AbstractOptPanel {

    private JSpinner partitioningThresholdField = new JSpinner(new SpinnerNumberModel(0.75, 0, 1, 0.01));

    private JComboBox scoringFunction = new JComboBox();

    private JCheckBox minimizeQuery_Checkbox = new JCheckBox("minimize query ", true);

    // Entailmenttypes
    private JCheckBox incInferenceTypeClassHierarchy_Checkbox = new JCheckBox("include entailments containing ClassHierarchy", true);
    private JCheckBox incInferenceTypeDisjointClasses_Checkbox = new JCheckBox("include entailments containing DisjointClasses", true);
    private JCheckBox incInferenceTypeObjectPropertyHierarchy_Checkbox = new JCheckBox("include entailments containing ObjectPropertyHierarchy", false);
    private JCheckBox incInferenceTypeDataPropertyHierarchy_Checkbox = new JCheckBox("include entailments containing DataPropertyHierarchy", false);
    private JCheckBox incInferenceTypeClassAssertions_Checkbox = new JCheckBox("include entailments containing ClassAssertions", false);
    private JCheckBox incInferenceTypeObjectPropertyAssertions_Checkbox = new JCheckBox("include entailments containing ObjectPropertyAssertions", false);
    private JCheckBox incInferenceTypeDataPropertyAssertions_Checkbox = new JCheckBox("include entailments containing DataPropertyAssertions", false);
    private JCheckBox incInferenceTypeSameIndividual_Checkbox = new JCheckBox("include entailments containing SameIndividual", false);
    private JCheckBox incInferenceTypeDifferentIndividuals_Checkbox = new JCheckBox("include entailments containing DifferentIndividuals", false);

    public QueryOptPanel(SearchConfiguration configuration, SearchConfiguration newConfiguration) {
        super(configuration,newConfiguration);

        for (SearchConfiguration.QSS type : SearchConfiguration.QSS.values())
            scoringFunction.addItem(type);

        loadConfiguration();
        createPanel();
    }

    protected void createPanel() {
        setLayout(new BorderLayout());
        Box holder = Box.createVerticalBox();

        OptionGroupBox holderQueryGen = new OptionGroupBox("Query Generation");
        holderQueryGen.addOptionBox(new OptionBox("minimizequery",getListener(),minimizeQuery_Checkbox));
        holderQueryGen.addOptionBox(new OptionBox("qss",getListener(),new JLabel("QSS: "),scoringFunction));
        partitioningThresholdField.setPreferredSize(new Dimension(60, 22));
        holderQueryGen.addOptionBox(new OptionBox("entcalcthr",getListener(),new JLabel("EntCalcThreshold "),partitioningThresholdField));

        OptionGroupBox holderEntailments = new OptionGroupBox("Inference Types used to calculate entailments");
        holderEntailments.addOptionBox(new OptionBox("incclasshierarchy",getListener(),incInferenceTypeClassHierarchy_Checkbox));
        holderEntailments.addOptionBox(new OptionBox("incdisjointclasses",getListener(),incInferenceTypeDisjointClasses_Checkbox));
        holderEntailments.addOptionBox(new OptionBox("incobjectpropertyhierarchy",getListener(),incInferenceTypeObjectPropertyHierarchy_Checkbox));
        holderEntailments.addOptionBox(new OptionBox("incdatapropertyhierarchy",getListener(),incInferenceTypeDataPropertyHierarchy_Checkbox));
        holderEntailments.addOptionBox(new OptionBox("incclassassertions",getListener(),incInferenceTypeClassAssertions_Checkbox));
        holderEntailments.addOptionBox(new OptionBox("incobjectpropertyassertions",getListener(),incInferenceTypeObjectPropertyAssertions_Checkbox));
        holderEntailments.addOptionBox(new OptionBox("incdatapropertyassertions",getListener(),incInferenceTypeDataPropertyAssertions_Checkbox));
        holderEntailments.addOptionBox(new OptionBox("incsameindividual",getListener(),incInferenceTypeSameIndividual_Checkbox));
        holderEntailments.addOptionBox(new OptionBox("incdifferentindividuals",getListener(),incInferenceTypeDifferentIndividuals_Checkbox));

        holder.add(holderQueryGen);
        holder.add(holderEntailments);

        add(holder, BorderLayout.NORTH);
        add(getHelpAreaPane(),BorderLayout.CENTER);
    }

    protected void loadConfiguration() {
        minimizeQuery_Checkbox.setSelected(getConfiguration().minimizeQuery);
        scoringFunction.setSelectedItem(getConfiguration().qss);
        partitioningThresholdField.setValue(getConfiguration().entailmentCalThres);

        incInferenceTypeClassHierarchy_Checkbox.setSelected(getConfiguration().incInferenceTypeClassHierarchy);
        incInferenceTypeDisjointClasses_Checkbox.setSelected(getConfiguration().incInferenceTypeDisjointClasses);
        incInferenceTypeObjectPropertyHierarchy_Checkbox.setSelected(getConfiguration().incInferenceTypeObjectPropertyHierarchy);
        incInferenceTypeDataPropertyHierarchy_Checkbox.setSelected(getConfiguration().incInferenceTypeDataPropertyHierarchy);
        incInferenceTypeClassAssertions_Checkbox.setSelected(getConfiguration().incInferenceTypeClassAssertions);
        incInferenceTypeObjectPropertyAssertions_Checkbox.setSelected(getConfiguration().incInferenceTypeObjectPropertyAssertions);
        incInferenceTypeDataPropertyAssertions_Checkbox.setSelected(getConfiguration().incInferenceTypeDataPropertyAssertions);
        incInferenceTypeSameIndividual_Checkbox.setSelected(getConfiguration().incInferenceTypeSameIndividual);
        incInferenceTypeDifferentIndividuals_Checkbox.setSelected(getConfiguration().incInferenceTypeDifferentIndividuals);
    }

    @Override
    public void saveChanges() {

        getNewConfiguration().minimizeQuery = minimizeQuery_Checkbox.isSelected();
        getNewConfiguration().qss = (SearchConfiguration.QSS) scoringFunction.getSelectedItem();
        getNewConfiguration().entailmentCalThres = (Double) partitioningThresholdField.getValue();

        getNewConfiguration().incInferenceTypeClassHierarchy = incInferenceTypeClassHierarchy_Checkbox.isSelected();
        getNewConfiguration().incInferenceTypeDisjointClasses = incInferenceTypeDisjointClasses_Checkbox.isSelected();
        getNewConfiguration().incInferenceTypeObjectPropertyHierarchy = incInferenceTypeObjectPropertyHierarchy_Checkbox.isSelected();
        getNewConfiguration().incInferenceTypeDataPropertyHierarchy = incInferenceTypeDataPropertyHierarchy_Checkbox.isSelected();
        getNewConfiguration().incInferenceTypeClassAssertions = incInferenceTypeClassAssertions_Checkbox.isSelected();
        getNewConfiguration().incInferenceTypeObjectPropertyAssertions = incInferenceTypeObjectPropertyAssertions_Checkbox.isSelected();
        getNewConfiguration().incInferenceTypeDataPropertyAssertions = incInferenceTypeDataPropertyAssertions_Checkbox.isSelected();
        getNewConfiguration().incInferenceTypeSameIndividual = incInferenceTypeSameIndividual_Checkbox.isSelected();
        getNewConfiguration().incInferenceTypeDifferentIndividuals = incInferenceTypeDifferentIndividuals_Checkbox.isSelected();
    }

}
