package top.kwseeker.cloud.gateway.user.dal.mysql.oauth2;

import org.apache.ibatis.annotations.Mapper;
import top.kwseeker.cloud.gateway.user.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import top.kwseeker.cloud.gateway.user.dal.mysql.mybatis.mapper.BaseMapperX;

@Mapper
public interface OAuth2AccessTokenMapper extends BaseMapperX<OAuth2AccessTokenDO> {

    default OAuth2AccessTokenDO selectByAccessToken(String accessToken) {
        return selectOne(OAuth2AccessTokenDO::getAccessToken, accessToken);
    }
}
