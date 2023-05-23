package top.kwseeker.springboot.lab03.springsecurity.oauth2.dal.mysql;

import org.apache.ibatis.annotations.Mapper;
import top.kwseeker.springboot.lab03.springsecurity.common.mybatis.LambdaQueryWrapperX;
import top.kwseeker.springboot.lab03.springsecurity.common.page.PageResult;
import top.kwseeker.springboot.lab03.springsecurity.oauth2.controller.vo.OAuth2ClientPageReqVO;
import top.kwseeker.springboot.lab03.springsecurity.oauth2.dal.dataobject.OAuth2ClientDO;
import top.kwseeker.springboot.lab03.springsecurity.user.dal.mysql.BaseMapperX;


/**
 * OAuth2 客户端 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface OAuth2ClientMapper extends BaseMapperX<OAuth2ClientDO> {

    default PageResult<OAuth2ClientDO> selectPage(OAuth2ClientPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OAuth2ClientDO>()
                .likeIfPresent(OAuth2ClientDO::getName, reqVO.getName())
                .eqIfPresent(OAuth2ClientDO::getStatus, reqVO.getStatus())
                .orderByDesc(OAuth2ClientDO::getId));
    }

    default OAuth2ClientDO selectByClientId(String clientId) {
        return selectOne(OAuth2ClientDO::getClientId, clientId);
    }

}
