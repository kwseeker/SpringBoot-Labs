package top.kwseeker.labs.ut;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CyclomaticComplexityTest {

    @Test
    public void testInfo() {
        String name = "Arvin";
        int age = 18;
        int gender = 1;
        String result = CyclomaticComplexity.info(name, age, gender);
        assertEquals("Arvin: male, teenager", result);
    }
}