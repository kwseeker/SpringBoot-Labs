package top.kwseeker.labs.micrometer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MetricsTest {

    @Test
    public void testSimpleRegistry() {
        //SimpleMeterRegistry
        SimpleMeterRegistry simpleMeterRegistry = new SimpleMeterRegistry();
        //第一个参数作为指标名、后面的作为Tag
        Counter counter1 = simpleMeterRegistry.counter("some-service.http.requests", "/login", "POST");
        //模拟业务中统计
        counter1.increment();
        //数据上报时模拟读取
        double count = counter1.count();
        System.out.println(count);
        Assertions.assertEquals(count, 1.0);
    }

    @Test
    public void testCompositeRegistry() {
        //CompositeMeterRegistry 是 MeterRegistry 的聚合实现，可以组合多个 MeterRegistry
        CompositeMeterRegistry compositeMeterRegistry = new CompositeMeterRegistry();
        SimpleMeterRegistry simpleMeterRegistry = new SimpleMeterRegistry();
        compositeMeterRegistry.add(simpleMeterRegistry);

        Counter counter1 = compositeMeterRegistry.counter("counter_1");
        //模拟业务中统计
        counter1.increment();
        //数据上报时模拟读取
        double count = counter1.count();
        System.out.println(count);
        Assertions.assertEquals(count, 1.0);
    }

    @Test
    public void testMetricTypeTimer() {
        SimpleMeterRegistry simpleMeterRegistry = new SimpleMeterRegistry();
        //第一个参数作为指标名、后面的作为Tag
        Timer timer = simpleMeterRegistry.timer("some-service.http.requests", "/login", "POST");

        //模拟业务中统计
        //timer.record(200, TimeUnit.MILLISECONDS);
        timer.record(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        timer.record(50, TimeUnit.MILLISECONDS);
        timer.record(300, TimeUnit.MILLISECONDS);

        //数据上报时模拟读取
        double count = timer.count();
        Assertions.assertEquals(count, 3);
        double totalTime = timer.totalTime(TimeUnit.MILLISECONDS);
        System.out.println(totalTime);
        double max = timer.max(TimeUnit.MILLISECONDS);
        System.out.println(max);
    }

    /**
     * Timer 时间窗最大值
     */
    @Test
    public void testMetricTypeTimer_TimeWindowMax() throws InterruptedException {
        SimpleMeterRegistry simpleMeterRegistry = new SimpleMeterRegistry();
        Timer timer = simpleMeterRegistry.timer("some-service.http.requests", "/login", "POST");

        int[] apiCosts = new int[]{200, 100, 50};
        AtomicInteger i = new AtomicInteger(0);
        ScheduledExecutorService es = Executors.newScheduledThreadPool(1);
        es.scheduleAtFixedRate(() -> {
            if (i.get() >= apiCosts.length) {
                return;
            }
            timer.record(apiCosts[i.getAndIncrement()], TimeUnit.MILLISECONDS);
        }, 0, 60, TimeUnit.SECONDS);

        es.scheduleAtFixedRate(() -> {
            double apiCostMax = timer.max(TimeUnit.MILLISECONDS);
            System.out.println(apiCostMax);
        }, 30, 60, TimeUnit.SECONDS);

        boolean done = es.awaitTermination(1000, TimeUnit.SECONDS);
    }

    /**
     * Timer 直方图（累积直方图、百分位直方图）
     */
    @Test
    public void testMetricTypeTimer_Histogram() throws InterruptedException {
        SimpleMeterRegistry simpleMeterRegistry = new SimpleMeterRegistry();
        Timer timer = Timer.builder("some-service.http.requests")
                .tags("/login", "POST")
                .publishPercentileHistogram()   //启用百分位直方图统计
                .publishPercentiles(0.5, 0.9, 0.99)
                .percentilePrecision(3)   //默认是1，值越大精度越高(+1是精度提升10倍)，内存消耗越大
                // 累积直方图的配置，设置分级
                .serviceLevelObjectives(Duration.ofMillis(100), Duration.ofMillis(300), Duration.ofMillis(500))
                .minimumExpectedValue(Duration.ofMillis(1))
                .maximumExpectedValue(Duration.ofMillis(1000))
                .register(simpleMeterRegistry);

        timer.record(20, TimeUnit.MILLISECONDS);
        timer.record(60, TimeUnit.MILLISECONDS);
        timer.record(80, TimeUnit.MILLISECONDS);
        timer.record(90, TimeUnit.MILLISECONDS);
        timer.record(100, TimeUnit.MILLISECONDS);
        timer.record(200, TimeUnit.MILLISECONDS);
        timer.record(250, TimeUnit.MILLISECONDS);
        timer.record(280, TimeUnit.MILLISECONDS);
        timer.record(500, TimeUnit.MILLISECONDS);
        timer.record(550, TimeUnit.MILLISECONDS);

        HistogramSnapshot histogramSnapshot = timer.takeSnapshot();
        //ValueAtPercentile[] valueAtPercentiles = histogramSnapshot.percentileValues();
        //for (ValueAtPercentile valueAtPercentile : valueAtPercentiles) {
        //    System.out.println(valueAtPercentile.percentile() + ":" + valueAtPercentile.value(TimeUnit.MILLISECONDS));
        //}
    }
}
