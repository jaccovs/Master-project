package evaluations.dxc.synthetic.model;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * @author Thomas
 *
 */
public class DXCSystem {
	private String systemName;
	private String description;
	private Dictionary<String, DXCComponent> components;
	private List<DXCConnection> connections;
	
	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Dictionary<String, DXCComponent> getComponents() {
		return components;
	}

	public void setComponents(Dictionary<String, DXCComponent> components) {
		this.components = components;
	}

	public List<DXCConnection> getConnections() {
		return connections;
	}

	public void setConnections(List<DXCConnection> connections) {
		this.connections = connections;
	}
	
	public DXCSystem() {
		components = new Hashtable<String, DXCComponent>();
		connections = new ArrayList<DXCConnection>();
	}
}