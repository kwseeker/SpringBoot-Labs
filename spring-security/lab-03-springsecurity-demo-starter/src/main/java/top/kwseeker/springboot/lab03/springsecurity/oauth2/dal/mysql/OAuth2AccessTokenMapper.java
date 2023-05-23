package top.kwseeker.springboot.lab03.springsecurity.oauth2.dal.mysql;

import org.apache.ibatis.annotations.Mapper;
import top.kwseeker.springboot.lab03.springsecurity.common.mybatis.LambdaQueryWrapperX;
import top.kwseeker.springboot.lab03.springsecurity.common.page.PageResult;
import top.kwseeker.springboot.lab03.springsecurity.oauth2.controller.vo.OAuth2AccessTokenPageReqVO;
import top.kwseeker.springboot.lab03.springsecurity.oauth2.dal.dataobject.OAuth2AccessTokenDO;
import top.kwseeker.springboot.lab03.springsecurity.user.dal.mysql.BaseMapperX;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OAuth2AccessTokenMapper extends BaseMapperX<OAuth2AccessTokenDO> {

    default OAuth2AccessTokenDO selectByAccessToken(String accessToken) {
        return selectOne(OAuth2AccessTokenDO::getAccessToken, accessToken);
    }

    default List<OAuth2AccessTokenDO> selectListByRefreshToken(String refreshToken) {
        return selectList(OAuth2AccessTokenDO::getRefreshToken, refreshToken);
    }

    default PageResult<OAuth2AccessTokenDO> selectPage(OAuth2AccessTokenPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OAuth2AccessTokenDO>()
                .eqIfPresent(OAuth2AccessTokenDO::getUserId, reqVO.getUserId())
                .eqIfPresent(OAuth2AccessTokenDO::getUserType, reqVO.getUserType())
                .likeIfPresent(OAuth2AccessTokenDO::getClientId, reqVO.getClientId())
                .gt(OAuth2AccessTokenDO::getExpiresTime, LocalDateTime.now())
                .orderByDesc(OAuth2AccessTokenDO::getId));
    }

}
