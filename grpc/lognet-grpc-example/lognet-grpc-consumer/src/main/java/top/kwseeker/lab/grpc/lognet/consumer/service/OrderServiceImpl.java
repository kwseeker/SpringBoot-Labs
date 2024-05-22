package top.kwseeker.lab.grpc.lognet.consumer.service;

import org.springframework.stereotype.Service;
import top.kwseeker.lab.grpc.lognet.consumer.entity.Order;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private PaymentService paymentService;

    @Override
    public String pay(String orderId, Integer amount) {
        Order order = new Order();
        order.setId(orderId);
        order.setUserId("10001");
        order.setAmount(amount);

        try {
            System.out.println("模拟订单检查...");
            TimeUnit.MILLISECONDS.sleep(20);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("模拟RPC调用账户服务扣款支付");
        paymentService.makePayment(order);

        return null;
    }
}
