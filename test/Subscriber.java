

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Subscriber implements Runnable{
    private String address;
    private int port;
    private String getHead;
    private String subHead;
    private String completeHead;
    private String wrongHead;
    private final String id;

    private final String sub_id;

    public Subscriber(String id, String sub_id) {
        Properties pro = new Properties();
        this.id = id;
        this.sub_id = sub_id;
        try (FileInputStream fis = new FileInputStream("test/config.properties")) {
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
                    case "getHead":
                        getHead = pro.getProperty(key);
                        break;
                    case "subHead":
                        subHead = pro.getProperty(key);
                    case "completeHead":
                        completeHead = pro.getProperty(key);
                    case "wrongHead":
                        wrongHead = pro.getProperty(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try (Socket s = new Socket(address, port);
             ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());) {
            oos.writeObject(new Request(subHead, sub_id, id));
            oos.close();
        } catch (Exception e) {

            e.printStackTrace();
        }

        try (Socket socket = new Socket(address, port);
             ObjectOutputStream oos1 = new ObjectOutputStream(socket.getOutputStream());) {
            oos1.writeObject(new Request(getHead, "", id));
//            sleep(1000);
            while (true) {
                ObjectInputStream ooi = new ObjectInputStream(socket.getInputStream());
                Request req = (Request) ooi.readObject();
                if (Objects.equals(req.getHead(), wrongHead)) {
                    System.out.println(req.getBody());
                    break;
                } else if (Objects.equals(req.getHead(), completeHead)) {
                    System.out.println(req.getBody());
                    break;
                }
                else{
                    System.out.println(req.getBody());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
