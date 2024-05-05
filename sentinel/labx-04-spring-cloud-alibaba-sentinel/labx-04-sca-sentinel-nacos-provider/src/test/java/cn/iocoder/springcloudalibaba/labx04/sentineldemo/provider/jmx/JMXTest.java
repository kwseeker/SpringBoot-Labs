package cn.iocoder.springcloudalibaba.labx04.sentineldemo.provider.jmx;

import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.sun.management.OperatingSystemMXBean;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JMXTest {

    //系统最近一分钟的平均负载，(可用处理器的任务队列中排队的任务实体数量+可用处理器正在运行的任务实体数量)/时间周期
    volatile double currentLoad = -1;
    //当前CPU负载(取系统统计的CPU负载和计算出来的进程的CPU负载的较大值)
    volatile double currentCpuUsage = -1;

    //用于下次对比
    volatile long processCpuTime = 0;
    volatile long processUpTime = 0;

    /**
     * currentLoad= 2.43896484375, currentCpuUsage=0.24752475247524752
     * currentLoad= 2.43896484375, currentCpuUsage=0.6922077922077923
     * currentLoad= 2.43896484375, currentCpuUsage=0.43198992443324935
     * currentLoad= 2.43896484375, currentCpuUsage=0.3372395833333333
     * currentLoad= 2.43896484375, currentCpuUsage=0.22685788787483702
     * currentLoad= 2.24365234375, currentCpuUsage=0.1888466413181242
     */
    @Test
    public void testJMX() throws InterruptedException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1,
                new NamedThreadFactory("calculateSystemStatusTask", false));
        scheduler.scheduleAtFixedRate(this::task, 0, 1, TimeUnit.SECONDS);
        Thread.sleep(5000);
    }

    private void task() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        //1 系统最近一分钟的平均负载，(可用处理器的任务队列中排队的任务实体数量+可用处理器正在运行的任务实体数量)/时间周期
        currentLoad = osBean.getSystemLoadAverage();

        //2 当前CPU负载
        double systemCpuUsage = osBean.getSystemCpuLoad();
        //计算进程为支持在容器环境中运行应用程序对 CPU 的使用率
        RuntimeMXBean runtimeBean = ManagementFactory.getPlatformMXBean(RuntimeMXBean.class);
        //返回运行 Java 虚拟机的进程使用的 CPU 时间（以纳秒为单位）
        long newProcessCpuTime = osBean.getProcessCpuTime();
        //返回 Java 虚拟机的正常运行时间（以毫秒为单位）
        long newProcessUpTime = runtimeBean.getUptime();
        //可用处理器核心数
        int cpuCores = osBean.getAvailableProcessors();
        //和上一秒进程使用CPU时间的差值（即这两次任务执行间隔内进程使用CPU时间）
        long processCpuTimeDiffInMs = TimeUnit.NANOSECONDS
                .toMillis(newProcessCpuTime - processCpuTime);
        //和上一秒 Java虚拟机运行时间的差值（即这两次任务执行间隔内虚拟机运行时间）
        long processUpTimeDiffInMs = newProcessUpTime - processUpTime;
        double processCpuUsage = (double) processCpuTimeDiffInMs / processUpTimeDiffInMs / cpuCores;
        //记录一下用于下次对比
        processCpuTime = newProcessCpuTime;
        processUpTime = newProcessUpTime;
        currentCpuUsage = Math.max(processCpuUsage, systemCpuUsage);

        System.out.println("currentLoad= " + currentLoad + ", currentCpuUsage=" + currentCpuUsage);
    }
}
