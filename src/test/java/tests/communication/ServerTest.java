package tests.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.exquisite.communication.messages.ClientMessages;
import org.exquisite.data.ExampleTestData;

/**
 * NOTE: this won't work as I had to change the input and output stream types in the 
 * server in order to get the server to respond to requests coming from the client
 * written in c#.
 * 
 * @author David
 *
 */
public class ServerTest {

	// Connection objects
	Socket socket = null;
	ObjectOutputStream out = null;
    ObjectInputStream in = null;	
    
    public ServerTest()
    {
    	try {
			socket = new Socket("localhost", Server.port);
			out = new ObjectOutputStream(socket.getOutputStream());
	        in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
    }
    
    /**
     * The main logic
     */
    public void run() throws Exception 
    {
		sendMessage(ClientMessages.POST_MODEL);
    	sendMessage(ExampleTestData.MAPPING_TEST_XML);
    	sendMessage(ClientMessages.REQUEST_DIAGNOSIS);
    	sendMessage(ClientMessages.REQUEST_DIAGNOSIS_RESULT);
    }
    
	/**
	 * The client
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting client");
        
		try {
			
			new ServerTest().run(); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("ServerTest terminated");
	}
	
	/**
	 * Sends the message and returns the response from the stream
	 * @param msg
	 */
	String sendMessage(String msg) throws Exception  {
		
        System.out.println("Sending message: " + msg);
        out.writeObject(msg);
        out.flush();
        try{
        	String response = (String) in.readObject();
        	System.out.println("Got response:" + response);
            return response;
        }
        catch(IOException e)
        {
        	System.out.println("Exception caught... reader is closed");
        	e.printStackTrace();
        	return null;
        } 
	}
}
