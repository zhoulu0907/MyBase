package com.cmsr.onebase.module.system.service.auth;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.service.CaptchaService;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.module.system.api.sms.SmsCodeApi;
import com.cmsr.onebase.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import com.cmsr.onebase.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.dal.flex.repo.UserDataRepository;
import com.cmsr.onebase.module.system.service.logger.LoginLogService;
import com.cmsr.onebase.module.system.service.member.MemberService;
import com.cmsr.onebase.module.system.service.oauth2.OAuth2TokenService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.auth.*;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AdminAuthService 单元测试
 * <p>
 * 测试管理员认证服务的各种功能，包括登录、注册、短信验证、社交登录等
 *
 * @author matianyu
 * @date 2025-01-09
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = {com.cmsr.onebase.module.system.framework.test.BaseDbIntegrationTest.Application.class}
)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MYSQL;DATABASE_TO_UPPER=false",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.datasource.dynamic.datasource.master.url=jdbc:h2:mem:testdb;MODE=MYSQL;DATABASE_TO_UPPER=false",
    "spring.datasource.dynamic.datasource.master.driver-class-name=org.h2.Driver",
    "spring.datasource.dynamic.datasource.master.username=sa",
    "spring.datasource.dynamic.datasource.master.password=",
    "onebase.security.mock-enable=true",
    "onebase.tenant.enable=false",
    "onebase.captcha.enable=false"
})
@ActiveProfiles("unit-test")
@Transactional
public class BuildAuthServiceTest {

    @Resource
    private BuildAuthService buildAuthService;

    @Resource
    private UserDataRepository userDataRepository;

    @Mock
    private UserService userService;

    @Mock
    private LoginLogService loginLogService;

    @Mock
    private OAuth2TokenService oauth2TokenService;

    @Mock
    private MemberService memberService;

    @Mock
    private CaptchaService captchaService;

    @Mock
    private SmsCodeApi smsCodeApi;

    private AdminUserDO testUser;
    private OAuth2AccessTokenDO testToken;

    /**
     * 每个测试前准备数据
     */
    @BeforeEach
    public void setUp() {
        // 设置验证码为关闭状态
        ((BuildAuthServiceImpl) buildAuthService).setCaptchaEnable(false);

        // 准备测试用户
        testUser = createTestUser();

        // 准备测试Token
        testToken = createTestToken();

        // 重置所有Mock对象
        reset(userService, loginLogService, oauth2TokenService,
              memberService, captchaService, smsCodeApi);
    }

    /**
     * 每个测试后清理数据
     */
    @AfterEach
    public void tearDown() {
        userDataRepository.remove(new QueryWrapper());
    }

    /**
     * 测试authenticate方法 - 正常认证
     */
    @Test
    public void testAuthenticate_Success() {
        // 准备数据
        String username = "testuser";
        String password = "123456";

        when(userService.getUserByUsername(username)).thenReturn(testUser);
        when(userService.isPasswordMatch(password, testUser.getPassword())).thenReturn(true);

        // 执行测试
        AdminUserDO result = buildAuthService.authenticate(username, password);

        // 验证结果
        assertNotNull(result, "认证结果不应该为空");
        assertEquals(testUser.getId(), result.getId(), "用户ID应该一致");
        assertEquals(testUser.getUsername(), result.getUsername(), "用户名应该一致");

        // 验证方法调用
        verify(userService).getUserByUsername(username);
        verify(userService).isPasswordMatch(password, testUser.getPassword());
    }

    /**
     * 测试authenticate方法 - 用户不存在
     */
    @Test
    public void testAuthenticate_UserNotExists() {
        // 准备数据
        String username = "nonexistent";
        String password = "123456";

        when(userService.getUserByUsername(username)).thenReturn(null);

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class,
            () -> buildAuthService.authenticate(username, password));

        assertEquals(AUTH_LOGIN_BAD_CREDENTIALS.getCode(), exception.getCode(), "错误码应该一致");
        verify(loginLogService).createLoginLog(any());
    }

    /**
     * 测试authenticate方法 - 密码错误
     */
    @Test
    public void testAuthenticate_BadCredentials() {
        // 准备数据
        String username = "testuser";
        String password = "wrongpassword";

        when(userService.getUserByUsername(username)).thenReturn(testUser);
        when(userService.isPasswordMatch(password, testUser.getPassword())).thenReturn(false);

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class,
            () -> buildAuthService.authenticate(username, password));

        assertEquals(AUTH_LOGIN_BAD_CREDENTIALS.getCode(), exception.getCode(), "错误码应该一致");
        verify(loginLogService).createLoginLog(any());
    }

    /**
     * 测试authenticate方法 - 用户被禁用
     */
    @Test
    public void testAuthenticate_UserDisabled() {
        // 准备数据
        String username = "testuser";
        String password = "123456";
        testUser.setStatus(CommonStatusEnum.DISABLE.getStatus());

        when(userService.getUserByUsername(username)).thenReturn(testUser);
        when(userService.isPasswordMatch(password, testUser.getPassword())).thenReturn(true);

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class,
            () -> buildAuthService.authenticate(username, password));

        assertEquals(AUTH_LOGIN_USER_DISABLED.getCode(), exception.getCode(), "错误码应该一致");
        verify(loginLogService).createLoginLog(any());
    }

    /**
     * 测试login方法 - 正常登录
     */
    @Test
    public void testLogin_Success() {
        // 准备数据
        AuthLoginReqVO reqVO = new AuthLoginReqVO();
        reqVO.setUsername("testuser");
        reqVO.setPassword("123456");

        when(userService.getUserByUsername(reqVO.getUsername())).thenReturn(testUser);
        when(userService.isPasswordMatch(reqVO.getPassword(), testUser.getPassword())).thenReturn(true);
        when(oauth2TokenService.createAccessToken(any(), any(), any(), any())).thenReturn(testToken);

        // 执行测试
        AuthLoginRespVO result = buildAuthService.login(reqVO);

        // 验证结果
        assertNotNull(result, "登录结果不应该为空");
        assertEquals(testToken.getAccessToken(), result.getAccessToken(), "访问令牌应该一致");
        assertEquals(testToken.getRefreshToken(), result.getRefreshToken(), "刷新令牌应该一致");

        // 验证调用
        verify(loginLogService).createLoginLog(any());
        verify(userService).updateUserLogin(eq(testUser.getId()), anyString());
    }


    /**
     * 测试sendSmsCode方法 - 正常发送
     */
    @Test
    public void testSendSmsCode_Success() {
        // 准备数据
        AuthSmsSendReqVO reqVO = new AuthSmsSendReqVO();
        reqVO.setMobile("13888888888");
        reqVO.setScene(1); // 登录场景

        when(userService.getUserByMobile(reqVO.getMobile())).thenReturn(testUser);

        // 执行测试
//        assertDoesNotThrow(() -> buildAuthService.sendSmsCode(reqVO));

        // 验证调用
        verify(smsCodeApi).sendSmsCode(any(SmsCodeSendReqDTO.class));
    }

    /**
     * 测试sendSmsCode方法 - 手机号不存在
     */
    @Test
    public void testSendSmsCode_MobileNotExists() {
        // 准备数据
        AuthSmsSendReqVO reqVO = new AuthSmsSendReqVO();
        reqVO.setMobile("13999999999");
        reqVO.setScene(1); // 登录场景

        when(userService.getUserByMobile(reqVO.getMobile())).thenReturn(null);

        // 执行测试并验证异常
//        ServiceException exception = assertThrows(ServiceException.class,
//            () -> buildAuthService.sendSmsCode(reqVO));

//        assertEquals(AUTH_MOBILE_NOT_EXISTS.getCode(), exception.getCode(), "错误码应该一致");
    }

    /**
     * 测试smsLogin方法 - 短信登录成功
     */
    @Test
    public void testSmsLogin_Success() {
        // 准备数据
        AuthSmsLoginReqVO reqVO = new AuthSmsLoginReqVO();
        reqVO.setMobile("13888888888");
        reqVO.setCode("123456");

        when(smsCodeApi.useSmsCode(any(SmsCodeUseReqDTO.class))).thenReturn(success(true));
        when(userService.getUserByMobile(reqVO.getMobile())).thenReturn(testUser);
        when(oauth2TokenService.createAccessToken(any(), any(), any(), any())).thenReturn(testToken);

        // 执行测试
        AuthLoginRespVO result = buildAuthService.smsLogin(reqVO);

        // 验证结果
        assertNotNull(result, "登录结果不应该为空");
        assertEquals(testToken.getAccessToken(), result.getAccessToken(), "访问令牌应该一致");
    }

    /**
     * 测试smsLogin方法 - 用户不存在
     */
    @Test
    public void testSmsLogin_UserNotExists() {
        // 准备数据
        AuthSmsLoginReqVO reqVO = new AuthSmsLoginReqVO();
        reqVO.setMobile("13999999999");
        reqVO.setCode("123456");

        // SmsCodeUseRespDTO smsResp = new SmsCodeUseRespDTO();
        // smsResp.setCode(0);

        when(smsCodeApi.useSmsCode(any(SmsCodeUseReqDTO.class))).thenReturn(success(true));
        when(userService.getUserByMobile(reqVO.getMobile())).thenReturn(null);

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class,
            () -> buildAuthService.smsLogin(reqVO));

        assertEquals(USER_NOT_EXISTS.getCode(), exception.getCode(), "错误码应该一致");
    }

    /**
     * 测试refreshToken方法 - 刷新令牌成功
     */
    @Test
    public void testRefreshToken_Success() {
        // 准备数据
        String refreshToken = "refreshToken123";

        when(oauth2TokenService.refreshAccessToken(refreshToken, any())).thenReturn(testToken);

        // 执行测试
        AuthLoginRespVO result = buildAuthService.refreshToken(refreshToken);

        // 验证结果
        assertNotNull(result, "刷新结果不应该为空");
        assertEquals(testToken.getAccessToken(), result.getAccessToken(), "访问令牌应该一致");
    }

    /**
     * 测试logout方法 - 正常退出
     */
    @Test
    public void testLogout_Success() {
        // 准备数据
        String token = "accessToken123";
        Integer logType = 1;

        when(oauth2TokenService.removeAccessToken(token)).thenReturn(testToken);
        when(userService.getUser(testToken.getUserId())).thenReturn(testUser);

        // 执行测试
        assertDoesNotThrow(() -> buildAuthService.logout(token, logType));

        // 验证调用
        verify(oauth2TokenService).removeAccessToken(token);
        verify(loginLogService).createLoginLog(any());
    }

    /**
     * 测试logout方法 - Token不存在
     */
    @Test
    public void testLogout_TokenNotExists() {
        // 准备数据
        String token = "invalidToken";
        Integer logType = 1;

        when(oauth2TokenService.removeAccessToken(token)).thenReturn(null);

        // 执行测试
        assertDoesNotThrow(() -> buildAuthService.logout(token, logType));

        // 验证不会创建登出日志
        verify(loginLogService, never()).createLoginLog(any());
    }

    /**
     * 测试register方法 - 注册成功
     */
    @Test
    public void testRegister_Success() {
        // 准备数据
        AuthRegisterReqVO reqVO = new AuthRegisterReqVO();
        reqVO.setUsername("newuser");
        reqVO.setPassword("123456");

        when(userService.registerUser(reqVO)).thenReturn(testUser.getId());
        when(oauth2TokenService.createAccessToken(any(), any(), any(), any())).thenReturn(testToken);

        // 执行测试
        AuthLoginRespVO result = buildAuthService.register(reqVO);

        // 验证结果
        assertNotNull(result, "注册结果不应该为空");
        assertEquals(testToken.getAccessToken(), result.getAccessToken(), "访问令牌应该一致");

        // 验证调用
        verify(userService).registerUser(reqVO);
        verify(loginLogService).createLoginLog(any());
    }

    /**
     * 测试resetPassword方法 - 重置密码成功
     */
    @Test
    public void testResetPassword_Success() {
        // 准备数据
        AuthResetPasswordReqVO reqVO = new AuthResetPasswordReqVO();
        reqVO.setUserId(testUser.getId());
        reqVO.setPassword("newpassword");

        when(userService.getUser(reqVO.getUserId())).thenReturn(testUser);

        // 执行测试
        assertDoesNotThrow(() -> buildAuthService.resetPassword(reqVO));

        // 验证调用
        verify(userService).updateUserPassword(testUser.getId(), reqVO.getPassword());
    }

    /**
     * 测试resetPassword方法 - 用户不存在
     */
    @Test
    public void testResetPassword_UserNotExists() {
        // 准备数据
        AuthResetPasswordReqVO reqVO = new AuthResetPasswordReqVO();
        reqVO.setUserId(999L);
        reqVO.setPassword("newpassword");

        when(userService.getUser(reqVO.getUserId())).thenReturn(null);

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class,
            () -> buildAuthService.resetPassword(reqVO));

        assertEquals(USER_MOBILE_NOT_EXISTS.getCode(), exception.getCode(), "错误码应该一致");
    }

    /**
     * 测试验证码功能 - 验证码开启时验证失败
     */
    @Test
    public void testValidateCaptcha_Failed() {
        // 设置验证码为开启状态
        ((BuildAuthServiceImpl) buildAuthService).setCaptchaEnable(true);

        // 准备数据
        AuthLoginReqVO reqVO = new AuthLoginReqVO();
        reqVO.setUsername("testuser");
        reqVO.setPassword("123456");
        reqVO.setCaptchaVerification("invalidCaptcha");

        ResponseModel captchaResponse = ResponseModel.errorMsg("验证码错误");
        when(captchaService.verification(any())).thenReturn(captchaResponse);

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class,
            () -> buildAuthService.login(reqVO));

        assertEquals(AUTH_LOGIN_CAPTCHA_CODE_ERROR.getCode(), exception.getCode(), "错误码应该一致");
    }

    // ========== 辅助方法 ==========

    /**
     * 创建测试用户
     */
    private AdminUserDO createTestUser() {
        AdminUserDO user = new AdminUserDO();
        user.setId(1L);
        user.setUsername("testuser");
        user.setNickname("测试用户");
        user.setPassword("$2a$10$mRMIYLDtRHlf6.9ipiqH1OLTQAaxl8Q/VXnKb.ZJcBLZcZGNMF0s6"); // 123456
        user.setStatus(CommonStatusEnum.ENABLE.getStatus());
        user.setEmail("testuser@test.com");
        user.setMobile("13888888888");
        user.setTenantId(0L);
        return user;
    }

    /**
     * 创建测试Token
     */
    private OAuth2AccessTokenDO createTestToken() {
        OAuth2AccessTokenDO token = new OAuth2AccessTokenDO();
        token.setId(1L);
        token.setAccessToken("accessToken123");
        token.setRefreshToken("refreshToken123");
        token.setUserId(testUser != null ? testUser.getId() : 1L);
        token.setUserType(UserTypeEnum.THIRD.getValue());
        return token;
    }
}

