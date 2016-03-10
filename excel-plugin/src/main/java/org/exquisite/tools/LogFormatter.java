package org.exquisite.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Custom formatter for logging that optimizes output for the console.
 *
 * @author Tom-Philipp Seifert (Philipp.Seifert@udo.edu)
 * @since 20.09.2015 $Id: LogFormatter.java 46 2015-10-10 20:56:45Z seifert $
 */
public class LogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        if (record.getThrown() == null) {
            return millisToTime(record.getMillis()) + " "
                    + record.getLoggerName() + "."
                    + record.getSourceMethodName() + ": "
                    + record.getMessage() + "\n";
        } else {
            return millisToTime(record.getMillis()) + " "
                    + record.getLoggerName() + "."
                    + record.getSourceMethodName() + ": "
                    + record.getMessage() + "\n"
                    + record.getThrown().getMessage() + "\n";
        }
    }

    private String millisToTime(long millis) {
        Date date = new Date(millis);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(date);
    }
}
