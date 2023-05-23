package top.kwseeker.springboot.lab03.springsecurity.oauth2.controller.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.kwseeker.springboot.lab03.springsecurity.common.page.PageParam;

@Data
@EqualsAndHashCode(callSuper = true)
public class OAuth2AccessTokenPageReqVO extends PageParam {

    private Long userId;

    private Integer userType;

    private String clientId;

}