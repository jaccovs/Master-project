package evaluations.dxc.synthetic.minizinc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import evaluations.dxc.synthetic.model.DXCComponent;
import evaluations.dxc.synthetic.model.DXCConnection;
import evaluations.dxc.synthetic.model.DXCSystem;
import evaluations.dxc.synthetic.model.DXCSystemDescription;
import evaluations.dxc.synthetic.tools.DXCScenarioParser;
import evaluations.dxc.synthetic.tools.DXCSyntheticXMLParser;

/**
 * Class to create a DiagnosisModel of a DXCSystem and a DXC scenario.
 *
 * @author Kostya
 */
public class DXCDiagnosisMZModelGenerator {
    private List<String> componentTypesToVariables = new ArrayList<String>();
    private List<String> componentTypesToIgnore = new ArrayList<String>();

    public int getAbnormalCounter() {
        return abCounter;
    }

    private int abCounter = 0;
    private List<String> diagnoses = new LinkedList<>();
    private int diagnosisMinCard = 0;
    private int diagnosesMaxCard = 0;

    public DXCDiagnosisMZModelGenerator() {
        Initialize();
    }



    public static void main(String[] args) {
        String mode = "findall";
        int threads = 4;
        boolean verbose = false;

        // Parse a system description first
        String xmlFilePath = "experiments/DXCSynthetic/74283.xml";

        // Parse the scenario
        String scnFilePath = "experiments/DXCSynthetic/74283/74283.000.scn";

        if (args.length != 0) {
            for (String arg : args) {
                if (arg.startsWith("--mode=")) {
                    mode = arg.substring(arg.indexOf("=") + 1);
                }
                if (arg.startsWith("--threads=")) {
                    threads = Integer.parseInt(arg.substring(arg.indexOf("=") + 1));
                }
                if (arg.startsWith("--verbose")) {
                    verbose = true;
                }
                if (arg.startsWith("--system=")) {
                    xmlFilePath = arg.substring(arg.indexOf("=") + 1);
                }
                if (arg.startsWith("--scenario=")) {
                    scnFilePath = arg.substring(arg.indexOf("=") + 1);
                }
            }
        }


        DXCSyntheticXMLParser parser = new DXCSyntheticXMLParser();


        System.out.println("Trying to load xml file: " + xmlFilePath);
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(xmlFilePath)));
            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
            br.close();
            parser.parse(sb.toString());

            DXCSystemDescription sd = parser.getSystemDescription();

            System.out.println("System name: " + sd.getSystems().get(0).getSystemName());

            System.out.println("FINISH");

            DXCScenarioParser scnParser = new DXCScenarioParser();

            System.out.println("Trying to load scn file: " + scnFilePath);

            br = new BufferedReader(new FileReader(new File(scnFilePath)));
            sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line.trim() + "\n");
            }
            br.close();
            scnParser.parse(sb.toString(), sd.getSystems().get(0));

            Dictionary<DXCComponent, Boolean> scenario = scnParser.getScenario().getFaultyState();

            System.out.println("Scenario size: " + scenario.size());

            System.out.println("FINISH");


            // Now create the diagnosis model
            DXCDiagnosisMZModelGenerator dmg = new DXCDiagnosisMZModelGenerator();
            List<String> model = dmg.createDiagnosisModel(sd.getSystems().get(0), scenario);

            long start = System.currentTimeMillis();
            switch (mode) {
                case "findall":
                    dmg.findAllDiagnoses(model, false, "minizinc", "-a", "-f fzn-gecode", "-p" + threads);
                    break;
                case "minc":
                    dmg.findMinCardDiagnosis(model, "minizinc", "-f fzn-gecode", "-p" + threads);
                    break;
                case "allminc":
                    dmg.findAllDiagnoses(model, true, "minizinc", "-a", "-f fzn-gecode", "-p" + threads);
                    break;
                default:
                    System.out.println("Unknown mode! Use --mode=<findall,minc,allminc> to find all, one or all minimum cardinality diagnoses.");
                    System.exit(1);
            }
            long end = System.currentTimeMillis();
            System.out.println("Found " + dmg.getDiagnoses().size() + " diag(s). min(|D|) = " + dmg.getDiagnosisMinCard() +
                    " max(|D|) = " + dmg.getDiagnosesMaxCard());
            System.out.println("Time needed: " + (end - start));
            if (verbose)
                System.out.println(dmg.getDiagnoses());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void Initialize() {
        componentTypesToVariables.add("port");
        componentTypesToVariables.add("probe");

        componentTypesToIgnore.add("port");
        componentTypesToIgnore.add("probe");
        componentTypesToIgnore.add("wire");

//		componentTypesToConstraints.add("inverter");
//		componentTypesToConstraints.add("or");
//		componentTypesToConstraints.add("and");
//		componentTypesToConstraints.add("nand");
//		componentTypesToConstraints.add("nor");
//		componentTypesToConstraints.add("buffer");
    }

    /**
     * Creates a diagnosis model for the given system and scenario
     *
     * @param system
     * @param faultyScenario
     * @return
     * @throws Exception
     */
    public List<String> createDiagnosisModel(DXCSystem system, Dictionary<DXCComponent, Boolean> faultyScenario) {
        List<String> model = new LinkedList<>();


        // create variables
        Enumeration<DXCComponent> components = system.getComponents().elements();

        while (components.hasMoreElements()) {
            DXCComponent component = components.nextElement();
            if (componentTypesToVariables.contains(component.getComponentType().getName())) {
                //String var = makeBoolVar(component.getName());
                model.add("var bool: " + component.getName() + ";");
            }
        }

//		System.out.println("Variables created: " + variables.size());

        // create constraints
        components = system.getComponents().elements();
        while (components.hasMoreElements()) {
            DXCComponent component = (DXCComponent) components.nextElement();

//			if (!component.getName().contains(".")) {
            // split type name and number of inputs
            String typeWoNr;
            int nr = 1;
            String[] typeArr = component.getComponentType().getName().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
            if (typeArr.length > 2) {
                throw new RuntimeException("Type " + component.getComponentType().getName() + " could not be handled.");
            } else if (typeArr.length == 2) {
                nr = Integer.parseInt(typeArr[1]);
            }
            typeWoNr = typeArr[0];

            if (!componentTypesToIgnore.contains(typeWoNr)) {
//				System.out.println("Type: " + typeWoNr + " Nr: " + nr + " Name: " + component.getName());
                String outputConnection = component.getName() + ".o";
                DXCComponent outputComp = SearchConnections(system.getComponents().get(outputConnection), system.getConnections());
                String output = outputComp.getName();
                String[] inputs = new String[nr];
                for (int i = 0; i < nr; i++) {
                    String inputConnection = component.getName() + ".i" + (i + 1);
                    DXCComponent inputComp = SearchConnections(system.getComponents().get(inputConnection), system.getConnections());
                    inputs[i] = inputComp.getName();
                }
                switch (typeWoNr) {
                    case "inverter":
                        if (nr != 1) {
                            throw new RuntimeException("Found inverter with more than 1 input!");
                        }
                        model.add(not(output, inputs[0]));
                        break;

                    case "or":
                        model.add(or(output, inputs));
                        break;

                    case "and":
                        model.add(and(output, inputs));
                        break;

                    case "nor":
                        model.add(nor(output, inputs));
                        break;

                    case "buffer":
                        model.add(eq(output, inputs[0]));
                        break;

                    case "nand":
                        model.add(nand(output, inputs));
                        break;

                    case "xor":
                        if (nr != 2) {
                            throw new RuntimeException("Found xor with more or less than 2 inputs!");
                        }
                        model.add(xor(output, inputs[0], inputs[1]));
                        break;

                    default:
                        if (!componentTypesToIgnore.contains(typeWoNr)) {
                            throw new RuntimeException("Component type " + typeWoNr + " is unhandled.");
                        }
                        break;
                }
            }
        }

        //include prerequisites
        String pre = "% minizinc encoding " + system.getSystemName() + " : " + system.getDescription() + "\n" +
                "set of int: Const = 0.." + (abCounter - 1) + ";\n" +
                "array[Const] of var bool : ab;\n" +
                "int : diagCard;\n" +
                "constraint if (diagCard > 0) then sum(i in Const)(bool2int(ab[i])) = diagCard else true endif;";

        model.add(0, pre);

        Enumeration<DXCComponent> scenarioComps = faultyScenario.keys();
        while (scenarioComps.hasMoreElements()) {
            DXCComponent scenarioComp = scenarioComps.nextElement();
            // generate constraints for the specific scenario
            String value = String.valueOf(faultyScenario.get(scenarioComp));
            model.add("constraint " + scenarioComp.getName() + " = " + value + ";");
        }
        model.add("output [if fix(ab[i]) then \"ab[\" ++ show(i) ++ \"] \" else \"\" endif | i in Const] ++ [\"\\n\"] ;");
        return model;
    }

    private DXCComponent SearchConnections(DXCComponent searchConnection, List<DXCConnection> connections) {
        DXCComponent connected = null;
        for (Iterator<DXCConnection> it = connections.iterator(); it.hasNext(); ) {
            DXCConnection connection = it.next();
            if (connection.getC1() == searchConnection) {
                if (connected == null) {
                    connected = connection.getC2();
                } else {
                    throw new RuntimeException("Duplicate connection found!");
                }
            } else if (connection.getC2() == searchConnection) {
                if (connected == null) {
                    connected = connection.getC1();
                } else {
                    throw new RuntimeException("Duplicate connection found!");
                }
            }
        }
        return connected;
    }

    private String not(String output, String input) {
        return getHeader() + "(not(" + output + ") <-> " + input + ");";
    }

    private String eq(String output, String input) {
        return getHeader() + "(" + output + " <-> " + input + ");";
    }

    private String or(String output, String... inputs) {
        return getClause(" \\/ ", output, inputs, false);
    }

    private String and(String output, String... inputs) {
        return getClause(" /\\ ", output, inputs, false);
    }

    private String nor(String output, String... inputs) {
        return getClause(" \\/ ", output, inputs, true);
    }

    private String nand(String output, String... inputs) {
        return getClause(" /\\ ", output, inputs, true);
    }

    private String xor(String output, String input1, String input2) {
        return getClause(" xor ", output, new String[]{input1, input2}, false);
    }

    /**
     * Generates a constraint.
     *
     * @param conn boolean connective
     * @param output output variable
     * @param inputs input variables
     * @param neg indicated whether the expression build from the input variables and the boolean
     *            connective has to be negated
     * @return a minizinc constraint
     */
    private String getClause(String conn, String output, String[] inputs, boolean neg) {
        String header = getHeader();
        StringBuilder constraint = new StringBuilder(header + " (" + output + " <-> ");
        if (neg) constraint.append(" not");
        constraint.append("(");
        for (int i = 0; i < inputs.length; i++) {
            constraint.append(inputs[i]);
            if (i < inputs.length - 1) constraint.append(conn);
        }
        return constraint.append("));").toString();
    }

    /**
     * Generates a constraint header with the abnormal atom corresponding to the gate represented by the constraint
     *
     * @return constraint header
     */
    private String getHeader() {
        return "constraint " + "ab[" + abCounter++ + "]" + " \\/ ";
    }

    /**
     * Finds all (minimum cardinality) minimal diagnoses. It iteratively increases the cardinality of diagnoses starting
     * from 1. In each iteration all found diagnoses are converted to constraints that are added to a model. In this way
     * the method guarantees identification of only minimal diagnoses.
     *
     * The methods quits either after a set of minimum cardinality diagnoses is found or if no further diagnoses
     * exist.
     *
     * @param model list of strings representing a model
     * @param minc if <code>true</code> the methods quits after a first set of diagnoses is found
     * @param cmd command line for the solver
     * @throws IOException
     */
    private void findAllDiagnoses(List<String> model, boolean minc, String... cmd) throws IOException {
        File mzn = File.createTempFile("debug", ".mzn");
        File dzn = File.createTempFile("settings", ".dzn");
        File dbg = File.createTempFile("constraints", ".mzn");

        model.add(0, "include \"" + dzn.getName() + "\";");
        model.add(0, "include \"" + dbg.getName() + "\";");
        model.add("solve satisfy;");

        saveModel(model, mzn);

        boolean foundDiagnoses = false;
        int diagCard = 1;
        List<String> diagConstraints = new LinkedList<>();
        while (true) {
            if (diagCard > this.abCounter) {
                break;
            }
            saveModel("diagCard =" + diagCard + ";", dzn);
            saveModel(diagConstraints, dbg);

            List<String> newConstraints = processModel(mzn, cmd);
            diagConstraints.addAll(newConstraints);
            if (!foundDiagnoses && !newConstraints.isEmpty()) {
                foundDiagnoses = true;
                this.diagnosisMinCard = diagCard;
            }
            this.diagnosesMaxCard = diagCard++;

            if (foundDiagnoses && (minc || newConstraints.isEmpty()))
                break;

        }
    }

    /**
     * Extends a model with an optimization statement to find exactly one minimum cardinality diagnosis.
     * @param model list of strings representing a model
     * @param cmd command line for the solver
     * @throws IOException
     */

    private void findMinCardDiagnosis(List<String> model, String... cmd) throws IOException {
        File mzn = File.createTempFile("debug", ".mzn");

        model.add("diagCard=0;");
        model.add("solve minimize sum(i in Const)(bool2int(ab[i]));");
        saveModel(model, mzn);

        processModel(mzn, cmd);

        this.diagnosesMaxCard = diagnoses.get(0).split(" ").length;
        this.diagnosisMinCard = this.diagnosesMaxCard;
    }

    /**
     * Helper methods that converts a command given as an array of strings to a list of strings.
     * @param mzn model file to be provided to the solver
     * @param cmd command line
     * @return a list of strings representing constraints corresponding to found diagnoses
     */
    private List<String> processModel(File mzn, String... cmd) {
        List<String> command = new LinkedList<>();
        command.addAll(Arrays.asList(cmd));
        return executeSolver(mzn, command);
    }

    /**
     * Writes a set of strings to a file
     * @param strings a set of strings to write
     * @param dbg file handler
     * @throws IOException
     */
    private void saveModel(List<String> strings, File dbg) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(dbg));
        for (String s : strings) {
            bw.write(s);
            bw.write("\n");
        }
        bw.close();
    }

    /**
     * Writes a string to a given file
     * @param str string to write
     * @param dzn file handler
     * @throws IOException
     */
    private void saveModel(String str, File dzn) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(dzn));
        bw.write(str);
        bw.close();
    }

    public List<String> executeSolver(File file, List<String> command) {
        List<String> constraints = new LinkedList<>();
        try {
            command.add(file.getName());
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(file.getParentFile());
            pb.redirectErrorStream(true);
            Process p = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String ln;
            while ((ln = br.readLine()) != null) {
                if (ln.startsWith("-") || ln.startsWith("="))
                    continue;
                if (!ln.startsWith("ab"))
                    throw new RuntimeException("Unexpected result returned by minizinc! " + ln);

                this.diagnoses.add(ln);
                StringBuilder st = new StringBuilder(50);
                st.append("constraint ");
                String[] split = ln.split(" ");
                for (int i = 0; i < split.length; i++) {
                    st.append(split[i]).append("=false");
                    if (i + 1 < split.length) st.append(" \\/ ");
                }
                st.append(";");
                constraints.add(st.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return constraints;
    }

    public List<String> getDiagnoses() {
        return diagnoses;
    }

    public int getDiagnosisMinCard() {
        return diagnosisMinCard;
    }

    public int getDiagnosesMaxCard() {
        return diagnosesMaxCard;
    }
}
