package com.cmsr.onebase.module.system.service.oauth2;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.vo.oauth.AuthorizeURIRespVO;
import com.cmsr.onebase.module.system.vo.oauth.OAuth2OpenAccessTokenRespVO;
import com.cmsr.onebase.module.system.vo.oauth.OAuth2OpenAuthorizeInfoRespVO;
import com.cmsr.onebase.module.system.vo.oauth.OAuth2OpenCheckTokenRespVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * OAuth2.0 开放接口 Service 接口
 *
 * 提供对外部应用调用的 OAuth2 授权相关接口
 *
 */
public interface OAuth2OpenService {

    /**
     * 获得访问令牌
     *
     * @param request HTTP请求
     * @param grantType 授权类型
     * @param code 授权码（授权码模式）
     * @param redirectUri 重定向URI（授权码模式）
     * @param state 状态（授权码模式）
     * @param username 用户名（密码模式）
     * @param password 密码（密码模式）
     * @param scope 授权范围
     * @param refreshToken 刷新令牌（刷新模式）
     * @return 访问令牌信息
     */
    CommonResult<OAuth2OpenAccessTokenRespVO> postAccessToken(HttpServletRequest request,
                                                              String grantType,
                                                              String code,
                                                              String redirectUri,
                                                              String state,
                                                              String username,
                                                              String password,
                                                              String scope,
                                                              String refreshToken,
                                                              String runMode);

    /**
     * 删除访问令牌
     *
     * @param request HTTP请求
     * @param token 访问令牌
     * @return 是否删除成功
     */
    CommonResult<Boolean> revokeToken(HttpServletRequest request, String token);

    /**
     * 校验访问令牌
     *
     * @param request HTTP请求
     * @param token 访问令牌
     * @return 令牌校验信息
     */
    CommonResult<OAuth2OpenCheckTokenRespVO> checkToken(HttpServletRequest request, String token);

    /**
     * 获得授权信息
     *
     * @param clientId 客户端编号
     * @return 授权信息
     */
    CommonResult<OAuth2OpenAuthorizeInfoRespVO> authorize(String clientId);

    /**
     * 申请授权
     *
     * @param responseType 响应类型
     * @param clientId 客户端编号
     * @param scope 授权范围
     * @param redirectUri 重定向URI
     * @param autoApprove 是否自动授权
     * @param state 状态
     * @return 授权结果
     */
    CommonResult<AuthorizeURIRespVO> approveOrDeny(String responseType,
                                                   String clientId,
                                                   String scope,
                                                   String redirectUri,
                                                   Boolean autoApprove,
                                                   String state);

}