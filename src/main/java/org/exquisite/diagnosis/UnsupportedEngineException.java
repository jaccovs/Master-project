package org.exquisite.diagnosis;

public class UnsupportedEngineException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4896664756700556232L;

	public UnsupportedEngineException(){
		super("The tests.diagnosis engine described is not currently supported.");
	}
		
    //Constructor that accepts a message
    public UnsupportedEngineException(String message)
    {
       super(message);
    }
}
