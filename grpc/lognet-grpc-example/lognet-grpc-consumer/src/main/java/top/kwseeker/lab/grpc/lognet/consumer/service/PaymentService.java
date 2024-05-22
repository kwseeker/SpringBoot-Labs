package top.kwseeker.lab.grpc.lognet.consumer.service;

import top.kwseeker.lab.grpc.lognet.consumer.entity.Order;

public interface PaymentService {

    void makePayment(Order order);
}
