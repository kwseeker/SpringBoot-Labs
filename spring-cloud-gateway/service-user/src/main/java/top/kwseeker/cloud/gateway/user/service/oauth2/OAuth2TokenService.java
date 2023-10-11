package top.kwseeker.cloud.gateway.user.service.oauth2;

import top.kwseeker.cloud.gateway.user.dal.dataobject.oauth2.OAuth2AccessTokenDO;

public interface OAuth2TokenService {

    OAuth2AccessTokenDO getAccessToken(String accessToken);

    OAuth2AccessTokenDO checkAccessToken(String accessToken);
}
