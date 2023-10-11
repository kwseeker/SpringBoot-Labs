package top.kwseeker.cloud.gateway.user.convert;

import org.junit.Test;
import top.kwseeker.cloud.gateway.user.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import top.kwseeker.cloud.gateway.user.util.RandomUtils;
import top.kwseeker.cloud.user.api.oauth2.dto.OAuth2AccessTokenCheckRespDTO;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class OAuth2TokenConvertTest {

    @Test
    public void testConvert() {
        OAuth2AccessTokenDO oAuth2AccessTokenDO = RandomUtils.randomPojo(OAuth2AccessTokenDO.class)
                .setExpiresTime(LocalDateTime.now().plusDays(1));
        OAuth2AccessTokenCheckRespDTO converted = OAuth2TokenConvert.INSTANCE.convert(oAuth2AccessTokenDO);
        assertEquals(oAuth2AccessTokenDO.getUserId(), converted.getUserId());
    }
}