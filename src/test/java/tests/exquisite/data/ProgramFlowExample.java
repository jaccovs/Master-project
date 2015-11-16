package tests.exquisite.data;

import choco.kernel.model.constraints.Constraint;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProgramFlowExample {
	
	public static void main(String[] args) 
	{
		new ProgramFlowExample().run();
	}
	
	public void run()
	{
		Debug.DEBUGGING_ON = false;
		Debug.msg("Program flow example... START\n");
		
		Debug.msg("Load ExquisiteAppXML from client.");
		String xmlFilePath = "src\\tests\\exquisite\\data\\exquisite_10_sf_if_b.xml";

		IDiagnosisEngine<Constraint> diagnosisEngine = null;
//		diagnosisEngine = EngineFactory.makeParaDagEngineStandardQx(sessionData, 6);
		try {
			diagnosisEngine = EngineFactory.makeEngineFromXMLFile(EngineType.HSDagStandardQX, xmlFilePath, -1);
		} catch (Exception e1) {
			e1.printStackTrace();
		}	
		
		Debug.msg("    diagnosisEngine.calculateDiagnoses()\n");
		try{
			Debug.DEBUGGING_ON = false;
			long startTime = System.currentTimeMillis();
			List<Diagnosis<Constraint>> result = diagnosisEngine.calculateDiagnoses();
			long endTime = System.currentTimeMillis();		
			
			long duration = endTime - startTime;
			
			System.out.println("Diagnosis result (alphabetical order):\n Count = " + result.size());
			int cnt = 1;
			
			//Sort diagnosis elements into alphabetical order.
			ArrayList<String> prettyResults = new ArrayList<String>();
			for (Diagnosis<Constraint> diagnosis : result) {
				String list = Utilities.printConstraintListOrderedByName(
						new ArrayList<>(
									diagnosis.getElements()), diagnosisEngine.getSessionData().diagnosisModel);
				prettyResults.add(list);				
			}
			Collections.sort(prettyResults);
			for(String diagnosis : prettyResults){
				System.out.println("-- Diagnosis: " + cnt);
				cnt++;
				System.out.println(diagnosis);				
				System.out.println("---------------");
			}
			System.out.println("DURATION = " + duration);
			
			
		} catch (DiagnosisException e){
			e.printStackTrace();
		}
		Debug.msg("\nEND of Program flow example.");
	}	
}
