package top.kwseeker.spring.expression.example01;

import org.junit.jupiter.api.Test;
import top.kwseeker.spring.expression.example01.pojo.Inventor;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LrVariablesTest extends LrBaseTest {

    @Test
    public void testVariables() {
        Inventor tesla = new Inventor("Nikola Tesla", "Serbian");

        //EvaluationContext context = SimpleEvaluationContext.forReadWriteDataBinding().build();
        context.setVariable("newName", "Mike Tesla");

        //这里的getValue只是触发赋值语句处理，所以返回值可以忽略
        Object name = parser.parseExpression("name = #newName").getValue(context, tesla);
        assertEquals("Mike Tesla", name);
        assertEquals("Mike Tesla", tesla.getName());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testThisAndRoot() {
        List<Integer> primes = Arrays.asList(2, 3, 5, 7, 11, 13, 17);

        context.setVariable("primes", primes);

        // Select all prime numbers > 10 from the list (using selection ?{...}).
        // .?参考 Collection Selection
        String expression = "#primes.?[#this > 10]";
        List<Integer> primesGreaterThanTen = parser.parseExpression(expression).getValue(context, List.class);
        assert primesGreaterThanTen != null;
        assertEquals(3, primesGreaterThanTen.size());

        Object thisObject = parser.parseExpression("#this").getValue(context, primes);
        Object rootObject = parser.parseExpression("#root").getValue(context, primes);

        List<Integer> rootList = parser.parseExpression("#root").getValue(context, primes, List.class);
        assert rootList != null;
        assertEquals(7, rootList.size());
    }
}
