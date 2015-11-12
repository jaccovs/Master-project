package tests.communication;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The server side
 * @author Dietmar
 *
 */
public class Server {

	
	public static int port = 81;
	
	public void run() throws Exception {
		System.out.println("Starting server ...");
		ServerSocket socket = null;
		try {			
			CalculationThread calculationThread = null;			
			socket = new ServerSocket(port); 
			// loop forever
			while (true) 
			{
				Socket connection = null;
				try {
					System.out.println("Waiting...");
					connection = socket.accept();
					System.out.println("Accepted something");
					// Read stuff
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	                OutputStream out = new BufferedOutputStream(connection.getOutputStream());
	                PrintStream pout = new PrintStream(out);
	                
	                // Read what comes in in plain text
	                System.out.println("About to read something");
	                String line = in.readLine();
	                System.out.println("Read line: " + line);
	                
	                String[] tokens = line.split(":");
	                
	                
	                if ("START".equalsIgnoreCase(tokens[0].trim())) {
	                	System.out.println("Start command");
	                	calculationThread = new CalculationThread();
	                	calculationThread.start();
	                }
	                else if ("STOP".equalsIgnoreCase(tokens[0].trim())) {
	                	System.out.println("Stop command");
	                	calculationThread.interrupt();
	                } 
	                // Sending an OK back.
	                pout.print("OK\n");
	                out.flush();
					
				} catch (Exception e) {
					System.err.print("Error in connection: " + e.getMessage());
					if (connection != null) {
						System.out.println("Closing connection");
						connection.close();
					}
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (socket != null) {
			socket.close();
		}
		
		System.out.println("-- Program terminated");
	}
	
	/**
	 * Starts the server and waits for connections
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new Server().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// An inner class
	class CalculationThread extends Thread {
		
		
		public void run() {
			int counter = 0;
			System.out.println("Calculation started");
			try {
				while (true) {
					counter++;
					Thread.sleep(1000);
					System.out.print(".");
					
					if (counter > 3) {
						System.out.println("Time's up..");
						break;
					}
				}
			} catch (Exception e) {
				System.out.println("Was interrupted. Will stop doing things..");
//				e.printStackTrace();
			}
		}
		
	}

}
