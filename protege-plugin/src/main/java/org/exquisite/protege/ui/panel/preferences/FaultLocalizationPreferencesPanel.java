package org.exquisite.protege.ui.panel.preferences;

import org.exquisite.protege.model.preferences.DebuggerConfiguration;
import org.exquisite.protege.model.preferences.DefaultPreferences;
import org.exquisite.protege.model.preferences.InputValidator;
import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Preferences panel used for defining preferences for the fault localization (a.k.a. diagnosis search).
 */
class FaultLocalizationPreferencesPanel extends AbstractDebuggerPreferencesPanel {

    private JSpinner numOfLeadingDiagsSpinner = new JSpinner(
            new SpinnerNumberModel(
                    DefaultPreferences.getDefaultNumOfLeadingDiags(),
                    DefaultPreferences.getMinNumOfLeadingDiags(),
                    DefaultPreferences.getMaxNumOfLeadingDiags() ,
                    1));

    private JCheckBox reduceIncoherencyCheckbox = new JCheckBox("also repair incoherency", DefaultPreferences.getDefaultReduceIncoherency());

    private JCheckBox extractModulesCheckbox = new JCheckBox("enable module extraction", DefaultPreferences.getDefaultExtractModules());

    private OptionBox extractModules;

    private JComboBox<DebuggerConfiguration.DiagnosisEngineType> engineTypeCombobox = new JComboBox<>();

    private JComboBox<DebuggerConfiguration.ConflictSearcher> conflictSearcherJComboBox = new JComboBox<>();

    private JComboBox<DebuggerConfiguration.CostEstimator> estimatorComboBox = new JComboBox<>();

    // A reference to the query computation preferences is necessary to listen to events
    private QueryComputationPreferencesPanel queryComputationPreferencesPanel;

    FaultLocalizationPreferencesPanel(DebuggerConfiguration configuration, DebuggerConfiguration newConfiguration, QueryComputationPreferencesPanel queryComputationPreferencesPanel) {
        super(configuration, newConfiguration);

        this.queryComputationPreferencesPanel = queryComputationPreferencesPanel;

        for (DebuggerConfiguration.CostEstimator costEstimator : DebuggerConfiguration.CostEstimator.values())
            estimatorComboBox.addItem(costEstimator);

        for (DebuggerConfiguration.DiagnosisEngineType type : DebuggerConfiguration.DiagnosisEngineType.values())
            engineTypeCombobox.addItem(type);

        for (DebuggerConfiguration.ConflictSearcher conflictSearcher : DebuggerConfiguration.ConflictSearcher.values())
            conflictSearcherJComboBox.addItem(conflictSearcher);

        loadConfiguration();
        createPanel();
    }

    private void createPanel() {
        setLayout(new BorderLayout());

        add(getHelpLabel("<html><body>" +
                "Fault Localization is the search for <u>ontology repairs</u> (also called <u>diagnoses</u>). Each axiom in one ontology repair must be appropriately modified or deleted<br>" +
                "in order to repair the inconsistency/incoherency of the faulty ontology. Queries serve to discriminate between multiple possible ontology repairs.<br>" +
                "Sequential answering of queries provides information enabling the debugger to gradually reduce the set of possible ontology repairs." +
                "</body></html>"), BorderLayout.NORTH);

        Box box = Box.createVerticalBox();
        OptionGroupBox groupEngineType = createEngineTypePanel();
        OptionGroupBox groupCostEstimator = createCostEstimationPanel();
        OptionGroupBox groupDiagnosisCalculation = createDiagnosisCalculationPanel();

        box.add(groupEngineType);
        box.add(groupCostEstimator);
        box.add(groupDiagnosisCalculation);

        add(box, BorderLayout.CENTER);
    }

    private OptionGroupBox createEngineTypePanel() {
        final PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        panel.addHelpText("<html><body>" +
                "Conflict sets are minimal fault-preserving subsets of ontology axioms. " +
                "At least one axiom out of each conflict set must be repaired (default: QuickXPlain)." +
                "</body></html>");
        panel.addLabelledGroupComponent("Conflict Searcher: ", new OptionBox("conflictsearcher", conflictSearcherJComboBox));
        panel.addHelpText("<html><body>" +
                "The Diagnosis Engine is responsible for the computation of ontology repairs based on conflict sets (default: HS-Tree)." +
                "</body></html>");
        panel.addLabelledGroupComponent("Diagnosis Engine: ", new OptionBox("enginetype", engineTypeCombobox));
        OptionGroupBox holderEngineType = new OptionGroupBox("Engine Type");
        holderEngineType.add(panel);
        return holderEngineType;
    }

    private OptionGroupBox createCostEstimationPanel() {
        final PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        panel.addHelpText("<html><body>" +
                "Depending on the selected preference function ontology repairs are computed in a different order. <u>Cardinality</u> prefers repairs with fewer axioms.<br>" +
                "<u>EqualCosts</u> means no preference. <u>Syntax</u> depends on specified fault probabilities of keywords that occur in the axioms of repairs (default: Cardinality)" +
                "</body></html>");
        panel.addLabelledGroupComponent("Preference Function: ", new OptionBox("costEstimator", estimatorComboBox));
        OptionGroupBox optionGroupCostEstimator = new OptionGroupBox("Repair Preference Order");
        optionGroupCostEstimator.add(panel);
        return optionGroupCostEstimator;
    }

    private OptionGroupBox createDiagnosisCalculationPanel() {
        final PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        panel.addHelpText("<html><body>" +
                "The repair calculation stops when the specified max. number of repairs is found or when no further repairs exist.<br>" +
                "At least 2 repairs are required for Query Computation. Higher # of repairs tends to generate better queries at the expense of higher computation time (default: 9)." +
                "</body></html>");
        panel.addLabelledGroupComponent("Max. Number of Computed Ontology Repairs per Iteration: ", new OptionBox("numofleadingdiags", numOfLeadingDiagsSpinner));
        numOfLeadingDiagsSpinner.addChangeListener(e ->
                queryComputationPreferencesPanel.updateThresholdParameter(
                        Integer.parseInt(
                                InputValidator.validateInt(
                                        numOfLeadingDiagsSpinner.getValue(),
                                        DefaultPreferences.getMinNumOfLeadingDiags(),
                                        DefaultPreferences.getMaxNumOfLeadingDiags(),
                                        DefaultPreferences.getDefaultNumOfLeadingDiags()
                                )
                        )
                ));
        panel.addHelpText("<html><body>" +
                "Per default the debugger repairs both inconsistency and incoherency of the ontology. Uncheck if only inconsistency should be repaired.<br>" +
                "Module extraction can considerably reduce the search space for repair and query search to a small subset of the ontology's axioms." +
                "</body></html>");


        //JPanel checkBoxesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //checkBoxesPanel.add(new OptionBox("testincoherencyinconsistency", reduceIncoherencyCheckbox));
        extractModules = new OptionBox("extractModules", extractModulesCheckbox);
        extractModules.setEnabled(reduceIncoherencyCheckbox.isSelected());
        extractModules.setEnabledLabel(reduceIncoherencyCheckbox.isSelected());
        //checkBoxesPanel.add(extractModules);
        reduceIncoherencyCheckbox.addItemListener(
                e -> {
                    extractModules.setEnabled(reduceIncoherencyCheckbox.isEnabled());
                    extractModules.setEnabledLabel(reduceIncoherencyCheckbox.isSelected());
                }
        );

        JButton defaultButton = new JButton("Reset to default preferences...");
        defaultButton.addActionListener(e -> resetValues());

        JPanel lastPanel = new JPanel (new BorderLayout(80,0));
        lastPanel.add(new OptionBox("testincoherencyinconsistency", reduceIncoherencyCheckbox), BorderLayout.WEST);
        lastPanel.add(extractModules, BorderLayout.CENTER);
        lastPanel.add(defaultButton, BorderLayout.EAST);

        panel.addGroupComponent(lastPanel);

        OptionGroupBox holderCalculation = new OptionGroupBox("Repair Calculation");

        holderCalculation.add(panel);
        return holderCalculation;
    }

    private void loadConfiguration() {
        engineTypeCombobox.setSelectedItem(InputValidator.validateEngineType(getConfiguration().engineType));
        conflictSearcherJComboBox.setSelectedItem(InputValidator.validateConflictSearcher(getConfiguration().conflictSearcher));
        estimatorComboBox.setSelectedItem(getConfiguration().costEstimator);
        numOfLeadingDiagsSpinner.setValue(
                Integer.parseInt(
                        InputValidator.validateInt(
                                getConfiguration().numOfLeadingDiags,
                                DefaultPreferences.getMinNumOfLeadingDiags(),
                                DefaultPreferences.getMaxNumOfLeadingDiags(),
                                DefaultPreferences.getDefaultNumOfLeadingDiags()
                        )
                )
        );
        reduceIncoherencyCheckbox.setSelected(getConfiguration().reduceIncoherency);
        extractModulesCheckbox.setSelected(getConfiguration().extractModules);
    }

    @Override
    public void saveChanges() {
        getNewConfiguration().engineType = InputValidator.validateEngineType(engineTypeCombobox.getSelectedItem());
        getNewConfiguration().conflictSearcher = InputValidator.validateConflictSearcher(conflictSearcherJComboBox.getSelectedItem());
        getNewConfiguration().costEstimator = (DebuggerConfiguration.CostEstimator) estimatorComboBox.getSelectedItem();
        getNewConfiguration().numOfLeadingDiags = Integer.parseInt(
                InputValidator.validateInt(
                        numOfLeadingDiagsSpinner.getValue(),
                        DefaultPreferences.getMinNumOfLeadingDiags(),
                        DefaultPreferences.getMaxNumOfLeadingDiags(),
                        DefaultPreferences.getDefaultNumOfLeadingDiags()
                )
        );
        getNewConfiguration().reduceIncoherency = reduceIncoherencyCheckbox.isSelected();
        getNewConfiguration().extractModules = extractModulesCheckbox.isSelected();
    }


    /**
     * Button action when user wishes to reset preferences to default values.
     */
    private void resetValues() {
        engineTypeCombobox.setSelectedItem(DefaultPreferences.getDefaultDiagnosisEngineType());
        conflictSearcherJComboBox.setSelectedItem(DefaultPreferences.getDefaultConflictSearcher());
        estimatorComboBox.setSelectedItem(DefaultPreferences.getDefaultCostEstimator());
        numOfLeadingDiagsSpinner.setValue(
                Integer.parseInt(
                        InputValidator.validateInt(
                                DefaultPreferences.getDefaultNumOfLeadingDiags(),
                                DefaultPreferences.getMinNumOfLeadingDiags(),
                                DefaultPreferences.getMaxNumOfLeadingDiags(),
                                DefaultPreferences.getDefaultNumOfLeadingDiags()
                        )
                )
        );
        reduceIncoherencyCheckbox.setSelected(DefaultPreferences.getDefaultReduceIncoherency());
        extractModulesCheckbox.setSelected(DefaultPreferences.getDefaultExtractModules());
    }
}
