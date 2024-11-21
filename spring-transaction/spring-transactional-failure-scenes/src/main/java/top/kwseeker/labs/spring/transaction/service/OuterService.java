package top.kwseeker.labs.spring.transaction.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.kwseeker.labs.spring.transaction.dao.ITransferRecordDao;

import javax.annotation.Resource;

@Service
public class OuterService {

    @Resource
    private TransferService transferService;
    @Resource
    private ITransferRecordDao transferRecordDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void transfer(String fromUserId, String toUserId, Integer amount) {
        record(fromUserId, toUserId, amount);
        transferService.transfer(fromUserId, toUserId, amount);
    }

    /**
     * 添加一条转帐记录
     */
    private void record(String fromUserId, String userId, Integer amount) {
        transferRecordDao.record(fromUserId, userId, amount);
    }
}
