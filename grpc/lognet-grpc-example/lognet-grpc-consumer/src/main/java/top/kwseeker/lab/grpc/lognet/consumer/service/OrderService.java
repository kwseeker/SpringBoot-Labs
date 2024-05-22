package top.kwseeker.lab.grpc.lognet.consumer.service;

public interface OrderService {

    String pay(String orderId, Integer amount);
}
