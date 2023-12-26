package top.kwseeker.spring.expression.example01;

import org.junit.jupiter.api.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 字面量表达式
 * 支持：
 * 1 字符串
 * 2 数字类型：整数、16进制数、浮点数
 * 3 boolean类型
 * 4 null
 */
public class LrLiteralExpressionsTest extends LrBaseTest {

    @Test
    public void testLiteralExpressions() {
        ExpressionParser parser = new SpelExpressionParser();

        //若要在用单引号括起来的字符串文本中包含单引号，请使用两个相邻的单引号字符
        String pizzaParlor = (String) parser.parseExpression("'Tony''s Pizza'").getValue();
        assertEquals("Tony's Pizza", pizzaParlor);

        Double number = (Double) parser.parseExpression("6.0221415E+23").getValue();
        assertEquals(6.0221415E+23, number);

        Integer maxValue = (Integer) parser.parseExpression("0x7FFFFFFF").getValue();
        assertEquals(0x7FFFFFFF, maxValue);

        Boolean trueValue = (Boolean) parser.parseExpression("true").getValue();
        assertEquals(Boolean.TRUE, trueValue);

        Object nullValue = parser.parseExpression("null").getValue();
        assertNull(nullValue);
    }
}
