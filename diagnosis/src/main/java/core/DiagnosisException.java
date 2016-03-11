package core;

public class DiagnosisException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 2634454095876868966L;

    public DiagnosisException() {
        super("Error in tests.diagnosis process.");
    }

    //Constructor that accepts a message
    public DiagnosisException(String message) {
        super(message);
    }

    public DiagnosisException(Throwable cause) {
        super(cause);
    }

    public DiagnosisException(String message, Throwable cause) {
        super(message, cause);
    }

    public DiagnosisException(String message, Throwable cause,
                              boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
