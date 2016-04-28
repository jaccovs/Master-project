package org.exquisite.protege.ui.panel;

import org.exquisite.protege.model.configuration.SearchConfiguration;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 11.09.12
 * Time: 09:49
 * To change this template use File | Settings | File Templates.
 */
public class DiagnosisOptPanel extends AbstractOptPanel {

    public static final int maxLeadingDiags = 18;

    private JCheckBox test_Tbox_Checkbox = new JCheckBox("include TBox in Background Knowledge", false);

    private JCheckBox test_Abox_Checkbox = new JCheckBox("include ABox in Background Knowledge", true);

    private JSpinner numofLeadingDiagsField = new JSpinner(new SpinnerNumberModel(9, 1, maxLeadingDiags + 1, 1));

    private JCheckBox test_incoherency_inconsistency_Checkbox = new JCheckBox("reduce incoherency to inconsistency ", false);

    private JCheckBox calcAllDiags_checkbox = new JCheckBox("calc all diagnoses ", true);

    private JComboBox treeType = new JComboBox();

    private JComboBox searchType = new JComboBox();

    public DiagnosisOptPanel(SearchConfiguration configuration, SearchConfiguration newConfiguration) {
        super(configuration, newConfiguration);

        for (SearchConfiguration.SearchType type : SearchConfiguration.SearchType.values())
            searchType.addItem(type);
        for (SearchConfiguration.TreeType type : SearchConfiguration.TreeType.values())
            treeType.addItem(type);

        loadConfiguration();
        createPanel();

    }

    protected void createPanel() {
        setLayout(new BorderLayout());
        Box holder = Box.createVerticalBox();


        OptionGroupBox holderBoxes = new OptionGroupBox("Background Axioms");
        holderBoxes.addOptionBox(new OptionBox("abox",getListener(),test_Abox_Checkbox));
        holderBoxes.addOptionBox(new OptionBox("tbox",getListener(),test_Tbox_Checkbox));

        OptionGroupBox holderSearch = new OptionGroupBox("Search Tree");
        holderSearch.addOptionBox(new OptionBox("searchtype",getListener(),new JLabel("Search Type: "), searchType));
        holderSearch.addOptionBox(new OptionBox("treetype",getListener(),new JLabel("Tree Type: "), treeType));

        OptionGroupBox holderCalculation = new OptionGroupBox("Calculation");
        holderCalculation.addOptionBox(new OptionBox("numofleadingdiags",getListener(),new JLabel("NumOfLeadingDiag: "), numofLeadingDiagsField));
        holderCalculation.addOptionBox(new OptionBox("calcalldiags",getListener(),calcAllDiags_checkbox));
        holderCalculation.addOptionBox(new OptionBox("testincoherencyinconsistency",getListener(),test_incoherency_inconsistency_Checkbox));

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
        calcAllDiags_checkbox.setSelected(getConfiguration().calcAllDiags);

        numofLeadingDiagsField.setValue(getConfiguration().numOfLeadingDiags);
        searchType.setSelectedItem(getConfiguration().searchType);
        treeType.setSelectedItem(getConfiguration().treeType);
    }

    @Override
    public void saveChanges() {

        getNewConfiguration().tBoxInBG = test_Tbox_Checkbox.isSelected();
        getNewConfiguration().aBoxInBG = test_Abox_Checkbox.isSelected();

        getNewConfiguration().reduceIncoherency = test_incoherency_inconsistency_Checkbox.isSelected();
        getNewConfiguration().calcAllDiags = calcAllDiags_checkbox.isSelected();

        getNewConfiguration().numOfLeadingDiags = (Integer) numofLeadingDiagsField.getValue();
        getNewConfiguration().searchType = (SearchConfiguration.SearchType) searchType.getSelectedItem();
        getNewConfiguration().treeType = (SearchConfiguration.TreeType) treeType.getSelectedItem();

    }

}
