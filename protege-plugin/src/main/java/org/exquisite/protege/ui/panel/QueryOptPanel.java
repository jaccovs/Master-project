package org.exquisite.protege.ui.panel;

import org.exquisite.protege.model.configuration.DefaultConfiguration;
import org.exquisite.protege.model.configuration.SearchConfiguration;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

class QueryOptPanel extends AbstractOptPanel {

    // Query computation
    private JSpinner minimalQueriesSpinner = new JSpinner(new SpinnerNumberModel(DefaultConfiguration.getDefaultMinimalQueries().intValue(), 1, 5, 1));
    private JSpinner maximalQueriesSpinner = new JSpinner(new SpinnerNumberModel(DefaultConfiguration.getDefaultMaximalQueries().intValue(), 1, 10, 1));
    private JCheckBox enrichQueryCheckbox = new JCheckBox("enrich query ", DefaultConfiguration.getDefaultEnrichQuery());
    private JComboBox<SearchConfiguration.SortCriterion> sortCriterionCombobox = new JComboBox<>();
    private JComboBox<SearchConfiguration.RM> rmComboBox = new JComboBox<>();
    private JSpinner entropyThresholdSpinner = new JSpinner(new SpinnerNumberModel(DefaultConfiguration.getDefaultEntropyThreshold().doubleValue(), 0, 1, 0.01));
    private JSpinner cardinalityThresholdSpinner = new JSpinner(new SpinnerNumberModel(DefaultConfiguration.getDefaultCardinalityThreshold().doubleValue(), 0, 1, 0.01));
    private JSpinner cautiousParameterSpinner = new JSpinner(new SpinnerNumberModel(DefaultConfiguration.getDefaultCautiousParameter().doubleValue(), 0, 1, 0.01));
    private OptionBox entropyThresholdOptionBox;
    private OptionBox cautiousParameterOptionBox;
    private OptionBox cardinalityThresholdOptionBox;

    QueryOptPanel(SearchConfiguration configuration, SearchConfiguration newConfiguration) {
        super(configuration,newConfiguration);

        for (SearchConfiguration.RM type : SearchConfiguration.RM.values())
            rmComboBox.addItem(type);

        for (SearchConfiguration.SortCriterion type : SearchConfiguration.SortCriterion.values())
            sortCriterionCombobox.addItem(type);

        loadConfiguration();
        createPanel();
    }

    private void createPanel() {
        setLayout(new BorderLayout());
        Box holder = Box.createVerticalBox();

        OptionGroupBox holderQueryComputation = new OptionGroupBox("Query Computation");

        minimalQueriesSpinner.setPreferredSize(new Dimension(60, 22));
        holderQueryComputation.addOptionBox(new OptionBox("minimalQueries",getListener(),new JLabel("Minimal Queries: "),minimalQueriesSpinner));
        minimalQueriesSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Integer minimalQueriesValue = (Integer)((JSpinner)e.getSource()).getValue();
                if ((Integer)maximalQueriesSpinner.getValue() < minimalQueriesValue)
                    maximalQueriesSpinner.setValue(minimalQueriesValue);
            }
        });

        maximalQueriesSpinner.setPreferredSize(new Dimension(60, 22));
        holderQueryComputation.addOptionBox(new OptionBox("maximalQueries",getListener(),new JLabel("Maximal Queries: "),maximalQueriesSpinner));
        maximalQueriesSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Integer maximalQueriesSpinnerValue = (Integer)((JSpinner)e.getSource()).getValue();
                if ((Integer)minimalQueriesSpinner.getValue() > maximalQueriesSpinnerValue)
                    minimalQueriesSpinner.setValue(maximalQueriesSpinnerValue);

            }
        });

        holderQueryComputation.addOptionBox(new OptionBox("enrichquery",getListener(), enrichQueryCheckbox));


        OptionGroupBox holderQueryMeasurements = new OptionGroupBox("Measurements");
        holderQueryMeasurements.addOptionBox(new OptionBox("sortcriterion",getListener(),new JLabel("SortCriterion: "), sortCriterionCombobox));
        holderQueryMeasurements.addOptionBox(new OptionBox("rm",getListener(),new JLabel("RequirementsMeasure: "), rmComboBox));
        rmComboBox.addActionListener(e -> {
            handleRMChanged((SearchConfiguration.RM) ((JComboBox<SearchConfiguration.RM>) e.getSource()).getSelectedItem());
        });

        OptionGroupBox holderQueryThresholds = new OptionGroupBox("Thresholds");
        entropyThresholdSpinner.setPreferredSize(new Dimension(60, 22));
        entropyThresholdOptionBox = new OptionBox("entropythreshold", getListener(), new JLabel("EntropyThreshold: "), entropyThresholdSpinner);
        holderQueryThresholds.addOptionBox(entropyThresholdOptionBox);
        cardinalityThresholdSpinner.setPreferredSize(new Dimension(60, 22));
        cardinalityThresholdOptionBox = new OptionBox("cardinalitythreshold", getListener(), new JLabel("CardinalityThreshold: "), cardinalityThresholdSpinner);
        holderQueryThresholds.addOptionBox(cardinalityThresholdOptionBox);
        cautiousParameterSpinner.setPreferredSize(new Dimension(60, 22));
        cautiousParameterOptionBox = new OptionBox("cautiousparameter", getListener(), new JLabel("CautiousParameter: "), cautiousParameterSpinner);
        holderQueryThresholds.addOptionBox(cautiousParameterOptionBox);

        holder.add(holderQueryComputation);
        holder.add(holderQueryMeasurements);
        holder.add(holderQueryThresholds);

        add(holder, BorderLayout.NORTH);
        add(getHelpAreaPane(),BorderLayout.SOUTH);

        handleRMChanged((SearchConfiguration.RM) this.rmComboBox.getSelectedItem());
    }

    private void handleRMChanged(SearchConfiguration.RM selectedItem) {
        switch (selectedItem) {
            case SPL:
            case ENT:
                enableSpinnerForRIO(false);
                break;
            case RIO:
                enableSpinnerForRIO(true);
                break;
            default:
                throw new RuntimeException("Unknown item in combobox for RM: " + selectedItem);
        }
    }

    private void enableSpinnerForRIO(boolean b) {
        this.cardinalityThresholdSpinner.setEnabled(b);
        this.cardinalityThresholdOptionBox.setEnabledLabel(b);
        this.cautiousParameterSpinner.setEnabled(b);
        this.cautiousParameterOptionBox.setEnabledLabel(b);
    }

    private void loadConfiguration() {
        minimalQueriesSpinner.setValue(getConfiguration().minimalQueries);
        maximalQueriesSpinner.setValue(getConfiguration().maximalQueries);
        enrichQueryCheckbox.setSelected(getConfiguration().enrichQuery);
        sortCriterionCombobox.setSelectedItem(getConfiguration().sortCriterion);
        rmComboBox.setSelectedItem(getConfiguration().rm);
        entropyThresholdSpinner.setValue(getConfiguration().entropyThreshold);
        cardinalityThresholdSpinner.setValue(getConfiguration().cardinalityThreshold);
        cautiousParameterSpinner.setValue(getConfiguration().cautiousParameter);
    }

    @Override
    public void saveChanges() {
        getNewConfiguration().minimalQueries = (Integer) minimalQueriesSpinner.getValue();
        getNewConfiguration().maximalQueries = (Integer) maximalQueriesSpinner.getValue();
        getNewConfiguration().enrichQuery = enrichQueryCheckbox.isSelected();
        getNewConfiguration().sortCriterion = (SearchConfiguration.SortCriterion) sortCriterionCombobox.getSelectedItem();
        getNewConfiguration().rm = (SearchConfiguration.RM) rmComboBox.getSelectedItem();
        getNewConfiguration().entropyThreshold = (Double) entropyThresholdSpinner.getValue();
        getNewConfiguration().cardinalityThreshold = (Double) cardinalityThresholdSpinner.getValue();
        getNewConfiguration().cautiousParameter = (Double) cautiousParameterSpinner.getValue();
    }

}
