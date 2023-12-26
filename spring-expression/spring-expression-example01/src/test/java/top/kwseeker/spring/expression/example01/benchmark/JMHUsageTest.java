package top.kwseeker.spring.expression.example01.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试JMH本身的用法
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 2)   //一共会Fork 1个JVM进程（0 * Warmup Fork + 2 * Fork）
//@Measurement(iterations = 1, time = 1, batchSize = 1)
//@Measurement(iterations = 1, time = 1, batchSize = 3)
@Measurement(iterations = 1, time = 1, batchSize = 8)
@OutputTimeUnit(TimeUnit.MILLISECONDS)  //以微秒为单位
@State(Scope.Thread)            //每个测试线程分配一个实例
@Threads(2)                     //每个进程只创建一个测试线程
@Warmup(iterations = 1, time = 1, batchSize = 8)
public class JMHUsageTest {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Setup
    public void setup() {
        System.out.println("setup called");
    }

    @TearDown
    public void tearDown() {
        System.out.println("tearDown called");
    }

    @Benchmark
    public void testMeasurementBatchSize() {
        try {
            Thread.sleep(100);
            System.out.println(counter.incrementAndGet());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @OperationsPerInvocation(10)
    public void testOperationsPerInvocation() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @Threads(1)
    @Timeout(time = 2)
    public void testTimeout() {
        try {
            if (counter.get() == 9) {
                Thread.sleep(2000);
            } else {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(opt).run();
    }
}
