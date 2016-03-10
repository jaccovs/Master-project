package tests.exquisite.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.exquisite.data.VariablesFactory;

import choco.kernel.model.variables.integer.IntegerExpressionVariable;

public class VariablesFactoryTest {
	
	public static void main(String[]args)
	{
		new VariablesFactoryTest().run();
	}

	
	public void run()
	{
		VariablesFactory factory = new VariablesFactory(new Hashtable<String, IntegerExpressionVariable>());
		
		int min = 0;
		int max = 100;
		List<String> variableNames = new ArrayList<String>();
		variableNames.add("A1:A3");
		variableNames.add("B1");
		variableNames.add("C4:C7");
		variableNames.add("D1:E5");
		
		factory.makeVariables(variableNames, min, max);
		
		Enumeration<String> keys = factory.getVariablesMap().keys();
		
		while(keys.hasMoreElements())
		{
			String key = keys.nextElement();
			IntegerExpressionVariable var = factory.getVariablesMap().get(key);
			System.out.println("var = " + var);
		}		
	}
}
