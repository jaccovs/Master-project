package tests.dj.othertests;

import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import org.exquisite.data.ConstraintsFactory;
import org.exquisite.data.DiagnosisModelLoader;
import org.exquisite.data.VariablesFactory;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;
import org.exquisite.diagnosis.ranking.ConstraintComplexityEstimator;
import org.exquisite.diagnosis.ranking.DiagnosisRanker;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * A class to assess the complexity of a constraint
 * @author dietmar
 *
 */
public class ConstraintComplexityTest {

	String xmlFilePath = ".\\experiments\\spreadsheetsindividual\\11_or_12_diagnoses_tc0.xml";

	/**
	 * Main worker
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting complexity tests");
		try {
			ConstraintComplexityTest c = new ConstraintComplexityTest();
//			c.runBasicTest();
			c.runRankingTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Program ended");
		
	}
	
	
	// Do a test here 
   void runRankingTest() throws Exception {
		ExquisiteAppXML appXML = ExquisiteAppXML.parseToAppXML(xmlFilePath);
	   ExquisiteSession<Constraint> sessionData = new ExquisiteSession<>(appXML);
		ConstraintsFactory conFactory = new ConstraintsFactory(sessionData);
		Dictionary<String, IntegerExpressionVariable> variablesMap = new Hashtable<String, IntegerExpressionVariable>();
		VariablesFactory varFactory = new VariablesFactory(variablesMap);
		DiagnosisModelLoader modelLoader = new DiagnosisModelLoader(sessionData, varFactory, conFactory);
	   modelLoader.loadDiagnosisModelFromXML();

	   IDiagnosisEngine<Constraint> diagnosisEngine;
		sessionData.config.searchStrategy = SearchStrategies.Default;
		
		diagnosisEngine = EngineFactory.makeEngine(EngineType.HSDagStandardQX, sessionData,1);
	   List<Diagnosis<Constraint>> diags = diagnosisEngine.calculateDiagnoses();
	   List<Diagnosis<Constraint>> reranked = DiagnosisRanker.rankDiagnoses(diags, sessionData);
		System.out.println("Reranked: " + reranked);
		

   }


	/** 
	 * Do the work
	 * @throws Exception
	 */
	public void runBasicTest() throws Exception {
		
		System.out.println(" -- Running CGT -- ");
		String formula = "IF(A2=3;A1+B2;C3)"; 
//		formula = "A1+B2";
		
		ConstraintComplexityEstimator estimator = new ConstraintComplexityEstimator(formula);
		double estimate = estimator.estimateComplexity();
		System.out.println("Estimating complexity " + estimate + " for formula " + formula);
		System.out.println(estimator);
		
		
	}
	

}
