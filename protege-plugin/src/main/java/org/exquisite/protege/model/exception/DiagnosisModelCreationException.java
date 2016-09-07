package org.exquisite.protege.model.exception;

/**
 * An exception type that might occur when creating a new diagnosis model.
 */
public class DiagnosisModelCreationException extends Exception {

    public DiagnosisModelCreationException() {
        super();
    }

    public DiagnosisModelCreationException(String message) {
        super(message);
    }

    public DiagnosisModelCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DiagnosisModelCreationException(Throwable cause) {
        super(cause);
    }

    protected DiagnosisModelCreationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
