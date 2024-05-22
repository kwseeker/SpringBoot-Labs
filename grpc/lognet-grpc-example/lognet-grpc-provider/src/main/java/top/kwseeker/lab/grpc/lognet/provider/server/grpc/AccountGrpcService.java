package top.kwseeker.lab.grpc.lognet.provider.server.grpc;

import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import top.kwseeker.lab.grpc.lognet.common.consumer.service.AccountRequest;
import top.kwseeker.lab.grpc.lognet.common.consumer.service.AccountResponse;
import top.kwseeker.lab.grpc.lognet.common.consumer.service.AccountServiceGrpc;
import top.kwseeker.lab.grpc.lognet.common.filter.GrpcServerFilter;
import top.kwseeker.lab.grpc.lognet.provider.dto.AccountDTO;
import top.kwseeker.lab.grpc.lognet.provider.service.AccountService;

import javax.annotation.Resource;

@GRpcService(interceptors = {GrpcServerFilter.class})
public class AccountGrpcService extends AccountServiceGrpc.AccountServiceImplBase {

    @Resource
    private AccountService accountService;

    @Override
    public void payment(AccountRequest accountRequest, StreamObserver<AccountResponse> responseObserver) {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAmount(accountRequest.getAmount());
        accountDTO.setUserId(accountRequest.getUserId());

        boolean res = accountService.payment(accountDTO);

        AccountResponse response = AccountResponse.newBuilder()
                .setResult(res)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
