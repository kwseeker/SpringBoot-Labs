package top.kwseeker.lab.redis.client.simple;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

class RedisClientTest {

    @Test
    public void testSendCommandAndReply() {
        RedisClient redisClient = new RedisClient("127.0.0.1", 6379);
        redisClient.set("keyA", "AA");
        redisClient.set("keyA", "AA");
        redisClient.set("keyA", "BB");
        String v = redisClient.get("keyA");
        System.out.println(v);

        redisClient.publish("topic-1", "Msg:" + System.currentTimeMillis());
    }
}