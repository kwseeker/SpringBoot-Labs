package top.kwseeker.cloud.gateway.predicate;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

public class ZonedDateTimeTest {

    @Test
    public void testZonedDateTime() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        System.out.println(zonedDateTime);
    }
}
