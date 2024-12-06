package top.kwseeker.labs.ut.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.kwseeker.labs.ut.dao.IUserAccountDao;

import javax.annotation.Resource;

@Service
public class TransferService {

    @Resource
    private IUserAccountDao userAccountDao;

    //@Transactional(propagation = Propagation.NOT_SUPPORTED)
    //@Transactional(propagation = Propagation.REQUIRED)
    //@Transactional(propagation = Propagation.REQUIRES_NEW)
    @Transactional(propagation = Propagation.NESTED)
    public void transfer(String fromUserId, String toUserId, Integer amount) {
        decreaseBalance(fromUserId, amount);
        increaseBalance(toUserId, amount);
    }

    private void decreaseBalance(String userId, Integer amount) {
        userAccountDao.increaseBalance(userId, -amount);
    }

    private void increaseBalance(String userId, Integer amount) {
        userAccountDao.increaseBalance(userId, amount);
        throw new RuntimeException("increaseBalance error");
    }
}
