package top.kwseeker.cloud.gateway.user.api.oauth2;

import top.kwseeker.cloud.common.pojo.CommonResult;
import top.kwseeker.cloud.user.api.oauth2.OAuth2TokenApi;
import top.kwseeker.cloud.user.api.oauth2.dto.OAuth2AccessTokenCheckRespDTO;

import javax.annotation.Resource;

import static top.kwseeker.cloud.common.pojo.CommonResult.success;

public class OAuth2TokenApiImpl implements OAuth2TokenApi {

    @Resource
    private OAuth2TokenService oauth2TokenService;

    @Override
    public CommonResult<OAuth2AccessTokenCheckRespDTO> checkAccessToken(String accessToken) {
        return success(OAuth2TokenConvert.INSTANCE.convert(
                oauth2TokenService.checkAccessToken(accessToken)));
    }
}
