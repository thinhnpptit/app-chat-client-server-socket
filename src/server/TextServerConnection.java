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
public class TextServerConnection implements Runnable {

    Socket socket;
    TextServer server;
    DataInputStream din;
    DataOutputStream dout;
    boolean shouldRun = true;

    public TextServerConnection(TextServer server, Socket socket) {
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
                String process[] = textIn.split("\\$");
                String type = process[0];
                String msg = process[1];
                if (type.equals("newuser")) {   //Chuỗi đến: newuser$a - chuỗi đi: newusser$a@b@c@
                    server.connections.put(msg, this);  //Thêm vào danh sách các client
                    sendNewUserList();
                } else if (type.equals("close")) { //Chuỗi đến: close$a - chuỗi đi: close$a#b@c@
                    close_sendUserList(msg, textIn);

                } else if (type.equals("text")) {   //Chuỗi đến: text$s@r@m@ - chuỗi đi: text$m
                    sendTextMessage(msg);

                }
            }

        } catch (IOException ex) {
            ex.getStackTrace();

        }
    }
// Gửi chuỗi đến sender và recipient
    private void sendStringToOneClient(String message, String recipient, String sender) {
        for (Map.Entry<String, TextServerConnection> client : server.connections.entrySet()) {
            if (client.getKey().equals(recipient) || client.getKey().equals(sender)) {
                client.getValue().sendStringToClient(message);
            }

        }
    }
// Gửi chuỗi đến tất cả client
    private void sendStringToAllClients(String textIn) {
        for (Map.Entry<String, TextServerConnection> client : server.connections.entrySet()) {
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
//Gửi chuỗi chức năng thêm user     
    private void sendNewUserList() {
        String clients = "newuser$";
//        Thêm tên các client là key lưu trong map
        for (Map.Entry<String, TextServerConnection> client : server.connections.entrySet()) {
            clients += client.getKey() + "@";
        }
        sendStringToAllClients(clients);    //chuỗi gửi đi có dạng newuser$a@b@c@
    }
//Gửi chuỗi chức năng close
    private void close_sendUserList(String msg, String textIn) {
        String clients = textIn + "#";
        String toRemove = "";   //Tên client cần xóa
        for (Map.Entry<String, TextServerConnection> client : server.connections.entrySet()) {
            if (client.getKey().equals(msg)) {
                toRemove = client.getKey();
            } else {
                clients += client.getKey() + "@";
            }
        }
        server.connections.remove(toRemove);    // xóa tên client ở map
        sendStringToAllClients(clients);    //chuỗi gửi cí dạng
    }
//Gửi chuỗi chức năng nhắn tin
    private void sendTextMessage(String msg) {
        String[] split = msg.split("@");
        String sender = split[0];
        String recipient = split[1];
        String message = split[2];
        if (recipient.equals("Everyone")) {
            for (Map.Entry<String, TextServerConnection> client : server.connections.entrySet()) {
                System.out.println(client.getKey());
            }
            sendStringToAllClients("text$" + message);
        } else {
            sendStringToOneClient("text$" + message, recipient, sender);
        }
    }
}
