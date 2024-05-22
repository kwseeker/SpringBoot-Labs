package top.kwseeker.lab.grpc.lognet.provider.service;

import org.springframework.stereotype.Service;
import top.kwseeker.lab.grpc.lognet.provider.dto.AccountDTO;

@Service
public class AccountServiceImpl implements AccountService {

    @Override
    public boolean payment(AccountDTO accountDTO) {
        System.out.println("模拟支付扣款，accountDTO=" + accountDTO);
        return true;
    }
}
