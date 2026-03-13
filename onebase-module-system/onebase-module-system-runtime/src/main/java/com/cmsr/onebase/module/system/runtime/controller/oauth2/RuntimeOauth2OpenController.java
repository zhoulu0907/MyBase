package com.cmsr.onebase.module.system.runtime.controller.oauth2;

import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.common.enums.RunModeEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.service.oauth2.OAuth2OpenService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.oauth.AuthorizeURIRespVO;
import com.cmsr.onebase.module.system.vo.oauth.OAuth2OpenAccessTokenRespVO;
import com.cmsr.onebase.module.system.vo.user.OAuth2UserInfoRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 运行态第三方获取token信息相关服务
 *
 * @author yuxin
 * @date 2026-02
 */
@Tag(name = "Runtime - 第三方应用获取token信息")
@RestController
@RequestMapping("/system/oauth2")
@Validated
@Slf4j
public class RuntimeOauth2OpenController {

    @Resource
    private OAuth2OpenService oAuth2OpenService;

    @Resource
    private UserService userService;

    /**
     * 对应 Spring Security OAuth 的 TokenEndpoint 类的 postAccessToken 方法
     *
     * 授权码 authorization_code 模式时：code + redirectUri + state 参数
     * 密码 password 模式时：username + password + scope 参数
     * 刷新 refresh_token 模式时：refreshToken 参数
     * 客户端 client_credentials 模式：scope 参数
     * 简化 implicit 模式时：不支持
     *
     * 注意，默认需要传递 client_id + client_secret 参数
     */
    @PostMapping("/authorize/token")
    @PermitAll
    @ApiSignIgnore
    @Operation(summary = "获得访问令牌", description = "适合 code 授权码模式，或者 implicit 简化模式；在 sso.vue 单点登录界面被【获取】调用")
    @Parameters({
            @Parameter(name = "grant_type", required = true, description = "授权类型", example = "code"),
            @Parameter(name = "code", description = "授权范围", example = "userinfo.read"),
            @Parameter(name = "redirect_uri", description = "重定向 URI", example = "http://cmsr.com"),
            @Parameter(name = "state", description = "状态", example = "1"),
            @Parameter(name = "username", example = "tudou"),
            @Parameter(name = "password", example = "cai"), // 多个使用空格分隔
            @Parameter(name = "scope", example = "user_info"),
            @Parameter(name = "refresh_token", example = "123424233"),
    })
    public CommonResult<OAuth2OpenAccessTokenRespVO> postAccessToken(HttpServletRequest request,
                                                                     @RequestParam("grant_type") String grantType,
                                                                     @RequestParam(value = "code", required = false) String code, // 授权码模式
                                                                     @RequestParam(value = "redirect_uri", required = false) String redirectUri, // 授权码模式
                                                                     @RequestParam(value = "state", required = false) String state, // 授权码模式
                                                                     @RequestParam(value = "username", required = false) String username, // 密码模式
                                                                     @RequestParam(value = "password", required = false) String password, // 密码模式
                                                                     @RequestParam(value = "scope", required = false) String scope, // 密码模式
                                                                     @RequestParam(value = "refresh_token", required = false) String refreshToken) { // 刷新模式

        return oAuth2OpenService.postAccessToken(request, grantType, code, redirectUri, state, username, password, scope, refreshToken, RunModeEnum.RUNTIME.getValue());
    }

    /**
     * 对应 Spring Security OAuth 的 AuthorizationEndpoint 类的 approveOrDeny 方法
     *
     * 场景一：【自动授权 autoApprove = true】
     *      刚进入 sso.vue 界面，调用该接口，用户历史已经给该应用做过对应的授权，或者 OAuth2Client 支持该 scope 的自动授权
     * 场景二：【手动授权 autoApprove = false】
     *      在 sso.vue 界面，用户选择好 scope 授权范围，调用该接口，进行授权。此时，approved 为 true 或者 false
     *
     * 因为前后端分离，Axios 无法很好的处理 302 重定向，所以和 Spring Security OAuth 略有不同，返回结果是重定向的 URL，剩余交给前端处理
     */
    @ApiSignIgnore
    @PostMapping("/authorize/code")
    @Operation(summary = "申请授权", description = "适合 code 授权码模式，或者 implicit 简化模式；在 sso.vue 单点登录界面被【提交】调用")
    @Parameters({
            @Parameter(name = "response_type", required = true, description = "响应类型", example = "code"),
            @Parameter(name = "client_id", required = true, description = "客户端编号", example = "tudou"),
            @Parameter(name = "scope", description = "授权范围", example = "userinfo.read"), // 使用 Map<String, Boolean> 格式，Spring MVC 暂时不支持这么接收参数
            @Parameter(name = "redirect_uri", required = true, description = "重定向 URI", example = "http://cmsr.com"),
            @Parameter(name = "auto_approve", required = true, description = "用户是否接受", example = "true"),
            @Parameter(name = "state", example = "1")
    })
    public CommonResult<AuthorizeURIRespVO> approveOrDeny(@RequestParam("response_type") String responseType,
                                                          @RequestParam("client_id") String clientId,
                                                          @RequestParam(value = "scope", required = false) String scope,
                                                          @RequestParam("redirect_uri") String redirectUri,
                                                          @RequestParam(value = "auto_approve") Boolean autoApprove,
                                                          @RequestParam(value = "state", required = false) String state) {
        return oAuth2OpenService.approveOrDeny(responseType, clientId, scope, redirectUri, autoApprove, state);
    }

    /**
     * 对应 Spring Security OAuth 的 AuthorizationEndpoint 类的 authorize 方法
     */
    @GetMapping("/authorize/client")
    @Operation(summary = "获得授权信息", description = "适合 code 授权码模式，或者 implicit 简化模式；在 sso.vue 单点登录界面被【获取】调用")
    @Parameter(name = "clientId", required = true, description = "客户端编号", example = "tudou")
    public CommonResult<AuthorizeURIRespVO> authorize(@RequestParam("clientId") String clientId) {
        return oAuth2OpenService.authorize(clientId);
    }

    @PostMapping("/user/get")
    @Operation(summary = "获取用户信息")
    @PermitAll
    @ApiSignIgnore
    @TenantIgnore
    CommonResult<OAuth2UserInfoRespVO> getUser(@RequestParam("access_token") String accessToken) {

        return CommonResult.success(userService.getUserInfoByToken(accessToken));
    }


}