package top.kwseeker.spring.expression.example01;

import org.junit.jupiter.api.Test;

public class LrArrayConstructionTest extends LrBaseTest {

    @Test
    public void testArrayConstruction() {
        int[] numbers1 = (int[]) parser.parseExpression("new int[4]").getValue(context);

        int[] numbers2 = (int[]) parser.parseExpression("new int[]{1,2,3}").getValue(context);

        int[][] numbers3 = (int[][]) parser.parseExpression("new int[4][5]").getValue(context);
    }
}
