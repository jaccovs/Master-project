package org.exquisite.tools;

/**
 * Any utilities specific to Integers.
 * @author David
 *
 */
public class IntegerUtilities {
	
	/**
	 * Parses a string to int. Truncates any fractional values.
	 */
	public static int parseToInt(String targetToParse)
	{
		String decimalPoint = "\\.";
		targetToParse.trim();
		String[] splits = targetToParse.split(decimalPoint);
		int result = Integer.parseInt(splits[0]);		
		return result;
	}

}
