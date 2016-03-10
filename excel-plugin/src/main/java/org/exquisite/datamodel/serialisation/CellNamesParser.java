package org.exquisite.datamodel.serialisation;

import org.exquisite.xml.XMLParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class CellNamesParser {

    private ExquisiteCellNames cellNames = null;
    private String xml;

    public ExquisiteCellNames getCellNames() {
        return cellNames;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public void parse(String xml) {
        this.xml = xml;
        parse();
    }

    public void parse() {
        Document doc = null;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        List<String> list = new ArrayList<String>();


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
                    if (allNodes.item(i).getNodeType() == Element.ELEMENT_NODE) {
                        Element child = (Element) allNodes.item(i);

                        String childName = child.getNodeName();
                        //System.out.println("Child element :" + childName);

                        switch (childName) {
                            case "CellNames":
                                list = XMLParser.getStringList(child.getChildNodes());
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cellNames = new ExquisiteCellNames();
        cellNames.cellNames = list;
    }

}
