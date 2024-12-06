package top.kwseeker.labs.ut.dao;

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