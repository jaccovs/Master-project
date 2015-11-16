package org.exquisite.i8n.de;

import org.exquisite.i8n.CultureInfo;

/**
 * CultureInfo for German.
 *
 * @author David
 * @see org.exquisite.i8n.CultureInfo
 */
public class German extends CultureInfo {
    /**
     * Selected field values are overwritten with values appropriate for German language culture.
     */
    public German() {
        this.csvDelimiter = ";";
        this.paramDelimiter = this.csvDelimiter;
        this.decimalPoint = ",";
        this.sum = "SUMME";
        this.iff = "WENN";
        this.and = "UND";
        this.or = "ODER";
        this.not = "NICHT";
    }
}
