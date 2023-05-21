package top.kwseeker.springboot.lab03.springsecurity.api;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.kwseeker.lab.security.core.authentication.oauth2.OAuth2TokenApi;
import top.kwseeker.lab.security.core.authentication.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import top.kwseeker.lab.security.core.response.CommonResult;

@Slf4j
@Component
public class OAuth2TokenApiImpl implements OAuth2TokenApi {

    @Override
    public CommonResult<OAuth2AccessTokenCheckRespDTO> checkAccessToken(String accessToken) {
        log.info(">>> Checking access token, accessToken = " + accessToken);
        OAuth2AccessTokenCheckRespDTO dto = new OAuth2AccessTokenCheckRespDTO();
        dto.setUserId(0L);
        dto.setUserType(0);
        dto.setTenantId(0L);
        dto.setScopes(Lists.newArrayList());
        return CommonResult.success(dto);
    }
}
