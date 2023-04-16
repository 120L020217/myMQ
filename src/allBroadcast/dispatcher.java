package allBroadcast;

import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


public class dispatcher {
    private final Queue<String> infos = new LinkedList<>();
    private InetAddress mqAddress;
    private InetAddress multicastAddress;
    private int mqPort;
    private int multicastPort;

    public dispatcher(){
        Properties pro = new Properties();
        try(FileInputStream fis = new FileInputStream("src/allBroadcast/config.properties")) {
            pro.load(fis);
            Enumeration<?> enumeration = pro.propertyNames();

            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                switch (key) {
                    case "mqAddress":
                        mqAddress = InetAddress.getByName(pro.getProperty(key));
                        break;
                    case "mqPort":
                        mqPort = Integer.parseInt(pro.getProperty(key));
                        break;
                    case "multicastAddress":
                        multicastAddress = InetAddress.getByName(pro.getProperty(key));
                        break;
                    case "multicastPort":
                        multicastPort = Integer.parseInt(pro.getProperty(key));
                        break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void start(){
        receivePart rp = new receivePart(mqPort);
        while (true){
            FutureTask<String> ft = new FutureTask<>(rp);
            new Thread(ft).start();
            try{
                infos.add(ft.get());
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (!infos.isEmpty()){
                System.out.println("#####################");
                dispatchPart dp = new dispatchPart(multicastAddress, multicastPort, infos.poll());
                new Thread(dp).start();
            }
        }
    }

    public static void main(String[] args) {
        dispatcher dis = new dispatcher();
        dis.start();
    }
}

class receivePart implements Callable<String>{
    private final int port;
    public receivePart(int port) {
        this.port = port;
    }

    @Override
    public String call() throws Exception {
        try (DatagramSocket ds = new DatagramSocket(port)){
            byte[] buff = new byte[1024];
            DatagramPacket dp = new DatagramPacket(buff, buff.length);
            ds.receive(dp);
            byte[] data = dp.getData();
            String res = new String(data, 0, dp.getLength());
            System.out.println("the message received: " + res);
            return res;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}

class dispatchPart implements Runnable{
    private final InetAddress address;
    private final int port;
    private final String msg;

    dispatchPart(InetAddress address, int port, String msg) {
        this.address = address;
        this.port = port;
        this.msg = msg;
    }

    @Override
    public void run() {
        try(MulticastSocket ms = new MulticastSocket()){
            byte[] bytes = msg.getBytes();
            DatagramPacket dp = new DatagramPacket(bytes, bytes.length, address, port);
            ms.send(dp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}