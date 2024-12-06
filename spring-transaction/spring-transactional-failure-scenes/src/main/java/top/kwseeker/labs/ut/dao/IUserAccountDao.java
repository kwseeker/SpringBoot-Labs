package top.kwseeker.labs.ut.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.kwseeker.labs.ut.entity.UserAccount;

@Mapper
public interface IUserAccountDao {

    /**
     * 更新用户账户余额
     */
    void increaseBalance(@Param("userId") String userId, @Param("amount") Integer amount);

    UserAccount getUserAccount(String userId);
}
