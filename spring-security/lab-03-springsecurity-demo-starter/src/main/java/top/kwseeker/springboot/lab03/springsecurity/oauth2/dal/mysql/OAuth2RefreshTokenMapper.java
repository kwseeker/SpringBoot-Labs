package top.kwseeker.springboot.lab03.springsecurity.oauth2.dal.mysql;

import org.apache.ibatis.annotations.Mapper;
import top.kwseeker.springboot.lab03.springsecurity.common.mybatis.LambdaQueryWrapperX;
import top.kwseeker.springboot.lab03.springsecurity.oauth2.dal.dataobject.OAuth2RefreshTokenDO;
import top.kwseeker.springboot.lab03.springsecurity.user.dal.mysql.BaseMapperX;

@Mapper
public interface OAuth2RefreshTokenMapper extends BaseMapperX<OAuth2RefreshTokenDO> {

    default int deleteByRefreshToken(String refreshToken) {
        return delete(new LambdaQueryWrapperX<OAuth2RefreshTokenDO>()
                .eq(OAuth2RefreshTokenDO::getRefreshToken, refreshToken));
    }

    default OAuth2RefreshTokenDO selectByRefreshToken(String refreshToken) {
        return selectOne(OAuth2RefreshTokenDO::getRefreshToken, refreshToken);
    }

}
