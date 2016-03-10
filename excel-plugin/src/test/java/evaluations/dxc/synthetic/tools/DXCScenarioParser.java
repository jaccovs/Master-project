package evaluations.dxc.synthetic.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Dictionary;

import evaluations.dxc.synthetic.model.DXCComponent;
import evaluations.dxc.synthetic.model.DXCScenarioData;
import evaluations.dxc.synthetic.model.DXCSystem;
import evaluations.dxc.synthetic.model.DXCSystemDescription;

/**
 * Parser to read a scenario scn file from the synthetic track of the DXC competition.
 * @author Thomas
 *
 */
public class DXCScenarioParser {
	private DXCScenarioData scenario;
	private String file;
	private boolean correctStateFound = false;
	
	public DXCScenarioData getScenario() {
		return scenario;
	}

	public void setScenario(DXCScenarioData scenario) {
		this.scenario = scenario;
	}
	
	public DXCScenarioParser() {
		file = "";
		scenario = new DXCScenarioData();
	}

	public DXCScenarioParser(String file) {
		this();
		this.file = file;
	}

	public DXCScenarioParser(DXCScenarioData scenario, String file) {
		super();
		this.scenario = scenario;
		this.file = file;
	}
	
	public void parse(String file, DXCSystem system) {
		this.file = file;
		parse(system);
	}
	
	/**
	 * Parses the previously given file.
	 * @param system Is needed for assigning the components
	 */
	public void parse(DXCSystem system) {
		Dictionary<String, DXCComponent> components = system.getComponents();
		
		String[] lines = file.split("\n");
		
		for (int i = 0; i < lines.length; i++) {
//		for (int i = lines.length - 1; i >= 0; i--) {
			String line = lines[i];
			
			int left = line.indexOf('{');
			if (left != -1) {
				int right = line.indexOf('}');
				String content = line.substring(left + 1, right);
//				System.out.println(content);
				String[] values = content.split(",");
			
				// line with sensors describes correct state or faulty state
				if (line.trim().startsWith("sensors")) {
					
					Dictionary<DXCComponent, Boolean> currentState;
					if (!correctStateFound) {
						currentState = scenario.getCorrectState();
						correctStateFound = true;
					} else {
						currentState = scenario.getFaultyState();
					}
			
				
					for (int k = 0; k < values.length; k++) {
						String[] assignment = values[k].split("=");
						String compName = assignment[0].trim();
						String value = assignment[1].trim();
	//					System.out.println(compName + "=" + value);
						
						DXCComponent comp = components.get(compName);
						if (comp != null) {
							currentState.put(comp, Boolean.parseBoolean(value));
						}
					}
				
				} else if (line.trim().startsWith("faultInjection")) {
					// line with faultInjection describes a faulty component
					for (int k = 0; k < values.length; k++) {
						String[] assignment = values[k].split("=");
						String compName = assignment[0].trim();
						String value = assignment[1].trim();
						
						if (value.equals("faulty")) {
							DXCComponent comp = components.get(compName);
							if (comp != null) {
								scenario.getFaultyComponents().add(comp);
							}

						}
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		// Parse a system description first
		String xmlFilePath = "experiments/DXCSynthetic/74182.xml";
		
		DXCSyntheticXMLParser parser = new DXCSyntheticXMLParser();
		
		try{
			System.out.println("Trying to load xml file: " + xmlFilePath);
			
			BufferedReader br = new BufferedReader(new FileReader(new File(xmlFilePath)));
			String line;
			StringBuilder sb = new StringBuilder();
	
			while((line=br.readLine())!= null){
			    sb.append(line.trim());
			}
			br.close();
			parser.parse(sb.toString());
			
			DXCSystemDescription sd = parser.getSystemDescription();
			
			System.out.println("System name: " + sd.getSystems().get(0).getSystemName());

			System.out.println("FINISH");
		
			// Parse the scenario
			String scnFilePath = "experiments/DXCSynthetic/74182/74182.000.scn";
			
			
			
			DXCScenarioParser scnParser = new DXCScenarioParser();
		
			System.out.println("Trying to load scn file: " + scnFilePath);
			
			br = new BufferedReader(new FileReader(new File(scnFilePath)));
			sb = new StringBuilder();
	
			while((line=br.readLine())!= null){
			    sb.append(line.trim() + "\n");
			}
			br.close();
			scnParser.parse(sb.toString(), sd.getSystems().get(0));
			
			Dictionary<DXCComponent, Boolean> scenario = scnParser.getScenario().getFaultyState();
			
			System.out.println("Scenario size: " + scenario.size());

			System.out.println("FINISH");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
