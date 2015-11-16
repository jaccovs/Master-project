package org.exquisite.communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Basic multi-threaded server implementation, for making calls to tests.diagnosis engine over sockets...
 */
public class Server {
    /**
     * The port this <tt>Server</tt> will listen on.<p>
     * <em>Currently set to 81.</em>
     */
    public static int port = 81;

    /**
     * Starts the server and listens for connections.<p>
     * <p>
     * When a client request comes in <tt>Server</tt> accepts the connection and
     * creates a new <tt>DiagnosisServerThread</tt> object to process it.
     * The server hands to the thread the socket returned from the accept then
     * starts the thread. Then the server goes back to listening for more
     * connection requests.
     *
     * @see org.exquisite.communication.DiagnosisServerThread
     */
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        boolean listening = true;

        System.out.println("Starting server ...");
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port + ".");
            System.exit(-1);
        }

        while (listening) {
            try {
                System.out.println("Waiting...");
                Socket socket = serverSocket.accept();
                System.out.println("Accepted something");
                new DiagnosisServerThread(socket).start();
            } catch (Exception e) {
                System.err.print("Error in connection: " + e.getMessage());
                System.exit(-1);
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("-- Program terminated");
    }
}