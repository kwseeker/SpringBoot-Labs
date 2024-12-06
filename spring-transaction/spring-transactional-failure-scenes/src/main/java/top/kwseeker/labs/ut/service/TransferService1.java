package top.kwseeker.labs.ut.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import top.kwseeker.labs.ut.dao.IUserAccountDao;

import javax.annotation.Resource;

/**
 * 事务失效原因：方法访问权限非public
 */
@Service
public class TransferService1 {

    @Resource
    private IUserAccountDao userAccountDao;

    @Transactional
    public void transfer(String fromUserId, String toUserId, Integer amount) {
        try {
            decreaseBalance(fromUserId, amount);
            increaseBalance(toUserId, amount);
        } catch (Exception e) {
            // 内部事务抛的异常不处理的话会导致外部事务回滚
            // 如果想让外部事务也回滚，可以设置 rollbackOnly = true
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    private void decreaseBalance(String userId, Integer amount) {
        userAccountDao.increaseBalance(userId, -amount);
    }

    private void increaseBalance(String userId, Integer amount) {
        userAccountDao.increaseBalance(userId, amount);
        throw new RuntimeException("increaseBalance error");
    }
}
