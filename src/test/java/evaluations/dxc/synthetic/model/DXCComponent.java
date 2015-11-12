package evaluations.dxc.synthetic.model;

/**
 * @author Thomas
 *
 */
public class DXCComponent {
	private String name;
	private DXCComponentType componentType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DXCComponentType getComponentType() {
		return componentType;
	}

	public void setComponentType(DXCComponentType componentType) {
		this.componentType = componentType;
	}
}
