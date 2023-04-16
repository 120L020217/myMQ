package testPublishSubscribe;

import publishSubscribe.publisher;

/**
 * @author: coldcodacode
 * @description:
 * @date: 2023-04-16 09:19
 */
public class testpublisher {
    public static void main(String[] args) {
        publisher p1 = new publisher("001");
        publisher p2 = new publisher("002");
        p1.publish("p1 has published a message");
        p2.publish("p2 has published a message");
    }
}
