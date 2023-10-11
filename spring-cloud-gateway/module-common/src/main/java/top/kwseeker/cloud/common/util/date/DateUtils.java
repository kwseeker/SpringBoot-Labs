package top.kwseeker.cloud.common.util.date;

import java.time.LocalDateTime;

public class DateUtils {

    public static boolean isExpired(LocalDateTime time) {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(time);
    }
}
