package top.kwseeker.spring.expression.example01;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LrTypesTest extends LrBaseTest {

    @Test
    public void testTypes() {
        Class<?> dateClass = parser.parseExpression("T(java.util.Date)").getValue(Class.class);
        //注意如果是不同的类加载器加载的类，类型是不能这么比较的
        assertEquals(Date.class, dateClass);

        Class<?> stringClass = parser.parseExpression("T(String)").getValue(Class.class);
        assertEquals(String.class, stringClass);

        Boolean trueValue = parser.parseExpression("T(java.math.RoundingMode).CEILING < T(java.math.RoundingMode).FLOOR")
                .getValue(Boolean.class);
        assertEquals(Boolean.TRUE, trueValue);
    }
}
