package org.exquisite.communication;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;

import org.exquisite.communication.messages.ClientMessages;
import org.exquisite.communication.protocol.ServerProtocol;
import org.exquisite.datamodel.ExquisiteEnums.StatusCodes;
import org.exquisite.datamodel.ExquisiteMessage;

/**
 * A thread for handling requests from a client.<p>
 * 
 * This thread is instantiated when the <tt>Server</tt> has accepted a connection from a client.
 * The <tt>Socket</tt> object returned from <tt>Server</tt> when it accepted the connection is passed
 * to this thread.<p>
 * Inside Run, a <tt>ServerProtocol</tt> object is instantiated that contains the logic
 * for how to respond to particular messages from the client.
 * Messages from the client are then routed to the <tt>ServerProtocol</tt> object and conversely
 * the responses from <tt>ServerProtocol</tt> are then sent back over the socket connection to
 * the client.
 * 
 * @author David
 * @see org.exquisite.communication.Server
 * @see org.exquisite.protocol.ServerProtocol
 *
 */
public class DiagnosisServerThread extends Thread 
{
	/**
	 * Symbol that delimits messages being sent from/to <tt>Server</tt>.
	 */
	public static final String MESSAGE_DELIMITER = "\n";
	
	/**
	 * The socket created by the server when it accepted the client connection request.
	 */
	private Socket socket = null;
	
	/**
	 * @param socket    the socket created by <tt>Server</tt> when it accepted the connection from the client.
	 */
	public DiagnosisServerThread(Socket socket){
	    super("DiagnosisServerThread");
	    this.socket = socket;
    }
	
	/**
	 * Set up the <tt>PrintStream</tt>, reads requests from a client and posts results back.</p>
	 * Closes the socket when requested by the client, when client issues a DISCONNECT request, goes offline or some other error.
	 */
    public void run(){	 
	    try {		        
	    	OutputStream out = new BufferedOutputStream(socket.getOutputStream());
	    	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    	PrintStream pout = new PrintStream(out);				 
	        
	    	String clientMessage; 
	        ExquisiteMessage result;
	        ServerProtocol protocol = new ServerProtocol();
	       		        
	        try{
		        while ((clientMessage = in.readLine()) != null) 
				{  
					//Give ServerProtocol the message received from the client.
		        	result = protocol.processClientInput(clientMessage); 
					//Send the output from ServerProtocol back to the client.
		        	printToStream(result, pout);
		        	
					//If the client has sent a Disconnect request then close connection.
					if (clientMessage.equalsIgnoreCase(ClientMessages.DISCONNECT)){
						System.out.println("Client has requested to disconnect.");
						ExquisiteMessage response = new ExquisiteMessage();
						response.status = StatusCodes.OK;
						printToStream(response, pout);
						out.close();
				    	in.close();
						socket.close();	
						break;
				    }				
				}	
	        }
	        catch (SocketException e) {
	        	System.out.println("Client is offline.");
	        	out.close();
	        	in.close();
	        	socket.close();
	        }
	    } 
	    catch (IOException e) {
	        e.printStackTrace();
	    }	    
    }
    
    //Send a message back to the client over the given PrintStream
  	private void printToStream(ExquisiteMessage response, PrintStream pout){
  		if (pout != null){			
  			String msg = response.toXML();
  			pout.println(msg + MESSAGE_DELIMITER);
  			pout.flush();
  		}
  	}
}