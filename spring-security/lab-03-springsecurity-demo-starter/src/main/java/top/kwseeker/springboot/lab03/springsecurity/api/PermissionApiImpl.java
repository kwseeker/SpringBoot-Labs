package top.kwseeker.springboot.lab03.springsecurity.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.kwseeker.lab.security.core.authentication.permission.PermissionApi;
import top.kwseeker.lab.security.core.response.CommonResult;

@Slf4j
@Component
public class PermissionApiImpl implements PermissionApi {

    @Override
    public CommonResult<Boolean> hasAnyPermissions(Long userId, String... permissions) {
        log.info(">>> hasAnyPermissions: userId={}, permissions={}", userId, permissions);
        return null;
    }

    @Override
    public CommonResult<Boolean> hasAnyRoles(Long userId, String... roles) {
        log.info(">>> hasAnyPermissions: userId={}, permissions={}", userId, roles);
        return null;
    }
}
