package top.kwseeker.lab.redisson.pubsub;

import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.config.Config;
import top.kwseeker.lab.redisson.RedisLocalDockerTest;

public class RedissonTopicTest extends RedisLocalDockerTest {

    @Test
    public void testTopicPubSub() throws InterruptedException {
        Config config = createConfig();
        config.useSingleServer()
                .setSubscriptionConnectionPoolSize(1)
                .setSubscriptionConnectionMinimumIdleSize(1)
                .setSubscriptionsPerConnection(1)
                .setAddress(redisson.getConfig().useSingleServer().getAddress());
        RedissonClient redisson  = Redisson.create(config);

        RTopic topic = redisson.getTopic("topic-1");

        topic.addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence charSequence, String s) {
                System.out.println("Received message: " + s);
            }
        });

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                topic.publish("Msg:" + i);
            }
        });
        thread1.start();

        thread1.join();
        Thread.sleep(100);
    }
}
