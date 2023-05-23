package top.kwseeker.springboot.lab03.springsecurity.oauth2.dal.mysql;

import org.apache.ibatis.annotations.Mapper;
import top.kwseeker.springboot.lab03.springsecurity.common.mybatis.LambdaQueryWrapperX;
import top.kwseeker.springboot.lab03.springsecurity.oauth2.dal.dataobject.OAuth2ApproveDO;
import top.kwseeker.springboot.lab03.springsecurity.user.dal.mysql.BaseMapperX;

import java.util.List;

@Mapper
public interface OAuth2ApproveMapper extends BaseMapperX<OAuth2ApproveDO> {

    default int update(OAuth2ApproveDO updateObj) {
        return update(updateObj, new LambdaQueryWrapperX<OAuth2ApproveDO>()
                .eq(OAuth2ApproveDO::getUserId, updateObj.getUserId())
                .eq(OAuth2ApproveDO::getUserType, updateObj.getUserType())
                .eq(OAuth2ApproveDO::getClientId, updateObj.getClientId())
                .eq(OAuth2ApproveDO::getScope, updateObj.getScope()));
    }

    default List<OAuth2ApproveDO> selectListByUserIdAndUserTypeAndClientId(Long userId, Integer userType, String clientId) {
        return selectList(new LambdaQueryWrapperX<OAuth2ApproveDO>()
                .eq(OAuth2ApproveDO::getUserId, userId)
                .eq(OAuth2ApproveDO::getUserType, userType)
                .eq(OAuth2ApproveDO::getClientId, clientId));
    }

}
