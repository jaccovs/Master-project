package org.exquisite.core;

/**
 * @author wolfi
 */
public class DiagnosisRuntimeException extends RuntimeException{

    /**
     *
     */
    private static final long serialVersionUID = 2634452344956868966L;

    public DiagnosisRuntimeException() {
        super("Error in tests.diagnosis process.");
    }

    //Constructor that accepts a message
    public DiagnosisRuntimeException(String message) {
        super(message);
    }

    public DiagnosisRuntimeException(Throwable cause) {
        super(cause);
    }

    public DiagnosisRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DiagnosisRuntimeException(String message, Throwable cause,
                              boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
