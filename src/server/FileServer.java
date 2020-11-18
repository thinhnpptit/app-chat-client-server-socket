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
public class FileServer implements Runnable {

    private ServerSocket ss;
    private static final int PORT_NUMBER = 6666;
//    private int serverPort;
    Map<String, FileServerConnection> connections = new HashMap<>();
    boolean shouldRun = true;

    public Map<String, FileServerConnection> getConnections() {
        return connections;
    }

    public FileServer() {

    }

    @Override
    public void run() {
        try {
            ss = new ServerSocket(PORT_NUMBER);
            System.out.println("server starts port = " + ss.getLocalSocketAddress());
            while (shouldRun) {     // while(true)           
                Socket socket = ss.accept();    //socket la client
                System.out.println("accepts : " + socket.getRemoteSocketAddress());
                FileServerConnection sc = new FileServerConnection(this, socket);
                Thread thread = new Thread(sc);
                thread.start();

            }
        } catch (IOException ex) {
            System.err.println("Could not listen on port: " + PORT_NUMBER);
            System.exit(1);
        }
    }
}
