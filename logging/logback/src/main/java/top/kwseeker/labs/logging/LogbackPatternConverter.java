package top.kwseeker.labs.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * convert() 方法实现只是返回 "TID: N/A" ？
 * 那么实际使用时替换占位符 %tid 的值哪里来的？
 * 推测业务实际使用时可能 Skywalking JavaAgent 将这个 LogbackPatternConverter 类增强了，
 * 修改了 convert() 方法的实现
 */
public class LogbackPatternConverter extends ClassicConverter {

    /**
     * As default, return "TID: N/A" to the output message, if SkyWalking agent in active mode, return the real traceId
     * in the recent Context, if existed.
     *
     * @param iLoggingEvent the event
     * @return the traceId: N/A, empty String, or the real traceId.
     */
    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        return "TID: N/A";
    }
}
