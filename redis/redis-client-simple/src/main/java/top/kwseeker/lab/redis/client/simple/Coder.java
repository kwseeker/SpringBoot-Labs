package top.kwseeker.lab.redis.client.simple;

import java.io.UnsupportedEncodingException;

public class Coder {

    public static byte[] encode(final String str) {
        try {
            if (str == null) {
                throw new IllegalArgumentException("value sent to redis cannot be null");
            }
            return str.getBytes(RedisProtocol.CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decode(final byte[] data) {
        try {
            return new String(data, RedisProtocol.CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertRespCommandData(String cmdData) {
        StringBuilder respSb = new StringBuilder();
        for (byte cByte : cmdData.getBytes()) {
            if (cByte == 10) {
                respSb.append("\\n");   //为了打印原始字符串，对特殊字符加转义, RESP协议也只有回车换行这两种特殊字符
            } else if (cByte == 13) {
                respSb.append("\\r");
            } else {
                respSb.append((char)cByte);
            }
        }
        return respSb.toString();
    }
}