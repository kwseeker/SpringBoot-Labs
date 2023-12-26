package top.kwseeker.spring.expression.example01;

import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LrFunctionsTest extends LrBaseTest {

    @Test
    public void testFunctions() throws NoSuchMethodException {
        Method method = StringUtils.class.getDeclaredMethod("reverseString", String.class);
        context.setVariable("reverseString", method);

        String helloReversed = parser.parseExpression("#reverseString('hello')").getValue(context, String.class);
        assertEquals("olleh", helloReversed);
    }

    @Test
    public void testMethodHandle() throws Throwable {
        MethodHandle mh = MethodHandles.lookup().findStatic(StringUtils.class, "reverseString",
                MethodType.methodType(String.class, String.class));
        context.setVariable("rs", mh);

        String result = (String) mh.invoke("Hello world!");
        assertEquals("!dlrow olleH", result);

        //String message = parser.parseExpression("T(String).format('Simple message: <%s>', 'Hello World')")
        //        .getValue(context, String.class);
        //System.out.println(message);

        //跑不通，可能测试使用的版本（5.3.31）还不支持，后面在最新的版本上测试下
        // org.springframework.expression.spel.SpelEvaluationException: EL1022E:
        // The function 'rs' mapped to an object of type 'class org.springframework.expression.TypedValue' which cannot be invoked
        //String message = parser.parseExpression("#rs('Simple message: <%s>', 'Hello World')")
        //        .getValue(context, String.class);
        //String message = parser.parseExpression("#rs('Hello world!')")
        //        .getValue(context, String.class);
        //System.out.println(message);
    }

    static class StringUtils {

        public static String reverseString(String input) {
            StringBuilder backwards = new StringBuilder(input.length());
            for (int i = 0; i < input.length(); i++) {
                backwards.append(input.charAt(input.length() - 1 - i));
            }
            return backwards.toString();
        }
    }
}
