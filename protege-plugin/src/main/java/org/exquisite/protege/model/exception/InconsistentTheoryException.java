package org.exquisite.protege.model.exception;

public class InconsistentTheoryException extends Exception {

    private static final long serialVersionUID = 3763099660981905812L;

    public InconsistentTheoryException() {
        super();
    }

    public InconsistentTheoryException(String message) {
        super(message);
    }

    public InconsistentTheoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public InconsistentTheoryException(Throwable cause) {
        super(cause);
    }
}
