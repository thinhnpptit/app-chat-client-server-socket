package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author MyPC
 */
public class TextServer implements Runnable {

    private ServerSocket ss;
    private static final int PORT_NUMBER = 8080;
//    private int serverPort;
    Map<String, TextServerConnection> connections = new HashMap<>();    // Map<tên client, thread>
    boolean shouldRun = true;

    public Map<String, TextServerConnection> getConnections() {
        return connections;
    }

    public TextServer() {

    }

    @Override
    public void run() {
        try {
            ss = new ServerSocket(PORT_NUMBER);
            System.out.println("server starts port = " + ss.getLocalSocketAddress());
            while (shouldRun) {     // while(true)           
                Socket socket = ss.accept();    //socket la client
                System.out.println("accepts : " + socket.getRemoteSocketAddress());
                TextServerConnection sc = new TextServerConnection(this, socket);
                Thread thread = new Thread(sc);
                thread.start();

            }
        } catch (IOException ex) {
            System.err.println("Could not listen on port: " + PORT_NUMBER);
            System.exit(1);
        }
    }
}
