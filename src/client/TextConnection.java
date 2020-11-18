package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author MyPC
 */
public class TextConnection implements Runnable {

    private Client client;
    private Socket socket;
    private static final String HOST = "localhost";
    private static final int PORT_NUMBER = 8080;
    private DataInputStream din;
    private DataOutputStream dout;

    private final ClientFrame GUI;

    TextConnection(ClientFrame cf, Client c) {
        /*Khởi tạo socket*/
        client = c;
        try {
            socket = new Socket(HOST, PORT_NUMBER);
        } catch (IOException ex) {

        }
        GUI = cf;
    }

    public void sendStringToServer(String text) {   /*Gửi chuỗi đến server*/
        try {
            dout.writeUTF(text);
            dout.flush();
        } catch (IOException ex) {
            ex.getStackTrace();
            close();
        }
    }

    @Override
    public void run() {
        try {
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
            /*Gửi chuỗi newuser*/
            sendStringToServer("newuser$" + client.getUserName());
            /*Tạm nghỉ thread khi luồng rảnh*/
            while (!socket.isClosed()) {
                while (din.available() == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        ex.getStackTrace();
                        close();
                    }
                }
//                Đọc chuỗi từ server 
                if (din.available() > 0) {
                /*Tách chuỗi vd: newuser$user@user@ -> type: newuser, msg: user@user@*/
                    String msgFromServer = din.readUTF();
                    String[] process = msgFromServer.split("\\$");
                    String type = process[0];
                    String msg = process[1];
                    if (type.equals("newuser")) {
                        processUpdateUser(msg);
                    } else if (type.equals("close")) {
                        processClose_UpdateUser(msg);
                    } else if (type.equals("text")) {
                        displayOnChatbox(msg);
                    }

                }

            }
        } catch (IOException ex) {
            ex.getStackTrace();
            close();
        }
    }
/*Cập nhật danh sách user*/
    public void processUpdateUser(String msg) {
        String selectedItem = getSelectedItem();    //Lưu selecteditem
        clearDisplayUsers();        // xóa dnah sách hiện tại 
        String[] listUser = msg.split("@");     // tách msg theo ký tự @ để ra danh sách user
        for (String user : listUser) {
            refreshUser(user);
        }
        setSelectedItem(selectedItem);
    }
//Đóng thread và cập nhật danh sách user
    public void processClose_UpdateUser(String msg) {
        String[] split = msg.split("#");
        String closedUser = split[0];
        String msg2 = split[1];
        if (client.getUserName().equals(closedUser)) {  // nếu trùng closedUser: ngắt kết nối
            close();
        }
        String selectedItem = getSelectedItem();
        if ((selectedItem.equals(closedUser))) {    // Nếu selecteditem trùng với clesedUser
            selectedItem = "Everyone";              //chuyển thành everyone
        }
        clearDisplayUsers();
        String[] listUser = msg2.split("@");
        for (String user : listUser) {
            refreshUser(user);
        }
        setSelectedItem(selectedItem);
    }
// Ngắt kết nối
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
// Cập nhật danh sách user trên giao diện(list và combobox)
    public void refreshUser(String user) {
        if (!user.equals(client.getUserName())) {
            GUI.refreshComboUser(user);
        }
        GUI.refreshListUser(user);
    }
// Hiển thị tin nhắn lên chatbox
    public void displayOnChatbox(String text) {
        System.out.println(text);
        GUI.getChatBox().append(text);
    }

    public Socket getSocket() {
        return socket;
    }

}
