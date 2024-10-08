package top.kwseeker.labs.prometheus.client01;

import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Gauge;
import io.prometheus.metrics.core.metrics.Histogram;
import io.prometheus.metrics.core.metrics.Summary;
import io.prometheus.metrics.expositionformats.ExpositionFormatWriter;
import io.prometheus.metrics.expositionformats.OpenMetricsTextFormatWriter;
import io.prometheus.metrics.expositionformats.PrometheusProtobufWriter;
import io.prometheus.metrics.expositionformats.PrometheusTextFormatWriter;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MetricTypeTest {

    @Test
    void test_counter() throws IOException {
        Counter counter = Counter.builder()
                .name("test_counts_total")
                .help("Total number of test counts")
                .labelNames("method", "path")
                //.labelNames("requests", "requests_total")
                .build();
        PrometheusRegistry.defaultRegistry.register(counter);

        // 模拟业务计数, 内部会创建 CounterDataPoint 容器，并 inc()
        counter.labelValues("GET", "/").inc(1);
        counter.labelValues("POST", "/").inc(2);
        counter.labelValues("ALL", "/").inc(3);
        // 模拟采集数据
        CounterSnapshot snapshot = counter.collect();

        printToStandardOutput(FormatWriterType.PROMETHEUS, snapshot);
        printToStandardOutput(FormatWriterType.OPEN_METRICS, snapshot);
        //printToStandardOutput(FormatWriterType.PROMETHEUS_PROTOBUF, snapshot);    //输出到标准输出是乱码

        //# HELP test_counts_total Total number of test counts
        //# TYPE test_counts_total counter
        //test_counts_total{method="ALL",path="/"} 3.0
        //test_counts_total{method="GET",path="/"} 1.0
        //test_counts_total{method="POST",path="/"} 2.0

        //# TYPE test_counts counter
        //# HELP test_counts Total number of test counts
        //test_counts_total{method="ALL",path="/"} 3.0
        //test_counts_total{method="GET",path="/"} 1.0
        //test_counts_total{method="POST",path="/"} 2.0
        //# EOF
    }

    @Test
    void test_gauge() throws IOException {
        Gauge gauge = Gauge.builder()
                .name("test_cpu_usage")
                .help("Usage rate of cpu")
                .labelNames("core")
                .unit(Unit.RATIO)       //比如： test_cpu_usage_rate_ratio{core="cpu0"} 0.3
                .build();
        PrometheusRegistry.defaultRegistry.register(gauge);

        // 模拟业务读取
        gauge.labelValues("cpu0").set(0.5);
        gauge.labelValues("cpu1").set(0.2);
        // 模拟采集数据
        GaugeSnapshot snapshot = gauge.collect();
        // snapshot = {GaugeSnapshot@1876}
        // metadata = {MetricMetadata@1845}
        //  name = "test_cpu_usage_rate"
        //  prometheusName = "test_cpu_usage_rate"
        //  help = "Usage rate of cpu"
        //  unit = null
        // dataPoints = {Collections$UnmodifiableRandomAccessList@1878}  size = 2
        //  0 = {GaugeSnapshot$GaugeDataPointSnapshot@1880}
        //   value = 0.5
        //   exemplar = null
        //   labels = {Labels@1884} "{core="cpu0"}"
        //   createdTimestampMillis = 0
        //   scrapeTimestampMillis = 0
        //  1 = {GaugeSnapshot$GaugeDataPointSnapshot@1881}
        gauge.labelValues("cpu0").set(0.3);
        gauge.labelValues("cpu1").set(0.4);
        // 模拟采集数据
        snapshot = gauge.collect();

        printToStandardOutput(FormatWriterType.PROMETHEUS, snapshot);
        //# HELP test_cpu_usage_ratio Usage rate of cpu
        //# TYPE test_cpu_usage_ratio gauge
        //test_cpu_usage_ratio{core="cpu0"} 0.3
        //test_cpu_usage_ratio{core="cpu1"} 0.4
    }

    @Test
    void test_histogram() throws IOException {
        Histogram histogram = Histogram.builder()
                .name("test_cpu_temperature")
                .help("Temperature of cpu")
                .unit(Unit.CELSIUS) //摄氏度
                .classicLinearUpperBounds(30, 10, 10)
                .build();
        PrometheusRegistry.defaultRegistry.register(histogram);

        // 模拟业务读取
        histogram.observe(30);
        histogram.observe(40);
        histogram.observe(50);
        histogram.observe(55);
        histogram.observe(60);
        histogram.observe(65);
        histogram.observe(68);
        histogram.observe(72);
        histogram.observe(83);
        histogram.observe(90);
        // 模拟采集数据
        HistogramSnapshot snapshot = histogram.collect();
        printToStandardOutput(FormatWriterType.PROMETHEUS, snapshot);
        //# HELP test_cpu_temperature_celsius Temperature of cpu
        //# TYPE test_cpu_temperature_celsius histogram
        //test_cpu_temperature_celsius_bucket{le="30.0"} 1
        //test_cpu_temperature_celsius_bucket{le="40.0"} 2
        //test_cpu_temperature_celsius_bucket{le="50.0"} 3
        //test_cpu_temperature_celsius_bucket{le="60.0"} 5
        //test_cpu_temperature_celsius_bucket{le="70.0"} 7
        //test_cpu_temperature_celsius_bucket{le="80.0"} 8
        //test_cpu_temperature_celsius_bucket{le="90.0"} 10
        //test_cpu_temperature_celsius_bucket{le="100.0"} 10
        //test_cpu_temperature_celsius_bucket{le="110.0"} 10
        //test_cpu_temperature_celsius_bucket{le="120.0"} 10
        //test_cpu_temperature_celsius_bucket{le="+Inf"} 10
        //test_cpu_temperature_celsius_count 10
        //test_cpu_temperature_celsius_sum 613.0
    }

    /**
     * 请求响应时间分布
     */
    @Test
    void test_histogram_requests() throws IOException {
        Histogram histogram = Histogram.builder()
                .name("http_request_duration_millis")
                .help("HTTP request service time in ms")
                .unit(Unit.SECONDS)
                .labelNames("method", "path", "status_code")
                .classicUpperBounds(new double[]{0.01, 0.05, 0.1, 0.2, 0.3, 0.5, 1.0})
                .build();
        PrometheusRegistry.defaultRegistry.register(histogram);

        // 模拟业务读取
        histogram.labelValues("GET", "/", "200").observe(0.015);
        histogram.labelValues("GET", "/", "200").observe(0.025);
        histogram.labelValues("GET", "/", "200").observe(0.010);
        histogram.labelValues("GET", "/", "200").observe(0.085);
        histogram.labelValues("GET", "/", "200").observe(0.115);
        histogram.labelValues("GET", "/", "200").observe(0.215);
        histogram.labelValues("GET", "/", "200").observe(0.815);
        histogram.labelValues("GET", "/", "200").observe(0.025);
        histogram.labelValues("GET", "/", "200").observe(0.015);
        // 模拟采集数据
        HistogramSnapshot snapshot = histogram.collect();
        printToStandardOutput(FormatWriterType.PROMETHEUS, snapshot);
    }

    /**
     * 请求响应时间分布
     */
    @Test
    void test_summary_requests() throws IOException {
        Summary requestLatency = Summary.builder()
                .name("request_latency_seconds")
                .help("Request latency in seconds.")
                .unit(Unit.SECONDS)
                .quantile(0.5, 0.01)
                .quantile(0.95, 0.005)
                .quantile(0.99, 0.005)
                .labelNames("status")
                .register();

        // 模拟业务读取
        requestLatency.labelValues("ok").observe(0.05);
        requestLatency.labelValues("ok").observe(0.08);
        requestLatency.labelValues("ok").observe(0.12);
        requestLatency.labelValues("ok").observe(0.32);
        requestLatency.labelValues("ok").observe(0.485);
        requestLatency.labelValues("ok").observe(0.503);
        requestLatency.labelValues("ok").observe(0.55);
        requestLatency.labelValues("ok").observe(0.89);
        requestLatency.labelValues("ok").observe(0.951);
        requestLatency.labelValues("ok").observe(0.98);
        // 模拟采集数据
        SummarySnapshot snapshot = requestLatency.collect();
        printToStandardOutput(FormatWriterType.PROMETHEUS, snapshot);
    }

    private void printToStandardOutput(FormatWriterType type, MetricSnapshot...snapshots) throws IOException {
        ExpositionFormatWriter writer = null;
        if (type == FormatWriterType.PROMETHEUS) {
            //这里 true 会为这个指标额外添加一些标签统计创建时间
            //PrometheusTextFormatWriter writer = new PrometheusTextFormatWriter(true);
            writer = new PrometheusTextFormatWriter(false);
        } else if (type == FormatWriterType.OPEN_METRICS) {
            writer = new OpenMetricsTextFormatWriter(false, false);
        } else if (type == FormatWriterType.PROMETHEUS_PROTOBUF) {
            writer = new PrometheusProtobufWriter();
        } else {
            throw new IllegalArgumentException("Unsupported format writer type: " + type);
        }
        //ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        //当要写入数据大于初始分配的缓冲大小时会自动扩容
        ByteArrayOutputStream baos = new ByteArrayOutputStream(16);
        writer.write(baos, new MetricSnapshots(snapshots));
        // 将 baos 内容写到 标准输出
        baos.writeTo(System.out);
    }

    enum FormatWriterType {
        PROMETHEUS,
        OPEN_METRICS,
        PROMETHEUS_PROTOBUF;
    }
}
