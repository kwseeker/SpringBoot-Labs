package top.kwseeker.cloud.gateway.user.service.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.kwseeker.cloud.common.exception.ErrorCode;
import top.kwseeker.cloud.common.util.RandomUtils;
import top.kwseeker.cloud.common.util.date.DateUtils;
import top.kwseeker.cloud.common.util.json.JsonUtils;
import top.kwseeker.cloud.gateway.user.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import top.kwseeker.cloud.gateway.user.dal.mysql.oauth2.OAuth2AccessTokenMapper;
import top.kwseeker.cloud.gateway.user.dal.redis.oauth2.OAuth2AccessTokenRedisDAO;

import javax.annotation.Resource;

import java.time.LocalDateTime;

import static top.kwseeker.cloud.common.exception.util.ServiceExceptionUtil.exception0;

@Slf4j
@Service
public class OAuth2TokenServiceImpl implements OAuth2TokenService {

    @Resource
    private OAuth2AccessTokenRedisDAO oauth2AccessTokenRedisDAO;
    @Resource
    private OAuth2AccessTokenMapper oauth2AccessTokenMapper;

    @Override
    public OAuth2AccessTokenDO getAccessToken(String accessToken) {
        // 优先从 Redis 中获取
        OAuth2AccessTokenDO accessTokenDO = oauth2AccessTokenRedisDAO.get(accessToken);
        if (accessTokenDO != null) {
            return accessTokenDO;
        }

        // 获取不到，从 MySQL 中获取
        accessTokenDO = oauth2AccessTokenMapper.selectByAccessToken(accessToken);
        // 如果在 MySQL 存在，则往 Redis 中写入
        if (accessTokenDO != null && !DateUtils.isExpired(accessTokenDO.getExpiresTime())) {
            oauth2AccessTokenRedisDAO.set(accessTokenDO);
        }
        return accessTokenDO;
    }

    @Override
    public OAuth2AccessTokenDO checkAccessToken(String accessToken) {
        //获取数据库中存储的Token，存在且未过期即是有效的
        OAuth2AccessTokenDO accessTokenDO = getAccessToken(accessToken);
        if (accessTokenDO == null) {
            throw exception0(ErrorCode.UNAUTHORIZED.getCode(), "访问令牌不存在");
        }
        if (DateUtils.isExpired(accessTokenDO.getExpiresTime())) {
            throw exception0(ErrorCode.UNAUTHORIZED.getCode(), "访问令牌已过期");
        }

        log.info("校验AccessToken: accessTokenDO=" + JsonUtils.toJsonString(accessTokenDO));
        return accessTokenDO;
    }
}
