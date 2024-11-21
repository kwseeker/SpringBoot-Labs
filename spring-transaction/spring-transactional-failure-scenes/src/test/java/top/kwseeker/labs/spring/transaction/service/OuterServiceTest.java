package top.kwseeker.labs.spring.transaction.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class OuterServiceTest {

    @Resource
    private OuterService outerService;

    @Test
    public void testTransfer() {
        outerService.transfer("10001", "10002", 10);
    }
}
