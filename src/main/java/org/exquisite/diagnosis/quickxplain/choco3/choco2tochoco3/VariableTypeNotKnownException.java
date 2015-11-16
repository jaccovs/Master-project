package org.exquisite.diagnosis.quickxplain.choco3.choco2tochoco3;

public class VariableTypeNotKnownException extends Choco2ToChoco3Exception {

    /**
     *
     */
    private static final long serialVersionUID = -8775527701271907580L;

    public VariableTypeNotKnownException(String message) {
        super("Variable type not known: " + message);
    }

}
