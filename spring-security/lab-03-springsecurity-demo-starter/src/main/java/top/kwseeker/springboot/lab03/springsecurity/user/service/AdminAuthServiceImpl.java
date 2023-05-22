package top.kwseeker.springboot.lab03.springsecurity.user.service;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.kwseeker.lab.security.core.authentication.user.UserTypeEnum;
import top.kwseeker.springboot.lab03.springsecurity.oauth2.dal.dataobject.OAuth2AccessTokenDO;
import top.kwseeker.springboot.lab03.springsecurity.oauth2.enums.OAuth2ClientConstants;
import top.kwseeker.springboot.lab03.springsecurity.oauth2.service.OAuth2TokenService;
import top.kwseeker.springboot.lab03.springsecurity.user.controller.vo.AuthLoginReqVO;
import top.kwseeker.springboot.lab03.springsecurity.user.controller.vo.AuthLoginRespVO;
import top.kwseeker.springboot.lab03.springsecurity.user.dal.dataobject.AdminUserDO;

import javax.annotation.Resource;
import javax.validation.Validator;

/**
 * Auth Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class AdminAuthServiceImpl implements AdminAuthService {

    @Resource
    private AdminUserService userService;
    @Resource
    private OAuth2TokenService oauth2TokenService;
    @Resource
    private SocialUserService socialUserService;
    @Resource
    private MemberService memberService;
    @Resource
    private Validator validator;

    /**
     * 验证码的开关，默认为 true
     */
    @Value("${yudao.captcha.enable:true}")
    private Boolean captchaEnable;

    @Override
    public AdminUserDO authenticate(String username, String password) {
        // 校验账号是否存在
        AdminUserDO user = userService.getUserByUsername(username);
        if (user == null) {
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        if (!userService.isPasswordMatch(password, user.getPassword())) {
            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        // 校验是否禁用
        if (ObjectUtil.notEqual(user.getStatus(), CommonStatusEnum.ENABLE.getStatus())) {
            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.USER_DISABLED);
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }
        return user;
    }

    @Override
    public AuthLoginRespVO login(AuthLoginReqVO reqVO) {
        // 校验验证码
        //validateCaptcha(reqVO);

        // 使用账号密码，进行登录
        AdminUserDO user = authenticate(reqVO.getUsername(), reqVO.getPassword());

        // 如果 socialType 非空，说明需要绑定社交用户
        //if (reqVO.getSocialType() != null) {
        //    socialUserService.bindSocialUser(new SocialUserBindReqDTO(user.getId(), getUserType().getValue(),
        //            reqVO.getSocialType(), reqVO.getSocialCode(), reqVO.getSocialState()));
        //}
        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getId(), reqVO.getUsername());
    }

    @Override
    public AuthLoginRespVO socialLogin(AuthSocialLoginReqVO reqVO) {
        // 使用 code 授权码，进行登录。然后，获得到绑定的用户编号
        Long userId = socialUserService.getBindUserId(UserTypeEnum.ADMIN.getValue(), reqVO.getType(),
                reqVO.getCode(), reqVO.getState());
        if (userId == null) {
            throw exception(AUTH_THIRD_LOGIN_NOT_BIND);
        }

        // 获得用户
        AdminUserDO user = userService.getUser(userId);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }

        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getId(), user.getUsername());
    }

    private AuthLoginRespVO createTokenAfterLoginSuccess(Long userId, String username) {
        // 插入登陆日志
        //createLoginLog(userId, username, logType, LoginResultEnum.SUCCESS);
        // 创建访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(userId, getUserType().getValue(),
                OAuth2ClientConstants.CLIENT_ID_DEFAULT, null);
        // 构建返回结果
        return AuthConvert.INSTANCE.convert(accessTokenDO);
    }

    @Override
    public AuthLoginRespVO refreshToken(String refreshToken) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.refreshAccessToken(refreshToken, OAuth2ClientConstants.CLIENT_ID_DEFAULT);
        return AuthConvert.INSTANCE.convert(accessTokenDO);
    }

    @Override
    public void logout(String token, Integer logType) {
        // 删除访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.removeAccessToken(token);
        if (accessTokenDO == null) {
            return;
        }
        // 删除成功，则记录登出日志
        createLogoutLog(accessTokenDO.getUserId(), accessTokenDO.getUserType(), logType);
    }

    private String getUsername(Long userId) {
        if (userId == null) {
            return null;
        }
        AdminUserDO user = userService.getUser(userId);
        return user != null ? user.getUsername() : null;
    }

    private UserTypeEnum getUserType() {
        return UserTypeEnum.ADMIN;
    }

}
