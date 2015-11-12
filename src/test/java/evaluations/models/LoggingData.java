package evaluations.models;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Internal class for storing logging data prior to writing to file.
 * @author David
 *
 */
public class LoggingData
{
	Logger loggerInstance;
	List<String> fileContent; //each list item represents a row.
	
	public LoggingData(Logger loggerInstance)
	{
		this.loggerInstance = loggerInstance;
		this.fileContent = new ArrayList<String>();
	}
	
	public void setFileHeader(String fileHeader)
	{
		fileContent.add(fileHeader);
	}
	
	public void addRow(String rowData)
	{
		fileContent.add(rowData);
	}
	
	public void writeToFile()
	{
		loggerInstance.setLevel(Level.INFO);
		for(String row : this.fileContent)
		{
			loggerInstance.info(row);
		}
	}
}
