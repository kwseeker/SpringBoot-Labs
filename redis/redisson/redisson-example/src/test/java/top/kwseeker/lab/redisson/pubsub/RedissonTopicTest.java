package top.kwseeker.lab.redisson.pubsub;

import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.config.Config;
import org.redisson.config.Protocol;
import top.kwseeker.lab.redisson.RedisLocalDockerTest;

public class RedissonTopicTest {

    @Test
    public void testTopicPubSub() throws InterruptedException {
        Config config = new Config()
                .setProtocol(Protocol.RESP2)
                .setNettyThreads(2);    //默认Netty工作者线程数量是32,不方便调试
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:" + "6379")
                .setSubscriptionConnectionPoolSize(1)   //每个节点订阅连接池大小，默认24,这里设置为了到每个节点最多只有一个发布订阅连接
                .setSubscriptionConnectionMinimumIdleSize(1)
                .setSubscriptionsPerConnection(1);       //每个连接最多订阅次数，默认5
        RedissonClient redisson  = Redisson.create(config);

        RTopic topic = redisson.getTopic("topic-1");

        topic.addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence charSequence, String s) {
                System.out.println("Received message: " + s);
            }
        });

        Thread thread1 = new Thread(() -> {
            //for (int i = 0; i < 3; i++) {
            //    topic.publish("Msg:" + i);
            //}
            topic.publish("Msg:xxx");
        });
        thread1.start();

        thread1.join();
        Thread.sleep(100);
    }
}
