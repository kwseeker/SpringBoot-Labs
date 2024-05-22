package top.kwseeker.lab.grpc.lognet.consumer.service;

import org.springframework.stereotype.Service;
import top.kwseeker.lab.grpc.lognet.consumer.client.grpc.AccountClient;
import top.kwseeker.lab.grpc.lognet.consumer.entity.Order;

import javax.annotation.Resource;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private AccountClient accountClient;

    @Override
    public void makePayment(Order order) {
        accountClient.payment(order.getUserId(), order.getAmount());
    }
}
