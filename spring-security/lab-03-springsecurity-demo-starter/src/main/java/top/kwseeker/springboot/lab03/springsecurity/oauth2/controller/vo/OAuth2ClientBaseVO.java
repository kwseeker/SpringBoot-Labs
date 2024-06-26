package top.kwseeker.springboot.lab03.springsecurity.oauth2.controller.vo;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import top.kwseeker.lab.security.core.util.JsonUtils;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
* OAuth2 客户端 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class OAuth2ClientBaseVO {

    @NotNull(message = "客户端编号不能为空")
    private String clientId;

    @NotNull(message = "客户端密钥不能为空")
    private String secret;

    @NotNull(message = "应用名不能为空")
    private String name;

    @NotNull(message = "应用图标不能为空")
    @URL(message = "应用图标的地址不正确")
    private String logo;

    private String description;

    @NotNull(message = "状态不能为空")
    private Integer status;

    @NotNull(message = "访问令牌的有效期不能为空")
    private Integer accessTokenValiditySeconds;

    @NotNull(message = "刷新令牌的有效期不能为空")
    private Integer refreshTokenValiditySeconds;

    @NotNull(message = "可重定向的 URI 地址不能为空")
    private List<@NotEmpty(message = "重定向的 URI 不能为空")
        @URL(message = "重定向的 URI 格式不正确") String> redirectUris;

    @NotNull(message = "授权类型不能为空")
    private List<String> authorizedGrantTypes;

    private List<String> scopes;

    private List<String> autoApproveScopes;

    private List<String> authorities;

    private List<String> resourceIds;

    private String additionalInformation;

    @AssertTrue(message = "附加信息必须是 JSON 格式")
    public boolean isAdditionalInformationJson() {
        return StrUtil.isEmpty(additionalInformation) || JsonUtils.isJson(additionalInformation);
    }

}