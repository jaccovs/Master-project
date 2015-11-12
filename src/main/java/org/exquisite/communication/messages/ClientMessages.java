package org.exquisite.communication.messages;

/**
 * A collection of messages sent from a client.
 */
public class ClientMessages{	
	/**
	 * Client requests to sends its model data to the tests.diagnosis service.
	 */
	public static final String POST_MODEL = "POST_MODEL";
	
	/**
	 * Perform a tests.diagnosis on the model.
	 */
	public static final String REQUEST_DIAGNOSIS = "REQUEST_DIAGNOSIS";
	
	/**
	 * Client requests the formulas to query for correctness
	 */
	public static final String REQUEST_FORMULAS_TO_QUERY = "REQUEST_FORMULAS_TO_QUERY";
	
	/**
	 * Request the output of the last tests.diagnosis.
	 */
	public static final String REQUEST_DIAGNOSIS_RESULT = "REQUEST_DIAGNOSIS_RESULT";
	
	/**
	 * Request the fragmentation of the spreadsheet. 
	 */
	public static final String REQUEST_FRAGMENTATION = "REQUEST_FRAGMENTATION";
	
	/**
	 * Request the fragmentation of the spreadsheet. 
	 */
	public static final String REQUEST_FRAGMENT_OF_CELLS = "REQUEST_FRAGMENT_OF_CELLS";
	
	/**
	 * Requests the server to close the socket connection.
	 */
	public static final String DISCONNECT = "DISCONNECT";
	
	/**
	 * Quits what server is currently doing and returns its state to idle.
	 */
	public static final String CANCEL_REQUEST = "CANCEL_REQUEST";
	
	/**
	 * Terminates the tests.diagnosis service.
	 */
	public static final String SHUT_DOWN = "SHUT_DOWN";
}
