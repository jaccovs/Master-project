package org.exquisite.diagnosis.quickxplain.choco3.choco2tochoco3;

public class OperatorTypeNotKnownException extends Choco2ToChoco3Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1440176565252073891L;

	public OperatorTypeNotKnownException(String message) {
		super("Operator type not known: " + message);
	}

}
