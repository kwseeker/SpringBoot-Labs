package top.kwseeker.spring.expression.example01;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluationContextTest {

    private static ExpressionParser parser;

    @BeforeAll
    public static void init() {
        parser = new SpelExpressionParser();
    }

    @Test
    public void testSimpleEvaluationContext() {
        Simple simple = new Simple();
        simple.booleanList.add(Boolean.TRUE);

        EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

        // SpEL默认会使用Spring Core中的org.springframework.core.convert.ConversionService对对象类型按需执行自动转换
        //parser.parseExpression("booleanList[0]").setValue(context, simple, false);
        // 这里传String类型的false就是进行的自动转换
        parser.parseExpression("booleanList[0]").setValue(context, simple, "false");

        // b is false
        Boolean b = simple.booleanList.get(0);
        assertFalse(b);
    }

    static class Simple {
        public List<Boolean> booleanList = new ArrayList<>();
    }
}
