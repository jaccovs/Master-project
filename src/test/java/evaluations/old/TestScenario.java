package evaluations.old;

/**
 * Holds the data of one TestScenario used in MutatedConstraintsIndiviual
 * @author Thomas
 */
public class TestScenario {
	
	public String Filename;
	public int MaxDiagSize;
	public int Waittime;
	
	/**
	 * Creates a new TestScenario
	 * @param filename
	 * @param maxDiagSize
	 * @param waittime
	 */
	public TestScenario(String filename, int maxDiagSize, int waittime) {
		this.Filename = filename;
		this.MaxDiagSize = maxDiagSize;
		this.Waittime = waittime;
	}

}
