package server;

/**
 *
 * @author MyPC
 */
public class Server {
    
    public static void main(String[] args) {
       TextServer ts = new TextServer();
       Thread textThread = new Thread(ts);
       textThread.start();
       FileServer fs = new FileServer();
       Thread fileThread = new Thread(fs);
       fileThread.start();
    }
}
