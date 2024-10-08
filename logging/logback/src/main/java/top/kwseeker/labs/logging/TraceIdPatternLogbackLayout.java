package top.kwseeker.labs.logging;

import ch.qos.logback.classic.PatternLayout;

public class TraceIdPatternLogbackLayout extends PatternLayout {

    static {
        defaultConverterMap.put("tid", LogbackPatternConverter.class.getName());
        //defaultConverterMap.put("sw_ctx", LogbackSkyWalkingContextPatternConverter.class.getName());
    }
}
