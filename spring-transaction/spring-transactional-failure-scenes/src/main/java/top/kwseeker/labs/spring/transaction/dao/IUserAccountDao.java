package top.kwseeker.labs.spring.transaction.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.kwseeker.labs.spring.transaction.entity.UserAccount;

@Mapper
public interface IUserAccountDao {

    /**
     * 更新用户账户余额
     */
    void increaseBalance(@Param("userId") String userId, @Param("amount") Integer amount);

    UserAccount getUserAccount(String userId);
}
