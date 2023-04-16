package allBroadcast;

import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.util.Enumeration;
import java.util.Properties;


public class publisher {
    public void publish(String text){
        Properties pro = new Properties();
        try(FileInputStream fis = new FileInputStream("src/allBroadcast/config.properties");
            DatagramSocket ds = new DatagramSocket()) {
            pro.load(fis);
            Enumeration<?> enumeration = pro.propertyNames();
            InetAddress address = null;
            int port = 0;
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                if (key.equals("mqAddress")) {
                    address = InetAddress.getByName(pro.getProperty(key));
                } else if (key.equals("mqPort")) {
                    port = Integer.parseInt(pro.getProperty(key));
                }
            }
            byte[] bytes = text.getBytes();
            DatagramPacket dp = new DatagramPacket(bytes, bytes.length, address, port);
            ds.send(dp);
            System.out.println(">>>send successfully");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        publisher pub = new publisher();
        pub.publish("I have published a message.");
    }
}
