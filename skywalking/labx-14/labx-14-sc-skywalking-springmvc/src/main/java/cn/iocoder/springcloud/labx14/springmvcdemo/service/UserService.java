package cn.iocoder.springcloud.labx14.springmvcdemo.service;

import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Trace
    public String getUser(Integer id) {
        String traceId = TraceContext.traceId();
        System.out.println("traceId = " + traceId);
        return "user:" + id;
    }
}
