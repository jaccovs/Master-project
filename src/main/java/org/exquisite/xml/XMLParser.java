package org.exquisite.xml;

import org.exquisite.data.ExampleTestData;
import org.exquisite.datamodel.*;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.datamodel.ExquisiteEnums.ExquisiteFlag;
import org.exquisite.datamodel.ExquisiteEnums.ExquisiteLocaleFlag;
import org.exquisite.datamodel.ExquisiteEnums.ExquisiteTestcaseFlag;
import org.exquisite.tools.IntegerUtilities;
import org.exquisite.tools.Utilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * @author Arash
 */
public class XMLParser {

    private ExquisiteAppXML exquisiteAppXML;
    private String XML;

    public XMLParser() {
        XML = "";
        exquisiteAppXML = new ExquisiteAppXML();
    }

    /**
     * @param xML
     */
    public XMLParser(String xML) {
        this();
        XML = xML;
    }

    /**
     * @param exquisiteAppXML
     * @param xML
     */
    public XMLParser(ExquisiteAppXML exquisiteAppXML, String xML) {
        super();
        this.exquisiteAppXML = exquisiteAppXML;
        XML = xML;
    }

    public static List<String> getStringList(NodeList allNode) {
        List<String> list = new ArrayList<String>();
        if (allNode != null && allNode.getLength() > 0) {
            for (int j = 0; j < allNode.getLength(); j++) {
                if (allNode.item(j).getNodeType() == Element.ELEMENT_NODE) {
                    Element valueElement = (Element) allNode.item(j);
                    String value = valueElement.getTextContent();
                    list.add(value);
                    //System.out.println("LIST: " + value);
                }
            }
        }
        return list;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        XMLParser xmlParser = new XMLParser(ExampleTestData.SMALL_TEST);
        xmlParser.parse();
        System.out.println("FINISH");

    }

    public void parse() {
        Document doc = null;

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        Dictionary<String, String> Formulas = new Hashtable<String, String>();
        Dictionary<String, String> FormulasR1C1 = new Hashtable<String, String>();
        String PathOriginal = "";
        String PathCopy = "";
        ExquisiteFlag Flag = ExquisiteFlag.Original;
        ExquisiteValueBound DefaultValueBound = new ExquisiteValueBound();
        Dictionary<String, TestCase> TestCases = new Hashtable<String, TestCase>();
        Dictionary<String, Fragment> Fragments = new Hashtable<String, Fragment>();
        List<String> Inputs = new ArrayList<String>();
        List<String> Outputs = new ArrayList<String>();
        List<String> Interims = new ArrayList<String>();
        Dictionary<String, ExquisiteValueBound> ValueBounds = new Hashtable<String, ExquisiteValueBound>();
        Dictionary<String, List<String>> CellsInRange = new Hashtable<String, List<String>>();
        Dictionary<String, String> FaultyValues = new Hashtable<String, String>();
        Dictionary<String, String> Assertions = new Hashtable<String, String>();
        Dictionary<String, String> Types = new Hashtable<String, String>();
        Dictionary<String, String> CorrectFormulas = new Hashtable<String, String>();
        ExquisiteUserSettings UserSettings = new ExquisiteUserSettings();

        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(XML
                    .getBytes("utf-8"))));
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
                            case "Formulas":
                                Formulas = getFormulas(child.getChildNodes());
                                break;

                            case "FormulasR1C1":
                                FormulasR1C1 = getFormulas(child.getChildNodes());
                                break;

                            case "PathOriginal":
                                PathOriginal = child.getTextContent();
                                break;

                            case "PathCopy":
                                PathCopy = child.getTextContent();
                                break;

                            case "Flag":
                                Flag = (child.getTextContent() == "Original") ? ExquisiteFlag.Original
                                        : ExquisiteFlag.Copy;
                                break;

                            case "DefaultValueBound":
                                DefaultValueBound = getValueBound(child.getChildNodes());
                                break;

                            case "TestCases":
                                TestCases = getTestCases(child.getChildNodes());
                                break;

                            case "Fragments":
                                Fragments = getFragments(child.getChildNodes());

                            case "Inputs":
                                Inputs = getStringList(child.getChildNodes());
                                break;

                            case "Outputs":
                                Outputs = getStringList(child.getChildNodes());
                                break;

                            case "Interims":
                                Interims = getStringList(child.getChildNodes());
                                break;

                            case "ValueBounds":
                                ValueBounds = getValueBounds(child.getChildNodes());
                                break;

                            case "CellsInRange":
                                CellsInRange = getCellsInRange(child.getChildNodes());
                                break;

                            case "FaultyValues":
                                FaultyValues = getStringDictionary(child.getChildNodes());
                                break;

                            case "Assertions":
                                Assertions = getStringDictionary(child.getChildNodes());
                                break;

                            case "Types":
                                Types = getStringDictionary(child.getChildNodes());
                                break;

                            case "CorrectFormulas":
                                CorrectFormulas = getStringDictionary(child.getChildNodes());
                                break;

                            case "UserSettings":
                                UserSettings = getUserSettings(child.getChildNodes());
                                break;

                            default:
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        exquisiteAppXML = new ExquisiteAppXML(Formulas, FormulasR1C1, PathOriginal, PathCopy,
                Flag, DefaultValueBound, TestCases, Fragments, Inputs, Outputs, Interims,
                ValueBounds, CellsInRange, FaultyValues, Assertions, Types,
                CorrectFormulas, UserSettings);
    }

    public void parse(String xml) {
        XML = xml;
        parse();
    }

    private Dictionary<String, String> getFormulas(NodeList allFormulas) {
        Dictionary<String, String> formulas = new Hashtable<String, String>();
        if (allFormulas != null && allFormulas.getLength() > 0) {
            for (int j = 0; j < allFormulas.getLength(); j++) {
                if (allFormulas.item(j).getNodeType() == Element.ELEMENT_NODE) {
                    Element formulaReference = getFirstElement(allFormulas.item(j));
                    Element formulaContent = getLastElement(allFormulas.item(j));

                    String reference = formulaReference.getTextContent();
                    String content = Utilities.htmlspecialchars_decode_ENT_NOQUOTES(formulaContent.getTextContent());
                    //				System.out.println("Formula :" + reference + ": " + content);
                    formulas.put(reference, content);
                }
            }
        }
        return formulas;
    }

    private Element getLastElement(Node item) {
        NodeList nodes = item.getChildNodes();
        for (int i = nodes.getLength() - 1; i >= 0; i--)
            if (nodes.item(i).getNodeType() == Element.ELEMENT_NODE)
                return (Element) nodes.item(i);

        return null;
    }

    private Element getFirstElement(Node item) {
        NodeList nodes = item.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++)
            if (nodes.item(i).getNodeType() == Element.ELEMENT_NODE)
                return (Element) nodes.item(i);

        return null;
    }

    private ExquisiteValueBound getValueBound(NodeList valueBound) {
        ExquisiteValueBound defaultValueBound = new ExquisiteValueBound();
        if (valueBound != null && valueBound.getLength() > 0) {
            for (int j = 0; j < valueBound.getLength(); j++) {
                if (valueBound.item(j).getNodeType() == Element.ELEMENT_NODE) {
                    Element bound = (Element) valueBound.item(j);
                    String boundName = bound.getNodeName();
                    int pos = boundName.indexOf(":");
                    boundName = boundName.substring(pos + 1, boundName.length());
                    switch (boundName) {
                        case "Lower":
                            defaultValueBound.setLower(Double.parseDouble(bound
                                    .getTextContent()));
                            break;

                        case "Step":
                            defaultValueBound.setStep(Double.parseDouble(bound
                                    .getTextContent()));
                            break;

                        case "Upper":
                            defaultValueBound.setUpper(Double.parseDouble(bound
                                    .getTextContent()));
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        return defaultValueBound;
    }

    private Dictionary<String, TestCase> getTestCases(NodeList allTestCases) {
        Dictionary<String, TestCase> testCases = new Hashtable<String, TestCase>();
        if (allTestCases != null && allTestCases.getLength() > 0) {
            for (int j = 0; j < allTestCases.getLength(); j++) {
                if (allTestCases.item(j).getNodeType() == Element.ELEMENT_NODE) {
                    Element testCaseName = getFirstElement(allTestCases.item(j));
                    Element testCase = getLastElement(allTestCases.item(j));

                    String CaseID = "";
                    String ID = "";
                    String Description = "";
                    ExquisiteTestcaseFlag Flag = ExquisiteTestcaseFlag.Normal;
                    Dictionary<String, String> Values = new Hashtable<String, String>();
                    Dictionary<String, ExquisiteValueBound> ValueBounds = new Hashtable<String, ExquisiteValueBound>();
                    Dictionary<String, List<String>> CellsInRange = new Hashtable<String, List<String>>();
                    Dictionary<String, String> FaultyValues = new Hashtable<String, String>();
                    Dictionary<String, String> CorrectValues = new Hashtable<String, String>();
                    Dictionary<String, String> Assertions = new Hashtable<String, String>();
                    Dictionary<String, String> Types = new Hashtable<String, String>();

                    NodeList allTestCaseElements = testCase.getChildNodes();
                    if (allTestCaseElements != null
                            && allTestCaseElements.getLength() > 0) {
                        for (int k = 0; k < allTestCaseElements.getLength(); k++) {
                            if (allTestCaseElements.item(k).getNodeType() == Element.ELEMENT_NODE) {
                                Element testCaseElement = (Element) allTestCaseElements
                                        .item(k);
                                String TestCaseElementName = testCaseElement
                                        .getNodeName();
                                int pos = TestCaseElementName.indexOf(":");
                                TestCaseElementName = TestCaseElementName.substring(
                                        pos + 1, TestCaseElementName.length());

                                //System.out.println("TestCaseElementName: " + TestCaseElementName);

                                switch (TestCaseElementName) {
                                    case "Assertions":
                                        Assertions = getStringDictionary(testCaseElement
                                                .getChildNodes());
                                        break;

                                    case "CaseID":
                                        CaseID = testCaseElement.getTextContent();
                                        break;

                                    case "CellsInRange":
                                        CellsInRange = getCellsInRange(testCaseElement
                                                .getChildNodes());
                                        break;

                                    case "CorrectValues":
                                        CorrectValues = getStringDictionary(testCaseElement
                                                .getChildNodes());
                                        break;

                                    case "Description":
                                        Description = testCaseElement.getTextContent();
                                        break;

                                    case "FaultyValues":
                                        FaultyValues = getStringDictionary(testCaseElement
                                                .getChildNodes());
                                        break;

                                    case "Flag":
                                        if (testCaseElement.getTextContent().contentEquals(
                                                "Normal")) {
                                            Flag = ExquisiteTestcaseFlag.Normal;
                                        } else if (testCaseElement.getTextContent()
                                                .contentEquals("EverythingCorrect")) {
                                            Flag = ExquisiteTestcaseFlag.EverythingCorrect;
                                        } else {
                                            Flag = ExquisiteTestcaseFlag.EverythingWrong;
                                        }
                                        break;

                                    case "ID":
                                        ID = testCaseElement.getTextContent();
                                        break;

                                    case "Types":
                                        Types = getStringDictionary(testCaseElement
                                                .getChildNodes());
                                        break;

                                    case "ValueBounds":
                                        ValueBounds = getValueBounds(testCaseElement
                                                .getChildNodes());
                                        break;

                                    case "Values":
                                        Values = getStringDictionary(testCaseElement
                                                .getChildNodes());
                                        break;

                                    default:
                                        break;
                                }
                            }
                        }
                    }

                    TestCase tc = new TestCase(CaseID, ID, Description, Flag,
                            Values, ValueBounds, CellsInRange, FaultyValues,
                            CorrectValues, Assertions, Types);
                    testCases.put(testCaseName.getTextContent(), tc);
                }
            }
        }
        return testCases;
    }

    private Dictionary<String, Fragment> getFragments(NodeList allFragments) {

        Dictionary<String, Fragment> fragments = new Hashtable<String, Fragment>();
        if (allFragments != null && allFragments.getLength() > 0) {
            for (int j = 0; j < allFragments.getLength(); j++) {
                if (allFragments.item(j).getNodeType() == Element.ELEMENT_NODE) {
                    Element fragmentName = getFirstElement(allFragments.item(j));
                    Element fragment = getLastElement(allFragments.item(j));

                    String Name = "";
                    List<String> Inputs = new ArrayList<String>();
                    List<String> Outputs = new ArrayList<String>();
                    List<String> Interims = new ArrayList<String>();
                    Dictionary<String, TestCase> TestCases = new Hashtable<String, TestCase>();
                    String Representative = "";
                    List<String> LinkedFragments = new ArrayList<String>();
                    float Complexity = 0f;


                    NodeList allFragmentElements = fragment.getChildNodes();
                    if (allFragmentElements != null && allFragmentElements.getLength() > 0) {
                        for (int k = 0; k < allFragmentElements.getLength(); k++) {
                            if (allFragmentElements.item(k).getNodeType() == Element.ELEMENT_NODE) {
                                Element fragmentElement = (Element) allFragmentElements.item(k);
                                String FragmentElementName = fragmentElement.getNodeName();
                                int pos = FragmentElementName.indexOf(":");
                                FragmentElementName = FragmentElementName
                                        .substring(pos + 1, FragmentElementName.length());

                                //System.out.println("TestCaseElementName: " + TestCaseElementName);

                                switch (FragmentElementName) {
                                    case "Name":
                                        Name = fragmentElement.getTextContent();
                                        break;

                                    case "Inputs":
                                        Inputs = getStringList(fragmentElement.getChildNodes());
                                        break;

                                    case "Outputs":
                                        Outputs = getStringList(fragmentElement.getChildNodes());
                                        break;

                                    case "Interims":
                                        Interims = getStringList(fragmentElement.getChildNodes());
                                        break;

                                    case "TestCases":
                                        TestCases = getTestCases(fragmentElement.getChildNodes());
                                        break;

                                    case "Representative":
                                        Representative = fragmentElement.getTextContent();
                                        break;

                                    case "LinkedFragments":
                                        LinkedFragments = getStringList(fragmentElement.getChildNodes());
                                        break;

                                    case "Complexity":
                                        Complexity = Float.parseFloat(fragmentElement.getTextContent());

                                    default:
                                        break;
                                }
                            }
                        }
                    }

                    Fragment fg = new Fragment(Name, Inputs, Outputs, Interims,
                            TestCases, Representative, LinkedFragments, Complexity);
                    fragments.put(fragmentName.getTextContent(), fg);
                }
            }
        }
        return fragments;
    }

    private Dictionary<String, ExquisiteValueBound> getValueBounds(
            NodeList allValueBounds) {
        Dictionary<String, ExquisiteValueBound> valueBounds = new Hashtable<String, ExquisiteValueBound>();
        if (allValueBounds != null && allValueBounds.getLength() > 0) {
            for (int j = 0; j < allValueBounds.getLength(); j++) {
                if (allValueBounds.item(j).getNodeType() == Element.ELEMENT_NODE) {
                    Element keyElement = getFirstElement(allValueBounds.item(j));
                    Element valueElement = getLastElement(allValueBounds.item(j));
                    String key = keyElement.getTextContent();
                    ExquisiteValueBound value = getValueBound(valueElement
                            .getChildNodes());
                    valueBounds.put(key, value);
                }
            }
        }

        return valueBounds;
    }

    private ExquisiteUserSettings getUserSettings(NodeList nodeList) {
        ExquisiteUserSettings settings = new ExquisiteUserSettings();
        if (nodeList.getLength() > 0 && nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() == Element.ELEMENT_NODE) {
                    Element element = (Element) nodeList.item(i);
                    String key = element.getNodeName();
                    String value = element.getTextContent();
                    switch (key) {
                        case "DiagnosisEngine":
                            settings.setDiagnosisEngine(EngineType.valueOf(value));
                            break;
                        case "LocaleFlag":
                            settings.setLocaleFlag(ExquisiteLocaleFlag.valueOf(value));
                            break;
                        case "MaxDiagnoses":
                            settings.setMaxDiagnoses(IntegerUtilities.parseToInt(value));
                            break;
                        case "SearchDepth":
                            settings.setSearchDepth(IntegerUtilities.parseToInt(value));
                            break;
                        case "ProbabilityThreshold":
                            settings.setProbabilityThreshold(Double.parseDouble(value));
                            break;
                        case "ShowServerDebugMessages":
                            settings.setShowServerDebugMessages(Boolean.parseBoolean(value));
                            break;
                    }
                }
            }
        }
        return settings;
    }

    private Dictionary<String, String> getStringDictionary(NodeList allNode) {
        Dictionary<String, String> dictionary = new Hashtable<String, String>();
        if (allNode != null && allNode.getLength() > 0) {
            for (int j = 0; j < allNode.getLength(); j++) {
                if (allNode.item(j).getNodeType() == Element.ELEMENT_NODE) {
                    Element keyElement = getFirstElement(allNode.item(j));
                    Element valueElement = getLastElement(allNode.item(j));
                    String key = keyElement.getTextContent();
                    String value = valueElement.getTextContent();
                    dictionary.put(key, value);
                }
            }
        }
        return dictionary;
    }

    private Dictionary<String, List<String>> getCellsInRange(NodeList allNode) {
        Dictionary<String, List<String>> cellsInRange = new Hashtable<String, List<String>>();
        if (allNode != null && allNode.getLength() > 0) {
            for (int j = 0; j < allNode.getLength(); j++) {
                if (allNode.item(j).getNodeType() == Element.ELEMENT_NODE) {
                    Element keyElement = getFirstElement(allNode.item(j));
                    Element valueElement = getLastElement(allNode.item(j));
                    String key = keyElement.getTextContent();
                    List<String> value = getStringList(valueElement.getChildNodes());
                    cellsInRange.put(key, value);
                }
            }
        }
        return cellsInRange;
    }

    /**
     * @return the exquisiteAppXML
     */
    public ExquisiteAppXML getExquisiteAppXML() {
        return exquisiteAppXML;
    }

    /**
     * @param exquisiteAppXML the exquisiteAppXML to set
     */
    public void setExquisiteAppXML(ExquisiteAppXML exquisiteAppXML) {
        this.exquisiteAppXML = exquisiteAppXML;
    }
}
