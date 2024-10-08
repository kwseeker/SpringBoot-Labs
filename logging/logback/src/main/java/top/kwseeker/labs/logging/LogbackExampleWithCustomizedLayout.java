package top.kwseeker.labs.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Logback 中自定义 Layout
 * <p>
 * 通过VM选项指定使用的配置文件：
 *  -Dlogback.configurationFile=logback-layout.xml
 */
public class LogbackExampleWithCustomizedLayout {

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(LogbackExampleWithCustomizedLayout.class);
        MDC.put("ServiceId", "xxx");
        log.info("info message ...");
    }
}
