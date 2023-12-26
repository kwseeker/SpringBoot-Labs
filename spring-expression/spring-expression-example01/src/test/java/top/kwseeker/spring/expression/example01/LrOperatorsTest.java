package top.kwseeker.spring.expression.example01;

import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 支持的运算符：
 * 1 关系运算符： == !=  < <= > >= instanceof match 等, 对象比较大小需要执行Comparable接口
 * 2 逻辑运算符： and && or || not !
 * 3 数学运算符： + - * / %
 * 4 赋值运算符
 */
public class LrOperatorsTest extends LrBaseTest {

    @Test
    public void testRelationalOperators() {
        Boolean trueValue = parser.parseExpression("2 == 2").getValue(Boolean.class);
        assertEquals(Boolean.TRUE, trueValue);
        Boolean falseValue = parser.parseExpression("2 < -5.0").getValue(Boolean.class);
        assertEquals(Boolean.FALSE, falseValue);
        Boolean trueValue2 = parser.parseExpression("'black' < 'block'").getValue(Boolean.class);
        assertEquals(Boolean.TRUE, trueValue2);

        // uses CustomValue:::compareTo
        //Boolean trueValue3 = parser.parseExpression("new CustomValue(1) < new CustomValue(2)").getValue(Boolean.class);
        //assertEquals(Boolean.TRUE, trueValue3);

        // instanceof T(Integer)
        Boolean falseValue3 = parser.parseExpression("'xyz' instanceof T(Integer)").getValue(Boolean.class);
        assertEquals(Boolean.FALSE, falseValue3);

        //判断自定义类型
        context.setVariable("cv", new CustomValue(1));
        Boolean falseValue31 = parser.parseExpression("#cv instanceof T(top.kwseeker.spring.expression.example01.LrOperatorsTest.CustomValue)")
                .getValue(context, Boolean.class);
        assertEquals(Boolean.TRUE, falseValue31);

        Boolean trueValue4 = parser.parseExpression("'5.00' matches '^-?\\d+(\\.\\d{2})?$'").getValue(Boolean.class);
        assertEquals(Boolean.TRUE, trueValue4);
        Boolean falseValue5 = parser.parseExpression("'5.0067' matches '^-?\\d+(\\.\\d{2})?$'").getValue(Boolean.class);
        assertEquals(Boolean.FALSE, falseValue5);
    }

    @Test
    public void testLogicalOperators() {
        // -- AND --
        Boolean falseValue = parser.parseExpression("true and false").getValue(Boolean.class);
        assertEquals(Boolean.FALSE, falseValue);

        // -- OR --
        Boolean trueValue = parser.parseExpression("true or false").getValue(Boolean.class);
        assertEquals(Boolean.TRUE, trueValue);

        // -- NOT --
        Boolean falseValue2 = parser.parseExpression("!true").getValue(Boolean.class);
        assertEquals(Boolean.FALSE, falseValue2);
    }

    @Test
    public void testMathematicalOperators() {
        // Addition
        Integer two = parser.parseExpression("1 + 1").getValue(Integer.class);  // 2
        assertEquals(2, two);

        String testString = parser.parseExpression("'test' + ' ' + 'string'").getValue(String.class);  // 'test string'
        assertEquals("test string", testString);

        // Subtraction
        Integer four = parser.parseExpression("1 - -3").getValue(Integer.class);  // 4
        assertEquals(4, four);

        Double d = parser.parseExpression("1000.00 - 1e4").getValue(Double.class);  // -9000
        assertEquals(-9000, d);

        // Multiplication
        Integer six = parser.parseExpression("-2 * -3").getValue(Integer.class);  // 6
        assertEquals(6, six);

        Double twentyFour = parser.parseExpression("2.0 * 3e0 * 4").getValue(Double.class);  // 24.0
        assertEquals(24, twentyFour);

        // Division
        Integer minusTwo = parser.parseExpression("6 / -3").getValue(Integer.class);  // -2
        assertEquals(-2, minusTwo);

        Double one = parser.parseExpression("8.0 / 4e0 / 2").getValue(Double.class);  // 1.0
        assertEquals(1, one);

        // Modulus
        Integer three = parser.parseExpression("7 % 4").getValue(Integer.class);  // 3
        assertEquals(3, three);

        Integer one2 = parser.parseExpression("8 / 5 % 2").getValue(Integer.class);  // 1
        assertEquals(1, one2);

        // Operator precedence
        Integer minusTwentyOne = parser.parseExpression("1+2-3*8").getValue(Integer.class);  // -21
        assertEquals(-21, minusTwentyOne);
    }

    /**
     * 通过赋值操作符(=)给对象字段赋值。这通常在setValue调用中完成，但也可以在getValue调用中完成。
     */
    @Test
    public void testAssignmentOperators() {
        CustomValue cv = new CustomValue();
        parser.parseExpression("value").setValue(context, cv, 233);
        assertEquals(cv.getValue(), 233);

        CustomValue cv2 = new CustomValue();
        Integer value = parser.parseExpression("value = 666").getValue(context, cv2, Integer.class);
        assertEquals(value, 666);
    }


    static class CustomValue implements Comparable<Integer> {

        private Integer value = 0;

        public CustomValue() {
        }

        public CustomValue(Integer value) {
            this.value = value;
        }

        @Override
        public int compareTo(@NonNull Integer target) {
            return value - target;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }
}
