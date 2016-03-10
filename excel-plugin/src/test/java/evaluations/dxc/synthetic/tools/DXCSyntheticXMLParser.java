package evaluations.dxc.synthetic.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import evaluations.dxc.synthetic.model.DXCComponent;
import evaluations.dxc.synthetic.model.DXCComponentType;
import evaluations.dxc.synthetic.model.DXCConnection;
import evaluations.dxc.synthetic.model.DXCSystem;
import evaluations.dxc.synthetic.model.DXCSystemDescription;


/**
 * Parser to read a system description xml file from the synthetic track of the DXC competition.
 * @author Thomas
 *
 */
public class DXCSyntheticXMLParser {
	
	private DXCSystemDescription systemDescription = new DXCSystemDescription();
	private String xml;

	public DXCSystemDescription getSystemDescription() {
		return systemDescription;
	}

	public void setSystemDescription(DXCSystemDescription systemDescription) {
		this.systemDescription = systemDescription;
	}
	
	public DXCSyntheticXMLParser() {
		xml = "";
		systemDescription = new DXCSystemDescription();
	}

	public DXCSyntheticXMLParser(String xml) {
		this();
		this.xml = xml;
	}

	public DXCSyntheticXMLParser(DXCSystemDescription systemDescription, String xml) {
		super();
		this.systemDescription = systemDescription;
		this.xml = xml;
	}
	
	public void parse(String xml) {
		this.xml = xml;
		parse();
	}
	
	public void parse() {
		Document doc = null;

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		
		List<DXCSystem> systems = new ArrayList<DXCSystem>();
		Dictionary<String, DXCComponentType> componentTypeCatalog = new Hashtable<String, DXCComponentType>();
		
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
			doc.getDocumentElement().normalize();

			// get the root elememt
			Element root = doc.getDocumentElement();

			//System.out.println("Root element :" + root.getNodeName());

			NodeList allNodes = root.getChildNodes();
			if (allNodes != null && allNodes.getLength() > 0) {
				for (int i = 0; i < allNodes.getLength(); i++) {
					if (allNodes.item(i).getNodeType() == Element.ELEMENT_NODE)
					{
						Element child = (Element) allNodes.item(i);

						String childName = child.getNodeName();
						//System.out.println("Child element :" + childName);

						switch (childName) {
						case "systems":
							systems = getSystems(child.getChildNodes(), componentTypeCatalog);
							break;
						case "componentTypeCatalog":
							addComponentsToTypeCatalog(child.getChildNodes(), componentTypeCatalog);
							break;
						}
					}
				}
			}
			
			systemDescription.setSystems(systems);
			systemDescription.setComponentTypeCatalog(componentTypeCatalog);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<DXCSystem> getSystems(NodeList childNodes, Dictionary<String, DXCComponentType> componentTypeCatalog) {
		List<DXCSystem> systems = new ArrayList<DXCSystem>();
		if (childNodes != null && childNodes.getLength() > 0) {
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i).getNodeType() == Element.ELEMENT_NODE)
				{
					Element child = (Element) childNodes.item(i);
					
					String childName = child.getNodeName();
					//System.out.println("Child element :" + childName);

					switch (childName) {
					case "system":
						systems.add(getSystem(child.getChildNodes(), componentTypeCatalog));
						break;
					}
				}
			}
		}
		return systems;
	}

	private DXCSystem getSystem(NodeList childNodes, Dictionary<String, DXCComponentType> componentTypeCatalog) {
		DXCSystem system = new DXCSystem();
		if (childNodes != null && childNodes.getLength() > 0) {
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i).getNodeType() == Element.ELEMENT_NODE)
				{
					Element child = (Element) childNodes.item(i);
					
					String childName = child.getNodeName();
					//System.out.println("Child element :" + childName);

					switch (childName) {
					case "systemName":
						system.setSystemName(child.getTextContent());
						break;
					case "description":
						system.setDescription(child.getTextContent());
						break;
					case "components":
						system.setComponents(getComponents(child.getChildNodes(), componentTypeCatalog));
						break;
					case "connections":
						system.setConnections(getConnections(child.getChildNodes(), system.getComponents()));
						break;
					}
				}
			}
		}
		return system;
	}

	private Dictionary<String, DXCComponent> getComponents(NodeList childNodes, Dictionary<String, DXCComponentType> componentTypeCatalog) {
		Dictionary<String, DXCComponent> components = new Hashtable<String, DXCComponent>();
		if (childNodes != null && childNodes.getLength() > 0) {
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i).getNodeType() == Element.ELEMENT_NODE)
				{
					Element child = (Element) childNodes.item(i);
					
					String childName = child.getNodeName();
					//System.out.println("Child element :" + childName);

					switch (childName) {
					case "component":
						DXCComponent component = getComponent(child.getChildNodes(), componentTypeCatalog);
						components.put(component.getName(), component);
						break;
					}
				}
			}
		}
		return components;
	}

	private DXCComponent getComponent(NodeList childNodes, Dictionary<String, DXCComponentType> componentTypeCatalog) {
		DXCComponent component = new DXCComponent();
		if (childNodes != null && childNodes.getLength() > 0) {
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i).getNodeType() == Element.ELEMENT_NODE)
				{
					Element child = (Element) childNodes.item(i);
					
					String childName = child.getNodeName();
					//System.out.println("Child element :" + childName);

					switch (childName) {
					case "name":
						component.setName(child.getTextContent());
						break;
					case "componentType":
						String typeName = child.getTextContent();
						DXCComponentType type = componentTypeCatalog.get(typeName);
						if (type != null) {
							component.setComponentType(type);
						} else {
							type = new DXCComponentType();
							type.setName(typeName);
							component.setComponentType(type);
							componentTypeCatalog.put(typeName, type);
						}
						break;
					}
				}
			}
		}
		return component;
	}

	private List<DXCConnection> getConnections(NodeList childNodes, Dictionary<String, DXCComponent> components) {
		List<DXCConnection> connections = new ArrayList<DXCConnection>();
		if (childNodes != null && childNodes.getLength() > 0) {
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i).getNodeType() == Element.ELEMENT_NODE)
				{
					Element child = (Element) childNodes.item(i);
					
					String childName = child.getNodeName();
					//System.out.println("Child element :" + childName);

					switch (childName) {
					case "connection":
						connections.add(getConnection(child.getChildNodes(), components));
						break;
					}
				}
			}
		}
		return connections;
	}

	private DXCConnection getConnection(NodeList childNodes, Dictionary<String, DXCComponent> components) {
		DXCConnection connection = new DXCConnection();
		if (childNodes != null && childNodes.getLength() > 0) {
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i).getNodeType() == Element.ELEMENT_NODE)
				{
					Element child = (Element) childNodes.item(i);
					
					String childName = child.getNodeName();
					//System.out.println("Child element :" + childName);

					switch (childName) {
					case "c1":
						connection.setC1(components.get(child.getTextContent()));
						break;
					case "c2":
						connection.setC2(components.get(child.getTextContent()));
						break;
					}
				}
			}
		}
		return connection;
	}

	private void addComponentsToTypeCatalog(NodeList childNodes, Dictionary<String, DXCComponentType> componentTypeCatalog) {
		if (childNodes != null && childNodes.getLength() > 0) {
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i).getNodeType() == Element.ELEMENT_NODE)
				{
					Element child = (Element) childNodes.item(i);
					
					String childName = child.getNodeName();
					//System.out.println("Child element :" + childName);

					switch (childName) {
					case "componentType":
						addComponentToTypeCatalog(childNodes, componentTypeCatalog);
						break;
					}
				}
			}
		}
	}

	private void addComponentToTypeCatalog(NodeList childNodes, Dictionary<String, DXCComponentType> componentTypeCatalog) {
		String name = "";
		String description = "";
		String modesRef = "";
		if (childNodes != null && childNodes.getLength() > 0) {
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i).getNodeType() == Element.ELEMENT_NODE)
				{
					Element child = (Element) childNodes.item(i);
					
					String childName = child.getNodeName();
					//System.out.println("Child element :" + childName);

					switch (childName) {
					case "name":
						name = child.getTextContent();
						break;
					case "description":
						description = child.getTextContent();
						break;
					case "modesRef":
						modesRef = child.getTextContent();
						break;
					}
				}
			}
		}
		
		DXCComponentType componentType = componentTypeCatalog.get(name);
		
		if (componentType == null) {
			componentType = new DXCComponentType();
			componentType.setName(name);
			componentTypeCatalog.put(name, componentType);
		}
		componentType.setDescription(description);
		componentType.setModesRef(modesRef);
	}
	
	
	public static void main(String[] args) {
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
