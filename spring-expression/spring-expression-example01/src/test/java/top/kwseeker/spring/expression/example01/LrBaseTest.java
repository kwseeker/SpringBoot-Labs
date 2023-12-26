package top.kwseeker.spring.expression.example01;

import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class LrBaseTest {

    protected StandardEvaluationContext context;
    protected SpelExpressionParser parser;

    public LrBaseTest() {
        context = new StandardEvaluationContext();
        parser = new SpelExpressionParser();
    }
}
