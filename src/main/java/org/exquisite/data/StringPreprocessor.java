package org.exquisite.data;

import java.util.Enumeration;
import java.util.Locale;

import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.TestCase;
import org.exquisite.i8n.Culture;
import org.exquisite.i8n.CultureInfo;

/**
 * Transforms strings from one language culture to another. Important since all internal (i.e. java)
 * string representation is in English.
 *  
 * @author David
 */
public class StringPreprocessor {
	
	/**
	 * Runs a example that replaces the decimal points in a formula string formatted with German culture to English.
	 * @param args
	 */
	public static void main(String[]args){
		String test = "A1*1,2";
		String output = replaceDecimalPoints(test, Locale.GERMAN, Locale.ENGLISH);
		System.out.println("Output = " + output);
	}	
	
	/**
	 * Changes string representation of formulae into a string culture understandable in java e.g. decimal points as "." etc. 
	 * @param appXML - the ExquisiteAppXML object sent from a client.
	 * @return - the same ExquisiteAppXML but with modified content.
	 */
	public static ExquisiteAppXML processForImport(ExquisiteAppXML appXML, DiagnosisConfiguration config)
	{		
		//Check if any processing actually needs to happen, if not then just return the appXML untouched.
		if (config.defaultOutputLocale == Locale.ENGLISH){
			return appXML;
		}
		else{			
			//Process numbers and formulae.
			
			//formulae
			Enumeration<String> enumeration = appXML.getFormulas().keys();
			while(enumeration.hasMoreElements())
			{
				String key = enumeration.nextElement();
				String formula = appXML.getFormulas().get(key);				
				
				// TODO TS: Better way to deal with functions
				if (!formula.startsWith("SUM") && !formula.startsWith("IF") && !formula.startsWith("QUOTIENT")){
					String newFormula = replaceDecimalPoints(formula, config.defaultOutputLocale, Locale.ENGLISH);
					appXML.getFormulas().remove(key);
					appXML.getFormulas().put(key, newFormula);
				}
			}			
						
			//check test case values.
			Enumeration<String> testCaseEnumeration = appXML.getTestCases().keys();
			while(testCaseEnumeration.hasMoreElements())
			{
				String key = testCaseEnumeration.nextElement();
				TestCase testCase = appXML.getTestCases().get(key);
								
				Enumeration<String> tcEnumeration = testCase.getValues().keys();
				while(tcEnumeration.hasMoreElements())
				{
					String tcKey = tcEnumeration.nextElement();
					String value = testCase.getValues().get(tcKey);					
					if (value != null){
						String newValue = replaceDecimalPoints(value, config.defaultOutputLocale, Locale.ENGLISH);
						testCase.getValues().remove(tcKey);
						testCase.getValues().put(tcKey, newValue);						
					}
				}
				
				tcEnumeration = testCase.getFaultyValues().keys();
				while(tcEnumeration.hasMoreElements()){
					String tcKey = tcEnumeration.nextElement();
					String value = testCase.getFaultyValues().get(tcKey);
					String newValue = replaceDecimalPoints(value, config.defaultOutputLocale, Locale.ENGLISH);
					testCase.getFaultyValues().remove(tcKey);
					testCase.getFaultyValues().put(tcKey, newValue);						
				}
				
				tcEnumeration = testCase.getCorrectValues().keys();
				while(tcEnumeration.hasMoreElements())
				{
					String tcKey = tcEnumeration.nextElement();
					String value = testCase.getCorrectValues().get(tcKey);
					String newValue = replaceDecimalPoints(value, config.defaultOutputLocale, Locale.ENGLISH);
					testCase.getCorrectValues().remove(tcKey);
					testCase.getCorrectValues().put(tcKey, newValue);						
				}				
			}			
		}
		return appXML;
	}	
	
	/**
	 * Checks a formula for decimal points and replaces with the decimal point symbol specified by the locale.
	 * @param targetFormula - the formula to change.
	 * @return modified formula
	 */
	public static String replaceDecimalPoints(String targetString, Locale currentLocale, Locale targetLocale){
		String result = new String(targetString);
		CultureInfo currentCulture = Culture.getCultureForLocale(currentLocale);			
		String point = currentCulture.DECIMAL_POINT();
		
		CultureInfo targetCulture = Culture.getCultureForLocale(targetLocale);
		String targetPoint = targetCulture.DECIMAL_POINT();
		
		result = result.replaceAll(point, targetPoint);			
		return result;
	}
	
	/**
	 * Replaces function parameter delimiter (e.g. for Excel function parameters)
	 * @param targetFormula
	 * @param currentLocale
	 * @param targetLocale
	 * @return
	 */
	public static String replaceDelimiter(String targetFormula, Locale currentLocale, Locale targetLocale){
		String result = new String(targetFormula);
		CultureInfo currentCulture = Culture.getCultureForLocale(currentLocale);			
		String oldSymbol = currentCulture.PARAM_DELIMITER();
		
		CultureInfo targetCulture = Culture.getCultureForLocale(targetLocale);
		String newSymbol = targetCulture.PARAM_DELIMITER();
		
		result = result.replaceAll(oldSymbol, newSymbol);
		return result;
	}	
}
