package org.exquisite.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;

/**
 * A custom logger for logging test results to file. Logger output is formatted with 
 * ExqusiteFormatter.
 * @author David
 * @see org.exquisite.logging.ExquisiteFormatter
 *
 */
public class ExquisiteLogger {

	static private FileHandler fileTxt;
	static private ExquisiteFormatter formatterTxt;

	//takes name of file (and extension) to write log data too. 
	//All log files are stored in the logs folder located at the root level of the
	//project folder.
	static public Logger setup(String filename) throws IOException {
		// Create Logger
		Logger logger = Logger.getLogger("");
		logger.setLevel(Level.INFO);
		final boolean APPEND = true;
		fileTxt = new FileHandler(filename, APPEND);	
		
		// Create txt Formatter
		formatterTxt = new ExquisiteFormatter();
		
		fileTxt.setFormatter(formatterTxt);
		
		
		Handler[] handlers = logger.getHandlers();
		for(Handler handler : handlers)
		{
			handler.close();
			logger.removeHandler(handler);
		}
		
		logger.addHandler(fileTxt);
		
		return logger;
	}
	
	static public Logger setup(String filename, boolean append) throws IOException {
		// Create Logger
		Logger logger = Logger.getLogger("");
		logger.setLevel(Level.INFO);
		
		File dir = new File(FilenameUtils.getFullPathNoEndSeparator(filename));
		if (!dir.exists())
			dir.mkdirs();
		
		fileTxt = new FileHandler(filename, append);	
		
		// Create txt Formatter
		formatterTxt = new ExquisiteFormatter();
		
		fileTxt.setFormatter(formatterTxt);
		
		Handler[] handlers = logger.getHandlers();
		for(Handler handler : handlers)
		{
			handler.close();
			logger.removeHandler(handler);
		}
		
		logger.addHandler(fileTxt);
		return logger;
	}
}
