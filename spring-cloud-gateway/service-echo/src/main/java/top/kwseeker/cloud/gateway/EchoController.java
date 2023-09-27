package top.kwseeker.cloud.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/echo")
@RestController
public class EchoController {

    @GetMapping("/hello")
    public String echoHello() {
        return "hello";
    }
}
