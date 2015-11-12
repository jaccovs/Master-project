package org.exquisite.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ExquisiteFormatter extends Formatter 
{	
	// This method is called for every log record
	public String format(LogRecord rec)
	{
		StringBuffer buf = new StringBuffer(1000);			
		
		buf.append(formatMessage(rec));
		buf.append('\n');
		return buf.toString();
	}

	private String calcDate(long millisecs)
	{
		SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
		Date resultdate = new Date(millisecs);
		return date_format.format(resultdate);
	}

	// This method is called just after the handler using this
	// formatter is created
	public String getHead(Handler h)
	{
		//#Rows, #Vars., #Constrs., #CSP prop., #CSP Solved, #Diag. found, Diag. time(ms)\n";
		return "";
	}

	// This method is called just after the handler using this
	// formatter is closed
	public String getTail(Handler h)
	{
		return "";
	}
}
