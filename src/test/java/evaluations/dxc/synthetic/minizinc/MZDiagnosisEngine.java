package evaluations.dxc.synthetic.minizinc;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MZDiagnosisEngine implements IDiagnosisEngine {

	private final int abCounter;
	public String lastInput;
	public String lastOutput;
	List<String> model;
	SearchType searchType = SearchType.FindAll;
	int threads;
	// Time, where the search for diagnoses was finished
	long finishedTime = 0;
	private ExquisiteSession sessionData;
	private List<String> diagnoses = new LinkedList<>();
	private int diagnosesMinCard = 0;
    private int diagnosesMaxCard = 0;
	public MZDiagnosisEngine(List<String> model, int abnormals, SearchType searchType, int threads) {
        this.abCounter = abnormals;
		this.model = model;
		this.searchType = searchType;
		this.threads = threads;
	}
	
	public List<String> getMZModel() {
		return model;
	}
	
	public int getDiagnosesMinCard() {
		return diagnosesMinCard;
	}
	
	public int getDiagnosesMaxCard() {
		return diagnosesMaxCard;
	}

	public void setDiagnosesMaxCard(int diagnosesMaxCard) {
		this.diagnosesMaxCard = diagnosesMaxCard;
    }

	@Override
	public void resetEngine() {
		diagnoses.clear();
		diagnosesMinCard = 0;
		diagnosesMaxCard = 0;

	}

	@Override
	public ExquisiteSession getSessionData() {
        return this.sessionData;
	}

	@Override
	public void setSessionData(ExquisiteSession sessionData) {
        this.sessionData = sessionData;
	}

	@Override
	public List<Diagnosis> calculateDiagnoses() throws DiagnosisException {
		try {
			switch (searchType) {
		        case FindAll:
		        	findAllDiagnoses(model, false, "minizinc", "-a", "-f fzn-gecode", "-p" + threads);
		            break;
		        case OneMinCardinality:
		        	findMinCardDiagnosis(model, "minizinc", "-f fzn-gecode", "-p" + threads);
		            break;
		        case AllMinCardinality:
		        	findAllDiagnoses(model, true, "minizinc", "-a", "-f fzn-gecode", "-p" + threads);
		            break;
	            default:
	            	throw new DiagnosisException("SearchType not supported");
		    }
		}
		catch (IOException e) {
			throw new DiagnosisException(e);
		}
		List<Diagnosis> diags = new LinkedList<>();
		for (String s: diagnoses) {
			diags.add(new MZDiagnosis(s));
		}

		finishedTime = System.nanoTime();

		return diags;
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
        final int k = (getDiagnosesMaxCard() > 0) ? getDiagnosesMaxCard() : this.abCounter;
        while (diagCard <= k && !(foundDiagnoses && minc)) {
            saveModel("diagCard =" + diagCard + ";", dzn);
            saveModel(diagConstraints, dbg);

            List<String> newConstraints = processModel(mzn, cmd);
            diagConstraints.addAll(newConstraints);
            if (!foundDiagnoses && !newConstraints.isEmpty()) {
                foundDiagnoses = true;
                this.diagnosesMinCard = diagCard;
            }
            this.diagnosesMaxCard = diagCard++;

            //if (foundDiagnoses && (minc || newConstraints.isEmpty())) {break;}
        }
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

			String eol = System.getProperty("line.separator");

			lastInput = "";
			for (String s: command) {
            	lastInput += s + " ";
            }
            lastOutput = "";
            while ((ln = br.readLine()) != null) {
            	lastOutput += ln + eol;
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

        if (diagnoses != null && diagnoses.size() > 0) {
		    this.diagnosesMaxCard = diagnoses.get(0).split(" ").length;
		    this.diagnosesMinCard = this.diagnosesMaxCard;
        }
    }

	@Override
	public int getSolverCalls() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getSolverTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCspSolvedCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPropagationCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTPCalls() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getFinishedTime() {
		return finishedTime;
	}

	@Override
	public int getSearchesForConflicts() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMXPConflicts() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMXPSplittingTechniqueConflicts() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DiagnosisModel getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setModel(List<String> model) {
		this.model = model;
	}

	@Override
	public void incrementSolverCalls() {
		// TODO Auto-generated method stub

	}

	@Override
	public void incrementSolverTime(long time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void incrementCSPSolutionCount() {
		// TODO Auto-generated method stub

	}

	@Override
	public void incrementPropagationCount() {
		// TODO Auto-generated method stub

	}

	@Override
	public void incrementQXPCalls() {
		// TODO Auto-generated method stub

	}

	@Override
	public void incrementSearchesForConflicts() {
		// TODO Auto-generated method stub

	}

	@Override
	public void incrementMXPConflicts(int conflicts) {
		// TODO Auto-generated method stub

	}

	@Override
	public void incrementMXPSplittingTechniqueConflicts(int conflicts) {
		// TODO Auto-generated method stub

	}

	public enum SearchType {FindAll, OneMinCardinality, AllMinCardinality}

}
