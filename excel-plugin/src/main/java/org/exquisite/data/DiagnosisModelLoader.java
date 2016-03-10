package org.exquisite.data;

import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.exquisite.datamodel.*;
import org.exquisite.diagnosis.models.ConstraintsDiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.parser.FormulaParser;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

/**
 * Populates a <tt>DiagnosisModel</tt> object with data from an <tt>ExquisiteAppXML</tt> object received from a client.<p>
 * <p>
 * Mapping of attributes from ExquisiteAppXML to DiagnosisModel:
 * <ul>
 * <li>Formulas 					-> 	possiblyFaultyStatements</li>
 * <li>Inputs, Outputs, Interims 	-> 	variables</li>
 * <li>ValueBounds 					->  Example object</li>
 * <li>FaultyValues					->	Negative Example object</li>
 * <li>CorrectFormulas				-> 	correctStatements (also removed from possiblyFaultyStatements)</li>
 * </ul>
 * TestCase objects to DiagnosisModel:
 *
 * @author David
 */
public class DiagnosisModelLoader {
    // Let's see them from outside
    public Dictionary<String, Constraint> formulae;
    private ExcelExquisiteSession<Constraint> sessionData;
    private ExquisiteAppXML appXML;        //source data.
    private ExquisiteGraph<String> graph;
    private VariablesFactory varFactory;    //for making decision variables.
    private ConstraintsFactory conFactory;    //for making various constraint types.

    /**
     * @param targetModel  The target DiagnosisModel object to be populated.
     * @param sourceAppXML The source data to populate the DiagnosisModel object with.
     * @param varFactory   A helper class for building the constraint model variables.
     * @param conFactory   A helper class for making the various kinds of constraints for the model.
     */
    public DiagnosisModelLoader(ExcelExquisiteSession<Constraint> sessionData, VariablesFactory varFactory,
                                ConstraintsFactory conFactory) {
        this.sessionData = sessionData;
        this.appXML = this.sessionData.appXML;
        this.graph = this.sessionData.graph;
        this.varFactory = varFactory;
        this.conFactory = conFactory;
    }

    /**
     * Populates a DiagnosisModel object with data from ExquisiteAppXML object that was passed into the constructor.
     *
     * @return DiagnosisModel that has been populated from the ExquisiteAppXML object.
     */
    public void loadDiagnosisModelFromXML() {
        //Make variables with any **global** user defined value bounds.
        List<IntegerVariable> varsWithGlobalValueBounds = varFactory
                .makeVariablesWithGlobalValueBounds(appXML.getValueBounds(), appXML.getCellsInRange());
        ExquisiteValueBound defaultValueBound = appXML.getDefaultValueBound();

        //Input cell variables with default value bounds.
        int min = (int) defaultValueBound.getLower();
        int max = (int) defaultValueBound.getUpper();
        List<IntegerVariable> inputVariables = varFactory.makeVariables(appXML.getInputs(), min, max);

        //Interim cell variables.
        List<IntegerVariable> interimVariables = varFactory.makeVariables(appXML.getInterims(), min, max);

        //Output cell variables.
        List<IntegerVariable> outputVariables = varFactory.makeVariables(appXML.getOutputs(), min, max);

        //Now add all the variables to the tests.diagnosis model.
        final ConstraintsDiagnosisModel<Constraint> diagnosisModel =
                (ConstraintsDiagnosisModel<Constraint>) this.sessionData.getDiagnosisModel();
        diagnosisModel.getVariables().addAll(varsWithGlobalValueBounds);
        diagnosisModel.getVariables().addAll(inputVariables);
        diagnosisModel.getVariables().addAll(interimVariables);
        diagnosisModel.getVariables().addAll(outputVariables);

        //Build a graph representation of the dependencies between the variables.
        appXML.buildGraph(this.graph);
        diagnosisModel.graph = this.graph;

        //Make the constraint representations of the spreadsheet formulae.
        FormulaParser formulaParser = new FormulaParser(this.graph);
        formulae = conFactory.makeFormulae(appXML.getFormulas(),
                formulaParser,
                varFactory.getVariablesMap(),
                diagnosisModel);

        //Make globally defined value bounds constraints for the variables defined with global value bounds at the start of this method.
        // TS: We do not have to do this, as variables with global value bounds are directly created with their value bounds
        // in varFactory.makeVariablesWithGlobalValueBounds()
        //conFactory.makeGlobalValueBoundsConstraints(appXML.getValueBounds(), appXML.getCellsInRange(), varFactory.getVariablesMap());

        //Now start adding the constraints to the tests.diagnosis model...

        //Add formulae constraints - initially all constraints representing formulae from the spreadsheet go into the possibly faulty constraints list.
        for (Enumeration<String> keys = formulae.keys(); keys.hasMoreElements(); ) {
            String cellReference = keys.nextElement();
            Constraint constraint = formulae.get(cellReference);
            diagnosisModel.addPossiblyFaultyConstraint(constraint, cellReference);
        }

        //Add global test data
        //Sort correct formulae constraints - these constraints are removed from possibly faulty constraints list and added to the correct statements list.
        List<Constraint> correctFormulae = findCorrectStatementsInParser(appXML.getCorrectFormulas(), formulaParser);
        diagnosisModel.removeConstraintsToIgnore(correctFormulae);
        for (Constraint correctStatement : correctFormulae) {
            diagnosisModel.
                    addCorrectFormula(correctStatement,
                            diagnosisModel.getConstraintName(correctStatement));
        }

        //Add global value bound constraints.
        for (Constraint constraint : conFactory.getPositiveExample().constraints) {
            diagnosisModel
                    .addCorrectFormula(constraint, conFactory.getPositiveExample().constraintNames.get(constraint));
        }

        //Add negative examples - comprised of constraints derived from all of the test cases.");
        if (conFactory.getNegativeExample().constraints.size() != 0) {
            List<Example<Constraint>> negativeExamples = new ArrayList<>();
            negativeExamples.add(conFactory.getNegativeExample());
            diagnosisModel.setNegativeExamples(negativeExamples);
        }

        //Transform ExquisiteAppXML test cases into (positive) Example objects.
        //Some global constraints are defined during this process as well.
        List<Example<Constraint>> examples = conFactory.makeExamplesFromTestCases(appXML.getTestCases(), appXML
                        .getCellsInRange(),
                varFactory.getVariablesMap(), appXML.getInputs());

        // Add testcases from fragments, too
        Enumeration<Fragment> fragments = appXML.getFragments().elements();
        while (fragments.hasMoreElements()) {
            Fragment fragment = fragments.nextElement();
            if (fragment.getTestCases() != null && fragment.getTestCases().size() > 0) {
                examples.addAll(conFactory.makeExamplesFromTestCases(fragment.getTestCases(), appXML.getCellsInRange(),
                        varFactory.getVariablesMap(), fragment.getInputs()));
                for (String link : fragment.getLinkedFragments()) {
                    // TODO: add examples for linked fragments
                }
            }
        }

        //Add positive examples to the tests.diagnosis model
        diagnosisModel.setPositiveExamples(examples);
    }

    /**
     * Adds a constraint to the correctConstraints list
     * Removes constraint from possiblyFaultyStatements list.
     *
     * @param correctStatementsData
     * @param cellsInRange
     * @param formulaParser
     */
    private List<Constraint> findCorrectStatementsInParser(Dictionary<String, String> correctFormulae,
                                                           FormulaParser formulaParser) {
        List<Constraint> result = new ArrayList<Constraint>();
        for (Enumeration<String> keys = correctFormulae.keys(); keys.hasMoreElements(); ) {
            String cellReference = keys.nextElement();
            Constraint constraint = formulaParser.constraints.get(cellReference);
            result.add(constraint);
        }
        return result;
    }
}