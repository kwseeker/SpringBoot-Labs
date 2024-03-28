package top.kwseeker.lab.feign.server;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class EchoController {

    @GetMapping("/echo/{name}")
    public String echo(@PathVariable("name") String name) {
        return "Hello, " + name;
    }
}
