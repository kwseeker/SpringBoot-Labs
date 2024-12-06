package top.kwseeker.labs.ut.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ITransferRecordDaoTest {

    @Resource
    private ITransferRecordDao transferRecordDao;

    @Test
    public void test() {
        transferRecordDao.record("10001", "10002", 10);
    }
}