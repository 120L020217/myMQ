package testPublishSubscribe;

import publishSubscribe.subscriber;

/**
 * @author: coldcodacode
 * @description:
 * @date: 2023-04-16 09:19
 */
public class testsubscriber {
    public static void main(String[] args) {
        subscriber s1 = new subscriber("101");
        subscriber s2 = new subscriber("102");
        subscriber s3 = new subscriber("103");
        s1.subscribe("001");
        s2.subscribe("003");
        s3.subscribe("001");
        s1.getMsg();
        s2.getMsg();
        s3.getMsg();
    }
}
