package org.exquisite.protege.ui.panel;

import org.exquisite.protege.model.configuration.DefaultConfiguration;
import org.exquisite.protege.model.configuration.SearchConfiguration;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

class QueryOptPanel extends AbstractOptPanel {

    private Integer numOfLeadingDiags = null; // this value is either set while loading the configuration or when numOfLeadingDiags is changed in DiagnosisOptPanel

    // Query computation
    //private JSpinner minimalQueriesSpinner = new JSpinner(new SpinnerNumberModel(DefaultConfiguration.getDefaultMinimalQueries().intValue(), 1, 5, 1));
    //private JSpinner maximalQueriesSpinner = new JSpinner(new SpinnerNumberModel(DefaultConfiguration.getDefaultMaximalQueries().intValue(), 1, 10, 1));
    private JCheckBox enrichQueryCheckbox = new JCheckBox("enrich query ", DefaultConfiguration.getDefaultEnrichQuery());
    private JComboBox<SearchConfiguration.SortCriterion> sortCriterionCombobox = new JComboBox<>();
    private JComboBox<SearchConfiguration.RM> rmComboBox = new JComboBox<>();
    private JSpinner entropyThresholdSpinner = new JSpinner(new SpinnerNumberModel(DefaultConfiguration.getDefaultEntropyThreshold().doubleValue(), 0, 0.5, 0.01));
    private JSpinner cardinalityThresholdSpinner = new JSpinner(new SpinnerNumberModel(DefaultConfiguration.getDefaultCardinalityThreshold().doubleValue(), 0, 100, 1));
    private JSpinner cautiousParameterSpinner = new JSpinner(new SpinnerNumberModel(DefaultConfiguration.getDefaultCautiousParameter().doubleValue(), 0.1, 1, 0.01));
    private OptionBox entropyThresholdOptionBox;
    private OptionBox cautiousParameterOptionBox;
    private OptionBox cardinalityThresholdOptionBox;

    QueryOptPanel(SearchConfiguration configuration, SearchConfiguration newConfiguration) {
        super(configuration,newConfiguration);

        for (SearchConfiguration.RM type : SearchConfiguration.RM.values())
            if (type != SearchConfiguration.RM.KL && type != SearchConfiguration.RM.EMCb && type != SearchConfiguration.RM.BME) // deactivating KL, EMCb and BME
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
/*
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
*/
        holderQueryComputation.addOptionBox(new OptionBox("enrichquery",getListener(), enrichQueryCheckbox));


        OptionGroupBox holderQueryMeasurements = new OptionGroupBox("Measurements");
        holderQueryMeasurements.addOptionBox(new OptionBox("sortcriterion",getListener(),new JLabel("Sort Criterion: "), sortCriterionCombobox));
        holderQueryMeasurements.addOptionBox(new OptionBox("rm",getListener(),new JLabel("Requirements Measure: "), rmComboBox));
        rmComboBox.addActionListener(e -> {
            handleRMChanged((SearchConfiguration.RM) ((JComboBox<SearchConfiguration.RM>) e.getSource()).getSelectedItem());
        });

        OptionGroupBox holderQueryThresholds = new OptionGroupBox("Thresholds");
        entropyThresholdSpinner.setPreferredSize(new Dimension(60, 22));
        entropyThresholdOptionBox = new OptionBox("entropythreshold", getListener(), new JLabel("Entropy Threshold: "), entropyThresholdSpinner);
        holderQueryThresholds.addOptionBox(entropyThresholdOptionBox);
        cardinalityThresholdSpinner.setPreferredSize(new Dimension(60, 22));
        cardinalityThresholdOptionBox = new OptionBox("cardinalitythreshold", getListener(), new JLabel("Cardinality Threshold: "), cardinalityThresholdSpinner);
        holderQueryThresholds.addOptionBox(cardinalityThresholdOptionBox);
        cautiousParameterSpinner.setPreferredSize(new Dimension(60, 22));
        cautiousParameterSpinner = new JSpinner(
                new SpinnerNumberModel(
                        getCautiousValue(this.getConfiguration().cautiousParameter),
                        getCautiousLowerBound(),
                        getCautiousUpperBound(),
                        0.01));
        cautiousParameterOptionBox = new OptionBox("cautiousparameter", getListener(), new JLabel("Cautious Parameter: "), cautiousParameterSpinner);
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
            case KL:
                enableSpinners(true, false, false);
                break;
            case RIO:
                enableSpinners(true, true, true);
                updateThresholdParameter(this.numOfLeadingDiags);
                break;
            case EMCb:
                enableSpinners(false, false, false);
                break;
            case BME:
                enableSpinners(false, true, false);
                updateThresholdParameter(this.numOfLeadingDiags);
                break;
            default:
                throw new RuntimeException("Unknown item in combobox for RM: " + selectedItem);
        }
    }

    private void enableSpinners(boolean ent, boolean card, boolean caut) {
        this.entropyThresholdSpinner.setEnabled(ent);
        this.entropyThresholdOptionBox.setEnabledLabel(ent);

        this.cardinalityThresholdSpinner.setEnabled(card);
        this.cardinalityThresholdOptionBox.setEnabledLabel(card);

        this.cautiousParameterSpinner.setEnabled(caut);
        this.cautiousParameterOptionBox.setEnabledLabel(caut);

    }

    private void loadConfiguration() {
        //minimalQueriesSpinner.setValue(getConfiguration().minimalQueries);
        //maximalQueriesSpinner.setValue(getConfiguration().maximalQueries);
        enrichQueryCheckbox.setSelected(getConfiguration().enrichQuery);
        sortCriterionCombobox.setSelectedItem(getConfiguration().sortCriterion);
        rmComboBox.setSelectedItem(getConfiguration().rm);
        entropyThresholdSpinner.setValue(getConfiguration().entropyThreshold);
        cardinalityThresholdSpinner.setValue(getConfiguration().cardinalityThreshold);
        this.numOfLeadingDiags = this.getConfiguration().numOfLeadingDiags;
        updateMaxCardinalityParameter();
        cautiousParameterSpinner.setValue(getCautiousValue(this.getConfiguration().cautiousParameter));
    }

    @Override
    public void saveChanges() {
        //getNewConfiguration().minimalQueries = (Integer) minimalQueriesSpinner.getValue();
        //getNewConfiguration().maximalQueries = (Integer) maximalQueriesSpinner.getValue();
        getNewConfiguration().enrichQuery = enrichQueryCheckbox.isSelected();
        getNewConfiguration().sortCriterion = (SearchConfiguration.SortCriterion) sortCriterionCombobox.getSelectedItem();
        getNewConfiguration().rm = (SearchConfiguration.RM) rmComboBox.getSelectedItem();
        getNewConfiguration().entropyThreshold = (Double) entropyThresholdSpinner.getValue();
        getNewConfiguration().cardinalityThreshold = (Double) cardinalityThresholdSpinner.getValue();
        getNewConfiguration().cautiousParameter = (Double) cautiousParameterSpinner.getValue();
    }

    void updateThresholdParameter(int numOfLeadingDiags) {
        this.numOfLeadingDiags = numOfLeadingDiags;
        updateCautiousParameter();
        updateMaxCardinalityParameter();
    }

    /**
     * When user updates the preference value for numOfLeadingDiags, the spinner model for RIO's cautious parameter has
     * to be updated.
     *
     */
    private void updateCautiousParameter() {
        final SearchConfiguration.RM selectedRequirementsMeasure = (SearchConfiguration.RM) rmComboBox.getSelectedItem();
        if (selectedRequirementsMeasure == SearchConfiguration.RM.RIO) {
            final SpinnerNumberModel spinnerNumberModel = (SpinnerNumberModel)cautiousParameterSpinner.getModel();
            spinnerNumberModel.setValue(getCautiousValue((Double) spinnerNumberModel.getValue()));
            spinnerNumberModel.setMinimum(getCautiousLowerBound());
            final double cautiousUpperBound = getCautiousUpperBound();
            spinnerNumberModel.setMaximum(cautiousUpperBound);
        }
    }

    /**
     * Update the maximal possible cardinality parameter for selected RIO and BME.
     * Unimportant for any other requirements measure.
     */
    private void updateMaxCardinalityParameter() {
        final SearchConfiguration.RM selectedRequirementsMeasure = (SearchConfiguration.RM) rmComboBox.getSelectedItem();
        if (selectedRequirementsMeasure == SearchConfiguration.RM.RIO || selectedRequirementsMeasure == SearchConfiguration.RM.BME) {
            final SpinnerNumberModel spinnerNumberModel = (SpinnerNumberModel)cardinalityThresholdSpinner.getModel();
            final Double value = (Double) spinnerNumberModel.getValue();

            double maximum = 0.0;
            // dynamically calculate the max value range of cardinality parameter depending on the currently selected rm
            switch (selectedRequirementsMeasure) {
                case RIO:
                    maximum = (double) numOfLeadingDiags - 1d - Math.floor((double) numOfLeadingDiags / 2);
                    break;
                case BME:
                    maximum = (double) numOfLeadingDiags - 2d;
                    break;
            }
            spinnerNumberModel.setMaximum(maximum);
            if (value > maximum) spinnerNumberModel.setValue(maximum); // update the value if value is out of range
        }
    }

    private double getCautiousValue(double val) {
        double value = val;//getConfiguration().cautiousParameter;
        final double cautiousLowerBound = getCautiousLowerBound();
        final double cautiousUpperBound = getCautiousUpperBound();

        if (value < cautiousLowerBound) value = cautiousLowerBound;
        else if (value > cautiousUpperBound) value = cautiousUpperBound;

        return value;
    }

    /**
     * Calculate the upper bound value for RIO's cautious parameter correctly rounded to two decimals.
     *
     * @return correctly rounded upper bound.
     */
    private double getCautiousUpperBound() {
        double tmpCautiousUpperBound = Math.floor((double)numOfLeadingDiags / 2.0) / (double)numOfLeadingDiags;
        double cautiousUpperBound = round(tmpCautiousUpperBound, 2, RoundingMode.FLOOR);
        return cautiousUpperBound;
    }

    /**
     * Calculate the lower bound value for RIO's cautious parameter correctly rounded to two decimals.
     *
     * @return correctly rounded lower bound.
     */
    private double getCautiousLowerBound() {
        /*
        double tmpCautiousLowerBound = 1.0 / (double) numOfLeadingDiagnoses;
        double cautiousLowerBound = round(tmpCautiousLowerBound, 2, RoundingMode.CEILING);
        return cautiousLowerBound;
        */
        return 0.01;
    }

    /**
     * Helper method to round correctly the bound values for the cautious parameter.
     *
     * @param value A value with possible infinite commas.
     * @param places Round to this specified number of decimal places.
     * @param roundingMode The rounding mode.
     * @return correctly rounded value.
     */
    private static double round(double value, int places, RoundingMode roundingMode) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, roundingMode);
        return bd.doubleValue();
    }
}
