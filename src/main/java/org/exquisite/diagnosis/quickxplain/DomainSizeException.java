package org.exquisite.diagnosis.quickxplain;

import org.exquisite.diagnosis.DiagnosisException;

/**
 * A custom exception for handling errors originating from Choco solver for situations such
 * as out of memory or if a value is not within the domain of the target decision variable.
 * @author David
 *
 */
public class DomainSizeException extends DiagnosisException {

	/**
	 * auto generated serialVersionUID
	 */
	private static final long serialVersionUID = 1230276008172119077L;

	public DomainSizeException() {
		super("The solver has thrown an out of memory exception, likely caused by trying to read a model with a set of domains to vast for the memory resources available.");
	}

	public DomainSizeException(String message) {
		super(message);
	}

	public DomainSizeException(Throwable cause) {
		super(cause);
	}

	public DomainSizeException(String message, Throwable cause) {
		super(message, cause);
	}

	public DomainSizeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
