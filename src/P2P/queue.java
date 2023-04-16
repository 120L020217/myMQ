package P2P;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

public class queue {
    private int port;
    private String pubHead;
    private String getHead;

    public queue() {
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
                    case "pubHead":
                        pubHead = pro.getProperty(key);
                        break;
                    case "getHead":
                        getHead = pro.getProperty(key);
                        break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start(){
        try(ServerSocket ss = new ServerSocket(port)){
            while (true){
                Socket s = ss.accept();
                Process p = new Process(s, pubHead, getHead);
                new Thread(p).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        queue q = new queue();
        q.start();
    }
}

class Process implements Runnable{
    private final Socket s;
    private final String pubHead;
    private final String getHead;

    public Process(Socket s, String pubHead, String getHead){
        this.s = s;
        this.pubHead = pubHead;
        this.getHead = getHead;
    }

    @Override
    public void run() {
        try(ObjectInputStream ois = new ObjectInputStream(s.getInputStream())){
            Request r = (Request) ois.readObject();
            if (r.getHead().equals(pubHead)){
                System.out.println(">>> Successfully receive Publish request");
                storage.addMessage(r);
            }else if (r.getHead().equals(getHead)){
                System.out.println(">>> Successfully receive Get request");
                Request res = storage.getMessage();
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                if(res != null){
                    oos.writeObject(res);
                    System.out.println("Successfully send back");
                }else{
                    oos.writeObject(new Request(getHead, "there are no messages to return"));
                    System.out.println("there are no messages to return");
                }
                oos.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            s.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class storage {
    public static Queue<Request> msg = new LinkedList<>();

    public static Request getMessage() {
        return msg.poll();
    }

    public static void addMessage(Request req) {
        msg.add(req);
    }
}