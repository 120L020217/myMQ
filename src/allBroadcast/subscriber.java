package allBroadcast;

import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Enumeration;
import java.util.Properties;


public class subscriber {
    public void subscribe(){
        Properties pro = new Properties();
        try(FileInputStream fis = new FileInputStream("src/allBroadcast/config.properties")
            ) {
            pro.load(fis);
            Enumeration<?> enumeration = pro.propertyNames();
            InetAddress address = null;
            int port = 0;
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                if (key.equals("multicastAddress")) {
                    address = InetAddress.getByName(pro.getProperty(key));
                } else if (key.equals("multicastPort")) {
                    port = Integer.parseInt(pro.getProperty(key));
                }
            }
            try(MulticastSocket ms = new MulticastSocket(port)){
                ms.joinGroup(address);
                byte[] buff = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buff, buff.length);
                ms.receive(dp);
                System.out.println(">>>receive successfully");
                System.out.println(new String(buff, 0, dp.getLength()));
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        subscriber sub = new subscriber();
        sub.subscribe();
    }
}
