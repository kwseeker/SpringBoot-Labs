package top.kwseeker.labs.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackExample {

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(LogbackExample.class);
        log.trace("trace message ...");
        log.debug("debug message ...");
        log.info("info message ...");
        log.info("info message with params: {}", "hello");
        log.error("error message ...");
        try {
            throw new RuntimeException("some exception occurred");
        } catch (RuntimeException e) {
            log.error("error message ...", e);
        }
    }
}
