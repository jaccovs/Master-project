package org.exquisite.diagnosis.ranking.smell;

/**
 * 
 * @author Philipp-Malte Lingnau
 * 
 */

public class Config {
	private static ConfigReader reader;
	
	public static final String MultipleReferencesSmellValue = "multipleReferencesSmellValue";
	public static final String EmptyCellSmellValue = "emptyCellSmellValue";
	public static final String IfSmellValue = "ifSmellValue";
	public static final String MultipleOperationsSmellValue = "multipleOperationsSmellValue";
	public static final String LengthSmellValue = "lengthSmellValue";
	public static final String CellsAroundCellSmellValue = "cellsAroundCellSmellValue";
	public static final String ReferencesSmellValue = "referencesSmellValue";
	
	public static final String LengthFunction = "lengthFunction";
	public static final String LengthStart = "lengthStart";
	public static final String LengthEnd = "lengthEnd";
	public static final String LengthUpperEdge = "lenghtUpperEdge";
	public static final String LengthCurveSlope = "lenghtCurveSlope";
	public static final String LengthTurningPoint = "lenghtTurningPoint";
	public static final String MultipleOperationsFunction= "multipleOperationsFunction";
	public static final String MultipleOperationsStart = "multipleOperationsStart";
	public static final String MultipleOperationsEnd = "multipleOperationsEnd";
	public static final String MultipleOperationsUpperEdge = "multipleOperationsUpperEdge";
	public static final String MultipleOperationsCurveSlope = "multipleOperationsCurveSlope";
	public static final String MultipleOperationsTurningPoint = "multipleOperationsTurningPoint";
	public static final String IfFunction = "ifFunction";
	public static final String IfStart = "ifStart";
	public static final String IfEnd = "ifEnd";
	public static final String IfUpperEdge = "ifUpperEdge";
	public static final String IfCurveSlope = "ifCurveSlope";
	public static final String IfTurningPoint = "ifTurningPoint";
	public static final String MultipleReferencesFunction = "multipleReferencesFunction";
	public static final String MultipleReferencesStart = "multipleReferencesStart";
	public static final String MultipleReferencesEnd = "multipleReferencesEnd";
	public static final String MultipleReferencesUpperEdge = "multipleReferencesUpperEdge";
	public static final String MultipleReferencesCurveSlope = "multipleReferencesCurveSlope";
	public static final String MultipleReferencesTurningPoint = "multipleReferencesTurningPoint";
	public static final String ReferencesFunction = "referencesFunction";
	public static final String ReferencesStart = "referencesStart";
	public static final String ReferencesEnd = "referencesEnd";
	public static final String ReferencesUpperEdge = "referencesUpperEdge";
	public static final String ReferencesCurveSlope = "referencesCurveSlope";
	public static final String ReferencesTurningPoint = "referencesTurningPoint";
	public static final String EmptyCellFunction = "emptyCellFunction";
	public static final String EmptyCellsStart = "emptyCellsStart";
	public static final String EmptyCellsEnd = "emptyCellsEnd";
	public static final String EmptyCellUpperEdge = "emptyCellUpperEdge";
	public static final String EmptyCellCurveSlope = "emptyCellCurveSlope";
	public static final String EmptyCellTurningPoint = "emptyCellTurningPoint";
	public static final String CellsAroundCellFunction = "cellsAroundCellFunction";
	public static final String CellsAroundCellStart = "cellsAroundCellStart";
	public static final String CellsAroundCellEnd = "cellsAroundCellEnd";
	public static final String CellsAroundCellUpperEdge = "cellsAroundCellUpperEdge";
	public static final String CellsAroundCellCurveSlope = "cellsAroundCellCurveSlope";
	public static final String CellsAroundCellTurningPoint = "cellsAroundCellTurningPoint";

	public static final String TopologicallyOrderListPath = "topologicallyOrderListPath";
	public static final String XMLFilesPath = "xmlFilesPath";
	public static final String FormulaQualityEdge = "formulaQualityEdge";
	public static final String XPathFormulaCountExpression = "xpathFormulaCountExpression";
	public static final String FirstEvaluationFilePath = "firstEvaluationFilePath";
	public static final String SecondEvaluationFilePath = "secondEvaluationFilePath";
	/**
	 * Creates a reader for ConfigReader if not existing and delivers the string
	 * with the config parameter.
	 * Works for all int returns
	 * 
	 * @param configurationString
	 * @return Value of the given configurationString as integer
	 */
	public static int getIntValue(String configurationString) {
		if (reader == null) {
			reader = new ConfigReader();
		}
		return reader.getIntConfigValue(configurationString);
	}

	/**
	 * Creates a reader for ConfigReader if not existing and delivers the string
	 * with the config parameter.
	 * Works for all float returns
	 * 
	 * @param configurationString
	 * @return Value of the given configurationString as float
	 */
	public static float getFloatValue(String configurationString) {
		if (reader == null) {
			reader = new ConfigReader();
		}
		return reader.getFloatConfigValue(configurationString);
	}

	/**
	 * Creates a reader for ConfigReader if not existing and delivers the string
	 * with the config parameter.
	 * Works for all string returns
	 * 
	 * @param configurationString
	 * @return Value of the given configurationString as string
	 */
	public static String getStringValue(String configurationString) {
		if (reader == null) {
			reader = new ConfigReader();
		}
		return reader.getStringConfigValue(configurationString);
	}
}
