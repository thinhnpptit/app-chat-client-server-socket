package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author MyPC
 */
public class FileConnection implements Runnable {

    private Client client;
    private Socket socket;
    private static final String HOST = "localhost";
    private static final int PORT_NUMBER = 6666;
    private DataInputStream din;
    private DataOutputStream dout;
    private final ClientFrame GUI;
    JFileChooser fc = new JFileChooser();
    String fileName="";
    
    FileConnection(ClientFrame cf, Client c) {
        client = c;
        try {
            socket = new Socket(HOST, PORT_NUMBER);
        } catch (IOException ex) {

        }
        GUI = cf;
        fc.showOpenDialog(cf);
    }

    public void sendStringToServer(String text) {
        try {
            System.out.println("send " + text);
            dout.writeUTF(text);
            dout.flush();
        } catch (IOException ex) {
            ex.getStackTrace();
            close();
        }
    }

    public void sendFileToServer(File file) {
        try {
            fileName = file.getName();
            byte[] fileContent = Files.readAllBytes(file.toPath());
            System.out.println("FileBytes: " + fileContent);
            dout.write(fileContent, 0, fileContent.length);
            dout.flush();
        } catch (IOException ex) {
            Logger.getLogger(FileConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
            sendStringToServer("newuser$" + client.getUserName());
            while (!socket.isClosed()) {
                while (din.available() == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        ex.getStackTrace();
                        close();
                    }
                }
//                Đọc tin nhắn từ server của cac client 
                if (din.available() > 0) {
                    String msgFromServer = din.readUTF();
                    String[] process = msgFromServer.split("\\$");
                    String type = process[0];
                    String msg = process[1];
                    // read byte[] from server and Download File
                    OutputStream out = new FileOutputStream("D:\\"+fileName);
                    try {
                        byte buf[] = new byte[4096];
                        for (int n = din.read(buf); n > 0; n = din.read(buf)) {
                            out.write(buf, 0, n);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(FileConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (type.equals("newuser")) {
                        processUpdateUser(msg);
                    } else if (type.equals("close")) {
                        processClose_UpdateUser(msg);
                    } else if (type.equals("file")) {
                        displayOnChatbox(msg);
//                      them code file o day
                    }

                }

            }
        } catch (IOException ex) {
            ex.getStackTrace();
            close();
        }
    }

    public void processUpdateUser(String msg) {
        String selectedItem = getSelectedItem();
        clearDisplayUsers();
        String[] listUser = msg.split("@");
        for (String user : listUser) {
            refreshUser(user);
        }
        setSelectedItem(selectedItem);
    }

    public void processClose_UpdateUser(String msg) {
        String[] split = msg.split("#");
        String closedUser = split[0];
        String msg2 = split[1];
        if (client.getUserName().equals(closedUser)) {
            close();
        }
        String selectedItem = getSelectedItem();
        if ((selectedItem.equals(closedUser))) {
            selectedItem = "Everyone";
        }
        clearDisplayUsers();
        String[] listUser = msg2.split("@");
        for (String user : listUser) {
            refreshUser(user);
        }
        setSelectedItem(selectedItem);
    }

    public void close() {
        try {
            din.close();
            dout.close();
        } catch (IOException ex) {
            ex.getStackTrace();
        }
    }

    public String getSelectedItem() {
        return GUI.getSelectedItem();
    }

    public void setSelectedItem(String item) {
        GUI.setSelectedItem(item);
    }

    public void clearDisplayUsers() {
        GUI.clearUser();
    }

    public void refreshUser(String user) {
        if (!user.equals(client.getUserName())) {
            GUI.refreshComboUser(user);
        }
        GUI.refreshListUser(user);
    }

    public void displayOnChatbox(String text) {
        System.out.println(text);
        GUI.getChatBox().append(text);
    }

    public Socket getSocket() {
        return socket;
    }

}
