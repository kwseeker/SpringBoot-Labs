package top.kwseeker.lab.redis.client.simple;

import java.io.IOException;
import java.io.OutputStream;

public class RedisProtocol {

    public static final String CHARSET = "UTF-8";
    public static final char DOLLAR_BYTE = '$';
    public static final char ASTERISK_BYTE = '*';
    public static final char PLUS_BYTE = '+';
    public static final char MINUS_BYTE = '-';
    public static final char COLON_BYTE = ':';
    public static final char CR_BYTE = '\r';
    public static final char LF_BYTE = '\n';

    /**
     * *3
     * $3
     * SET
     * $4
     * toby
     * $2
     * xu
     */
    public static void sendCommand(final OutputStream os, final Command command, final String... args) {
        try {
            StringBuilder sb = new StringBuilder()
                    .append(ASTERISK_BYTE)
                    .append(args.length + 1)
                    .append(CR_BYTE)
                    .append(LF_BYTE)
                    .append(DOLLAR_BYTE)
                    .append(command.name().length())
                    .append(CR_BYTE)
                    .append(LF_BYTE)
                    .append(command.name())
                    .append(CR_BYTE)
                    .append(LF_BYTE);
            for (String arg : args) {
                sb.append(DOLLAR_BYTE)
                        .append(arg.length())
                        .append(CR_BYTE)
                        .append(LF_BYTE)
                        .append(arg)
                        .append(CR_BYTE)
                        .append(LF_BYTE);
            }
            String cmdData = sb.toString();
            String respCommandData = Coder.convertRespCommandData(cmdData);
            System.out.println("CMD: " + respCommandData);
            os.write(Coder.encode(sb.toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public enum Command {
        SET,
        GET,
        PUBLISH,
    }

}