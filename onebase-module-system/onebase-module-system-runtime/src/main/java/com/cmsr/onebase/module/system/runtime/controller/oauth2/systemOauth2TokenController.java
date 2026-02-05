package com.cmsr.onebase.module.system.runtime.controller.oauth2;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.common.enums.RunModeEnum;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.http.HttpUtils;
import com.cmsr.onebase.module.system.convert.oauth2.OAuth2OpenConvert;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2ClientDO;
import com.cmsr.onebase.module.system.enums.oauth2.OAuth2GrantTypeEnum;
import com.cmsr.onebase.module.system.service.oauth2.OAuth2ApproveService;
import com.cmsr.onebase.module.system.service.oauth2.OAuth2ClientService;
import com.cmsr.onebase.module.system.service.oauth2.OAuth2GrantService;
import com.cmsr.onebase.module.system.service.oauth2.OAuth2TokenService;
import com.cmsr.onebase.module.system.util.oauth2.OAuth2Utils;
import com.cmsr.onebase.module.system.vo.oauth.OAuth2OpenAccessTokenRespVO;
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

import java.util.List;

import static com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;
import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 运行态第三方获取token信息相关服务
 *
 * @author yuxin
 * @date 2026-02
 */
@Tag(name = "Runtime - 第三方应用获取token信息")
@RestController
@RequestMapping("/system/oauth2/authorize")
@Validated
@Slf4j
public class systemOauth2TokenController {

    @Resource
    private OAuth2GrantService oauth2GrantService;
    @Resource
    private OAuth2ClientService oauth2ClientService;

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
        List<String> scopes = OAuth2Utils.buildScopes(scope);
        // 1.1 校验授权类型
        OAuth2GrantTypeEnum grantTypeEnum = OAuth2GrantTypeEnum.getByGrantType(grantType);
        if (grantTypeEnum == null) {
            throw exception(BAD_REQUEST.getCode(), StrUtil.format("未知授权类型({})", grantType));
        }
        if (grantTypeEnum == OAuth2GrantTypeEnum.IMPLICIT) {
            throw exception(BAD_REQUEST.getCode(), "Token 接口不支持 implicit 授权模式");
        }

        // 1.2 校验客户端
        String[] clientIdAndSecret = obtainBasicAuthorization(request);
        OAuth2ClientDO client = oauth2ClientService.validOAuthClientFromCache(clientIdAndSecret[0], clientIdAndSecret[1],
                grantType, scopes, redirectUri);

        // 2. 根据授权模式，获取访问令牌
        OAuth2AccessTokenDO accessTokenDO;
        switch (grantTypeEnum) {
            case AUTHORIZATION_CODE:
                accessTokenDO = oauth2GrantService.grantAuthorizationCodeForAccessToken(RunModeEnum.RUNTIME.getValue(),client.getClientId(), code, redirectUri, state);
                break;
            case PASSWORD:
                accessTokenDO = oauth2GrantService.grantPassword(username, password, client.getClientId(), scopes);
                break;
            case CLIENT_CREDENTIALS:
                accessTokenDO = oauth2GrantService.grantClientCredentials(client.getClientId(), scopes);
                break;
            case REFRESH_TOKEN:
                accessTokenDO = oauth2GrantService.grantRefreshToken(refreshToken, client.getClientId());
                break;
            default:
                throw new IllegalArgumentException("未知授权类型：" + grantType);
        }
        Assert.notNull(accessTokenDO, "访问令牌不能为空"); // 防御性检查
        return success(OAuth2OpenConvert.INSTANCE.convert(accessTokenDO));
    }

    private String[] obtainBasicAuthorization(HttpServletRequest request) {
        String[] clientIdAndSecret = HttpUtils.obtainBasicAuthorization(request);
        if (ArrayUtil.isEmpty(clientIdAndSecret) || clientIdAndSecret.length != 2) {
            throw exception(BAD_REQUEST.getCode(), "client_id 或 client_secret 未正确传递");
        }
        return clientIdAndSecret;
    }


}