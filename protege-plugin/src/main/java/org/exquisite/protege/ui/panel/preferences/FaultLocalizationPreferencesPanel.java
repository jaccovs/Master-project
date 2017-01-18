package org.exquisite.protege.ui.panel.preferences;

import org.exquisite.protege.model.preferences.DefaultPreferences;
import org.exquisite.protege.model.preferences.DebuggerConfiguration;
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

    private JCheckBox reduceIncoherencyCheckbox = new JCheckBox("also check for incoherency", DefaultPreferences.getDefaultReduceIncoherency());

    // TODO: at the moment the extract modules option causes a major bug (see Issue #1) therefore the preferences does not show the option to change it.
    // private JCheckBox extractModulesCheckbox = new JCheckBox("extract *star* modules of unsatisfiable classes it the ontology is consistent, but its terminology is incoherent", DefaultConfiguration.getDefaultExtractModules());

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
        Box box = Box.createVerticalBox();

        OptionGroupBox groupEngineType = createEngineTypePanel();
        OptionGroupBox groupCostEstimator = createCostEstimationPanel();
        OptionGroupBox groupDiagnosisCalculation = createDiagnosisCalculationPanel();

        box.add(groupEngineType);
        box.add(groupCostEstimator);
        box.add(groupDiagnosisCalculation);

        add(box, BorderLayout.NORTH);
    }

    private OptionGroupBox createEngineTypePanel() {
        final PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        panel.addLabelledGroupComponent("Diagnosis Engine: ", new OptionBox("enginetype", engineTypeCombobox));
        panel.addLabelledGroupComponent("Conflict Searcher: ", new OptionBox("conflictsearcher", conflictSearcherJComboBox));
        OptionGroupBox holderEngineType = new OptionGroupBox("Engine Type");
        holderEngineType.add(panel);
        return holderEngineType;
    }

    private OptionGroupBox createCostEstimationPanel() {
        final PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        panel.addLabelledGroupComponent("Preference Function: ", new OptionBox("costEstimator", estimatorComboBox));
        OptionGroupBox optionGroupCostEstimator = new OptionGroupBox("Cost Estimation");
        optionGroupCostEstimator.add(panel);
        return optionGroupCostEstimator;
    }

    private OptionGroupBox createDiagnosisCalculationPanel() {
        final PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        panel.addLabelledGroupComponent("Number of Faulty Axiom Sets: ", new OptionBox("numofleadingdiags", numOfLeadingDiagsSpinner));
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
        panel.addGroupComponent(new OptionBox("testincoherencyinconsistency", reduceIncoherencyCheckbox));
        //holderCalculation.addOptionBox(new OptionBox("extractModules",  extractModulesCheckbox));

        OptionGroupBox holderCalculation = new OptionGroupBox("Diagnoses Calculation");
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
        //extractModulesCheckbox.setSelected(getConfiguration().extractModules); // TODO deactivated because of issue #1
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
        //getNewConfiguration().extractModules = extractModulesCheckbox.isSelected(); // TODO deactivated because of issue #1
    }

}
