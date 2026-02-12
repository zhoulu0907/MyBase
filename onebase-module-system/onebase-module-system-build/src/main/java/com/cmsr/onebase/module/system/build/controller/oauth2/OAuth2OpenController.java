package com.cmsr.onebase.module.system.build.controller.oauth2;

import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.common.enums.RunModeEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.oauth.AuthorizeURIRespVO;
import com.cmsr.onebase.module.system.vo.oauth.OAuth2OpenAccessTokenRespVO;
import com.cmsr.onebase.module.system.vo.oauth.OAuth2OpenAuthorizeInfoRespVO;
import com.cmsr.onebase.module.system.vo.oauth.OAuth2OpenCheckTokenRespVO;
import com.cmsr.onebase.module.system.service.oauth2.OAuth2OpenService;
import com.cmsr.onebase.module.system.vo.user.UserSimpleRespVO;
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
 * 提供给外部应用调用为主
 *
 * 一般来说，管理后台的 /system-api/* 是不直接提供给外部应用使用，主要是外部应用能够访问的数据与接口是有限的，而管理后台的 RBAC 无法很好的控制。
 * 参考大量的开放平台，都是独立的一套 OpenAPI，对应到【本系统】就是在 Controller 下新建 open 包，实现 /open-api/* 接口，然后通过 scope 进行控制。
 * 另外，一个公司如果有多个管理后台，它们 client_id 产生的 access token 相互之间是无法互通的，即无法访问它们系统的 API 接口，直到两个 client_id 产生信任授权。
 *
 * 考虑到【本系统】暂时不想做的过于复杂，默认只有获取到 access token 之后，可以访问【本系统】管理后台的 /system-api/* 所有接口，除非手动添加 scope 控制。
 * scope 的使用示例，可见 {@link OAuth2UserController} 类
 *
 */
@Tag(name = "管理后台 - OAuth2.0 授权")
@RestController
@RequestMapping("/system/oauth2")
@Validated
@Slf4j
public class OAuth2OpenController {

    @Resource
    private OAuth2OpenService oauth2OpenService;

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
    @PostMapping("/token")
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
        return oauth2OpenService.postAccessToken(request, grantType, code, redirectUri, state, username, password, scope, refreshToken, RunModeEnum.BUILD.getValue());
    }

    @PostMapping("/revoke-token")
    @PermitAll
    @Operation(summary = "删除访问令牌")
    @Parameter(name = "token", required = true, description = "访问令牌", example = "biu")
    public CommonResult<Boolean> revokeToken(HttpServletRequest request,
                                             @RequestParam("token") String token) {
        return oauth2OpenService.revokeToken(request, token);
    }

    /**
     * 对应 Spring Security OAuth 的 CheckTokenEndpoint 类的 checkToken 方法
     */
    @PostMapping("/check-token")
    @PermitAll
    @Operation(summary = "校验访问令牌")
    @Parameter(name = "token", required = true, description = "访问令牌", example = "biu")
    public CommonResult<OAuth2OpenCheckTokenRespVO> checkToken(HttpServletRequest request,
                                                               @RequestParam("token") String token) {
        return oauth2OpenService.checkToken(request, token);
    }

    /**
     * 对应 Spring Security OAuth 的 AuthorizationEndpoint 类的 authorize 方法
     */
    @GetMapping("/authorize")
    @Operation(summary = "获得授权信息", description = "适合 code 授权码模式，或者 implicit 简化模式；在 sso.vue 单点登录界面被【获取】调用")
    @Parameter(name = "clientId", required = true, description = "客户端编号", example = "tudou")
    public CommonResult<OAuth2OpenAuthorizeInfoRespVO> authorize(@RequestParam("clientId") String clientId) {
        return oauth2OpenService.authorize(clientId);
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
        return oauth2OpenService.approveOrDeny(responseType, clientId, scope, redirectUri, autoApprove, state);
    }

    @PostMapping("/user/get")
    @Operation(summary = "获取用户信息")
    @PermitAll
    @ApiSignIgnore
    CommonResult<UserSimpleRespVO> getUser(@RequestParam("access_token") String accessToken) {

        return CommonResult.success(userService.getUserInfoByToken(accessToken));
    }



}
