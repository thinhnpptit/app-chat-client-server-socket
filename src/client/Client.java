package client;

/**
 *
 * @author MyPC
 */
public class Client {

    private String userName;    /*tên client*/
    TextConnection tc;
    FileConnection fc;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setConnections(ClientFrame cf) {
//        chạy cac thread text và file
        tc = new TextConnection(cf, this);
//        fc = new FileConnection(cf, this);
        Thread textThread = new Thread(tc);
        textThread.start();
//        Thread fileThread = new Thread(fc);
//        fileThread.start();

    }

    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        //gọi đến giao diện login
        Login login = new Login(this);
        login.setVisible(true);
    }
}
