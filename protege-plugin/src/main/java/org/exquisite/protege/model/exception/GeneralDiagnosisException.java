package org.exquisite.protege.model.exception;

/**
 * Created by IntelliJ IDEA.
 * User: kostya
 * Date: 13.07.11
 * Time: 17:19
 * To change this template use File | Settings | File Templates.
 */
public class GeneralDiagnosisException extends Exception {

    public GeneralDiagnosisException(String arg0) {
        super(arg0);
    }

    public GeneralDiagnosisException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public GeneralDiagnosisException(Throwable arg0) {
        super(arg0);
    }

    public GeneralDiagnosisException() {
        super();
    }
}
