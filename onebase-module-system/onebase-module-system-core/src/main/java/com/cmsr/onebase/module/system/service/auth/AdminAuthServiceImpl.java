package com.cmsr.onebase.module.system.service.auth;

import cn.hutool.core.util.ObjUtil;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.framework.common.util.validation.ValidationUtils;
import com.cmsr.onebase.framework.common.biz.security.SecurityConfigApi;
import com.cmsr.onebase.framework.common.biz.security.dto.LoginFailureResultDTO;
import com.cmsr.onebase.framework.common.biz.security.dto.PasswordExpiryCheckDTO;
import com.cmsr.onebase.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.cmsr.onebase.module.system.api.sms.SmsCodeApi;
import com.cmsr.onebase.module.system.api.user.AdminUserRoleApi;
import com.cmsr.onebase.module.system.convert.auth.AuthConvert;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.logger.LoginLogTypeEnum;
import com.cmsr.onebase.module.system.enums.logger.LoginResultEnum;
import com.cmsr.onebase.module.system.enums.oauth2.OAuth2ClientConstants;
import com.cmsr.onebase.module.system.enums.permission.RoleCodeEnum;
import com.cmsr.onebase.module.system.enums.sms.SmsSceneEnum;
import com.cmsr.onebase.module.system.enums.tenant.TenantCodeEnum;
import com.cmsr.onebase.module.system.service.logger.LoginLogService;
import com.cmsr.onebase.module.system.service.member.MemberService;
import com.cmsr.onebase.module.system.service.oauth2.OAuth2TokenService;
import com.cmsr.onebase.module.system.service.permission.PermissionService;
import com.cmsr.onebase.module.system.service.permission.RoleService;
import com.cmsr.onebase.module.system.service.tenant.TenantService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.CaptchaVerificationReqVO;
import com.cmsr.onebase.module.system.vo.auth.*;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import jakarta.validation.Validator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.util.servlet.ServletUtils.getClientIP;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;

/**
 * Auth Service 实现类
 */
@Service
@RefreshScope
@Slf4j
public class AdminAuthServiceImpl implements AdminAuthService {

    @Resource
    private UserService        userService;
    @Resource
    private LoginLogService    loginLogService;
    @Resource
    private OAuth2TokenService oauth2TokenService;
    @Resource
    private MemberService      memberService;
    @Resource
    private Validator          validator;
    @Resource
    private CaptchaService     captchaService;
    @Resource
    private SmsCodeApi         smsCodeApi;
    /**
     * 验证码的开关，默认为 true
     */
    @Value("${onebase.captcha.enable:true}")
    @Setter // 为了单测：开启或者关闭验证码
    private Boolean            captchaEnable;
    /**
     * 平台租户验证开关，默认为 false
     */
    @Value("${onebase.platform-tenant.enable-create-app:false}")
    @Setter // 为了单测：开启或者关闭验证码
    private Boolean            platformTenantEnableCreateApp;

    @Resource
    private TenantService     tenantService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private SecurityConfigApi securityConfigApi;

    @Resource
    private RoleService roleService;

    @Resource
    private AdminUserRoleApi adminUserRoleApi;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public AdminUserDO authenticate(String username, String password) {
        final LoginLogTypeEnum logTypeEnum = LoginLogTypeEnum.LOGIN_USERNAME;
        // 校验账号是否存在
        AdminUserDO user = userService.getUserByUsername(username);
        checkUserPsdAndStatus(username, password, user, logTypeEnum);
        return user;
    }

    public AdminUserDO mobileAuthenticate(String mobile, String password) {
        final LoginLogTypeEnum logTypeEnum = LoginLogTypeEnum.LOGIN_USERNAME;
        // 校验账号是否存在
        AdminUserDO user = userService.getUserByMobile(mobile);
        checkUserPsdAndStatus(mobile, password, user, logTypeEnum);
        return user;
    }

    private void checkUserPsdAndStatus(String account, String password, AdminUserDO user, LoginLogTypeEnum logTypeEnum) {
        if (user == null) {
            createLoginLog(null, account, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_NO_EXISTS);
        }

        Long userId = user.getId();

        // 检查账号是否被防暴力破解锁定
        securityConfigApi.checkAccountLocked(userId);

        // 验证密码
        boolean passwordMatched = userService.isPasswordMatch(password, user.getPassword());
        if (!passwordMatched) {
            // 密码错误，记录失败次数并获取返回结果
            LoginFailureResultDTO failureResult = securityConfigApi.recordLoginFailure(userId).getData();
            createLoginLog(userId, account, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            // 使用失败记录中的提示信息作为错误信息参数
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS, failureResult.getMessage());
        }

        // 密码正确，清除失败记录
        securityConfigApi.clearLoginFailureRecord(userId);

        // 校验是否禁用
        if (CommonStatusEnum.isDisable(user.getStatus())) {
            createLoginLog(userId, account, logTypeEnum, LoginResultEnum.USER_DISABLED);
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }
    }


    @Override
    public AuthLoginRespVO adminLogin(UserLoginReqVO reqVO) {
        // 1. 校验验证码
        validateCaptcha(reqVO);

        // 2. 使用账号密码，进行登录
        AdminUserDO user = authenticate(reqVO.getUsername(), reqVO.getPassword());

        // 3. 校验是否是平台管理员
        boolean isAdmin = permissionService.isPlatformSuperAdmin(user.getId());
        if (!isAdmin) {
            throw exception(AUTH_LOGIN_USER_NOT_ADMIN_ERROR);
        }

        // 4. 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getId(), reqVO.getUsername(), reqVO.getDeviceId(), LoginLogTypeEnum.LOGIN_USERNAME);
    }

    @Override
    public AuthLoginRespVO login(AuthLoginReqVO reqVO) {
        // 校验验证码
        validateCaptcha(reqVO);

        // 增加日志输出，便于调试
        log.debug("platformTenantEnableCreateApp配置值: {}", platformTenantEnableCreateApp);

        // 确保配置值不为null，并且为false时才执行校验
        if (Boolean.FALSE.equals(platformTenantEnableCreateApp)) {
            log.info("平台租户创建应用功能已禁用，开始校验租户信息");
            // 校验当前用户绑定的租户是否为平台租户，是则不允许登录
            tenantService.handleTenantInfo(tenant -> {
                if (tenant.getTenantCode().equals(TenantCodeEnum.PLATFORM_TENANT.getCode())) {
                    log.warn("平台租户用户尝试登录，但平台租户创建应用功能已禁用");
                    throw exception(AUTH_LOGIN_PLATFORM_TENANT_ERROR);
                }
            });
        } else {
            log.debug("平台租户创建应用功能已启用，跳过租户校验");
        }

        // 使用账号密码，进行登录
        AdminUserDO user = authenticate(reqVO.getUsername(), reqVO.getPassword());
        AuthLoginRespVO authLoginRespVO = createTokenAfterLoginSuccess(user.getId(), reqVO.getUsername(), reqVO.getDeviceId(), LoginLogTypeEnum.LOGIN_USERNAME);
        // 设置是否管理员
        authLoginRespVO.setAdminFlag(findAdminFlag(RoleCodeEnum.TENANT_ADMIN.getCode(), user.getId()));
        return authLoginRespVO;
    }

    @Override
    public AuthLoginRespVO corpLogin(CorpAuthLoginReqVO reqVO) {
        // 校验验证码
        mobileValidateCaptcha(reqVO);

        // 2. 使用账号密码，进行登录
        AdminUserDO user = mobileAuthenticate(reqVO.getMobile(), reqVO.getPassword());

        AuthLoginRespVO authLoginRespVO = createCorpAfterLoginSuccess(user.getCorpId(), user.getId(), reqVO.getMobile(), reqVO.getDeviceId(), LoginLogTypeEnum.LOGIN_MOBILE);
        // 设置是否管理员
        authLoginRespVO.setAdminFlag(findAdminFlag(RoleCodeEnum.CORP_ADMIN.getCode(), user.getId()));
        // 回显当前登录用户的企业id
        authLoginRespVO.setCorpId(user.getCorpId());
        return authLoginRespVO;
    }

    public boolean findAdminFlag(String roleCode, Long userId) {
        // 获取权限
        RoleDO roleDO = roleService.getRoleIdsByCode(roleCode);
        if (null != roleDO) {
            // 查询用户是否管理员
            boolean adminFlag = userService.findAdminByRoleIdAndUserId(roleDO.getId(), userId);
            return adminFlag;
        }
        return false;
    }

    @Override
    public void sendSmsCode(AuthSmsSendReqVO reqVO) {
        // 如果是重置密码场景，需要校验图形验证码是否正确
        if (Objects.equals(SmsSceneEnum.ADMIN_MEMBER_RESET_PASSWORD.getScene(), reqVO.getScene())) {
            ResponseModel response = doValidateCaptcha(reqVO);
            if (!response.isSuccess()) {
                throw exception(AUTH_REGISTER_CAPTCHA_CODE_ERROR, response.getRepMsg());
            }
        }

        // 登录场景，验证是否存在
        if (userService.getUserByMobile(reqVO.getMobile()) == null) {
            throw exception(AUTH_MOBILE_NOT_EXISTS);
        }
        // 发送验证码
        smsCodeApi.sendSmsCode(AuthConvert.INSTANCE.convert(reqVO).setCreateIp(getClientIP()));
    }

    @Override
    public AuthLoginRespVO smsLogin(AuthSmsLoginReqVO reqVO) {
        // 校验验证码
        smsCodeApi.useSmsCode(AuthConvert.INSTANCE.convert(reqVO, SmsSceneEnum.ADMIN_MEMBER_LOGIN.getScene(), getClientIP())).checkError();

        // 获得用户信息
        AdminUserDO user = userService.getUserByMobile(reqVO.getMobile());
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }

        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getId(), reqVO.getMobile(), reqVO.getDeviceId(), LoginLogTypeEnum.LOGIN_MOBILE);
    }

    private void createLoginLog(Long userId, String username,
                                LoginLogTypeEnum logTypeEnum, LoginResultEnum loginResult) {
        // 插入登录日志
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(logTypeEnum.getType());
        reqDTO.setTraceId("n/a");
        reqDTO.setUserId(userId);
        reqDTO.setUserType(getUserType().getValue());
        reqDTO.setUsername(username);
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setUserIp(ServletUtils.getClientIP());
        reqDTO.setResult(loginResult.getResult());
        loginLogService.createLoginLog(reqDTO);
        // 更新最后登录时间
        if (userId != null && Objects.equals(LoginResultEnum.SUCCESS.getResult(), loginResult.getResult())) {
            userService.updateUserLogin(userId, ServletUtils.getClientIP());
        }
    }

    void validateCaptcha(UserLoginReqVO reqVO) {
        ResponseModel response = doValidateCaptcha(reqVO);
        // 校验验证码
        if (!response.isSuccess()) {
            // 创建登录失败日志（验证码不正确)
            createLoginLog(null, reqVO.getUsername(), LoginLogTypeEnum.LOGIN_USERNAME, LoginResultEnum.CAPTCHA_CODE_ERROR);
            throw exception(AUTH_LOGIN_CAPTCHA_CODE_ERROR, response.getRepMsg());
        }
    }

    void mobileValidateCaptcha(MobileLoginReqVO reqVO) {
        ResponseModel response = doValidateCaptcha(reqVO);
        // 校验验证码
        if (!response.isSuccess()) {
            // 创建登录失败日志（验证码不正确)
            createLoginLog(null, reqVO.getMobile(), LoginLogTypeEnum.LOGIN_USERNAME, LoginResultEnum.CAPTCHA_CODE_ERROR);
            throw exception(AUTH_LOGIN_CAPTCHA_CODE_ERROR, response.getRepMsg());
        }
    }

    private ResponseModel doValidateCaptcha(CaptchaVerificationReqVO reqVO) {
        // 如果验证码关闭，则不进行校验
        if (!captchaEnable) {
            return ResponseModel.success();
        }
        ValidationUtils.validate(validator, reqVO, CaptchaVerificationReqVO.CodeEnableGroup.class);
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaVerification(reqVO.getCaptchaVerification());
        return captchaService.verification(captchaVO);
    }

    private AuthLoginRespVO createTokenAfterLoginSuccess(Long userId, String username, String deviceId, LoginLogTypeEnum logType) {
        return createCorpAfterLoginSuccess(null, userId, username, deviceId, logType);
    }

    private AuthLoginRespVO createCorpAfterLoginSuccess(Long corpId, Long userId, String username, String deviceId, LoginLogTypeEnum logType) {
        // 插入登陆日志
        createLoginLog(userId, username, logType, LoginResultEnum.SUCCESS);
        // 创建访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createCorpAccessToken(corpId, userId, getUserType().getValue(),
                OAuth2ClientConstants.CLIENT_ID_DEFAULT, null);

        // 检查并限制设备数，踢出超限的设备
        List<String> removedTokens = securityConfigApi.checkAndLimitDevices(userId, deviceId, accessTokenDO.getAccessToken()).getData();

        // 删除被踢出的Token
        if (removedTokens != null && !removedTokens.isEmpty()) {
            for (String removedToken : removedTokens) {
                oauth2TokenService.removeAccessToken(removedToken);
                log.info("用户[{}]设备数超限，已踢出Token: {}", userId, removedToken);
            }
        }

        TenantDO tennantDO = tenantService.getTenant(accessTokenDO.getTenantId());
        // 构建返回结果
        AuthLoginRespVO respVO = AuthConvert.INSTANCE.convert(accessTokenDO, tennantDO);

        // 检查密码有效期
        PasswordExpiryCheckDTO expiryCheckResult = securityConfigApi.checkPasswordExpiry(userId).getData();
        respVO.setPasswordExpiryInfo(expiryCheckResult);

        // 创建会话空闲检测Key
        securityConfigApi.createSessionIdleKey(userId, deviceId);

        return respVO;
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

        // 清理在线设备记录
        securityConfigApi.removeOnlineDevice(null, accessTokenDO.getUserId(), token);

        // 删除成功，则记录登出日志
        createLogoutLog(accessTokenDO.getUserId(), accessTokenDO.getUserType(), logType);
    }

    private void createLogoutLog(Long userId, Integer userType, Integer logType) {
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(logType);
        reqDTO.setTraceId("n/a");
        reqDTO.setUserId(userId);
        reqDTO.setUserType(userType);
        if (ObjUtil.equal(getUserType().getValue(), userType)) {
            reqDTO.setUsername(getUsername(userId));
        } else {
            reqDTO.setUsername(memberService.getMemberUserMobile(userId));
        }
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setUserIp(ServletUtils.getClientIP());
        reqDTO.setResult(LoginResultEnum.SUCCESS.getResult());
        loginLogService.createLoginLog(reqDTO);
    }

    private String getUsername(Long userId) {
        if (userId == null) {
            return null;
        }
        AdminUserDO user = userService.getUser(userId);
        return user != null ? user.getUsername() : null;
    }

    private UserTypeEnum getUserType() {
        return UserTypeEnum.THIRD;
    }

    @Override
    public AuthLoginRespVO register(AuthRegisterReqVO registerReqVO) {
        // 1. 校验验证码
        validateCaptcha(registerReqVO);

        // 2. 校验用户名是否已存在
        Long userId = userService.registerUser(registerReqVO);

        // 3. 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(userId, registerReqVO.getUsername(), registerReqVO.getDeviceId(), LoginLogTypeEnum.LOGIN_USERNAME);
    }

    @VisibleForTesting
    void validateCaptcha(AuthRegisterReqVO reqVO) {
        ResponseModel response = doValidateCaptcha(reqVO);
        // 验证不通过
        if (!response.isSuccess()) {
            throw exception(AUTH_REGISTER_CAPTCHA_CODE_ERROR, response.getRepMsg());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(AuthResetPasswordReqVO reqVO) {
        // 暂时屏蔽验证码功能后续恢复
        // AdminUserDO userByMobile = userService.getUserByMobile(reqVO.getMobile());
        // if (userByMobile == null) {
        //     throw exception(USER_MOBILE_NOT_EXISTS);
        // }
        //
        // smsCodeApi.useSmsCode(new SmsCodeUseReqDTO()
        //         .setCode(reqVO.getCode())
        //         .setMobile(reqVO.getMobile())
        //         .setScene(SmsSceneEnum.ADMIN_MEMBER_RESET_PASSWORD.getScene())
        //         .setUsedIp(getClientIP())
        // ).checkError();

        AdminUserDO user = userService.getUser(reqVO.getUserId());
        if (user == null) {
            throw exception(USER_MOBILE_NOT_EXISTS);
        }
        userService.updateUserPassword(user.getId(), reqVO.getPassword());
    }
}
