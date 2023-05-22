package top.kwseeker.springboot.lab03.springsecurity.user.controller.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserCreateReqVO extends UserBaseVO {

    @NotEmpty(message = "密码不能为空")
    @Length(min = 4, max = 16, message = "密码长度为 4-16 位")
    private String password;

}