package com.cmsr.onebase.module.system.runtime.service.auth;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.cmsr.onebase.framework.common.biz.security.SecurityConfigApi;
import com.cmsr.onebase.framework.common.biz.security.dto.LoginFailureResultDTO;
import com.cmsr.onebase.framework.common.biz.security.dto.PasswordExpiryCheckDTO;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.RunModeEnum;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.util.servlet.ServletUtils;
import com.cmsr.onebase.framework.common.util.validation.ValidationUtils;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.app.api.security.AppAuthSecurityApi;
import com.cmsr.onebase.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.cmsr.onebase.module.system.api.sms.SmsCodeApi;
import com.cmsr.onebase.module.system.convert.auth.AuthConvert;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.logger.LoginLogTypeEnum;
import com.cmsr.onebase.module.system.enums.logger.LoginResultEnum;
import com.cmsr.onebase.module.system.enums.login.LoginSourceEnum;
import com.cmsr.onebase.module.system.enums.login.LongTypeEnum;
import com.cmsr.onebase.module.system.enums.oauth2.OAuth2ClientConstants;
import com.cmsr.onebase.module.system.enums.permission.RoleCodeEnum;
import com.cmsr.onebase.module.system.enums.tenant.TenantCodeEnum;
import com.cmsr.onebase.module.system.framework.security.core.PwdEnHelper;
import com.cmsr.onebase.module.system.service.corp.CorpService;
import com.cmsr.onebase.module.system.service.corpapprelation.CorpAppRelationService;
import com.cmsr.onebase.module.system.service.logger.LoginLogService;
import com.cmsr.onebase.module.system.service.member.MemberService;
import com.cmsr.onebase.module.system.service.oauth2.OAuth2TokenService;
import com.cmsr.onebase.module.system.service.permission.RoleService;
import com.cmsr.onebase.module.system.service.tenant.TenantService;
import com.cmsr.onebase.module.system.service.user.UserAppRelationService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.CaptchaVerificationReqVO;
import com.cmsr.onebase.module.system.vo.auth.*;
import com.cmsr.onebase.module.system.vo.corp.CorpRespVO;
import com.cmsr.onebase.module.system.vo.user.ThirdSupplementUserReqVO;
import com.cmsr.onebase.module.system.vo.user.UserAppVO;
import com.cmsr.onebase.module.system.vo.user.UserForgetPasswordReqVO;
import com.google.common.annotations.VisibleForTesting;
import com.mybatisflex.core.tenant.TenantManager;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import jakarta.validation.Validator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;
import static com.cmsr.onebase.module.system.enums.LogRecordConstants.*;

/**
 * Auth Service 实现类
 * 1. 运行态登录：自有用户 登录
 * 1. 运行态登录：三方用户 登录
 *
 * @author mty
 */
@Service
@RefreshScope
@Slf4j
public class RuntimeAuthServiceImpl implements RuntimeAuthService {

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
    private SecurityConfigApi securityConfigApi;

    @Resource
    private AppAuthSecurityApi appAuthSecurityApi;

    @Resource
    private AppApplicationApi appApplicationApi;

    @Resource
    private CorpService corpService;

    @Resource
    private RoleService roleService;

    @Resource
    private CorpAppRelationService corpAppRelationService;

    @Resource
    private PwdEnHelper pwdEnHelper;

    @Resource
    private UserAppRelationService userAppRelationService;

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

    public AdminUserDO thirdAuthenticate(String mobile, String verifyCode) {
        final LoginLogTypeEnum logTypeEnum = LoginLogTypeEnum.LOGIN_MOBILE;
        // 校验账号是否存在     //验证码登录，不需要在验证密码
        return userService.getUserByMobile(mobile);
    }


    private void checkUserPsdAndStatus(String account, String password, AdminUserDO user, LoginLogTypeEnum logTypeEnum) {
        if (user == null) {
            createLoginLog(null, account, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_NO_EXISTS);
        }
        // 检查账号是否被防暴力破解锁定
        securityConfigApi.checkAccountLocked(user.getId());
        // 验证密码
        checkPasswordMatched(password, user, account, logTypeEnum);
        // 校验是否禁用
        if (CommonStatusEnum.isDisable(user.getStatus())) {
            createLoginLog(user.getId(), account, logTypeEnum, LoginResultEnum.USER_DISABLED);
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }
    }


    private Long checkAppAndGetTenantId(Long appId) {
        ApplicationDTO applicationDTO = TenantManager.withoutTenantCondition(() -> appApplicationApi.findAppApplicationById(appId));

        if (applicationDTO == null) {
            throw exception(AUTH_LOGIN_APP_DELETE_OR_DISABLE);
        }
        return applicationDTO.getTenantId();
    }

    private void checkPasswordMatched(String password, AdminUserDO user, String account, LoginLogTypeEnum logTypeEnum) {
        boolean passwordMatched = userService.isPasswordMatch(password, user.getPassword());
        if (!passwordMatched) {
            Long userId = user.getId();
            // 密码错误，记录失败次数并获取返回结果
            LoginFailureResultDTO failureResult = securityConfigApi.recordLoginFailure(userId).getData();
            createLoginLog(userId, account, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            // 使用失败记录中的提示信息作为错误信息参数
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS, failureResult.getMessage());
        }
    }

    @Override
    @LogRecord(type = LOGIN_USER_TYPE, subType = LOGIN_USER_APP_SUB_TYPE, bizNo = "{{#user.id}}",
            success = LOGIN_USER_APP_SUCCESS)
    public AuthLoginRespVO appUsernameLogin(AppUserNameLoginReqVO reqVO) {
        // 校验验证码
        validateCaptcha(reqVO);

        // 验证应用是否存在,同时获取租户ID
        Long tenanId = checkAppAndGetTenantId(reqVO.getAppId());

        AtomicReference<AuthLoginRespVO> authLoginRespVO = new AtomicReference<>();
        // 设置应用所在的租户环境
        TenantUtils.execute(tenanId, () -> {
            // 检查平台租户是否允许创建应用
            // checkPlatformAdminEnableAppCreate();

            // 解密原文
            reqVO.setPassword(pwdEnHelper.decryptHexStr(reqVO.getPassword()));
            // 使用账号密码，进行登录
            AdminUserDO user = authenticate(reqVO.getUsername(), reqVO.getPassword());

            authLoginRespVO.set(createAfterLoginSuccess(user.getUserType(), user.getCorpId(), reqVO.getAppId(), user.getId(), reqVO.getUsername(), reqVO.getDeviceId(), LoginLogTypeEnum.LOGIN_USERNAME));

            LogRecordContext.putVariable("user", user);
        });

        AuthLoginRespVO authLogVO = authLoginRespVO.get();
        authLogVO.setLoginSource(LoginSourceEnum.APPLOGIN.getCode());
        return authLogVO;

    }

    public boolean findCorpAdminFlag(String roleCode, Long userId) {
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
    @LogRecord(type = LOGIN_USER_TYPE, subType = LOGIN_USER_SAAS_SUB_TYPE, bizNo = "{{#user.id}}",
            success = LOGIN_USER_SAAS_SUCCESS)
    public AuthLoginRespVO appMobileLogin(AppMobileLoginReqVO reqVO) {
        // 校验验证码
        mobileValidateCaptcha(reqVO);

        // 验证应用是否存在
        Long tenanId = checkAppAndGetTenantId(reqVO.getAppId());

        AtomicReference<AuthLoginRespVO> authLoginRespVO = new AtomicReference<>();
        // 设置应用所在的租户环境
        TenantUtils.execute(tenanId, () -> {
            // 检查平台租户是否允许创建应用
            // checkPlatformAdminEnableAppCreate();

            // 解密原文
            reqVO.setPassword(pwdEnHelper.decryptHexStr(reqVO.getPassword()));
            // 使用手机密码，进行登录
            AdminUserDO user = mobileAuthenticate(reqVO.getMobile(), reqVO.getPassword());

            // 验证企业下，应用是否禁用，是否过期
            corpAppRelationService.validCorpAppRelationStatusOrExpireTime(user.getCorpId(), reqVO.getAppId());

            authLoginRespVO.set(createAfterLoginSuccess(user.getUserType(), user.getCorpId(), reqVO.getAppId(), user.getId(), reqVO.getMobile(), reqVO.getDeviceId(), LoginLogTypeEnum.LOGIN_MOBILE));
            LogRecordContext.putVariable("user", user);
        });

        AuthLoginRespVO authLogVO = authLoginRespVO.get();
        authLogVO.setLoginSource(LoginSourceEnum.APPLOGINMOBILE.getCode());
        return authLogVO;

    }


    @Override
    public AuthLoginRespVO corpLogin(CorpAuthLoginReqVO reqVO) {
        // 1.校验验证码
        mobileValidateCaptcha(reqVO);

        // 2.1 解密原文
        reqVO.setPassword(pwdEnHelper.decryptHexStr(reqVO.getPassword()));
        // 2.2 使用账号密码，进行登录
        AdminUserDO user = mobileAuthenticate(reqVO.getMobile(), reqVO.getPassword());

        // 验证企业状态是否异常
        checkCropStatus(user.getCorpId());

        AuthLoginRespVO authLoginRespVO = createAfterLoginSuccess(user.getUserType(), user.getCorpId(), null, user.getId(), reqVO.getMobile(), reqVO.getDeviceId(), LoginLogTypeEnum.LOGIN_MOBILE);
        // 设置是否管理员
        authLoginRespVO.setAdminFlag(findCorpAdminFlag(RoleCodeEnum.CORP_ADMIN.getCode(), user.getId()));
        // 回显当前登录用户的企业id
        authLoginRespVO.setCorpId(user.getCorpId());
        authLoginRespVO.setLoginSource(LoginSourceEnum.CORPLOGIN.getCode());
        return authLoginRespVO;
    }

    @Override
    public ThirdAuthLoginRespVO thirdLogin(ThirdAuthLoginReqVO reqVO) {

        // 校验验证码
        thirdValidateCaptcha(reqVO);
        // 验证应用是否存在
        Long appId = reqVO.getAppId();
        Long tenantId = checkAppAndGetTenantId(appId);

        AtomicReference<ThirdAuthLoginRespVO> authLoginRespVO = new AtomicReference<>();
        // 设置应用所在的租户环境
        TenantUtils.execute(tenantId, () -> {
            AdminUserDO user = null;
            // 判断登录方式
            if (LongTypeEnum.PASSWORD.getCode().equals(reqVO.getLoginType())) {
                //  解密原文
                reqVO.setPassword(pwdEnHelper.decryptHexStr(reqVO.getPassword()));
                // 使用手机密码，进行登录
                user = mobileAuthenticate(reqVO.getMobile(), reqVO.getPassword());

            } else if (LongTypeEnum.VERIFYCODE.getCode().equals(reqVO.getLoginType())) {
                // 使用手机验证码，进行登录
                validateVerfiyCode(reqVO);
                user = thirdAuthenticate(reqVO.getMobile(), reqVO.getVerifyCode());
                if (null == user) {
                    //如果未注册，直接返回，首次注册为true
                    ThirdAuthLoginRespVO thirdAuthLoginRespVO = new ThirdAuthLoginRespVO();
                    thirdAuthLoginRespVO.setUserUnRegistFlag(true);
                    authLoginRespVO.set(thirdAuthLoginRespVO);
                }
            }
            if (null != user) {
                AuthLoginRespVO vo = createAfterLoginSuccess(user.getUserType(), user.getCorpId(), reqVO.getAppId(), user.getId(), reqVO.getMobile(), reqVO.getDeviceId(), LoginLogTypeEnum.LOGIN_MOBILE);
                ThirdAuthLoginRespVO thirdAuthLoginRespVO = BeanUtils.toBean(vo, ThirdAuthLoginRespVO.class);
                // 判断用户是否关联应用
                thirdAuthLoginRespVO.setUserAppRelationFlag(findUserAppRelationFlag(appId, user.getId()));
                thirdAuthLoginRespVO.setUserUnRegistFlag(false);
                thirdAuthLoginRespVO.setEmail(user.getEmail());
                thirdAuthLoginRespVO.setNickName(user.getNickname());
                authLoginRespVO.set(thirdAuthLoginRespVO);
                LogRecordContext.putVariable("user", user);
            }
        });
        return authLoginRespVO.get();
    }

    /**
     * 对密码进行加密
     *
     * @param password 密码
     * @return 加密后的密码
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public void thirdUserForgetPassword(UserForgetPasswordReqVO reqVO) {

        // 1.校验验证码
        ThirdAuthLoginReqVO thirdAuthLoginReqVO=new ThirdAuthLoginReqVO();
        thirdAuthLoginReqVO.setMobile(reqVO.getMobile());
        thirdAuthLoginReqVO.setVerifyCode(reqVO.getVerifyCode());
        thirdAuthLoginReqVO.setPassword(reqVO.getPassword());
        //调用通用的验证码校验
        validateVerfiyCode(thirdAuthLoginReqVO);

        // 2.通过手机号，获取 用户
        AdminUserDO user = userService.getUserByMobile(reqVO.getMobile());
        if (null == user) {
            throw exception(USER_NOT_EXISTS);
        }
        // 3.加密密码
        userService.thirdUserForgetPassword(user.getId(), thirdAuthLoginReqVO.getPassword());


    }

    private boolean findUserAppRelationFlag(Long appId, Long userId) {
        List<UserAppVO> voList = userAppRelationService.getAppByUserId(userId);
        if (!CollectionUtils.isEmpty(voList)) {
            return !voList.stream()
                    .anyMatch(userAppVO -> null != userAppVO.getAppId() && userAppVO.getAppId().equals(appId));
        }
        return true;
    }


    private void validateVerfiyCode(ThirdAuthLoginReqVO reqVO) {
        // 1. 获取安全配置项
        boolean verifyCodeEnable = true; // todo 暂时直接校验
        if (!verifyCodeEnable) {
            return;
        }

        // 2. 如果配置了验证码
        if (StringUtils.isBlank(reqVO.getVerifyCode())) {
            return;
            // throw exception(AUTH_VERIFY_CODE_NULL);
        }

        if (!"888888".equalsIgnoreCase(reqVO.getVerifyCode())) { // todo 后续动态读取真实验证码
            throw exception(AUTH_VERIFY_CODE_ERROR);
        }
    }


    private void thirdValidateCaptcha(ThirdAuthLoginReqVO reqVO) {
        ResponseModel response = doValidateCaptcha(reqVO);
        // 校验验证码
        if (!response.isSuccess()) {
            // 创建登录失败日志（验证码不正确)
            createLoginLog(null, reqVO.getMobile(), LoginLogTypeEnum.LOGIN_MOBILE, LoginResultEnum.CAPTCHA_CODE_ERROR);
            throw exception(AUTH_LOGIN_CAPTCHA_CODE_ERROR, response.getRepMsg());
        }
    }


    private void checkCropStatus(Long corpId) {
        CorpRespVO corp = corpService.getCorp(corpId);
        if (null == corp || CommonStatusEnum.DISABLE.getStatus().equals(corp.getStatus())) {
            throw exception(AUTH_LOGIN_CORP_DELETE_OR_DISABLE);
        }
    }

    private AuthLoginRespVO createAfterLoginSuccess(Integer userType, Long corpId, Long appId, Long userId, String username, String deviceId, LoginLogTypeEnum logType) {
        // 插入登陆日志
        createLoginLog(userId, username, logType, LoginResultEnum.SUCCESS);
        // 创建访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessTokenWithMode(
                RunModeEnum.RUNTIME.getValue(), corpId, appId,
                userId, userType,
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

    private void checkPlatformAdminEnableAppCreate() {
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
    }

    private void createLoginLog(Long userId, String username,
                                LoginLogTypeEnum logTypeEnum, LoginResultEnum loginResult) {
        // 插入登录日志
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(logTypeEnum.getType());
        reqDTO.setTraceId("n/a");
        reqDTO.setUserId(userId);
        reqDTO.setUserType(getUserType());
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

    @VisibleForTesting
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
            createLoginLog(null, reqVO.getMobile(), LoginLogTypeEnum.LOGIN_MOBILE, LoginResultEnum.CAPTCHA_CODE_ERROR);
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

        // 清理在线设备
        securityConfigApi.removeOnlineDevice(null, accessTokenDO.getUserId(), token);

        // 删除成功,则记录登出日志
        createLogoutLog(accessTokenDO.getUserId(), accessTokenDO.getUserType(), logType);
    }

    private void createLogoutLog(Long userId, Integer userType, Integer logType) {
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(logType);
        reqDTO.setTraceId("n/a");
        reqDTO.setUserId(userId);
        reqDTO.setUserType(userType);
        reqDTO.setUsername(getUsername(userId));
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

    private Integer getUserType() {
        return SecurityFrameworkUtils.getLoginUserType();
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
        AdminUserDO user = userService.getUser(reqVO.getUserId());
        if (user == null) {
            throw exception(USER_MOBILE_NOT_EXISTS);
        }
        // 解密原文
        reqVO.setPassword(pwdEnHelper.decryptHexStr(reqVO.getPassword()));
        userService.updateUserPassword(user.getId(), reqVO.getPassword());
    }


    @Override
    public ThirdAuthLoginRespVO supplementLogin(AdminUserDO user, ThirdSupplementUserReqVO reqVO) {
        AuthLoginRespVO vo = createAfterLoginSuccess(user.getUserType(), user.getCorpId(), reqVO.getAppId(), user.getId(), reqVO.getMobile(), reqVO.getDeviceId(), LoginLogTypeEnum.LOGIN_MOBILE);
        ThirdAuthLoginRespVO thirdAuthLoginRespVO = BeanUtils.toBean(vo, ThirdAuthLoginRespVO.class);
        // 判断用户是否关联应用
        thirdAuthLoginRespVO.setUserAppRelationFlag(false);
        thirdAuthLoginRespVO.setUserUnRegistFlag(false);
        return thirdAuthLoginRespVO;
    }

}
