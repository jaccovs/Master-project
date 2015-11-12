package evaluations.models;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.ExquisiteValueBound;
import org.exquisite.tools.StringUtilities;

public class JCKBSEModel {	
	
	//public static final String ProductNameColumn = "B";
	public static final String ProductCostPCColumn = "C";
	public static final String CostColumn = "D";
	public static final String SalesNumbersStartColumn = "F";
	public static final String TotalSalesColumn = "S";
	public static final String RevenueColumn = "T";
	public static final String ProductCostColumn = "U";
	public static final String OverallFiguresColumn = "T"; //output cells.
	
	//since excel row starts at 1.
	public static final int FirstRowNumber = 1;
	
	//public static final String ProductNamePrefix = "Product";
	public static final String VariableNamePrefix = "WS_1_";
	public static final int DefaultMin = 0;
	public static final int DefaultMax = 10000;
	
	//Convenience list for easy access to specific interim variables.
	private List<String> totalSalesNames;
	private List<String> revenueNames;
	private List<String> prodCostNames;
	private List<String> outputNames;
	
	private int inputRows;
	private int inputSalesColumns; 
	private int inputVarCount;
	private int interimVarCount;
	private int outputVarCount;
	
	
	public static void main(String[] args){
		System.out.println("Starting");
		final int InputRows = 1;
		final int InputMinValue = 0;
		final int InputMaxValue = 10;
		JCKBSEModel modelBuilder = new JCKBSEModel();
		ExquisiteAppXML appXML = modelBuilder.defineModel(InputRows, InputMinValue, InputMaxValue);
		//String test = "ZZZ";
		//String output = StringUtilities.next(test);
		//System.out.println("output = " + output);
//		System.out.println("Done ..");
	}
	
	public JCKBSEModel()
	{
		totalSalesNames = new ArrayList<String>();
		revenueNames = new ArrayList<String>();
		prodCostNames = new ArrayList<String>();
		outputNames = new ArrayList<String>();
	}
	
	public String getTotalSalesCellName()
	{
		return outputNames.get(0);
	}
	
	public String getTotalRevenueCellName()
	{
		return outputNames.get(1);
	}
	
	public String getTotalProdCostsCellName()
	{
		return outputNames.get(2);
	}

	public String getProfitCellName()
	{
		return outputNames.get(3);
	}
	
	/**
	 * @return the number of input variables in the model.
	 */
	public int getInputVarCount()
	{
		return inputVarCount + interimVarCount + outputVarCount;
	}
	
	/**
	 * Returns an ExquisiteAppXML object representing the model from the paper.
	 * @param rows
	 * @param minInputDomain
	 * @param maxInputDomain
	 * @return
	 */
	public ExquisiteAppXML defineModel(int rows, int minInputDomain, int maxInputDomain)
	{
		inputRows = rows;
		inputSalesColumns = 12;//12;//Jan-Dec.
		
		ExquisiteAppXML model = new ExquisiteAppXML();
				
		//set default value bound
		model.setDefaultValueBound(makeValueBound(DefaultMin, DefaultMax));
		//define input variables
		model.setInputs(makeInputNames(inputRows));						
		//define interim variables
		model.setInterims(makeInterimNames(inputRows));		
		//define output variables
		model.setOutputs(makeOutputNames(inputRows));
		
		//define domain sizes for variables
		//input domain
		Dictionary<String, ExquisiteValueBound> domainCollection = new Hashtable<String, ExquisiteValueBound>();
		defineVariablesDomain(domainCollection, model.getInputs(), minInputDomain, maxInputDomain);
		
		//"S" column Total Sales interim cells domain
		int maxTotalSales = calculateMaxDomainSize(maxInputDomain, inputSalesColumns);
		int minTotalSales = minInputDomain;//calculateMinDomainSize(minInputDomain, inputSalesColumns);
		defineVariablesDomain(domainCollection, totalSalesNames, minTotalSales, maxTotalSales);
		
		//"T" column Revenue interim cells domain
		int maxRevenue = calculateMaxDomainSize(maxTotalSales, maxInputDomain);
		int minRevenue = minInputDomain;//calculateMinDomainSize(maxTotalSales, minInputDomain);
		defineVariablesDomain(domainCollection, revenueNames, minRevenue, maxRevenue);
		
		//"U" column Prod.cost interim cells domain
		int maxProdCost = maxRevenue;
		int minProdCost = minInputDomain;//minRevenue;
		defineVariablesDomain(domainCollection, prodCostNames, minProdCost, maxProdCost);
		
		//add domains to model.
		model.setValueBounds(domainCollection);
		
		//define interim formulae
		Dictionary<String, String> formulaCollection = new Hashtable<String, String>();
		makeTotalSalesFormulae(formulaCollection);
		makeRevenueFormulae(formulaCollection);
		makeProdCostFormulae(formulaCollection);
		//define output formulae
		makeOutputFormulae(formulaCollection);
		model.setFormulas(formulaCollection);	
	
		return model;
	}
	
	/**
	 * Makes a list of strings corresponding to the input cell names.
	 * @param rows
	 * @return
	 */
	public List<String> makeInputNames(int rows)
	{
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < rows; i++) 
		{
			String productCostPcVarName = VariableNamePrefix + ProductCostPCColumn + (FirstRowNumber + i);
			String costVarName = VariableNamePrefix + CostColumn + (FirstRowNumber + i);
			result.add(productCostPcVarName);
			result.add(costVarName);
			//sales numbers row
			String salesColName = SalesNumbersStartColumn;
			for(int salesCol=0; salesCol < inputSalesColumns; salesCol++)
			{
				String salesVarName = VariableNamePrefix + salesColName + (FirstRowNumber + i);
				salesColName = StringUtilities.next(salesColName);//.toUpperCase();
				result.add(salesVarName);				
			}
		}			
		inputVarCount = result.size();
		return result;
	}
	
	/**
	 * Makes a list of strings corresponding to the interim cell names.
	 * @param rows
	 * @return
	 */		
	public List<String> makeInterimNames(int rows)
	{		
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < rows; i++) 
		{
			String totalSalesVarName = VariableNamePrefix + TotalSalesColumn + (FirstRowNumber + i);
			String revenueVarName = VariableNamePrefix + RevenueColumn + (FirstRowNumber + i);
			String prodCostName = VariableNamePrefix + ProductCostColumn + (FirstRowNumber + i);
			result.add(totalSalesVarName);
			
			totalSalesNames.add(totalSalesVarName);
			result.add(revenueVarName);			
			revenueNames.add(revenueVarName);
			result.add(prodCostName);			
			prodCostNames.add(prodCostName);			
		}	
		interimVarCount = result.size();
		return result;
	}
	
	/**
	 * Makes a list of strings corresponding to the output cell names.
	 * @param rows
	 * @return
	 */
	public List<String> makeOutputNames(int rows)
	{
		final int OutputCellCount = 4;
		final int RowSpacing = 2;
		int startRow = rows + RowSpacing;
		List<String> result = new ArrayList<String>();
		for(int i=startRow; i < (startRow + OutputCellCount); i++)
		{
			String outputCellName = VariableNamePrefix + OverallFiguresColumn + (FirstRowNumber + i);
			result.add(outputCellName);
			outputNames.add(outputCellName);
		}	
		outputVarCount = result.size();
		return result;		
	}
	
	/**
	 * Makes the formulae for the cells in total sales column "S"
	 * @return
	 */
	public void makeTotalSalesFormulae(Dictionary<String, String> formulaCollection )
	{
		Pattern pattern = Pattern.compile("[0-9]+|[A-Z]+");
		
		for(String cellName : totalSalesNames)
		{		
			String trimmedCellName = cellName.substring(VariableNamePrefix.length());
			String row = parse(trimmedCellName, pattern).get(1);	
			String cellFormula = "";
			String currentCol = SalesNumbersStartColumn;
			for(int i=0; i<inputSalesColumns; i++)
			{
				String operator = (i==0) ? "" : "+";
				cellFormula += operator + currentCol + row;
				currentCol = StringUtilities.next(currentCol);//.toUpperCase();
			}
			formulaCollection.put(cellName, cellFormula);
		}
		
	}
	
	/**
	 * Makes the formulae for cells in the revenue column "T"
	 * @return
	 */
	public void makeRevenueFormulae(Dictionary<String, String> formulaCollection)
	{				
		int i = 0;
		for(String cellName : revenueNames)
		{
			Pattern pattern = Pattern.compile("[0-9]+|[A-Z]+");
			String trimmedCellName = totalSalesNames.get(i).substring(VariableNamePrefix.length());
			i++;
			String row = parse(trimmedCellName, pattern).get(1);	
			String cellFormula =  trimmedCellName + "*" + CostColumn + row ;
			formulaCollection.put(cellName, cellFormula);
		}		
	}
	
	/**
	 * Make prodcost column "U" formulae 
	 * @return
	 */
	public void makeProdCostFormulae(Dictionary<String, String> formulaCollection)
	{		
		int i = 0;
		for(String cellName : prodCostNames)
		{
			Pattern pattern = Pattern.compile("[0-9]+|[A-Z]+");
			String trimmedCellName = totalSalesNames.get(i).substring(VariableNamePrefix.length());
			i++;
			String row = parse(trimmedCellName, pattern).get(1);				
			String cellFormula = trimmedCellName + "*" + ProductCostPCColumn + row ;
			formulaCollection.put(cellName, cellFormula);
		}		
	}
	
	/**
	 * The output formulae...
	 * @param formulaCollection
	 */
	public void makeOutputFormulae(Dictionary<String, String> formulaCollection)
	{
		//total salesTotalSalesColumn
		String totalSalesFormula = "";
		for(int i=0; i<inputRows; i++)
		{
			String operator = (i==0) ? "" : "+";
			totalSalesFormula += operator + TotalSalesColumn + (FirstRowNumber + i);
		}
		formulaCollection.put(outputNames.get(0), totalSalesFormula);
		
		//total revenue
		String totalRevenueFormula = "";
		for(int i=0; i<inputRows; i++)
		{
			String operator = (i==0) ? "" : "+";
			totalRevenueFormula += operator + RevenueColumn + (FirstRowNumber + i);
		}
		formulaCollection.put(outputNames.get(1), totalRevenueFormula);
		
		//total prod. costs
		String totalProdCostsFormula = "";
		for(int i=0; i<inputRows; i++)
		{
			String operator = (i==0) ? "" : "+";
			totalProdCostsFormula += operator + ProductCostColumn + (FirstRowNumber + i);
		}		
		formulaCollection.put(outputNames.get(2), totalProdCostsFormula);
		
		//profit
		String trimmedRevenueCellName = outputNames.get(1).substring(VariableNamePrefix.length());
		String trimmedProductCellName = outputNames.get(2).substring(VariableNamePrefix.length());
		String profitFormula = trimmedRevenueCellName + "-" + trimmedProductCellName;
		formulaCollection.put(outputNames.get(3), profitFormula);
	}
	
	/**
	 * Breaks a string up into a list of chunks based on regex pattern.
	 * @param toParse
	 * @param pattern
	 * @return
	 */
	private List<String> parse(String toParse, Pattern pattern) {
	    List<String> chunks = new ArrayList<String>();
	    Matcher matcher = pattern.matcher(toParse);
	    while (matcher.find()) {
	        chunks.add( matcher.group() );
	    }
	    return chunks;
	}
	
	/**
	 * The default value bounds to use on variables with no explicitly defined domain.
	 * @return
	 */
	public ExquisiteValueBound makeValueBound(int min, int max)
	{
		return new ExquisiteValueBound(min, max, 0.1);
	}
	
	/**
	 * Creates a domain for a given collection of variables and adds it to a collection.
	 * @param domainCollection - the collection to add the new domain to.
	 * @param variables - the variables to create the domain for.
	 * @param min - minimum domain value.
	 * @param max - maximum domain value.
	 */
	public void defineVariablesDomain(Dictionary<String, ExquisiteValueBound> domainCollection, List<String> variables, int min, int max)
	{
		for(String variable : variables)
		{
			ExquisiteValueBound valueBound = makeValueBound(min, max);
			domainCollection.put(variable, valueBound);
		}
	}
	
	/**
	 * Assuming all inputs have the same domain size
	 * @param inputDomain
	 * @param inputCount
	 * @return
	 */
	public int calculateMaxDomainSize(int inputMaxDomainValue, int inputCount)
	{
		return inputCount * inputMaxDomainValue;
	}
	
	
	/**
	 * Assuming all inputs have the same domain size
	 * @param inputDomain
	 * @param inputCount
	 * @return
	 */
	public int calculateMinDomainSize(int inputMinDomainValue, int inputCount)
	{
		return inputCount * inputMinDomainValue;
	}
	
}


