package org.exquisite.tools;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.IVariableArray;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.MetaConstraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.diagnosis.engines.common.ConstraintComparator;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.*;
import java.util.Map.Entry;

class SwitchedComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        if (o1.length() != o2.length())
            return o1.length() - o2.length();
        return o1.compareTo(o2);
    }


}

/**
 * A utility class
 *
 * @author Dietmar
 */
public class Utilities {


    static String TMPVAR_PREFIX = "TMP_";

    /**
     * Returns a string representation of the current state of the solver's variable
     *
     * @param solver the solver object
     * @return a string representation
     */
    public static String printSolution(CPSolver solver) {
        StringBuffer result = new StringBuffer();
        Iterator<IntDomainVar> it = solver.getIntVarIterator();
        //Iterator<RealVar> it = solver.getRealVarIterator();
        while (it.hasNext()) {
            //RealVar var = it.next();
            IntDomainVar var = it.next();
            if (!var.getName().startsWith(TMPVAR_PREFIX)) {
                result.append("var: " + var.getName() + " " + var.getDomain() + " " + var.getVal() + "\n");
            }
        }
        return result.toString();
    }

    /**
     * Returns a string representation of the current state of the solver's variable
     * Prints only the intermediates and outputs and not the TMP vars + inputs
     *
     * @param solver the solver object
     * @param appXML the source model
     * @return a string representation
     */
    public static String printRelevantVarsOfSolution(CPSolver solver, ExquisiteAppXML appXML) {
        StringBuffer result = new StringBuffer();
        Iterator<IntDomainVar> it = solver.getIntVarIterator();
        //Iterator<RealVar> it = solver.getRealVarIterator();
        while (it.hasNext()) {
            //RealVar var = it.next();
            IntDomainVar var = it.next();
            if (!var.getName().startsWith(TMPVAR_PREFIX) && !appXML.getInputs().contains(var.getName())) {
                result.append("var: " + var.getName() + " " + var.getDomain() + " " + var.getVal() + "\n");
            }
        }
        return result.toString();
    }


    /**
     * A method that returns a list of all intdomainvar variables of a solver (excluding tmp objects)
     *
     * @param solver the solver
     * @return a list of variables
     */
    public static List<IntDomainVar> getIntVars(CPSolver solver) {
        List<IntDomainVar> variables = new ArrayList<IntDomainVar>();
        Iterator<IntDomainVar> var_it = solver.getIntVarIterator();
        IntDomainVar var;
        while (var_it.hasNext()) {
            var = var_it.next();
            if (!var.getName().startsWith(TMPVAR_PREFIX)) {
                variables.add(var);
            }
        }
        return variables;

    }

    /**
     * A method that returns a list of all constraints of a model
     *
     * @param model a model
     * @return a list of constraint
     */
    public static List<Constraint> getConstraints(CPModel model) {
        List<Constraint> constraints = new ArrayList<Constraint>();
        Iterator<Constraint> ct_it = model.getConstraintIterator();
        Constraint ct;
        while (ct_it.hasNext()) {
            ct = ct_it.next();
            constraints.add(ct);
        }
        return constraints;

    }


    /**
     * Sort a Map by value in descending order
     *
     * @param The map to sort
     * @return the sorted map
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {

                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Prints a conflict
     *
     * @param constraints
     */
    public static <T> String printConstraintList(Collection<T> constraints, DiagnosisModel<T> model) {
        String result = "[ ";
        for (T c : constraints) {
            result += model.getConstraintName(c) + " ";
        }
        result += "]";
        return result;
    }

    /**
     * Prints a conflict given as a set
     *
     * @param conflict
     */
    public static <T> String printConstraintList(List<T> conflict, DiagnosisModel<T> model) {
        String result = "[ ";
        for (T c : conflict) {
            String name = "";
            if (model != null) {
                name = model.getConstraintName(c);
            }
            result += "\"" + name + "\" ";
        }
        result += "]";
        return result;
    }

    /**
     * Create an ordered list of constraints as string
     *
     * @param conflict
     * @param model
     * @return
     */
    public static <T> String printConstraintListOrderedByName(Collection<T> conflict, DiagnosisModel<T> model) {
        List<String> names = new ArrayList<String>();
        for (T c : conflict) {
            String name = model.getConstraintName(c);
            if (name == null) {
                name = c.toString();
            }
            names.add(name);
        }
        Collections.sort(names);

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (String name : names) {
            sb.append(name);
            sb.append(" ");
        }
        String result = sb.substring(0, sb.length() - 1);
        result += "]";
        return result;
    }

    /**
     * Exhange keys and values
     *
     * @param map
     * @return
     */
    public static <K, V> HashMap<V, K> reverse(Map<K, V> map) {
        HashMap<V, K> rev = new HashMap<V, K>();
        for (Map.Entry<K, V> entry : map.entrySet())
            rev.put(entry.getValue(), entry.getKey());
        return rev;
    }

    /**
     * A method that returns all variables involved in the constraint
     *
     * @param constraint
     * @return
     */
    static public <T extends Constraint> Set<Variable> getAllVariablesOfConstraint(T constraint) {
        Set<Variable> result = new HashSet<Variable>();
        Iterator<Variable> var_it = constraint.getVariableIterator();
        Variable var = null;
        while (var_it.hasNext()) {
            var = var_it.next();
            Set<Variable> oneResult = new HashSet<Variable>();
            Utilities.getAllVariables(oneResult, var);
            result.addAll(oneResult);
        }

        return result;
    }


    /**
     * The method goes down recursively and is called by "getAllVariablesOfConstraint"
     *
     * @param knownVars
     * @param v
     */
    static void getAllVariables(Set<Variable> knownVars, Variable var) {
        if (var.getNbVars() == 1) {
            // This is the last var
            knownVars.add(var);
        } else {
            Variable[] vars = var.getVariables();
            for (Variable v : vars) {
                getAllVariables(knownVars, v);
            }
        }
    }

    /**
     * A method that increments the counter value in a map. If no value exists,
     * it adds 1. Otherwise we increment the value
     *
     * @param map
     * @param key
     */
    public static <K> void incrementMapValue(Map<K, Integer> map, K key) {
        Integer existingValue = map.get(key);
        if (existingValue == null) {
            map.put(key, 1);
        } else {
            map.put(key, existingValue + 1);
        }
    }


    /**
     * Count the values of the variables (integer only) for relevant variables
     */
    public static void collectStatsFromSolution(Solver solver, Map<String, Map<Integer, Integer>> statsMap,
                                                ExquisiteAppXML appXML) {
        Iterator<IntDomainVar> it = solver.getIntVarIterator();
        while (it.hasNext()) {
            IntDomainVar var = it.next();
            int value;
            if (!var.getName().startsWith(TMPVAR_PREFIX) && !appXML.getInputs().contains(var.getName())) {
                value = var.getVal();
                Map<Integer, Integer> valueMapOfVar = statsMap.get(var.getName());
                if (valueMapOfVar == null) {
                    valueMapOfVar = new HashMap<Integer, Integer>();
                    statsMap.put(var.getName(), valueMapOfVar);
                }
                Utilities.incrementMapValue(valueMapOfVar, value);
            }
        }

    }


    /**
     * Prints xml content to the console in a tidy format.
     *
     * @param root
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     */
    public static void prettyPrintXML(Node root) throws TransformerFactoryConfigurationError, TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        //initialize StreamResult with File object to save to file
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(root);
        transformer.transform(source, result);
        String xmlString = result.getWriter().toString();
        System.out.println(xmlString);
    }

    /**
     * Reads in XML string and returns a root XML node that can be consumed by a parseXML() to populate a DiagnosisModel object.
     *
     * @param xml
     * @return an XML Element object
     */
    public static Element readXML(String xml) {
        Document dom = null;
        Element root = null;
        // Make an  instance of the DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use the factory to take an instance of the document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // parse using the builder to get the DOM mapping of the    
            // XML file
            dom = db.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
            root = dom.getDocumentElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    /**
     * Are there any other forms a cell range can take in Excel??
     * <p>
     * Checks if the cell reference points to a single cell or to a range of cells.
     *
     * @return false - if reference does not contain ":" character implying a single cell reference.
     */
    public static boolean isCellRangeReference(String reference) {
        boolean result = (reference.contains(":") || reference.contains(","));
        return result;
    }

    /**
     * Are there any forms of constants, that cannot be parsed to a float?
     * <p>
     * Checks if s is a constant or not.
     *
     * @param s
     * @return
     */
    public static boolean isConstant(String s) {
        try {
            Float.parseFloat(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Assuming 1:1 relationship, returns key of a map based on value.
     * <p>
     * http://stackoverflow.com/questions/1383797/java-hashmap-how-to-get-key-from-value
     *
     * @param map
     * @param value
     * @return
     */
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Converts a html specialchar to string
     *
     * @param s
     * @return corresponding string to given html specialchar
     */
    public static String htmlspecialchars_decode_ENT_NOQUOTES(String s) {
        Hashtable<String, String> html_specialchars_table = new Hashtable<String, String>();

        html_specialchars_table.put("&lt;", "<");
        html_specialchars_table.put("&gt;", ">");
        html_specialchars_table.put("&amp;", "&");

        for (String key : html_specialchars_table.keySet()) {
            String val = html_specialchars_table.get(key);
            s = s.replaceAll(key, val);
        }
        return s;
    }

    /**
     * Converts a string to html specialchar
     *
     * @param s
     * @return corresponding html specialchar to given string
     */
    public static String htmlspecialchars_encode_ENT_NOQUOTES(String s) {
        Hashtable<String, String> html_specialchars_table = new Hashtable<String, String>();

        html_specialchars_table.put("<", "&lt;");
        html_specialchars_table.put(">", "&gt;");
        html_specialchars_table.put("&", "&amp;");

        for (String key : html_specialchars_table.keySet()) {
            String val = html_specialchars_table.get(key);
            s = s.replaceAll(key, val);
        }
        return s;
    }

    /**
     * Gets an intdomain variable by name
     *
     * @param solver
     * @param name
     * @return
     */
    public static IntegerVariable getIntVariableByName(CPModel model, String name) {
        IntegerVariable result = null;
        Iterator<IntegerVariable> var_it = model.getIntVarIterator();
        IntegerVariable var;
        while (var_it.hasNext()) {
            var = var_it.next();
            if (var.getName().equals(name)) {
                return var;
            }
        }
        return result;
    }


    /**
     * Returns a List<T> from HashSet<T>
     *
     * @param targetSet
     * @return
     */
    public static <T> List<T> hashSetToList(Set<T> targetSet) {
        List<T> result = new ArrayList<T>(targetSet);
        return result;
    }

    /**
     * Returns a TreeSet from an array.
     *
     * @param args
     * @return
     */
    public static Set<Constraint> makeTreeSet(Constraint[] args) {
        Set<Constraint> set = new TreeSet<Constraint>(new ConstraintComparator());
        set.addAll(Arrays.asList(args));
        return set;
    }


    /**
     * Apply some default ordering
     *
     * @param diags
     * @param model
     * @return
     */
    public static <T> List<Diagnosis<T>> sortDiagnosesLexicographically(List<Diagnosis<T>> diagnoses, DiagnosisModel<T>
            model) {

        Map<String, Diagnosis<T>> string2DiagMap = new HashMap<String, Diagnosis<T>>();

        List<Diagnosis<T>> result = new ArrayList<Diagnosis<T>>();
        // Get a string representation of the constraints
        List<String> diagnosesAsString = new ArrayList<String>();
        for (Diagnosis<T> diag : diagnoses) {
            String stringRep = diag.toString();
//			System.out.println("String rep: " + stringRep);
            diagnosesAsString.add(stringRep);
            string2DiagMap.put(stringRep, diag);
        }
        Collections.sort(diagnosesAsString);
//		System.out.println("Sorted my lists..: " + diagnosesAsString);
        for (String diagAsString : diagnosesAsString) {
            result.add(string2DiagMap.get(diagAsString));
        }
        return result;
    }

    /**
     * A sorted string representation of the diagnoses for debugging
     *
     * @param diagnoses
     * @param separator what to use as a separator, e.g., \n \t etc.
     * @return
     */
    public static <T> String printSortedDiagnoses(List<Diagnosis<T>> diagnoses, char separator) {
        List<String> diagnosesAsString = new ArrayList<String>();
        for (Diagnosis<T> diag : diagnoses) {
            if (diag != null) {
                diagnosesAsString.add(diag.toString());
            } else {
                diagnosesAsString.add("NULL");
                System.out.println("NULL DIAGNOSE");
            }
        }
        SwitchedComparator comparator = new SwitchedComparator();
        Collections.sort(diagnosesAsString, comparator);
        String result = "";
        for (String diagnosis : diagnosesAsString) {
            result += diagnosis + separator;
        }
        // Remove the last cr
        if (result.length() > 0) {
            result = result.substring(0, result.length() - 1);
        }
        return result;

    }

    /**
     * A sorted string representation of the conflicts for debugging
     *
     * @param conflicts
     * @param model
     * @param separator what to use as a separator, e.g., \n \t etc.
     * @return
     */
    public static <T> String printSortedConflicts(List<List<T>> conflicts, DiagnosisModel<T> model, char
            separator) {
        List<String> conflictsAsString = new ArrayList<String>();
        for (Collection<T> conflict : conflicts) {
            if (conflict != null) {
                conflictsAsString.add(printConstraintListOrderedByName(conflict, model));
            } else {
                conflictsAsString.add("NULL");
                System.out.println("NULL CONFLICT");
            }
        }

        SwitchedComparator comparator = new SwitchedComparator();
        Collections.sort(conflictsAsString, comparator);
        String result = "";
        for (String conflict : conflictsAsString) {
            result += conflict + separator;
        }
        // Remove the last cr
        if (result.length() > 0) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }


    /**
     * Sort the constraints in lexicographic order
     *
     * @param constraints
     * @param model
     * @return
     */
    public static <T> List<T> sortConstraintsLexicographically(Set<T> constraints, DiagnosisModel<T>
            model) {
        List<T> result = new ArrayList<>();
        List<String> constraintNames = new ArrayList<String>();
        for (T ct : constraints) {
            constraintNames.add(model.getConstraintName(ct));
        }
        Collections.sort(constraintNames);
        for (String ctname : constraintNames) {
            result.add(model.getConstraintByName(ctname));
        }
        return result;
    }

    /**
     * Returns a random number from within a specified range of values.
     *
     * @param min - minimum range value.
     * @param max - maximum range value.
     * @return
     */
    public static int randomRange(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    /**
     * Returns a set of randomly selected indices.
     *
     * @param indicesCount - the number of random indices to choose.
     * @param maxIndex     - the maximal index to choose.
     * @return The array of indices.
     */
    public static Integer[] getRandomIndices(int indicesCount, int maxIndex) {
        List<Integer> indices = new ArrayList<Integer>();

        if (indicesCount > maxIndex)
            indicesCount = maxIndex;

        int min = 0;
        int max = maxIndex;
        int index = randomRange(min, max);
        while (indices.size() < indicesCount) {
            if (indices.indexOf(index) == -1) {
                indices.add(index);
            }
            index = randomRange(min, max);
        }
        Integer[] result = new Integer[indicesCount];
        result = indices.toArray(new Integer[indicesCount]);
        Arrays.sort(result);
        return result;
    }

    /**
     * A method that looks up subsets of conflicts
     *
     * @param newConflict
     * @param knownConflicts
     * @return
     */
    public static boolean isSubsetOfKnownConflict(List<Constraint> newConflict, List<List<Constraint>> knownConflicts) {
        boolean result = false;
        for (List<Constraint> conflict : knownConflicts) {
            if (conflict.containsAll(newConflict) && conflict.size() > newConflict.size()) {
//				System.err.println("Found constraint which will lead to pruning");
                return true;
            }
        }
        return result;
    }

    /**
     * A method that looks up if a conflict is already known
     *
     * @param newConflict
     * @param knownConflicts
     * @return
     */
    public static boolean isKnownConflict(List<Constraint> newConflict, List<List<Constraint>> knownConflicts) {
        boolean result = false;
        for (List<Constraint> conflict : knownConflicts) {
            if (conflict.containsAll(newConflict) && conflict.size() == newConflict.size()) {
//				System.err.println("Found constraint which will lead to pruning");
                return true;
            }
        }
        return result;
    }

    /**
     * Add without duplicates
     *
     * @param list           current list (conflict)
     * @param knownConflicts (known conflicts)
     * @return
     */
    static boolean addConstraintListNoDuplicates(List<List<Constraint>> knownConflicts, List<Constraint> list) {

        if (!knownConflicts.contains(list)) {
            knownConflicts.add(list);
            return true;
        } else {
            return false;
        }
    }

    public static void printOutConstraintsOfModel(Model model) throws Exception {
        Iterator<Constraint> it = model.getConstraintIterator();
        while (it.hasNext()) {
            Constraint c = it.next();

            System.out.println(printConstraint(c));
        }
    }

    public static String printConstraints(Constraint[] cs) throws Exception {
        StringBuilder b = new StringBuilder();
        b.append('[');

        for (int i = 0; i < cs.length; i++) {
            if (i > 0) {
                b.append(',');
            }
            b.append(printConstraint(cs[i]));
        }

        b.append(']');
        return b.toString();
    }

    public static String printConstraint(Constraint c) throws Exception {
        StringBuilder b = new StringBuilder();
        b.append(c.getConstraintType());

        if (c instanceof MetaConstraint<?>) {
            // TS: Warning can be ignored because the ? of MetaConstraint extends Constraint
            @SuppressWarnings("unchecked")
            MetaConstraint<Constraint> mc = (MetaConstraint<Constraint>) c;
            Constraint[] cs = mc.getConstraints();
            b.append(printConstraints(cs));
        } else {
            b.append('(');
            b.append(printVariables(c));
            b.append(')');
        }
        return b.toString();
    }

    private static String printVariables(IVariableArray va) throws Exception {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < va.getNbVars(); i++) {
            if (i > 0) {
                b.append(',');
            }
            Variable v = va.getVariable(i);

            b.append(printVariable(v));
        }
        return b.toString();
    }

    private static String printVariable(Variable v) throws Exception {
        switch (v.getVariableType()) {
            case INTEGER_EXPRESSION:
                IntegerExpressionVariable iev = (IntegerExpressionVariable) v;
                StringBuilder b = new StringBuilder();

                b.append(iev.getOperator().name);

                Constraint[] cs = v.getConstraints();

                if (cs.length > 0) {
                    b.append(printConstraints(cs));
                }

                b.append('(');
                b.append(printVariables(iev));
                b.append(')');
                return b.toString();
            case CONSTANT_INTEGER:
                return v.getName();
            case INTEGER:
                return v.getName();
            default:
                throw new Exception("Variable type unknown: " + v.getVariableType().toString());
        }
    }


}
