package top.kwseeker.spring.expression.example01.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
//@Fork(value = 3, warmups = 2)   //一共会Fork 5个JVM进程（2 * Warmup Fork + 3 * Fork）
@Fork(value = 2)
@Measurement(iterations = 5, time = 3)
@OutputTimeUnit(TimeUnit.MICROSECONDS)  //以微秒为单位
@State(Scope.Thread)            //每个测试线程分配一个实例
@Threads(1)                     //每个进程只创建一个测试线程
@Warmup(iterations = 3, time = 3)
public class SpELCompilationTest {

    private Array array;
    private Expression expr1;
    private Expression expr2;

    static {
        System.out.println("static init done");
    }
    
    @Setup
    public void setup() {
        array = new Array();
        SomeOther someOther = new SomeOther(4);
        Some some = new Some(someOther);
        array.someArray.add(some);

        SpelParserConfiguration immediateModeConfig = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE,
                SpELCompilationTest.class.getClassLoader());
        SpelExpressionParser immediateModeParser = new SpelExpressionParser(immediateModeConfig);
        expr1 = immediateModeParser.parseExpression("someArray[0].someProperty.someOtherProperty < 5");

        SpelParserConfiguration offModeConfig = new SpelParserConfiguration(SpelCompilerMode.OFF,
                SpELCompilationTest.class.getClassLoader());
        SpelExpressionParser offModeParser = new SpelExpressionParser(offModeConfig);
        expr2 = offModeParser.parseExpression("someArray[0].someProperty.someOtherProperty < 5");

        System.out.println("setup done");
    }
    
    @Benchmark
    public void testCompileModeImmediate(Blackhole blackhole) {
        Boolean value = expr1.getValue(array, Boolean.class);
        blackhole.consume(value);
    }

    //为啥从结果上看，两种模式，性能基本没什么差别呢？
    @Benchmark
    public void testCompileModeOff(Blackhole blackhole) {
        Boolean value = expr2.getValue(array, Boolean.class);
        blackhole.consume(value);
    }

    static class Array {
        public List<Some> someArray = new ArrayList<>();
    }

    static class Some {
        private final SomeOther someProperty;

        public Some(SomeOther someProperty) {
            this.someProperty = someProperty;
        }

        public SomeOther getSomeProperty() {
            return someProperty;
        }
    }

    static class SomeOther {
        private final Integer someOtherProperty;

        public SomeOther(Integer someOtherProperty) {
            this.someOtherProperty = someOtherProperty;
        }

        public Integer getSomeOtherProperty() {
            return someOtherProperty;
        }
    }
}
