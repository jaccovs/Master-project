package org.exquisite.protege.ui.panel.preferences;

import org.exquisite.protege.model.preferences.DefaultPreferences;
import org.exquisite.protege.model.preferences.DebuggerConfiguration;
import org.exquisite.protege.model.preferences.InputValidator;
import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;

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
    private JCheckBox enrichQueryCheckbox = new JCheckBox("enrich and optimize query ", DefaultPreferences.getDefaultEnrichQuery());
    private JComboBox<DebuggerConfiguration.SortCriterion> sortCriterionCombobox = new JComboBox<>();
    private JComboBox<DebuggerConfiguration.RM> rmComboBox = new JComboBox<>();
    private JSpinner entropyThresholdSpinner = new JSpinner(
            new SpinnerNumberModel(
                    DefaultPreferences.getDefaultEntropyThreshold(),
                    DefaultPreferences.getMinEntropyThreshold(),
                    DefaultPreferences.getMaxEntropyThreshold(),
                    0.01));
    private JSpinner cardinalityThresholdSpinner = new JSpinner
            (new SpinnerNumberModel(
                    DefaultPreferences.getDefaultCardinalityThreshold(),
                    DefaultPreferences.getMinCardinalityThreshold(),
                    DefaultPreferences.getMaxCardinalityThreshold(),
                    1));
    private JSpinner cautiousParameterSpinner = new JSpinner(
            new SpinnerNumberModel(
                    DefaultPreferences.getDefaultCautiousParameter(),
                    DefaultPreferences.getMinCautiousParameter(),
                    DefaultPreferences.getMaxCautiousParameter(),
                    0.01));
    private OptionBox entropyThresholdOptionBox;
    private OptionBox cautiousParameterOptionBox;
    private OptionBox cardinalityThresholdOptionBox;

    private Component learningParametersLabel;

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

        add(getHelpLabel("<html><body>Query Computation is a 3-stage process...</body><html>"), BorderLayout.NORTH);

        Box stages = Box.createVerticalBox();
        OptionGroupBox stage1 = createStage1Panel();
        OptionGroupBox stage2 = createStage2Panel();
        OptionGroupBox stage3 = createStage3Panel();
        stages.add(stage1);
        stages.add(stage2);
        stages.add(stage3);
        add(stages,BorderLayout.CENTER);

        // call the listener implementation for RM immediately after creation
        handleRMChanged((DebuggerConfiguration.RM) this.rmComboBox.getSelectedItem());
    }

    private OptionGroupBox createStage1Panel() {
        final PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        panel.addHelpText("<html><body>" +
                "Goal: minimize the overall # of queries in debugging session. " +
                "There are several Query Quality Measures for this purpose (default: Entropy)." +
                "</body></html>");

        panel.addLabelledGroupComponent("Query Quality Measure: ", new OptionBox("rm", rmComboBox));
        rmComboBox.addActionListener(e -> {
            handleRMChanged((DebuggerConfiguration.RM) ((JComboBox<DebuggerConfiguration.RM>) e.getSource()).getSelectedItem());
        });

        panel.addHelpText("<html><body>" +
                "Approx. parameters control the quality of the found query w.r.t the selected measure.<br>" +
                "Lower values signalize better quality, but with a potentially longer query comp.time." +
                "</body></html>");

        entropyThresholdSpinner.setPreferredSize(new Dimension(60, 22));
        entropyThresholdOptionBox = new OptionBox("entropythreshold", entropyThresholdSpinner, new JLabel(" Entropy"));
        panel.addLabelledGroupComponent("Approx. Parameters: ", entropyThresholdOptionBox);

        cardinalityThresholdSpinner.setPreferredSize(new Dimension(60, 22));
        cardinalityThresholdOptionBox = new OptionBox("cardinalitythreshold", cardinalityThresholdSpinner, new JLabel(" Cardinality"));
        panel.addLabelledGroupComponent(null, cardinalityThresholdOptionBox);

        cautiousParameterSpinner.setPreferredSize(new Dimension(60, 22));
        cautiousParameterSpinner = new JSpinner(
                new SpinnerNumberModel(
                        getCautiousValue(this.getConfiguration().cautiousParameter),
                        getCautiousLowerBound(),
                        getCautiousUpperBound(),
                        0.01));
        cautiousParameterOptionBox = new OptionBox("cautiousparameter", cautiousParameterSpinner, new JLabel("Cautiousness"));
        panel.addLabelledGroupComponent("Learning Parameters: ", cautiousParameterOptionBox);
        this.learningParametersLabel = getLearningParametersLabel(panel);

        final OptionGroupBox stage1 = new OptionGroupBox("Stage 1");
        stage1.add(panel);
        return stage1;
    }

    /**
     * This is a quick and dirty helper method to get the label of the labelled group component from the PreferenceLayoutPanel.
     * <p>
     * <b>Notice that this depends heavily on the implementation of org.protege.editor.core.ui.preferences.PreferenceLayoutPanel and that the method is
     * called immediately after PreferenceLayoutPanel.addLabelledGroupComponent(label,component) has been called!!!</b>
     * </p>
     * @param panel an instance of PreferenceLayoutPanel
     * @return either the JLabel instance created in PreferenceLayoutPanel.addLabelledGroupComponent(label,component) or
     * <code>null</code> if not found or the implementation of PreferenceLayoutPanel has changed.
     * @see org.protege.editor.core.ui.preferences.PreferencesLayoutPanel
     */
    private JLabel getLearningParametersLabel(PreferencesLayoutPanel panel) {
        final Component[] components = panel.getComponents();
        if (components != null && components.length == 1) {
            final Component aComponent = components[0];
            if (aComponent instanceof JPanel) {
                final Component[] backingPanelComponents = ((JPanel) aComponent).getComponents();
                final int len = backingPanelComponents.length;
                if (len >= 2 && backingPanelComponents[len-2] instanceof JLabel) {
                    return (JLabel)backingPanelComponents[len-2];
                }
            }
        }
        return null;
    }

    private OptionGroupBox createStage2Panel() {
        final PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        panel.addHelpText("<html><body>" +
                "Goal: minimize the # of axioms per query (<u>MinCard</u>) or the complexity of the query axioms (<u>MinSum</u>: tries to minimize the overall complexity<br>" +
                "of all query axioms; <u>MinMax</u>: tries to min. the complexity of the most complex query axiom). Axiom complexity is estimated from syntax<br>" +
                "fault probabilities - the higher the estimated fault prob. of the axiom, the higher the estimated complexity of it (default: MinCard).<br>" +
                "</body></html>");

        panel.addLabelledGroupComponent("Criterion: ", new OptionBox("sortcriterion", sortCriterionCombobox));
        final OptionGroupBox stage2 = new OptionGroupBox("Stage 2");
        stage2.add(panel);
        return stage2;
    }

    private OptionGroupBox createStage3Panel() {
        final PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        panel.addHelpText("<html><body>" +
                "Goal: tries to make query easier to understand for the user by simplifying axioms in the query.  Users can select axiom types considered easy.<br>" +
                "These are used to <u>enrich</u> the query of Stage 2 first.<br>" +
                "Then the enriched query is <u>optimized</u>, i.e. a smallest and easiest possible subset of it is determined (default: enabled)." +
                "</body></html>");
        final OptionGroupBox stage3 = new OptionGroupBox("Stage 3");
        JPanel lastPanel = new JPanel (new BorderLayout(150,0));
        lastPanel.add(new OptionBox("enrichquery", enrichQueryCheckbox), BorderLayout.WEST);
        JButton defaultButton = new JButton("Reset to default preferences...");
        lastPanel.add(defaultButton, BorderLayout.EAST);
        defaultButton.addActionListener(e -> resetValues());
        panel.addGroupComponent(lastPanel);
        stage3.add(panel);
        return stage3;
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

        if (this.learningParametersLabel != null) this.learningParametersLabel.setEnabled(caut);
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

    /**
     * Button action when user wishes to reset preferences to default values.
     */
    private void resetValues() {
        enrichQueryCheckbox.setSelected(DefaultPreferences.getDefaultEnrichQuery());
        sortCriterionCombobox.setSelectedItem(DefaultPreferences.getDefaultSortCriterion());
        rmComboBox.setSelectedItem(DefaultPreferences.getDefaultRM());
        entropyThresholdSpinner.setValue(
                Double.parseDouble(
                        InputValidator.validateDouble(
                                DefaultPreferences.getDefaultEntropyThreshold(),
                                DefaultPreferences.getMinEntropyThreshold(),
                                DefaultPreferences.getMaxEntropyThreshold(),
                                DefaultPreferences.getDefaultEntropyThreshold()
                        )
                )
        );
        cardinalityThresholdSpinner.setValue(
                Double.parseDouble(
                        InputValidator.validateDouble(
                                DefaultPreferences.getDefaultCardinalityThreshold(),
                                DefaultPreferences.getMinCardinalityThreshold(),
                                DefaultPreferences.getMaxCardinalityThreshold(),
                                DefaultPreferences.getDefaultCardinalityThreshold()
                        )
                )
        );

        updateMaxCardinalityParameter();
        cautiousParameterSpinner.setValue(
                Double.parseDouble(
                        InputValidator.validateDouble(
                                DefaultPreferences.getDefaultCautiousParameter(),
                                DefaultPreferences.getMinCautiousParameter(),
                                DefaultPreferences.getMaxCautiousParameter(),
                                DefaultPreferences.getDefaultCautiousParameter()
                        )
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
