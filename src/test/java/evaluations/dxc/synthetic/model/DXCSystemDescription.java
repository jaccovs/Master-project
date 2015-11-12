package evaluations.dxc.synthetic.model;

import java.util.Dictionary;
import java.util.List;

/**
 * @author Thomas
 *
 */
public class DXCSystemDescription {
	private List<DXCSystem> systems;
	private Dictionary<String, DXCComponentType> componentTypeCatalog;
	
	public List<DXCSystem> getSystems() {
		return systems;
	}
	
	public void setSystems(List<DXCSystem> systems) {
		this.systems = systems;
	}
	
	public Dictionary<String, DXCComponentType> getComponentTypeCatalog() {
		return componentTypeCatalog;
	}
	
	public void setComponentTypeCatalog(
			Dictionary<String, DXCComponentType> componentTypeCatalog) {
		this.componentTypeCatalog = componentTypeCatalog;
	}
}
