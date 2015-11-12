package org.exquisite.data;

import java.util.Dictionary;
import java.util.Hashtable;

import choco.Choco;
import choco.kernel.model.variables.integer.IntegerVariable;

public class ConstantsFactory {
	
	private int constantsNameIndex = 0;
	private Dictionary<String, IntegerVariable> constants = new Hashtable<String, IntegerVariable>();
	
	public final Dictionary<String, IntegerVariable> getConstants()
	{
		return this.constants;
	}
	
	/**
	 * Make a Choco IntegerConstant "variable".
	 * @param constantName
	 * @param value
	 * @return
	 */
	public IntegerVariable makeIntegerConstant(String constantNamePrefix, int value)
	{
		IntegerVariable constant = Choco.constant(value);
		String constantName = constantNamePrefix + constantsNameIndex;
		constantsNameIndex++;		
		constants.put(constantName, constant);		
		return constant;
	}
}
