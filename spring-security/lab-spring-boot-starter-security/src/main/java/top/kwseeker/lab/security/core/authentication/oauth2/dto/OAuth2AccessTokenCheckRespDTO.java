package top.kwseeker.lab.security.core.authentication.oauth2.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OAuth2AccessTokenCheckRespDTO implements Serializable {

    private Long userId;

    private Integer userType;

    private Long tenantId;

    private List<String> scopes;

}