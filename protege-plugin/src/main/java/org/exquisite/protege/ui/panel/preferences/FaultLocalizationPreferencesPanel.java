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

        add(getHelpLabel("<html><body>" +
                "Fault Localization is the search for <u>candidate sets of faulty axioms</u> " +
                "(so called <u>diagnoses</u>). They explain inconsistencies/incoherencies in ontologies.<br>" +
                "Diagnoses are required for the computation of queries in the debugger." +
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
                "The Diagnosis Engine is responsible for the computation of diagnoses in faulty ontologies based on hitting sets over all conflict sets (default: HS-Tree)." +
                "</body></html>");
        panel.addLabelledGroupComponent("Diagnosis Engine: ", new OptionBox("enginetype", engineTypeCombobox));
        panel.addHelpText("<html><body>" +
                "Conflict sets are minimal subsets of axioms in the ontology such that ontologies become inconsistent.<br>" +
                "Conflict searchers find such minimal conflict sets (default: QuickXPlain)." +
                "</body></html>");
        panel.addLabelledGroupComponent("Conflict Searcher: ", new OptionBox("conflictsearcher", conflictSearcherJComboBox));
        OptionGroupBox holderEngineType = new OptionGroupBox("Engine Type");
        holderEngineType.add(panel);
        return holderEngineType;
    }

    private OptionGroupBox createCostEstimationPanel() {
        final PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        panel.addHelpText("<html><body>" +
                "Depending on the selected preference function diagnoses are sorted differently. <u>Cardinality</u> prefers diagnoses with fewer axioms.<br>" +
                "<u>EqualCosts</u> has no preference while <u>Syntax</u> depends on keywords that occur in the axioms of diagnoses (default: Cardinality)" +
                "</body></html>");
        panel.addLabelledGroupComponent("Preference Function: ", new OptionBox("costEstimator", estimatorComboBox));
        OptionGroupBox optionGroupCostEstimator = new OptionGroupBox("Cost Estimation");
        optionGroupCostEstimator.add(panel);
        return optionGroupCostEstimator;
    }

    private OptionGroupBox createDiagnosisCalculationPanel() {
        final PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        panel.addHelpText("<html><body>" +
                "Searches for a max. number of diagnoses. The diagnoses calculation stops when the max. number of diagnoses is found or when there exist only fewer diagnoses.<br>" +
                "At least 2 diagnoses are required for Query Computation. Higher number of diagnoses generate better queries but at the expense of time (default: 9)." +
                "</body></html>");
        panel.addLabelledGroupComponent("Max. Number of Faulty Axiom Sets: ", new OptionBox("numofleadingdiags", numOfLeadingDiagsSpinner));
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
                "Shall the Diagnosis Engine only check for consistency in the ontology or also check for coherency (default: check both)?" +
                "</body></html>");
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
