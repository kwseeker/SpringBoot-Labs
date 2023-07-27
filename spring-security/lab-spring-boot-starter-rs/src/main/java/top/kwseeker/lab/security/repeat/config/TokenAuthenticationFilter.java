package top.kwseeker.lab.security.repeat.config;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//import top.kwseeker.lab.security.config.SecurityProperties;
//import top.kwseeker.lab.security.core.LoginUser;
//import top.kwseeker.lab.security.core.authentication.oauth2.OAuth2TokenApi;
//import top.kwseeker.lab.security.core.authentication.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
//import top.kwseeker.lab.security.core.exception.ServiceException;
//import top.kwseeker.lab.security.core.exception.handler.GlobalExceptionHandler;
//import top.kwseeker.lab.security.core.response.CommonResult;
//import top.kwseeker.lab.security.core.util.JsonUtils;
//import top.kwseeker.lab.security.core.util.SecurityFrameworkUtils;
//import top.kwseeker.lab.security.core.util.ServletUtils;
//import top.kwseeker.lab.security.core.util.WebFrameworkUtils;

//@RequiredArgsConstructor //生成包含所有 final 字段的构造器
//public class TokenAuthenticationFilter extends OncePerRequestFilter implements Ordered {
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    //private final SecurityProperties securityProperties;
    //
    //private final GlobalExceptionHandler globalExceptionHandler;
    //
    //private final OAuth2TokenApi oauth2TokenApi;

    //public TokenAuthenticationFilter(SecurityProperties securityProperties, GlobalExceptionHandler globalExceptionHandler, OAuth2TokenApi oauth2TokenApi) {
    //    this.securityProperties = securityProperties;
    //    this.globalExceptionHandler = globalExceptionHandler;
    //    this.oauth2TokenApi = oauth2TokenApi;
    //}

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 情况一，基于 header[login-user] 获得用户，例如说来自 Gateway 或者其它服务透传
        //LoginUser loginUser = buildLoginUserByHeader(request);
        //
        //// 情况二，基于 Token 获得用户
        //// 注意，这里主要满足直接使用 Nginx 直接转发到 Spring Cloud 服务的场景。
        //if (loginUser == null) {
        //    String token = SecurityFrameworkUtils.obtainAuthorization(request, securityProperties.getTokenHeader());
        //    if (StrUtil.isNotEmpty(token)) {
        //        Integer userType = WebFrameworkUtils.getLoginUserType(request);
        //        try {
        //            // 1.1 基于 token 构建登录用户
        //            loginUser = buildLoginUserByToken(token, userType);
        //            // 1.2 模拟 Login 功能，方便日常开发调试
        //            if (loginUser == null) {
        //                loginUser = mockLoginUser(request, token, userType);
        //            }
        //        } catch (Throwable ex) {
        //            CommonResult<?> result = globalExceptionHandler.allExceptionHandler(request, ex);
        //            ServletUtils.writeJSON(response, result);
        //            return;
        //        }
        //    }
        //}
        //
        //// 设置当前用户
        //if (loginUser != null) {
        //    SecurityFrameworkUtils.setLoginUser(loginUser, request);
        //}
        System.out.println("TokenAuthenticationFilter doFilterInternal ");

        // 继续过滤链
        chain.doFilter(request, response);
    }

    //private LoginUser buildLoginUserByToken(String token, Integer userType) {
    //    try {
    //        // 校验访问令牌
    //        OAuth2AccessTokenCheckRespDTO accessToken = oauth2TokenApi.checkAccessToken(token).getCheckedData();
    //        if (accessToken == null) {
    //            return null;
    //        }
    //        // 用户类型不匹配，无权限
    //        if (ObjectUtil.notEqual(accessToken.getUserType(), userType)) {
    //            throw new AccessDeniedException("错误的用户类型");
    //        }
    //        // 构建登录用户
    //        return new LoginUser().setId(accessToken.getUserId()).setUserType(accessToken.getUserType())
    //                .setTenantId(accessToken.getTenantId()).setScopes(accessToken.getScopes());
    //    } catch (ServiceException serviceException) {
    //        // 校验 Token 不通过时，考虑到一些接口是无需登录的，所以直接返回 null 即可
    //        return null;
    //    }
    //}
    //
    ///**
    // * 模拟登录用户，方便日常开发调试
    // *
    // * 注意，在线上环境下，一定要关闭该功能！！！
    // *
    // * @param request 请求
    // * @param token 模拟的 token，格式为 {@link SecurityProperties#getMockSecret()} + 用户编号
    // * @param userType 用户类型
    // * @return 模拟的 LoginUser
    // */
    //private LoginUser mockLoginUser(HttpServletRequest request, String token, Integer userType) {
    //    if (!securityProperties.getMockEnable()) {
    //        return null;
    //    }
    //    // 必须以 mockSecret 开头
    //    if (!token.startsWith(securityProperties.getMockSecret())) {
    //        return null;
    //    }
    //    // 构建模拟用户
    //    Long userId = Long.valueOf(token.substring(securityProperties.getMockSecret().length()));
    //    return new LoginUser().setId(userId).setUserType(userType)
    //            .setTenantId(WebFrameworkUtils.getTenantId(request));
    //}
    //
    //private LoginUser buildLoginUserByHeader(HttpServletRequest request) {
    //    String loginUserStr = request.getHeader(SecurityFrameworkUtils.LOGIN_USER_HEADER);
    //    return StrUtil.isNotEmpty(loginUserStr) ? JsonUtils.parseObject(loginUserStr, LoginUser.class) : null;
    //}

    //@Override
    //public int getOrder() {
    //    return org.springframework.boot.autoconfigure.security.SecurityProperties.BASIC_AUTH_ORDER -1;
    //}
}
