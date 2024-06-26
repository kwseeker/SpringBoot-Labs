package top.kwseeker.lab.security.core.authentication.service;

import cn.hutool.core.collection.CollUtil;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import top.kwseeker.lab.security.core.LoginUser;
import top.kwseeker.lab.security.core.authentication.KeyValue;
import top.kwseeker.lab.security.core.authentication.permission.PermissionApi;
import top.kwseeker.lab.security.core.util.CacheUtils;
import top.kwseeker.lab.security.core.util.SecurityFrameworkUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * 默认的 {@link SecurityFrameworkService} 实现类
 *
 * @author 芋道源码
 */
@Slf4j
@AllArgsConstructor
public class SecurityFrameworkServiceImpl implements SecurityFrameworkService {

    private final PermissionApi permissionApi;

    /**
     * 针对 {@link #hasAnyRoles(String...)} 的缓存
     */
    private final LoadingCache<KeyValue<Long, List<String>>, Boolean> hasAnyRolesCache = CacheUtils.buildAsyncReloadingCache(
            Duration.ofMinutes(1L), // 过期时间 1 分钟
            new CacheLoader<KeyValue<Long, List<String>>, Boolean>() {

                @Override
                public Boolean load(KeyValue<Long, List<String>> key) {
                    return permissionApi.hasAnyRoles(key.getKey(), key.getValue().toArray(new String[0])).getCheckedData();
                }

            });

    /**
     * 针对 {@link #hasAnyPermissions(String...)} 的缓存
     */
    private final LoadingCache<KeyValue<Long, List<String>>, Boolean> hasAnyPermissionsCache = CacheUtils.buildAsyncReloadingCache(
            Duration.ofMinutes(1L), // 过期时间 1 分钟
            new CacheLoader<KeyValue<Long, List<String>>, Boolean>() {

                @Override
                public Boolean load(KeyValue<Long, List<String>> key) {
                    return permissionApi.hasAnyPermissions(key.getKey(), key.getValue().toArray(new String[0])).getCheckedData();
                }

            });

    @Override
    public boolean hasPermission(String permission) {
        return hasAnyPermissions(permission);
    }

    @Override
    @SneakyThrows
    public boolean hasAnyPermissions(String... permissions) {
        return hasAnyPermissionsCache.get(new KeyValue<>(SecurityFrameworkUtils.getLoginUserId(), Arrays.asList(permissions)));
    }

    @Override
    public boolean hasRole(String role) {
        log.info("hasRole: " + role);
        return hasAnyRoles(role);
    }

    @Override
    @SneakyThrows
    public boolean hasAnyRoles(String... roles) {
        return hasAnyRolesCache.get(new KeyValue<>(SecurityFrameworkUtils.getLoginUserId(), Arrays.asList(roles)));
    }

    @Override
    public boolean hasScope(String scope) {
        return hasAnyScopes(scope);
    }

    @Override
    public boolean hasAnyScopes(String... scope) {
        LoginUser user = SecurityFrameworkUtils.getLoginUser();
        if (user == null) {
            return false;
        }
        return CollUtil.containsAny(user.getScopes(), Arrays.asList(scope));
    }

}
