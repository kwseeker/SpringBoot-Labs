package top.kwseeker.lab.grpc.lognet.consumer.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.kwseeker.lab.grpc.lognet.consumer.service.OrderService;

import javax.annotation.Resource;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping("/pay")
    public String payment(@RequestParam(value = "orderId") String orderId,
                          @RequestParam(value = "amount") int amount) {
        return orderService.pay(orderId, amount);
    }
}
