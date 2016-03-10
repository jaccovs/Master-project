/**
 *
 */
package org.exquisite.datamodel;

import org.exquisite.datamodel.ExquisiteEnums.ExquisiteTestcaseFlag;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Arash
 */
public class TestCase {
    private String CaseID;
    private String ID;
    private String Description;
    private ExquisiteTestcaseFlag Flag;
    private Dictionary<String, String> Values;
    private Dictionary<String, ExquisiteValueBound> ValueBounds;
    private Dictionary<String, List<String>> CellsInRange;
    private Dictionary<String, String> FaultyValues;
    private Dictionary<String, String> CorrectValues;
    private Dictionary<String, String> Assertions;
    private Dictionary<String, String> Types;

    public TestCase() {
        ID = "";
        CaseID = "";
        Description = "";
        Flag = ExquisiteTestcaseFlag.Normal;
        Values = new Hashtable<String, String>();
        ValueBounds = new Hashtable<String, ExquisiteValueBound>();
        CellsInRange = new Hashtable<String, List<String>>();
        FaultyValues = new Hashtable<String, String>();
        CorrectValues = new Hashtable<String, String>();
        Assertions = new Hashtable<String, String>();
        Types = new Hashtable<String, String>();
    }

    /**
     * @param caseID
     * @param iD
     * @param description
     * @param flag
     * @param values
     * @param valueBounds
     * @param cellsInRange
     * @param faultyValues
     * @param correctValues
     * @param assertions
     * @param types
     */
    public TestCase(String caseID, String iD, String description,
                    ExquisiteTestcaseFlag flag, Dictionary<String, String> values,
                    Dictionary<String, ExquisiteValueBound> valueBounds,
                    Dictionary<String, List<String>> cellsInRange,
                    Dictionary<String, String> faultyValues,
                    Dictionary<String, String> correctValues,
                    Dictionary<String, String> assertions,
                    Dictionary<String, String> types) {
        super();
        CaseID = caseID;
        ID = iD;
        Description = description;
        Flag = flag;
        Values = values;
        ValueBounds = valueBounds;
        CellsInRange = cellsInRange;
        FaultyValues = faultyValues;
        CorrectValues = correctValues;
        Assertions = assertions;
        Types = types;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append(this.getClass().getName());
        result.append(" {");
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for (Field field : fields) {
            result.append("  ");
            try {
                result.append(field.getName());
                result.append(": ");
                //requires access to private field:
                result.append(field.get(this));
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }

    /**
     * @return inputs and expected values as csv.
     */
    public String toCSV(ExquisiteAppXML appXML) {
        Dictionary<String, String> line = new Hashtable<String, String>();

        //inputs
        Iterator<String> inputsIterator = appXML.getInputs().iterator();
        while (inputsIterator.hasNext()) {
            String key = inputsIterator.next();
            String value = this.Values.get(key);
            line.put(key, value);
        }

        //expected outputs
        Enumeration<String> keys = this.FaultyValues.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String value = this.FaultyValues.get(key);
            line.put(key, value);
        }

        //write all to csv
        String result = "";
        String delimiter = ",";

        keys = line.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String value = line.get(key);
            String item = key.substring(5) + "=" + value + delimiter;
            result += item;
        }

        result = result.substring(0, result.length() - delimiter.length());
        return result;
    }

    /**
     * @return the caseID
     */
    public String getCaseID() {
        return CaseID;
    }

    /**
     * @param caseID the caseID to set
     */
    public void setCaseID(String caseID) {
        CaseID = caseID;
    }

    /**
     * @return the iD
     */
    public String getID() {
        return ID;
    }

    /**
     * @param iD the iD to set
     */
    public void setID(String iD) {
        ID = iD;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return Description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        Description = description;
    }

    /**
     * @return the flag
     */
    public ExquisiteTestcaseFlag getFlag() {
        return Flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(ExquisiteTestcaseFlag flag) {
        Flag = flag;
    }

    /**
     * @return the values
     */
    public Dictionary<String, String> getValues() {
        return Values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(Dictionary<String, String> values) {
        Values = values;
    }

    /**
     * @return the valueBounds
     */
    public Dictionary<String, ExquisiteValueBound> getValueBounds() {
        return ValueBounds;
    }

    /**
     * @param valueBounds the valueBounds to set
     */
    public void setValueBounds(Dictionary<String, ExquisiteValueBound> valueBounds) {
        ValueBounds = valueBounds;
    }

    /**
     * @return the cellsInRange
     */
    public Dictionary<String, List<String>> getCellsInRange() {
        return CellsInRange;
    }

    /**
     * @param cellsInRange the cellsInRange to set
     */
    public void setCellsInRange(Dictionary<String, List<String>> cellsInRange) {
        CellsInRange = cellsInRange;
    }

    /**
     * @return the faultyValues
     */
    public Dictionary<String, String> getFaultyValues() {
        return FaultyValues;
    }

    /**
     * @param faultyValues the faultyValues to set
     */
    public void setFaultyValues(Dictionary<String, String> faultyValues) {
        FaultyValues = faultyValues;
    }

    /**
     * @return the correctValues
     */
    public Dictionary<String, String> getCorrectValues() {
        return CorrectValues;
    }

    /**
     * @param correctValues the correctValues to set
     */
    public void setCorrectValues(Dictionary<String, String> correctValues) {
        CorrectValues = correctValues;
    }

    /**
     * @return the assertions
     */
    public Dictionary<String, String> getAssertions() {
        return Assertions;
    }

    /**
     * @param assertions the assertions to set
     */
    public void setAssertions(Dictionary<String, String> assertions) {
        Assertions = assertions;
    }

    /**
     * @return the types
     */
    public Dictionary<String, String> getTypes() {
        return Types;
    }

    /**
     * @param types the types to set
     */
    public void setTypes(Dictionary<String, String> types) {
        Types = types;
    }
}
