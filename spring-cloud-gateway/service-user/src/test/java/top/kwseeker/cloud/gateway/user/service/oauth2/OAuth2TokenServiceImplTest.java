package top.kwseeker.cloud.gateway.user.service.oauth2;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import top.kwseeker.cloud.gateway.user.BaseDbAndRedisUnitTest;
import top.kwseeker.cloud.gateway.user.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import top.kwseeker.cloud.gateway.user.dal.mysql.oauth2.OAuth2AccessTokenMapper;
import top.kwseeker.cloud.gateway.user.dal.redis.oauth2.OAuth2AccessTokenRedisDAO;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static top.kwseeker.cloud.gateway.user.util.AssertUtils.assertPojoEquals;
import static top.kwseeker.cloud.gateway.user.util.RandomUtils.randomPojo;

@Import({OAuth2TokenServiceImpl.class, OAuth2AccessTokenRedisDAO.class})
class OAuth2TokenServiceImplTest extends BaseDbAndRedisUnitTest {

    @Resource
    private OAuth2TokenServiceImpl oauth2TokenService;
    @Resource
    private OAuth2AccessTokenMapper oauth2AccessTokenMapper;
    @Resource
    private OAuth2AccessTokenRedisDAO oauth2AccessTokenRedisDAO;

    @Test
    public void testGetAccessToken() {
        // mock 数据（访问令牌）
        OAuth2AccessTokenDO accessTokenDO = randomPojo(OAuth2AccessTokenDO.class)
                .setExpiresTime(LocalDateTime.now().plusDays(1));
        oauth2AccessTokenMapper.insert(accessTokenDO);
        // 准备参数
        String accessToken = accessTokenDO.getAccessToken();
        // 调用
        OAuth2AccessTokenDO result = oauth2TokenService.getAccessToken(accessToken);
        // 断言
        assertPojoEquals(accessTokenDO, result, "createTime", "updateTime", "deleted", "creator", "updater");
        assertPojoEquals(accessTokenDO, oauth2AccessTokenRedisDAO.get(accessToken),
                "createTime", "updateTime", "deleted", "creator", "updater");
    }

}