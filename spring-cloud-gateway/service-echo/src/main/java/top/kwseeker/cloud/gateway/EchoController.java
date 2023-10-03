package top.kwseeker.cloud.gateway;

import org.springframework.web.bind.annotation.*;

@RequestMapping("/echo")
@RestController
public class EchoController {

    @GetMapping("/hello")
    public String echoHello() {
        return "hello";
    }

    @GetMapping("/greet/{name}")
    public String echoGreet(@PathVariable("name") String name) {
        return "hello " + name;
    }
}
