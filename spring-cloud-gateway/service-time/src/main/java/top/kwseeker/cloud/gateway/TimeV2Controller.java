package top.kwseeker.cloud.gateway;

import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Date;

@RequestMapping("/time")
@RestController
public class TimeV2Controller {

    @GetMapping("/current")
    public String currentTime() {
        return ZonedDateTime.now().toString();
    }
}
