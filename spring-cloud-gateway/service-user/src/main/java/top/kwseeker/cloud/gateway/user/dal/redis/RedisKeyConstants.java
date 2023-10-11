package top.kwseeker.cloud.gateway.user.dal.redis;

import top.kwseeker.cloud.gateway.user.dal.dataobject.oauth2.OAuth2AccessTokenDO;

public interface RedisKeyConstants {

    /**
     * 访问令牌的缓存
     * <p>
     * KEY 格式：oauth2_access_token:{token}
     * VALUE 数据类型：String 访问令牌信息 {@link OAuth2AccessTokenDO}
     * <p>
     * 由于动态过期时间，使用 RedisTemplate 操作
     */
    String OAUTH2_ACCESS_TOKEN = "oauth2_access_token:%s";
}
