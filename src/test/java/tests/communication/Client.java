package tests.communication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

	
	// Connection objects
	Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;
	
    
    /**
     * The main logic
     */
    public void run() throws Exception {
        
    	System.out.println(sendMessage("START : 1 2 3"));
    	System.out.println(sendMessage("STOP : it"));
    }
    
	/**
	 * The client
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting client");
        
		try {
			new Client().run(); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Client terminated");

	}
	
	/**
	 * Sends the message and returns the response from the stream
	 * Not very efficient as new objects are created each time
	 * @param msg
	 */
	String sendMessage(String msg) throws Exception  {
		socket = new Socket("localhost", Server.port);
		out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
        System.out.println("Sending message: " + msg);
        out.write(msg + "\n");
        out.flush();
        String response = in.readLine();
        System.out.println("Got response:" + response);
        
        in.close();
        out.close();

        return response;
        
	}

}
