package top.kwseeker.spring.expression.example01;

import org.junit.jupiter.api.Test;

import java.util.List;

public class LrInlineListsTest extends LrBaseTest {

    @Test
    public void testInlineLists() {
        List<?> numbers = parser.parseExpression("{1,2,3,4}").getValue(context, List.class);
        List<?> listOfLists = parser.parseExpression("{{'a','b'},{'x','y'}}").getValue(context, List.class);
    }
}
