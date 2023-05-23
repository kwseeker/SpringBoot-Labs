package top.kwseeker.springboot.lab03.springsecurity.oauth2.controller.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import top.kwseeker.springboot.lab03.springsecurity.common.page.PageParam;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OAuth2ClientPageReqVO extends PageParam {

    private String name;

    private Integer status;

}