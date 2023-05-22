package top.kwseeker.springboot.lab03.springsecurity.user.controller;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.kwseeker.lab.security.config.SecurityProperties;
import top.kwseeker.lab.security.core.response.CommonResult;
import top.kwseeker.lab.security.core.util.SecurityFrameworkUtils;
import top.kwseeker.springboot.lab03.springsecurity.user.controller.vo.AuthLoginReqVO;
import top.kwseeker.springboot.lab03.springsecurity.user.controller.vo.AuthLoginRespVO;
import top.kwseeker.springboot.lab03.springsecurity.user.service.AdminAuthService;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/user/auth")
@Validated
@Slf4j
public class AuthController {

    @Resource
    private AdminAuthService authService;
    @Resource
    private AdminUserService userService;
    @Resource
    private SecurityProperties securityProperties;


    @PostMapping("/login")
    @PermitAll
    public CommonResult<AuthLoginRespVO> login(@RequestBody @Valid AuthLoginReqVO reqVO) {
        return CommonResult.success(authService.login(reqVO));
    }

    @PostMapping("/logout")
    @PermitAll
    public CommonResult<Boolean> logout(HttpServletRequest request) {
        String token = SecurityFrameworkUtils.obtainAuthorization(request, securityProperties.getTokenHeader());
        if (StrUtil.isNotBlank(token)) {
            authService.logout(token, LoginLogTypeEnum.LOGOUT_SELF.getType());
        }
        return CommonResult.success(true);
    }
}
