package org.exquisite.protege.ui.panel;

import org.exquisite.protege.model.configuration.SearchConfiguration;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 11.09.12
 * Time: 09:50
 * To change this template use File | Settings | File Templates.
 */
public class QueryOptPanel extends AbstractOptPanel {

    private JSpinner partitioningThresholdField = new JSpinner(new SpinnerNumberModel(0.75, 0, 1, 0.01));

    private JComboBox scoringFunction = new JComboBox();

    private JCheckBox minimizeQuery_Checkbox = new JCheckBox("minimize query ", true);


    private JCheckBox incSubClassOf = new JCheckBox("include entailments containing SubClassOf", false);

    private JCheckBox incClassAssert = new JCheckBox("include entailments containing ClassAssertion", false);

    private JCheckBox incEquivClass = new JCheckBox("include entailments containing EquivalentClass", false);

    private JCheckBox incDisjClass = new JCheckBox("include entailments containing DisjointClass", false);

    private JCheckBox incProperty = new JCheckBox("include entailments containing PropertyOf", false);

    private JCheckBox incOntologyAxioms = new JCheckBox("include ontology axioms", true);

    private JCheckBox incRefThingAxioms = new JCheckBox("include axioms referencing top", false);


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

        OptionGroupBox holderEntailments = new OptionGroupBox("Axioms included in query");
        holderEntailments.addOptionBox(new OptionBox("incontologyaxioms",getListener(),incOntologyAxioms));
        holderEntailments.addOptionBox(new OptionBox("increfthingaxioms",getListener(),incRefThingAxioms));
        holderEntailments.addOptionBox(new OptionBox("incsubclassofaxioms",getListener(),incSubClassOf));
        holderEntailments.addOptionBox(new OptionBox("incclassassertaxioms",getListener(),incClassAssert));
        holderEntailments.addOptionBox(new OptionBox("incequivclassaxioms",getListener(),incEquivClass));
        holderEntailments.addOptionBox(new OptionBox("incdisjclassaxioms",getListener(),incDisjClass));
        holderEntailments.addOptionBox(new OptionBox("incpropertyaxiomss",getListener(),incProperty));

        holder.add(holderQueryGen);
        holder.add(holderEntailments);

        add(holder, BorderLayout.NORTH);
        add(getHelpAreaPane(),BorderLayout.CENTER);
    }

    protected void loadConfiguration() {
        minimizeQuery_Checkbox.setSelected(getConfiguration().minimizeQuery);
        scoringFunction.setSelectedItem(getConfiguration().qss);
        partitioningThresholdField.setValue(getConfiguration().entailmentCalThres);
        incRefThingAxioms.setSelected(getConfiguration().incAxiomsRefThing);
        incOntologyAxioms.setSelected(getConfiguration().incOntolAxioms);
        incSubClassOf.setSelected(getConfiguration().inclEntSubClass);
        incClassAssert.setSelected(getConfiguration().incEntClassAssert);
        incEquivClass.setSelected(getConfiguration().incEntEquivClass);
        incDisjClass.setSelected(getConfiguration().incEntDisjClasses);
        incProperty.setSelected(getConfiguration().incEntPropAssert);
    }

    @Override
    public void saveChanges() {

        getNewConfiguration().minimizeQuery = minimizeQuery_Checkbox.isSelected();
        getNewConfiguration().qss = (SearchConfiguration.QSS) scoringFunction.getSelectedItem();
        getNewConfiguration().entailmentCalThres = (Double) partitioningThresholdField.getValue();
        getNewConfiguration().incAxiomsRefThing = incRefThingAxioms.isSelected();
        getNewConfiguration().incOntolAxioms = incOntologyAxioms.isSelected();
        getNewConfiguration().inclEntSubClass = incSubClassOf.isSelected();
        getNewConfiguration().incEntClassAssert = incClassAssert.isSelected();
        getNewConfiguration().incEntEquivClass = incEquivClass.isSelected();
        getNewConfiguration().incEntDisjClasses = incDisjClass.isSelected();
        getNewConfiguration().incEntPropAssert = incProperty.isSelected();
    }

}
