package top.kwseeker.cloud.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RequestMapping("/time")
@RestController
public class TimeV2Controller {

    @GetMapping("/current")
    public String currentTime() {
        return ZonedDateTime.now().toString();
    }
}
