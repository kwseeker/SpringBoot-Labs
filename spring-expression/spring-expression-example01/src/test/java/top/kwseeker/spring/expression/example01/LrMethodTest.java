package top.kwseeker.spring.expression.example01;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 支持使用典型的Java编程语法调用方法
 */
public class LrMethodTest extends LrBaseTest {

    @Test
    public void testMethod() {
        // string literal, evaluates to "bc"
        String bc = parser.parseExpression("'abc'.substring(1, 3)").getValue(String.class);
        assertEquals("bc" , bc);

        //调用静态方法
        String message = parser.parseExpression("T(String).format('Simple message: <%s>', 'Hello World')")
                .getValue(context, String.class);
        System.out.println(message);
    }
}
