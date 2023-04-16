package P2P;


import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Scanner;

public class consumer {
    private InetAddress address;
    private int port;
    private String getHead;

    public consumer() {
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
                    case "getHead":
                        getHead = pro.getProperty(key);
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void consume(){
        Scanner sc = new Scanner(System.in);
        String typeIn;
        while(true){
            System.out.println("type in '1' to fetch message, others to exit");
            typeIn = sc.nextLine();
            if (!typeIn.equals("1")) {break;}
            try(Socket s = new Socket(address, port)){
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(new Request(getHead, ""));

                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                Request res = (Request) ois.readObject();
                System.out.println(res.getBody());
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        consumer c = new consumer();
        c.consume();
    }
}
