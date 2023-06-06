

import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Properties;

public class Publisher implements Runnable{
    private final String id;
    private int port;
    private String address;
    private String pubHead;

    private String msg;

    public Publisher(String id, String msg) {
        this.id = id;
        Properties pro = new Properties();
        try (FileInputStream fis = new FileInputStream("test/config.properties");
             DatagramSocket ds = new DatagramSocket();) {
            pro.load(fis);
            Enumeration<?> enumeration = pro.propertyNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                switch (key) {
                    case "port":
                        port = Integer.parseInt(pro.getProperty(key));
                        break;
                    case "address":
                        address = pro.getProperty(key);
                        break;
                    case "pubHead":
                        pubHead = pro.getProperty(key);
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.msg = msg;

    }
    @Override
    public void run() {
        try (Socket socket = new Socket(address, port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());) {

            oos.writeObject(new Request(pubHead, msg, id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
