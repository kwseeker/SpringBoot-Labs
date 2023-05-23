package top.kwseeker.springboot.lab03.springsecurity.oauth2.controller.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OAuth2ClientUpdateReqVO extends OAuth2ClientBaseVO {

    @NotNull(message = "编号不能为空")
    private Long id;

}