package org.exquisite.protege.ui.panel;

import org.exquisite.protege.model.configuration.SearchConfiguration;

import javax.swing.*;
import java.awt.*;

public class QueryOptPanel extends AbstractOptPanel {

    // Query computation
    private JSpinner minimalQueriesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
    private JSpinner maximalQueriesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
    private JCheckBox enrichQuery_Checkbox = new JCheckBox("enrich query ", true);
    private JComboBox sortCriterion = new JComboBox();
    private JComboBox requirementsMeasurement = new JComboBox();
    private JSpinner entropyThresholdSpinner = new JSpinner(new SpinnerNumberModel(0.75, 0, 1, 0.01));
    private JSpinner cardinalityThresholdSpinner = new JSpinner(new SpinnerNumberModel(0.75, 0, 1, 0.01));
    private JSpinner cautiousParameterSpinner = new JSpinner(new SpinnerNumberModel(0.75, 0, 1, 0.01));

    public QueryOptPanel(SearchConfiguration configuration, SearchConfiguration newConfiguration) {
        super(configuration,newConfiguration);

        for (SearchConfiguration.RM type : SearchConfiguration.RM.values())
            requirementsMeasurement.addItem(type);

        for (SearchConfiguration.SortCriterion type : SearchConfiguration.SortCriterion.values())
            sortCriterion.addItem(type);

        loadConfiguration();
        createPanel();
    }

    protected void createPanel() {
        setLayout(new BorderLayout());
        Box holder = Box.createVerticalBox();

        OptionGroupBox holderQueryGen = new OptionGroupBox("Query Computation");

        minimalQueriesSpinner.setPreferredSize(new Dimension(60, 22));
        holderQueryGen.addOptionBox(new OptionBox("minimalQueries",getListener(),new JLabel("Minimal Queries "),minimalQueriesSpinner));
        maximalQueriesSpinner.setPreferredSize(new Dimension(60, 22));
        holderQueryGen.addOptionBox(new OptionBox("maximalQueries",getListener(),new JLabel("Maximal Queries "),maximalQueriesSpinner));
        holderQueryGen.addOptionBox(new OptionBox("entropythreshold",getListener(),new JLabel("EntropyThreshold "),entropyThresholdSpinner));
        holderQueryGen.addOptionBox(new OptionBox("enrichquery",getListener(),enrichQuery_Checkbox));
        holderQueryGen.addOptionBox(new OptionBox("sortcriterion",getListener(),new JLabel("SortCriterion: "), sortCriterion));
        holderQueryGen.addOptionBox(new OptionBox("rm",getListener(),new JLabel("RequirementsMeasure: "), requirementsMeasurement));
        entropyThresholdSpinner.setPreferredSize(new Dimension(60, 22));
        holderQueryGen.addOptionBox(new OptionBox("entropythreshold",getListener(),new JLabel("EntropyThreshold "),entropyThresholdSpinner));
        cardinalityThresholdSpinner.setPreferredSize(new Dimension(60, 22));
        holderQueryGen.addOptionBox(new OptionBox("cardinalitythreshold",getListener(),new JLabel("CardinalityThreshold "),cardinalityThresholdSpinner));
        cautiousParameterSpinner.setPreferredSize(new Dimension(60, 22));
        holderQueryGen.addOptionBox(new OptionBox("cautiousparameter",getListener(),new JLabel("CautiousParameter "),cautiousParameterSpinner));

        holder.add(holderQueryGen);

        add(holder, BorderLayout.NORTH);
        add(getHelpAreaPane(),BorderLayout.CENTER);
    }

    protected void loadConfiguration() {
        minimalQueriesSpinner.setValue(getConfiguration().minimalQueries);
        maximalQueriesSpinner.setValue(getConfiguration().maximalQueries);
        enrichQuery_Checkbox.setSelected(getConfiguration().enrichQuery);
        sortCriterion.setSelectedItem(getConfiguration().sortCriterion);
        requirementsMeasurement.setSelectedItem(getConfiguration().rm);
        entropyThresholdSpinner.setValue(getConfiguration().entropyThreshold);
        cardinalityThresholdSpinner.setValue(getConfiguration().cardinalityThreshold);
        cautiousParameterSpinner.setValue(getConfiguration().cautiousParameter);
    }

    @Override
    public void saveChanges() {
        getNewConfiguration().minimalQueries = (Integer) minimalQueriesSpinner.getValue();
        getNewConfiguration().maximalQueries = (Integer) maximalQueriesSpinner.getValue();
        getNewConfiguration().enrichQuery = enrichQuery_Checkbox.isSelected();
        getNewConfiguration().sortCriterion = (SearchConfiguration.SortCriterion) sortCriterion.getSelectedItem();
        getNewConfiguration().rm = (SearchConfiguration.RM) requirementsMeasurement.getSelectedItem();
        getNewConfiguration().entropyThreshold = (Double) entropyThresholdSpinner.getValue();
        getNewConfiguration().cardinalityThreshold = (Double) cardinalityThresholdSpinner.getValue();
        getNewConfiguration().cautiousParameter = (Double) cautiousParameterSpinner.getValue();
    }

}
