


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: coldcodacode
 * @description:
 * @date: 2023-04-16 09:19
 */
public class testPublisher {
    public static void main(String[] args) {
        // 定义线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // 并发执行任务
        for (int i = 0; i < 5; i++) {
            executorService.execute(new Publisher(Integer.toString(i), "tz cjm "+ i));
        }

        // 关闭线程池
        executorService.shutdown();
    }

//    public static void main(String[] args) {
//        for (int i = 0; i < 30; ++i){
//            new Thread(new publisher(Integer.toString(i), "111")).start();
//        }
//    }
}
