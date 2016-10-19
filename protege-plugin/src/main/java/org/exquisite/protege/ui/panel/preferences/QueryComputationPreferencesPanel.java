package org.exquisite.protege.ui.panel.preferences;

import org.exquisite.protege.model.preferences.DefaultPreferences;
import org.exquisite.protege.model.preferences.DebuggerConfiguration;
import org.exquisite.protege.model.preferences.InputValidator;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Preferences panel used for defining preferences for the query computation.
 */
class QueryComputationPreferencesPanel extends AbstractDebuggerPreferencesPanel {

    private Integer numOfLeadingDiags = null; // this value is either set while loading the configuration or when numOfLeadingDiags is changed in DiagnosisOptPanel

    // Query computation
    private JCheckBox enrichQueryCheckbox = new JCheckBox("enrich query ", DefaultPreferences.getDefaultEnrichQuery());
    private JComboBox<DebuggerConfiguration.SortCriterion> sortCriterionCombobox = new JComboBox<>();
    private JComboBox<DebuggerConfiguration.RM> rmComboBox = new JComboBox<>();
    private JSpinner entropyThresholdSpinner = new JSpinner(
            new SpinnerNumberModel(
                    DefaultPreferences.getDefaultEntropyThreshold(),
                    DefaultPreferences.getMinEntropyThreshold(),//0,
                    DefaultPreferences.getMaxEntropyThreshold(),//0.5,
                    0.01));
    private JSpinner cardinalityThresholdSpinner = new JSpinner
            (new SpinnerNumberModel(
                    DefaultPreferences.getDefaultCardinalityThreshold(),
                    DefaultPreferences.getMinCardinalityThreshold(),//0,
                    DefaultPreferences.getMaxCardinalityThreshold(),//100,
                    1));
    private JSpinner cautiousParameterSpinner = new JSpinner(
            new SpinnerNumberModel(
                    DefaultPreferences.getDefaultCautiousParameter(),
                    DefaultPreferences.getMinCautiousParameter(),//0.1,
                    DefaultPreferences.getMaxCautiousParameter(),//1,
                    0.01));
    private OptionBox entropyThresholdOptionBox;
    private OptionBox cautiousParameterOptionBox;
    private OptionBox cardinalityThresholdOptionBox;

    QueryComputationPreferencesPanel(DebuggerConfiguration configuration, DebuggerConfiguration newConfiguration) {
        super(configuration,newConfiguration);

        for (DebuggerConfiguration.RM type : DebuggerConfiguration.RM.values())
            // TODO KL, EMCb and BME are deactivated atm.
            if (type != DebuggerConfiguration.RM.KL && type != DebuggerConfiguration.RM.EMCb && type != DebuggerConfiguration.RM.BME)
                rmComboBox.addItem(type);

        for (DebuggerConfiguration.SortCriterion type : DebuggerConfiguration.SortCriterion.values())
            sortCriterionCombobox.addItem(type);

        loadConfiguration();

        createPanel();
    }

    private void createPanel() {
        setLayout(new BorderLayout());
        Box holder = Box.createVerticalBox();

        OptionGroupBox holderQueryComputation = new OptionGroupBox("Query Computation");

        holderQueryComputation.addOptionBox(new OptionBox("enrichquery",getListener(), enrichQueryCheckbox));

        OptionGroupBox holderQueryMeasurements = new OptionGroupBox("Measurements");
        holderQueryMeasurements.addOptionBox(new OptionBox("sortcriterion",getListener(),new JLabel("Sort Criterion: "), sortCriterionCombobox));
        holderQueryMeasurements.addOptionBox(new OptionBox("rm",getListener(),new JLabel("Requirements Measure: "), rmComboBox));
        rmComboBox.addActionListener(e -> {
            handleRMChanged((DebuggerConfiguration.RM) ((JComboBox<DebuggerConfiguration.RM>) e.getSource()).getSelectedItem());
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

        handleRMChanged((DebuggerConfiguration.RM) this.rmComboBox.getSelectedItem());
    }

    private void handleRMChanged(DebuggerConfiguration.RM selectedItem) {
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
        enrichQueryCheckbox.setSelected(InputValidator.validateBoolean(getConfiguration().enrichQuery,DefaultPreferences.getDefaultEnrichQuery()));
        sortCriterionCombobox.setSelectedItem(InputValidator.validateSortCriterion(getConfiguration().sortCriterion));
        rmComboBox.setSelectedItem(InputValidator.validateRM(getConfiguration().rm));
        entropyThresholdSpinner.setValue(
                Double.parseDouble(
                        InputValidator.validateDouble(
                                getConfiguration().entropyThreshold,
                                DefaultPreferences.getMinEntropyThreshold(),
                                DefaultPreferences.getMaxEntropyThreshold(),
                                DefaultPreferences.getDefaultEntropyThreshold()
                        )
                )
        );
        cardinalityThresholdSpinner.setValue(
                Double.parseDouble(
                        InputValidator.validateDouble(
                                getConfiguration().cardinalityThreshold,
                                DefaultPreferences.getMinCardinalityThreshold(),
                                DefaultPreferences.getMaxCardinalityThreshold(),
                                DefaultPreferences.getDefaultCardinalityThreshold()
                        )
                )
        );
        this.numOfLeadingDiags = Integer.parseInt(
                InputValidator.validateInt(
                        this.getConfiguration().numOfLeadingDiags,
                        DefaultPreferences.getMinNumOfLeadingDiags(),
                        DefaultPreferences.getMaxNumOfLeadingDiags(),
                        DefaultPreferences.getDefaultNumOfLeadingDiags()
                )
        );
        updateMaxCardinalityParameter();
        cautiousParameterSpinner.setValue(
                Double.parseDouble(
                        InputValidator.validateDouble(
                                getCautiousValue(this.getConfiguration().cautiousParameter),
                                DefaultPreferences.getMinCautiousParameter(),
                                DefaultPreferences.getMaxCautiousParameter(),
                                DefaultPreferences.getDefaultCautiousParameter()
                        )
                )
        );
    }

    @Override
    public void saveChanges() {
        getNewConfiguration().enrichQuery = InputValidator.validateBoolean(enrichQueryCheckbox.isSelected(), DefaultPreferences.getDefaultEnrichQuery());
        getNewConfiguration().sortCriterion = InputValidator.validateSortCriterion(sortCriterionCombobox.getSelectedItem());
        getNewConfiguration().rm = InputValidator.validateRM(rmComboBox.getSelectedItem());
        getNewConfiguration().entropyThreshold = Double.parseDouble(
                InputValidator.validateDouble(
                        entropyThresholdSpinner.getValue(),
                        DefaultPreferences.getMinEntropyThreshold(),
                        DefaultPreferences.getMaxEntropyThreshold(),
                        DefaultPreferences.getDefaultEntropyThreshold()
                )
        );
        getNewConfiguration().cardinalityThreshold = Double.parseDouble(
                InputValidator.validateDouble(
                        cardinalityThresholdSpinner.getValue(),
                        DefaultPreferences.getMinCardinalityThreshold(),
                        DefaultPreferences.getMaxCardinalityThreshold(),
                        DefaultPreferences.getDefaultCardinalityThreshold()
                )
        );
        getNewConfiguration().cautiousParameter = Double.parseDouble(
                InputValidator.validateDouble(
                        cautiousParameterSpinner.getValue(),
                        DefaultPreferences.getMinCautiousParameter(),
                        DefaultPreferences.getMaxCautiousParameter(),
                        DefaultPreferences.getDefaultCautiousParameter()
                )
        );

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
        final DebuggerConfiguration.RM selectedRequirementsMeasure = (DebuggerConfiguration.RM) rmComboBox.getSelectedItem();
        if (selectedRequirementsMeasure == DebuggerConfiguration.RM.RIO) {
            final SpinnerNumberModel spinnerNumberModel = (SpinnerNumberModel)cautiousParameterSpinner.getModel();
            spinnerNumberModel.setMinimum(getCautiousLowerBound());
            spinnerNumberModel.setMaximum(getCautiousUpperBound());
            spinnerNumberModel.setValue(getCautiousValue((Double) spinnerNumberModel.getValue()));
        }
    }

    /**
     * Update the maximal possible cardinality parameter for selected RIO and BME.
     * Unimportant for any other requirements measure.
     */
    private void updateMaxCardinalityParameter() {
        final DebuggerConfiguration.RM selectedRequirementsMeasure = (DebuggerConfiguration.RM) rmComboBox.getSelectedItem();
        if (selectedRequirementsMeasure == DebuggerConfiguration.RM.RIO || selectedRequirementsMeasure == DebuggerConfiguration.RM.BME) {
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
        double value = val;
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
        // validate that the cautious upper bound is within the predefined upper and lower bound
        if (cautiousUpperBound < DefaultPreferences.getMinCautiousParameter())
            cautiousUpperBound = DefaultPreferences.getMinCautiousParameter();
        if (cautiousUpperBound > DefaultPreferences.getMaxCautiousParameter())
            cautiousUpperBound = DefaultPreferences.getMaxCautiousParameter();
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
