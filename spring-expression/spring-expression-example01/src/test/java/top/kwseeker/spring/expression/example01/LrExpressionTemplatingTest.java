package top.kwseeker.spring.expression.example01;

import org.junit.jupiter.api.Test;
import org.springframework.expression.ParserContext;

public class LrExpressionTemplatingTest extends LrBaseTest {

    @Test
    public void testExpressionTemplating() {
        String randomPhrase;
        //默认使用 TEMPLATE_EXPRESSION
        randomPhrase = parser.parseExpression(
                "random number is #{T(java.lang.Math).random()}", ParserContext.TEMPLATE_EXPRESSION)
                .getValue(String.class);
        System.out.println(randomPhrase);

        randomPhrase = parser.parseExpression(
                "random number is #{T(java.lang.Math).random()}", new TemplateParserContext())
                .getValue(String.class);
        System.out.println(randomPhrase);
    }

    static class TemplateParserContext implements ParserContext {

        public String getExpressionPrefix() {
            return "#{";
        }

        public String getExpressionSuffix() {
            return "}";
        }

        public boolean isTemplate() {
            return true;
        }
    }
}
