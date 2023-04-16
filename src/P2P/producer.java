package P2P;

import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Scanner;

public class producer {
    private int port;
    private InetAddress address;
    private String pubHead;

    public producer() {
        Properties pro = new Properties();
        try (FileInputStream fis = new FileInputStream("src/P2P/config.properties")) {
            pro.load(fis);
            Enumeration<?> enumeration = pro.propertyNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                switch (key) {
                    case "port":
                        port = Integer.parseInt(pro.getProperty(key));
                        break;
                    case "address":
                        address = InetAddress.getByName(pro.getProperty(key));
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

    public void produce() {
        try (Socket socket = new Socket(address, port)) {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            Scanner sc = new Scanner(System.in);
            StringBuilder res = new StringBuilder();
            while (true) {
                System.out.println("type in what you want to send, enter End \"END\"");
                String text = sc.nextLine();
                if (text.equals("END")) break;
                res.append(text);
            }
            oos.writeObject(new Request(pubHead,res.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        producer p = new producer();
        p.produce();
    }
}
