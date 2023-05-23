package top.kwseeker.springboot.lab03.springsecurity.oauth2.convert;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import top.kwseeker.springboot.lab03.springsecurity.common.page.PageResult;
import top.kwseeker.springboot.lab03.springsecurity.oauth2.controller.vo.OAuth2ClientCreateReqVO;
import top.kwseeker.springboot.lab03.springsecurity.oauth2.controller.vo.OAuth2ClientRespVO;
import top.kwseeker.springboot.lab03.springsecurity.oauth2.controller.vo.OAuth2ClientUpdateReqVO;
import top.kwseeker.springboot.lab03.springsecurity.oauth2.dal.dataobject.OAuth2ClientDO;

import java.util.List;

/**
 * OAuth2 客户端 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface OAuth2ClientConvert {

    OAuth2ClientConvert INSTANCE = Mappers.getMapper(OAuth2ClientConvert.class);

    OAuth2ClientDO convert(OAuth2ClientCreateReqVO bean);

    OAuth2ClientDO convert(OAuth2ClientUpdateReqVO bean);

    OAuth2ClientRespVO convert(OAuth2ClientDO bean);

    List<OAuth2ClientRespVO> convertList(List<OAuth2ClientDO> list);

    PageResult<OAuth2ClientRespVO> convertPage(PageResult<OAuth2ClientDO> page);

}
