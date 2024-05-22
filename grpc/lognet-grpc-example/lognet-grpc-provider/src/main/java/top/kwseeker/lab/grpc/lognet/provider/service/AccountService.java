package top.kwseeker.lab.grpc.lognet.provider.service;

import top.kwseeker.lab.grpc.lognet.provider.dto.AccountDTO;

public interface AccountService {

    boolean payment(AccountDTO accountDTO);
}
