package org.exquisite.diagnosis.quickxplain.choco3.choco2tochoco3;

public class WrongVariableCountException extends Choco2ToChoco3Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7436500173135257347L;

	public WrongVariableCountException(String message) {
		super("Wrong number of variables: " + message);
	}

}
