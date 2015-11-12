package evaluations.tools;

import java.io.IOException;
import java.util.logging.Logger;

import org.exquisite.logging.ExquisiteLogger;

import evaluations.models.LoggingData;

/*
 * A class to log results in the csv format - reusable across scenarios 
 */
public class ResultsLogger {
	
	/**
	 * Simple util class  hold data from test runs and writing data to log file.
	 */
	private LoggingData loggingData;
	
	private String separator = ";";
	
	private String logfileName = "";
	
	private String pruningPath = "";
	
	private String LOG_PATH = "";
	
	/**
	 * Create a logger with the paths
	 * @param separator
	 * @param logPath
	 * @param pruningPath
	 * @param logFileName
	 */
	public ResultsLogger(String separator, String logPath, String pruningPath, String logFileName) {
		this.LOG_PATH = logPath;
		this.separator = separator;
		this.pruningPath = pruningPath;
		this.logfileName = logFileName;
		setupLogging();
		
	}
	
	
	/**
	 * Writes another line 
	 * @param text
	 */
	public void writeSeparatorLine(String text) {
		this.loggingData.addRow(text);
	}
	
	
	/**
	 * Write the file
	 */
	public void writeFile() {
		this.loggingData.writeToFile();
	}
	
	
	/**
	 * Add a row
	 * @param row
	 */
	public void addRow(String row) {
		System.out.println(row);
		loggingData.addRow(row);
	}
	
	/**
	 * Create the logging file
	 */
	private void setupLogging(){
		try {
			
			final boolean AppendContent = false;
			Logger loggerInstance;
			loggerInstance = ExquisiteLogger.setup(LOG_PATH + pruningPath + logfileName, AppendContent);
			this.loggingData = new LoggingData(loggerInstance);
			String logFileHeader = 	"#Vars" + separator + 
											"#Constraints" + separator + 
											"#CSP props." + separator + 
											"#CSP solved" + separator + 
											"Diag. time (ms)" + separator + 
											"Max Search Depth" + separator + 
											"Diagnoses" + separator +			
											"ThreadPoolSize" + separator +
											"#Diags";
						
			
			this.loggingData.addRow(logFileHeader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
