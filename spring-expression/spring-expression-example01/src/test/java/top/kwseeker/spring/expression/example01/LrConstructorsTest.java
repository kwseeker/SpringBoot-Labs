package top.kwseeker.spring.expression.example01;

import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import top.kwseeker.spring.expression.example01.pojo.IEEE;
import top.kwseeker.spring.expression.example01.pojo.Inventor;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LrConstructorsTest extends LrBaseTest {

    @Test
    public void testConstructors() {
        StandardEvaluationContext societyContext = new StandardEvaluationContext();
        IEEE ieee = new IEEE();
        societyContext.setRootObject(ieee);

        Inventor einstein = parser.parseExpression("new top.kwseeker.spring.expression.example01.pojo.Inventor('Albert Einstein', 'German')")
                .getValue(Inventor.class);

        // Members是IEEE的字段
        parser.parseExpression(
                "Members.add(new top.kwseeker.spring.expression.example01.pojo.Inventor('Albert Einstein', 'German'))")
                .getValue(societyContext);
        assertEquals(1, ieee.Members.size());
    }
}
