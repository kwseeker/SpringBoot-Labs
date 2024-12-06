package top.kwseeker.labs.ut;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AppTest {

    @Test
    public void testConstructor() {
        Calculator util = new Calculator();
        assertNotNull(util);
    }

    @Test
    public void test1() {
        int sum = Calculator.add(1, 1);
        assertEquals(2, sum);
    }
}
