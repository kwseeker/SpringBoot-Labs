package top.kwseeker.cloud.gateway.user.api.oauth2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.kwseeker.cloud.common.exception.ErrorCode;
import top.kwseeker.cloud.common.pojo.CommonResult;
import top.kwseeker.cloud.gateway.user.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import top.kwseeker.cloud.gateway.user.dal.mysql.oauth2.OAuth2AccessTokenMapper;
import top.kwseeker.cloud.user.api.oauth2.dto.OAuth2AccessTokenCheckRespDTO;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static top.kwseeker.cloud.gateway.user.util.RandomUtils.randomPojo;

@SpringBootTest
@RunWith(SpringRunner.class)
class OAuth2TokenApiImplTest {

    @Resource
    private OAuth2AccessTokenMapper oauth2AccessTokenMapper;
    @Resource
    private OAuth2TokenApiImpl oAuth2TokenApi;

    @Test
    public void generateRandomToken() {
        // 先创建令牌
        OAuth2AccessTokenDO accessTokenDO = randomPojo(OAuth2AccessTokenDO.class)
                .setExpiresTime(LocalDateTime.now().plusDays(1));
        oauth2AccessTokenMapper.insert(accessTokenDO);
    }

    @Test
    public void testCheckAccessToken() {
        // 先创建令牌
        OAuth2AccessTokenDO accessTokenDO = randomPojo(OAuth2AccessTokenDO.class)
                .setExpiresTime(LocalDateTime.now().plusDays(1));
        oauth2AccessTokenMapper.insert(accessTokenDO);
        // 校验令牌
        String accessToken = accessTokenDO.getAccessToken();
        CommonResult<OAuth2AccessTokenCheckRespDTO> result = oAuth2TokenApi.checkAccessToken(accessToken);
        // 断言
        Assertions.assertEquals(ErrorCode.SUCCESS.getCode(), result.getCode());
        Assertions.assertEquals(accessTokenDO.getUserId(), result.getData().getUserId());
    }
}