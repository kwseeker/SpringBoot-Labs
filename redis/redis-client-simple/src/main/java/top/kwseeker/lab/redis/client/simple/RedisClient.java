package top.kwseeker.lab.redis.client.simple;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RedisClient {

    private final String host;
    private final int port;

    public RedisClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String set(String key, String value) {
        try (Socket socket = new Socket(this.host, this.port);
             InputStream is = socket.getInputStream();
             OutputStream os = socket.getOutputStream()) {
            RedisProtocol.sendCommand(os, RedisProtocol.Command.SET, key, value);
            return getReply(is);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String get(String key) {
        try (Socket socket = new Socket(this.host, this.port);
             InputStream is = socket.getInputStream();
             OutputStream os = socket.getOutputStream()) {
            RedisProtocol.sendCommand(os, RedisProtocol.Command.GET, key);
            return getReply(is);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String publish(String topic, String message) {
        try (Socket socket = new Socket(this.host, this.port);
             InputStream is = socket.getInputStream();
             OutputStream os = socket.getOutputStream()) {
            RedisProtocol.sendCommand(os, RedisProtocol.Command.PUBLISH, topic, message);
            return getReply(is);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String getReply(InputStream is) {
        try {
            StringBuilder sb = new StringBuilder();
            while (is.available() <= 0) {//等待响应
                Thread.yield();
            }

            while (is.available() > 0) {
                int c = is.read();
                sb.append((char) c);
            }
            String reply = sb.toString();
            System.out.println("ACK: " + Coder.convertRespCommandData(reply));
            return reply;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}