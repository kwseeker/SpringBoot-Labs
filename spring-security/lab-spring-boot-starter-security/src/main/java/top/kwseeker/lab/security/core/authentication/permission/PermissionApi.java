package top.kwseeker.lab.security.core.authentication.permission;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.kwseeker.lab.security.core.response.CommonResult;

import java.util.Collection;
import java.util.Set;

//@FeignClient(name = ApiConstants.NAME)
public interface PermissionApi {

    //String PREFIX = ApiConstants.PREFIX + "/permission";

    //@GetMapping(PREFIX + "/user-role-id-list-by-role-id")
    //CommonResult<Set<Long>> getUserRoleIdListByRoleIds(@RequestParam("roleIds") Collection<Long> roleIds);

    //@GetMapping(PREFIX + "/has-any-permissions")
    CommonResult<Boolean> hasAnyPermissions(@RequestParam("userId") Long userId,
                                            @RequestParam("permissions") String... permissions);

    //@GetMapping(PREFIX + "/has-any-roles")
    CommonResult<Boolean> hasAnyRoles(@RequestParam("userId") Long userId,
                                      @RequestParam("roles") String... roles);

    //@GetMapping(PREFIX + "/get-dept-data-permission")
    //@Operation(summary = "获得登陆用户的部门数据权限")
    //@Parameter(name = "userId", description = "用户编号", example = "2", required = true)
    //CommonResult<DeptDataPermissionRespDTO> getDeptDataPermission(@RequestParam("userId") Long userId);

}