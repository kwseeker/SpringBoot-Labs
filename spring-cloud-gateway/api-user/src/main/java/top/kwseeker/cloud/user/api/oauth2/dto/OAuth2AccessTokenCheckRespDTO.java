package top.kwseeker.cloud.user.api.oauth2.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OAuth2AccessTokenCheckRespDTO implements Serializable {

    private Long userId;

    //private Integer userType;
}
