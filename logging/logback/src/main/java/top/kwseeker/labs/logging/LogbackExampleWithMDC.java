package top.kwseeker.labs.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Logback 中 MDC 的原理
 */
public class LogbackExampleWithMDC {

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(LogbackExample.class);
        MDC.put("ServiceId", "LogbackMDCExample");
        MDC.put("trace-id", "10001");
        // 最终效果：24-10-08.12:09:02.534 [main            ] INFO  LogbackExample         LogbackMDCExample-10001 info message with mdc ...
        log.info("info message with mdc ...");
    }
}
