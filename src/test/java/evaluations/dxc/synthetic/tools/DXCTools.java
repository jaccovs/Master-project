package evaluations.dxc.synthetic.tools;

import choco.kernel.model.constraints.Constraint;
import evaluations.dxc.synthetic.model.DXCComponent;
import evaluations.dxc.synthetic.model.DXCScenarioData;
import evaluations.dxc.synthetic.model.DXCSystem;
import evaluations.dxc.synthetic.model.DXCSystemDescription;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class DXCTools {
	public static DXCSystemDescription readSystemDescription(String xmlFilePath) {		
		DXCSyntheticXMLParser parser = new DXCSyntheticXMLParser();
		DXCSystemDescription sd = null;
		try{
//			System.out.println("Trying to load xml file: " + xmlFilePath);
			
			BufferedReader br = new BufferedReader(new FileReader(new File(xmlFilePath)));
			String line;
			StringBuilder sb = new StringBuilder();
	
			while((line=br.readLine())!= null){
			    sb.append(line.trim());
			}
			br.close();
			parser.parse(sb.toString());
			
			sd = parser.getSystemDescription();
			
//			System.out.println("System name: " + sd.getSystems().get(0).getSystemName());
//
//			System.out.println("FINISH");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sd;
	}
	
	public static DXCScenarioData readScenario(String scnFilePath, DXCSystem system) {
		// Parse the scenario
		DXCScenarioData scenario = null;
		try {
			DXCScenarioParser scnParser = new DXCScenarioParser();
		
//			System.out.println("Trying to load scn file: " + scnFilePath);
			
			BufferedReader br = new BufferedReader(new FileReader(new File(scnFilePath)));
			String line;
			StringBuilder sb = new StringBuilder();
	
			while((line=br.readLine())!= null){
			    sb.append(line.trim() + "\n");
			}
			br.close();
			scnParser.parse(sb.toString(), system);
			
			scenario = scnParser.getScenario();
			
//			System.out.println("Scenario size: " + scenario.size());

//			System.out.println("FINISH");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return scenario;
	}
	
	public static boolean checkCorrectState(DXCSystem system, DXCScenarioData scenario) {
		try
		{
			DXCDiagnosisModelGenerator dmg = new DXCDiagnosisModelGenerator();
			DiagnosisModel<Constraint> diagModel = dmg.createDiagnosisModel(system, scenario.getCorrectState());
			
			// Create the engine
			ExcelExquisiteSession<Constraint> sessionData = new ExcelExquisiteSession<>(null,
					null, new DiagnosisModel<>(diagModel));
			// Do not try to find a better strategy for the moment
			sessionData.getConfiguration().searchStrategy = SearchStrategies.Default;
			sessionData.getConfiguration().searchDepth = -1;

			IDiagnosisEngine<Constraint> engine = EngineFactory
					.makeDAGEngineStandardQx(sessionData);
			
//			long start = System.currentTimeMillis();
			List<Diagnosis<Constraint>> diagnoses = engine.calculateDiagnoses();
//			long end = System.currentTimeMillis();
//			long duration = end - start;
			return diagnoses.size() == 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Diagnosis<Constraint> checkFaultyComponentsinDiagnoses(DiagnosisModel<Constraint> model,
																		 List<Diagnosis<Constraint>> diagnoses,
																		 DXCSystem system, DXCScenarioData scenario) {
		for (int i = 0; i < diagnoses.size(); i++) {
			Diagnosis<Constraint> diag = diagnoses.get(i);
			boolean allIn = true;
			for (int k = 0; k < diag.getElements().size(); k++) {
				String constraintName = model.getConstraintName(diag.getElements().get(k));
				DXCComponent component = system.getComponents().get(constraintName);
				if (!scenario.getFaultyComponents().contains(component)) {
					allIn = false;
					break;
				}
			}
			if (allIn) {
				return diag;
			}
		}
		return null;
	}

}
