package top.kwseeker.labs.prometheus.client01;

import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.exporter.httpserver.HTTPServer;
import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;

import java.io.IOException;

public class Main {
    // 配置了两组监控，启动HTTPServer提供了一个监控接口
    public static void main(String[] args) throws InterruptedException, IOException {
        // 这个是 prometheus-metrics-instrumentation-jvm 中的类
        JvmMetrics.builder().register();

        // 还定义了 Counter 类型的监控指标
        Counter counter = Counter.builder()
                .name("my_count_total")
                .help("example counter")
                .labelNames("status")
                .register();
        // ok标签值设置为2
        counter.labelValues("ok").inc();
        counter.labelValues("ok").inc();
        // error标签值设置为1
        counter.labelValues("error").inc();
        // 通过上面配置写死的监控数据如下
        // # HELP my_count_total example counter
        // # TYPE my_count_total counter
        // my_count_total{status="error"} 1.0
        // my_count_total{status="ok"} 2.0

        // 启动 HTTPServe, 监听端口 9400, 默认的监控接口是 /metrics
        HTTPServer server = HTTPServer.builder()
                .port(9400)
                .buildAndStart();

        // Prometheus 监控任务配置
        // scrape_configs:
        //  - job_name: "java-example"
        //    static_configs:
        //      - targets: ["localhost:9400"]
        System.out.println("HTTPServer listening on port http://localhost:" + server.getPort() + "/metrics");
        Thread.currentThread().join(); // sleep forever

        // 请求 http://localhost:9400/metrics 可以看到 jvm 以及 my_count_total 的统计数据
    }
}
