package top.kwseeker.springboot.lab.ribbon.loadbalancer;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class URLConnectionLoadBalancerExampleTest {

    @Test
    public void testUrl() throws IOException {
        URL url = new URL("http://www.baidu.com:80");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        int responseCode = conn.getResponseCode();
        System.out.println(responseCode);
        System.out.println(conn.getResponseMessage());
        assertEquals(200, responseCode);
    }
}