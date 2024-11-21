package top.kwseeker.labs.spring.transaction.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class IUserAccountDaoTest {

    @Resource
    private IUserAccountDao userAccountDao;

    @Test
    public void test() {
        userAccountDao.increaseBalance("10001", 10);
    }
}