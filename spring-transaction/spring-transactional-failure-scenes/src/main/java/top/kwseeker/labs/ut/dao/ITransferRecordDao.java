package top.kwseeker.labs.ut.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ITransferRecordDao {

    void record(@Param("fromUserId") String fromUserId, @Param("toUserId") String toUserId, @Param("amount") Integer amount);
}
