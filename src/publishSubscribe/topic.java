package publishSubscribe;


import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class topic {
    private int port;
    private String pubHead;
    private String getHead;
    private String subHead;
    private String completeHead;
    private String wrongHead;
    private int updateTime;

    public topic() {
        Properties pro = new Properties();
        try (FileInputStream fis = new FileInputStream("src/publishSubscribe/config.properties")) {
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
                    case "subHead":
                        subHead = pro.getProperty(key);
                        break;
                    case "wrongHead":
                        wrongHead = pro.getProperty(key);
                        break;
                    case "completeHead":
                        completeHead = pro.getProperty(key);
                        break;
                    case "updateTime":
                        updateTime = Integer.parseInt(pro.getProperty(key));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try (ServerSocket ss = new ServerSocket(port);) {
            while (true) {
                Socket socket = ss.accept();
                topicProcess p = new topicProcess(socket, pubHead, getHead, subHead, completeHead,
                        wrongHead, updateTime);
                new Thread(p).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        topic t = new topic();
        t.start();
    }
}

class topicProcess implements Runnable{
    private final Socket socket;
    private final String pubHead;
    private final String getHead;
    private final String subHead;
    private final String completeHead;
    private final String wrongHead;
    private final int updateTime;

    public topicProcess(Socket socket, String pubHead, String getHead, String subHead, String completeHead,
                        String wrongHead, int updateTime) {
        this.socket = socket;
        this.pubHead = pubHead;
        this.getHead = getHead;
        this.completeHead = completeHead;
        this.wrongHead = wrongHead;
        this.subHead = subHead;
        this.updateTime = updateTime;
    }

    @Override
    public void run() {
        storage.update();
        try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());){
            Request req = (Request) ois.readObject();
            if (req.getHead().equals(pubHead)) {
                System.out.println(">>> Successfully receive publish request");
                storage.addMessage(req);
            } else if (req.getHead().equals(getHead)) {
                System.out.println(">>> Successfully receive get request");
                List<Request> res = storage.getMessage(req.getSource());
//                System.out.println(res);
                if (res == null) {
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(new Request(wrongHead, "you should subscribe first", "topic"));
                } else if (res.isEmpty()) {
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(new Request(wrongHead, "there are no messages from whom you subscribe", "topic"));
                } else {
                    for (Request re : res) {
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(re);
                    }
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(new Request(completeHead, "", "topic"));
                }
            } else if (req.getHead().equals(subHead)) {
                storage.accessSub(req.getSource(), req.getBody());
                System.out.println(">>> Successfully receive subscribe request");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

class storage{
    private static Map<String, List<String>> subLists = new HashMap<>();
    private static Map<String, List<Request>> pubMsgs = new HashMap<>();
    private static Lock lock = new ReentrantLock();

    public static void addMessage(Request request) {
        lock.lock();
        if (pubMsgs.containsKey(request.getSource())) {
            pubMsgs.get(request.getSource()).add(request);
        } else {
            List<Request> list = new ArrayList<>();
            list.add(request);
            pubMsgs.put(request.getSource(), list);
        }
        lock.unlock();
    }

    public static List<Request> getMessage(String source) {
        lock.lock();
        List<Request> result = new ArrayList<>();
        if (subLists.containsKey(source)) {
            List<String> list = subLists.get(source);
            for (String s : list) {
                if (!pubMsgs.containsKey(s)) continue;
                for (Request req : pubMsgs.get(s)) {
                    result.add(new Request(req.getHead(), req.getBody(), req.getSource()));
                }
            }
        } else {
            lock.unlock();
            return null; // wrong request
        }
        lock.unlock();
        return result; // if empty --> wrong request
    }

    public static void accessSub(String source, String destination) {
        lock.lock();
        if (subLists.containsKey(source)) {
            subLists.get(source).add(destination);
        } else {
            List<String> list = new ArrayList<>();
            list.add(destination);
            subLists.put(source, list);
        }
        lock.unlock();
    }

    /**
     * if the existence exceed 10s, then remove
     */
    public static void update() {
        lock.lock();
        Set<String> keys = pubMsgs.keySet();
        for (String key : keys) {
            List<Request> list = pubMsgs.get(key);
            Iterator<Request> iterator = list.listIterator();
            while (iterator.hasNext()) {
                Date now = new Date();
                Date date = iterator.next().getDate();
                if ((now.getTime() - date.getTime()) / 1000 > 10) {
                    iterator.remove();
                }
            }
        }
        lock.unlock();
    }
}
