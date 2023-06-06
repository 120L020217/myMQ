

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

/**
 * @author: coldcodacode
 * @description:
 * @date: 2023-06-06 17:20
 */
public class testSubscriber {
    public static void main(String[] args) throws InterruptedException {
        // 定义线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // 并发执行任务
        for (int i = 0; i < 5; i++) {
            executorService.execute(new Subscriber(Integer.toString(i+100), Integer.toString(i)));
        }

        // 关闭线程池
        executorService.shutdown();

        sleep(1000);
        storage.printPubMsgs();
    }
}
