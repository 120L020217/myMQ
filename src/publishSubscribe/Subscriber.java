package publishSubscribe;


import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

public class Subscriber {
    private String address;
    private int port;
    private String getHead;
    private String subHead;
    private String completeHead;
    private String wrongHead;
    private final String id;

    public Subscriber(String id) {
        Properties pro = new Properties();
        this.id = id;
        try (FileInputStream fis = new FileInputStream("src/publishSubscribe/config.properties")) {
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

    public void subscribe(){
        Scanner sc = new Scanner(System.in);
        String typeIn;
        while(true) {
            System.out.println("type in the publishers' id to subscribe publishers, enter \"END\" to exit");
            typeIn = sc.nextLine();
            if (typeIn.equals("END")) {
                break;
            }
            try (Socket s = new Socket(address, port);
                 ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());) {
                oos.writeObject(new Request(subHead, typeIn, id));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    public void subscribe(String typeIn){
//        try (Socket s = new Socket(address, port);
//             ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());) {
//            oos.writeObject(new Request(subHead, typeIn, id));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) {
        System.out.println("type in your subscriber id:");
        Scanner sc = new Scanner(System.in);
        String id = sc.nextLine();
        Subscriber s = new Subscriber(id);
        s.subscribe();
        s.getMsg();
    }

    public void getMsg() {
        try (Socket socket = new Socket(address, port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());) {
            oos.writeObject(new Request(getHead, "", id));
            while (true) {
                ObjectInputStream ooi = new ObjectInputStream(socket.getInputStream());
                Request req = (Request) ooi.readObject();
                if (Objects.equals(req.getHead(), wrongHead)) {
                    System.out.println(req.getBody());
                    break;
                } else if (Objects.equals(req.getHead(), completeHead)) {
                    System.out.println(">>> all messages have been gotten");
                    break;
                }
                System.out.println(req.getBody());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
