package top.kwseeker.cloud.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RequestMapping("/time")
@RestController
public class TimeController {

    @GetMapping("/current")
    public String currentTime() {
        return new Date().toString();
    }
}