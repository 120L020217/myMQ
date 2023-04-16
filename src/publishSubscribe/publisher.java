package publishSubscribe;

import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Scanner;

public class publisher {
    private final String id;
    private int port;
    private String address;
    private String pubHead;

    public publisher(String id) {
        this.id = id;
        Properties pro = new Properties();
        try (FileInputStream fis = new FileInputStream("src/publishSubscribe/config.properties");
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
    }

    public void publish() {
        try (Socket socket = new Socket(address, port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());) {
            Scanner sc = new Scanner(System.in);
            StringBuilder res = new StringBuilder();
            while (true) {
                System.out.println("type in what you want to send");
                String text = sc.nextLine();
                if (text.equals("END")) break;
                res.append(text);
            }
            oos.writeObject(new Request(pubHead, res.toString(), id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publish(String res) {
        try (Socket socket = new Socket(address, port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());) {

            oos.writeObject(new Request(pubHead, res, id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("type in your publisher id:");
        Scanner sc = new Scanner(System.in);
        String id = sc.nextLine();
        publisher p = new publisher(id);
        p.publish();
    }
}
