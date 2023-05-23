package top.kwseeker.springboot.lab03.springsecurity.oauth2.controller.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OAuth2ClientRespVO extends OAuth2ClientBaseVO {

    private Long id;

    private LocalDateTime createTime;

}