package evaluations.dxc.synthetic.model;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class DXCScenarioData {
	private Dictionary<DXCComponent, Boolean> correctState;
	private Dictionary<DXCComponent, Boolean> faultyState;
	private List<DXCComponent> faultyComponents;
	
	public Dictionary<DXCComponent, Boolean> getCorrectState() {
		return correctState;
	}

	public void setCorrectState(Dictionary<DXCComponent, Boolean> correctState) {
		this.correctState = correctState;
	}

	public Dictionary<DXCComponent, Boolean> getFaultyState() {
		return faultyState;
	}

	public void setFaultyState(Dictionary<DXCComponent, Boolean> faultyState) {
		this.faultyState = faultyState;
	}

	public List<DXCComponent> getFaultyComponents() {
		return faultyComponents;
	}

	public void setFaultyComponents(List<DXCComponent> faultyComponents) {
		this.faultyComponents = faultyComponents;
	}

	public DXCScenarioData() {
		correctState = new Hashtable<DXCComponent, Boolean>();
		faultyState = new Hashtable<DXCComponent, Boolean>();
		faultyComponents = new ArrayList<DXCComponent>();
	}
}
