package com.cmsr.onebase.module.system.runtime.service.auth;

import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.vo.auth.*;
import com.cmsr.onebase.module.system.vo.user.UserForgetPasswordReqVO;
import jakarta.validation.Valid;


/**
 * Auth Service 实现类
 * 1. 运行态登录：自有用户 登录
 * 1. 运行态登录：三方用户 登录
 * @author mty
 */
public interface RuntimeAuthService {

    /**
     * 验证账号 + 密码。如果通过，则返回用户
     *
     * @param username 账号
     * @param password 密码
     * @return 用户
     */
    AdminUserDO authenticate(String username, String password);

    /**
     * 基于 token 退出登录
     *
     * @param token token
     * @param logType 登出类型
     */
    void logout(String token, Integer logType);

    /**
     * 手机号登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    AuthLoginRespVO appMobileLogin(AppMobileLoginReqVO reqVO);

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 登录结果
     */
    AuthLoginRespVO refreshToken(String refreshToken);

    /**
     * 重置密码
     *
     * @param reqVO 验证码信息
     */
    void resetPassword(AuthResetPasswordReqVO reqVO);

    /**
     * 账号密码登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    AuthLoginRespVO appUsernameLogin(@Valid AppUserNameLoginReqVO reqVO);

    /**
     * 账号登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    AuthLoginRespVO corpLogin(@Valid CorpAuthLoginReqVO reqVO);

    /**
     * 账号登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    ThirdAuthLoginRespVO thirdLogin(@Valid ThirdAuthLoginReqVO reqVO);
    /**
     * 忘记密码
     *
     * @param reqVO 登录信息
     */
    void forgetPassword(@Valid UserForgetPasswordReqVO reqVO);
}
