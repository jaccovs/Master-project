package org.exquisite.protege.model.exception;

/**
 * Created by IntelliJ IDEA.
 * User: kostya
 * Date: 07.03.11
 * Time: 14:57
 * To change this template use File | Settings | File Templates.
 */
public class InconsistentTheoryException extends Exception {

    private static final long serialVersionUID = 3763099660981905812L;

    public InconsistentTheoryException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public InconsistentTheoryException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public InconsistentTheoryException(String message, Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public InconsistentTheoryException(Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
