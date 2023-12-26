package top.kwseeker.spring.expression.example01;

import org.junit.jupiter.api.Test;
import top.kwseeker.spring.expression.example01.pojo.Inventor;
import top.kwseeker.spring.expression.example01.pojo.PlaceOfBirth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LrSafeNavigationOperatorTest extends LrBaseTest {

    @Test
    public void testSafeNavigationOperator() {
        Inventor tesla = new Inventor("Nikola Tesla", "Serbian");
        tesla.setPlaceOfBirth(new PlaceOfBirth("Smiljan"));

        //placeOfBirth为null就直接返回null, 否则返回city字段值
        String city = parser.parseExpression("placeOfBirth?.city").getValue(context, tesla, String.class);
        assertEquals("Smiljan", city);

        tesla.setPlaceOfBirth(null);
        city = parser.parseExpression("placeOfBirth?.city").getValue(context, tesla, String.class);
        assertNull(city);
    }
}
