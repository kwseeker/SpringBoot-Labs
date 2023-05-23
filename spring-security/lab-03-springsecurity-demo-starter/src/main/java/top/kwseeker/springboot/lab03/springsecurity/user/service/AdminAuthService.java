package top.kwseeker.springboot.lab03.springsecurity.user.service;

import top.kwseeker.springboot.lab03.springsecurity.user.controller.vo.AuthLoginReqVO;
import top.kwseeker.springboot.lab03.springsecurity.user.controller.vo.AuthLoginRespVO;
import top.kwseeker.springboot.lab03.springsecurity.user.dal.dataobject.AdminUserDO;

import javax.validation.Valid;

/**
 * 管理后台的认证 Service 接口
 *
 * 提供用户的登录、登出的能力
 *
 * @author 芋道源码
 */
public interface AdminAuthService {

    /**
     * 验证账号 + 密码。如果通过，则返回用户
     *
     * @param username 账号
     * @param password 密码
     * @return 用户
     */
    AdminUserDO authenticate(String username, String password);

    /**
     * 账号登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    AuthLoginRespVO login(@Valid AuthLoginReqVO reqVO);

    /**
     * 基于 token 退出登录
     *
     * @param token token
     * @param logType 登出类型
     */
    void logout(String token);

    ///**
    // * 社交快捷登录，使用 code 授权码
    // *
    // * @param reqVO 登录信息
    // * @return 登录结果
    // */
    //AuthLoginRespVO socialLogin(@Valid AuthSocialLoginReqVO reqVO);

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 登录结果
     */
    AuthLoginRespVO refreshToken(String refreshToken);

}
