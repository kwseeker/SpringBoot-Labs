package top.kwseeker.springboot.lab03.springsecurity.oauth2.dal.mysql;

import org.apache.ibatis.annotations.Mapper;
import top.kwseeker.springboot.lab03.springsecurity.oauth2.dal.dataobject.OAuth2CodeDO;
import top.kwseeker.springboot.lab03.springsecurity.user.dal.mysql.BaseMapperX;

@Mapper
public interface OAuth2CodeMapper extends BaseMapperX<OAuth2CodeDO> {

    default OAuth2CodeDO selectByCode(String code) {
        return selectOne(OAuth2CodeDO::getCode, code);
    }

}
