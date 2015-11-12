package org.exquisite.diagnosis.quickxplain.choco3.choco2tochoco3;

public class ConstraintTypeNotKnownException extends Choco2ToChoco3Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1707702435636373312L;
	
	public ConstraintTypeNotKnownException(String message) {
		super("Constraint type not known: " + message);
	}

}
