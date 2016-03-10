/**
 * 
 */
package tests.diagnosis;

import org.antlr.runtime.tree.CommonTree;
import org.exquisite.datamodel.ExquisiteGraph;
import org.exquisite.parser.FormulaParser;

/**
 * @author Arash
 *
 */
public class UtilityTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		UtilityTest utilityTest = new UtilityTest();

		
		FormulaParser formulaParser = new FormulaParser(new ExquisiteGraph<String>());
		//CommonTree tree = formulaParser.parseToCommonTree("SUMME(A3:A10, ((B1 + B2) / 3), C1)");
		//CommonTree tree = formulaParser.parseToCommonTree("SUMME(A1:A3, B1:B3, ((C1*C2)/2), D1)");
		CommonTree tree = formulaParser.parseToCommonTree("WENN(A1<>A2;20;50)");
		//CommonTree tree = formulaParser.parseToCommonTree("((B1 + B2) / 3) * A6");
		//StringUtilities.printTree(tree, 0);
		
		//System.out.println(StringUtilities.rangeToCells("A1:ZZ10").toString());
		//System.out.println(s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)"));
		System.out.println(ParserTest.BuildExpression(tree));

		//System.out.println(utilityTest.getNext("AZ", 1)); 
	}
	
	private String getNext(String start, int lastPos){
		if(lastPos == -1)	return "A" + start;
		
		char last = start.charAt(lastPos);
		String prefix = start.substring(0, lastPos);
		String postfix = start.substring(lastPos + 1, start.length());
		if(last == 'Z'){
			return getNext(prefix + "A" + postfix, lastPos - 1);			
		} else {
			return prefix + (++last) + postfix;
		}
	}	
}
