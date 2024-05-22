package top.kwseeker.lab.grpc.lognet.consumer.client.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;
import top.kwseeker.lab.grpc.lognet.common.client.GrpcClient;
import top.kwseeker.lab.grpc.lognet.common.consumer.service.AccountRequest;
import top.kwseeker.lab.grpc.lognet.common.consumer.service.AccountResponse;
import top.kwseeker.lab.grpc.lognet.common.consumer.service.AccountServiceGrpc;
import top.kwseeker.lab.grpc.lognet.common.filter.GrpcClientFilter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class AccountClient {

    private AccountServiceGrpc.AccountServiceBlockingStub accountServiceBlockingStub;

    @Resource
    private GrpcClient grpcClient;

    @PostConstruct
    private void init() {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 7081)
                .intercept(new GrpcClientFilter())
                .usePlaintext()
                .build();
        accountServiceBlockingStub = AccountServiceGrpc.newBlockingStub(managedChannel);
    }

    public boolean payment(String userId, Integer amount) {
        AccountRequest request = AccountRequest.newBuilder().setUserId(userId).setAmount(amount).build();
        AccountResponse response = grpcClient.syncInvoke(accountServiceBlockingStub, "payment", request, AccountResponse.class);
        return response.getResult();
    }
}
