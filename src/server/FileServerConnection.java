package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

/**
 *
 * @author MyPC
 */
public class FileServerConnection implements Runnable {

    Socket socket;
    FileServer server;
    DataInputStream din;
    DataOutputStream dout;
    boolean shouldRun = true;

    public FileServerConnection(FileServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {

            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
            while (!socket.isClosed()) {
                while (din.available() == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        ex.getStackTrace();
                    }
                }
                String textIn = din.readUTF();
//                System.out.println(textIn);
//                read byte[] from client and send byte[] to others
                byte[] fileContent = new byte[1024];
                for (int n = din.read(fileContent); n > 0; n = din.read(fileContent)) {
                    dout.write(fileContent, 0, n);
                    dout.flush();
                }

                String process[] = textIn.split("\\$");
                String type = process[0];
                String msg = process[1];
                if (type.equals("newuser")) {
                    server.connections.put(msg, this);
//                    sendNewUserList();
                } else if (type.equals("close")) {
                    close_sendUserList(msg, textIn);

                } else if (type.equals("file")) {
//                    sendTextMessage(msg);
//                    them code file vao day(maybe)
                    sendFileMessage(msg);
                }
            }

        } catch (IOException ex) {
            ex.getStackTrace();

        }
    }

    private void sendStringToOneClient(String message, String recipient, String sender) {
        for (Map.Entry<String, FileServerConnection> client : server.connections.entrySet()) {
            if (client.getKey().equals(recipient) || client.getKey().equals(sender)) {
                client.getValue().sendStringToClient(message);
            }

        }
    }

    private void sendStringToAllClients(String textIn) {
        for (Map.Entry<String, FileServerConnection> client : server.connections.entrySet()) {
//            System.out.println(client.getKey());
            client.getValue().sendStringToClient(textIn);
        }
    }

    private void sendStringToClient(String text) {
        try {
            dout.writeUTF(text);
            dout.flush();
        } catch (IOException ex) {
            ex.getStackTrace();
        }
    }

    private void sendNewUserList() {
        String clients = "newuser$";
        for (Map.Entry<String, FileServerConnection> client : server.connections.entrySet()) {
//                        System.out.println(client.getKey() + " " + client.getValue());
            clients += client.getKey() + "@";
        }
//        System.out.println(clients);
        sendStringToAllClients(clients);
    }

    private void close_sendUserList(String msg, String textIn) {
        String clients = textIn + "#";
        String toRemove = "";
        for (Map.Entry<String, FileServerConnection> client : server.connections.entrySet()) {
            if (client.getKey().equals(msg)) {
                toRemove = client.getKey();
            } else {
                clients += client.getKey() + "@";
            }
        }
        server.connections.remove(toRemove);
//                    System.out.println(clients);
//        sendStringToAllClients(clients);
    }

    private void sendFileMessage(String msg) {
        String[] split = msg.split("@");
        String sender = split[0];
//                    System.out.println(sender);

        String recipient = split[1];
//                    System.out.println(recipient);
        String message = split[2];
//                    System.out.println(message);
        if (recipient.equals("Everyone")) {

            for (Map.Entry<String, FileServerConnection> client : server.connections.entrySet()) {
                System.out.println(client.getKey());
            }
            sendStringToAllClients("file$" + message);
        } else {
            sendStringToOneClient("file$" + message, recipient, sender);
        }
    }
}
