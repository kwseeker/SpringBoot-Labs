package top.kwseeker.labs.spring.transaction.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ITransferRecordDao {

    void record(@Param("fromUserId") String fromUserId, @Param("toUserId") String toUserId, @Param("amount") Integer amount);
}
