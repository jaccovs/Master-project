package org.exquisite.i8n;

/**
 * Base culture info class. For mapping operator and function names to different cultures.
 *
 * @author David
 */
public class CultureInfo {

    //operators
    protected String plus = "+";
    protected String minus = "-";
    protected String multiply = "*";
    protected String divide = "/";

    //excel functions
    protected String paramDelimiter;
    protected String sum;
    protected String min = "MIN";
    protected String quotient = "QUOTIENT";

    //excel language constructs
    protected String iff;

    //excel logical operations
    protected String and;
    protected String or;
    protected String not;

    //number formatting
    protected String decimalPoint;

    //utils
    protected String csvDelimiter;

    //getters
    public String PLUS() {
        return this.plus;
    }

    public String MINUS() {
        return this.minus;
    }

    public String MULTIPLY() {
        return this.multiply;
    }

    public String DIVIDE() {
        return this.divide;
    }

    //functions
    public String SUM() {
        return this.sum;
    }

    public String MIN() {
        return this.min;
    }

    public String QUOTIENT() {
        return this.quotient;
    }

    //language constructs
    public String IF() {
        return this.iff;
    }

    //logical operations
    public String AND() {
        return this.and;
    }

    public String OR() {
        return this.or;
    }

    public String NOT() {
        return this.not;
    }

    //misc.
    public String DECIMAL_POINT() {
        return this.decimalPoint;
    }

    public String PARAM_DELIMITER() {
        return this.paramDelimiter;
    }

    //utils
    public String CSV_DELIMITER() {
        return this.csvDelimiter;
    }
}
