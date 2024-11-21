package top.kwseeker.labs.spring.transaction.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class TransferServiceTest {

    @Resource
    private TransferService transferService;

    @Test
    public void testTransfer() {
        transferService.transfer("10001", "10002", 10);
    }
}