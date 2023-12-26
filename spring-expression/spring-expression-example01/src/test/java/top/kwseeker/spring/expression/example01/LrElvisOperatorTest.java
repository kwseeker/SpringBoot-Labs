package top.kwseeker.spring.expression.example01;

import org.junit.jupiter.api.Test;
import top.kwseeker.spring.expression.example01.pojo.Inventor;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LrElvisOperatorTest extends LrBaseTest {

    @Test
    public void testElvisOperator() {
        Inventor tesla = new Inventor("Nikola Tesla", "Serbian");
        String name = parser.parseExpression("name?:'Elvis Presley'").getValue(context, tesla, String.class);
        System.out.println(name);
        assertEquals("Nikola Tesla", name);

        tesla.setName("");
        name = parser.parseExpression("name?:'Elvis Presley'").getValue(context, tesla, String.class);
        System.out.println(name);  // Elvis Presley
        assertEquals("Elvis Presley", name);

        tesla.setName("");
        name = parser.parseExpression("(name != null and !name.isEmpty()) ? name : 'Elvis Presley2'").getValue(context, tesla, String.class);
        System.out.println(name);  // Elvis Presley
        assertEquals("Elvis Presley2", name);
    }
}
