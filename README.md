# 软件架构实验一 —— 消息中间件



## 零、学到的内容

### the properties class

​		Properties类表示一组持久的属性。

​		属性可以保存到流中，也可以从流中加载。

​		属性列表中的每个键及其对应值都是一个String。属性列表可以包含另一个属性列表作为其“默认值”;如果在原始属性列表中没有找到属性键，则搜索第二个属性列表。

​		因为Properties继承自Hashtable，所以put和putAll方法可以应用于Properties对象。但强烈不鼓励使用它们，因为它们允许调用者插入键或值不是String的条目。应该使用setProperty方法。如果在包含非string键或值的“compromised”Properties对象上调用存储或保存方法，则调用将失败。类似地，如果对包含非string键的“compromised”Properties对象调用propertyNames或list方法，则会失败。

### 什么是java主类

​		java程序必须要有主类，主类是程序的入口，含有main方法的类就是主类，而不必要类名是main。

### Java中的try-with-resources

```
static String readFirstLineFromFile(String path) throws IOException {
    try (FileReader fr = new FileReader(path);
         BufferedReader br = new BufferedReader(fr)) {
        return br.readLine();
    }
}
```

​		在try后面的（）中定义实现closeable接口的对象，作为资源使用，通常是文件流对象，缓存流对象。无论代码块是否抛出异常，都会调用close方法关闭申请的资源。

### Java中的字节数组和字符数组的区别

​		Java中，字符数组和字节数组都是数组类型，但是它们的元素类型不同。字符数组的元素类型是char，而字节数组的元素类型是byte。char类型占用两个字节，而byte类型占用一个字节1。因此，字符数组和字节数组在内存中所占用的空间大小也不同。另外，字符数组和字节数组在使用时也有所不同。字符数组主要用于存储字符串，而字节数组主要用于存储二进制数据1。

### 计算机网络中的多播

​		多播组的主机必须处于同一个子网下。这是因为多播数据包只能在本地网络上转发，不能跨越路由器。多播组主机共享同一个ip（多播组的每个主机除共享ip外，还有一个唯一的私有IP），在以太网中，根据MAC地址区分同一个多播组的不同主机。

### Java中多线程与FutureTask的使用

​		FutureTask可用于异步获取执行结果或取消执行任务的场景。 通过传入Runnable或者Callable的任务给FutureTask，直接调用其run方法或者放入线程池执行，之后可以在外部通过FutureTask的get方法（当计算任务没有完成时会被阻塞）异步获取执行结果，因此，FutureTask非常适合用于耗时的计算，主线程可以在完成自己的任务后，再去获取结果。 另外，FutureTask还可以确保即使调用了多次run方法，它都只会执行一次Runnable或者Callable任务

[^FutureTask可以确保即使调用了多次run方法，它都只会执行一次Runnable或者Callable任务。这是因为FutureTask内部维护了一个volatile int state变量，用于表示当前FutureTask的状态。当FutureTask的状态为NEW时，调用run方法会将state从NEW变为COMPLETING，然后再执行任务。当任务执行完成后，会将state从COMPLETING变为NORMAL或EXCEPTIONAL，表示任务执行成功或失败。如果在任务执行完成之前再次调用run方法，由于state已经不是NEW了，所以不会再次执行任务]: 

，或者通过cancel取消FutureTask的执行等。

​		假设我们有一个计算斐波那契数列的任务，这个任务需要耗费很长时间。如果我们在主线程中执行这个任务，那么主线程就会被阻塞，直到这个任务执行完成。但是，如果我们使用FutureTask，就可以将这个任务放到另一个线程中执行，主线程可以继续执行自己的任务。等到需要获取计算结果时，再通过FutureTask的get方法获取结果即可。

​		下面是一个简单的示例代码，计算斐波那契数列。

```
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FutureTaskDemo {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int n = 10;
                int[] fib = new int[n + 1];
                fib[0] = 0;
                fib[1] = 1;
                for (int i = 2; i <= n; i++) {
                    fib[i] = fib[i - 1] + fib[i - 2];
                }
                return fib[n];
            }
        });
        new Thread(futureTask).start();
        System.out.println("计算斐波那契数列结果为：" + futureTask.get());
    }
}
```

### Java的Serializable接口

​		Serializable是Java中的一个接口，用于标识一个类可以被序列化。序列化是指将一个对象转换成字节流的过程，可以将对象存储到文件中或通过网络传输。实现Serializable接口的类可以被序列化和反序列化，即可以在网络上传输或者存储到本地磁盘上。

​		实现Serializable接口的类可以被ObjectOutputStream转换为字节流，同时也可以通过ObjectInputStream再将其解析为对象。例如，我们可以将序列化对象写入文件后，再次从文件中读取它并反序列化成对象，也就是说，可以使用表示对象及其数据的类型信息和字节在内存中重新创建对象。

​		需要注意的是，如果一个类实现了Serializable接口，则该类的所有子类都可以被序列化。

​		下面是一个通过网络传输字节流的例子。

```
import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 1234);

        // Send object over the network
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        Person person = new Person("John", 30);
        oos.writeObject(person);

        // Receive object from the network
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        Person deserializedPerson = (Person) ois.readObject();

        System.out.println(deserializedPerson.getName());
        System.out.println(deserializedPerson.getAge());

        ois.close();
        oos.close();
        socket.close();
    }
}

class Server {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(1234);
        Socket socket = serverSocket.accept();

        // Receive object from the network
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        Person deserializedPerson = (Person) ois.readObject();

        System.out.println(deserializedPerson.getName());
        System.out.println(deserializedPerson.getAge());

        // Send object over the network
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        Person person = new Person("Jane", 25);
        oos.writeObject(person);

        ois.close();
        oos.close();
        socket.close();
    }
}

class Person implements Serializable {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
```

### Java中类的静态字段和方法

​		类的静态字段和方法是存储在方法区中的，而不是存储在堆栈中的。方法区是JVM的一部分，它存储类的信息，包括类的名称、字段、方法、接口和常量池等。当我们使用类名来访问静态字段或方法时，JVM会在方法区中查找该字段或方法，并执行相应的操作。

​		JVM的方法区是在JVM启动时创建的，它是JVM的一部分，用于存储类的信息，包括类的名称、字段、方法、接口和常量池等。方法区是各个线程共享的内存区域，与Java堆一样，它也可以选择固定大小或者可扩展。方法区的大小决定了系统可以保存多少个类，如果系统定义了太多的类，导致方法区的溢出，虚拟机同样会抛出内存溢出错误：java.lang.OutOfMemoryError:PermGen space 或者 java.lang.OutOfMemoryError:Metaspace

​		所以在对一个类的静态字段赋值时，不需要实例化这个对象。

## 一、allBroadcast（UDP实现）

dispatcher（中间件）

​		分为两部分: receiver和dispatcher. 

​		其中，receivepart收到消息把消息传给消息队列;

​		dispatchpart当消息队列有task, 将消息发给组播地址(用到multicast socket).

subscriber（订阅者）

​		绑定多播地址，确定监听端口。

​		使用Multicast Socket()(只接受目的地址是设定好的组播地址的数据报)

publisher（发布者）

​		配置目的ip、port（即中间件的ip和port）

## 二、P2P（TCP实现）

queue（中间件）

​		使用TCP，中间件启用服务器socket，用来接受来自consumer和producer的字节流。每收到一个TCP链接请求，开启新线程，同时分配一个新的套接字。新线程根据收到报文段的类型（pubRequset和getRequest）采取相应动作：

- ​		pubRequse：传递发布者的发布信息。将该信息存入消息中间件的存储队列。
- ​		getRequest：传递订阅者的订阅请求。取消息队列的队首消息，发送给该订阅者。

consumer（订阅者）

​		创建socket，指明中间件（目的）的ip和port。

producer（发布者）

​		创建socket，指明中间件（目的）的ip和port。

## 三、订阅发布mode（有状态|TCP实现）

所谓有状态，只报文包含了订阅者和发布者的信息（程序中为subscriber|publisher ID ）

topic（中间件）

​		使用TCP，中间件启用服务器socket，用来接受来自subscriber和publisher的字节流。每收到一个TCP链接请求，开启新线程，同时分配一个新的套接字。新线程根据收到报文段的类型采取相应动作。较于模式二，特殊之处在于两个新的报文

- ​				wrongRequest：当没有订阅任何发布者or订阅的发布者没有任何消息是，向订阅者返回的报文类型
- ​                completeRequest：当完成发送所有订阅的发布者的消息时，向订阅者返回的报文类型

subscriber（订阅者）

​		创建socket，指明中间件（目的）的ip和port。

​		发送的报文：

- ​				subRequest：用来向中间件等级订阅的发布者。
- ​                getRequest：用来向中间件请求订阅的发布者的消息。

publisher（发布者）

​		创建socket，指明中间件（目的）的ip和port。

​		发送的报文：

- ​                 pubRequest：用来向中间件发布消息。

