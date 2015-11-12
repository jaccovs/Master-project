package org.exquisite.communication.messages;

/**
 * A collection of messages that are sent from the Server to the Client.
 */	
public class ServerMessages{		
	
	/**
	 * If client tries to retrieve a tests.diagnosis before running the tests.diagnosis engine.
	 */
	public final static String RESPONSE_DIAGNOSIS_NOT_RUN = "DIAGNOSIS_NOT_RUN";
	
	/**
	 * Send this when the tests.diagnosis engine has finished.
	 */
	public final static String RESPONSE_DIAGNOSIS_READY = "DIAGNOSIS_READY";
	
	/**
	 * Sent when cancellation of tests.diagnosis process has finished - sometimes the cancellation takes a few  seconds
	 */
	public final static String RESPONSE_CANCELLATION_FINISHED = "CANCELLED_FINISHED";
	
	/**
	 * Sent when server is going to be shut down.
	 */
	public final static String RESPONSE_SHUTTING_DOWN = "SHUTTING_DOWN_SERVER";
	
}
