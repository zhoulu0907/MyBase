package com.cmsr.onebase.module.system.service.auth;

import cn.hutool.json.JSONObject;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.permission.RoleCodeEnum;
import com.cmsr.onebase.module.system.service.logger.LoginLogService;
import com.cmsr.onebase.module.system.service.oauth2.OAuth2TokenService;
import com.cmsr.onebase.module.system.service.permission.RoleService;
import com.cmsr.onebase.module.system.service.tenant.TenantPackageService;
import com.cmsr.onebase.module.system.service.tenant.TenantService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.util.oauth2.OkHttpClientUtils;
import com.cmsr.onebase.module.system.vo.auth.AuthLoginRespVO;
import com.cmsr.onebase.module.system.vo.user.UserInsertReqVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * LingjiSsoService 单元测试
 *
 * 测试分类：
 * 1. LoginTests - 登录流程测试（配置校验、API调用、Token生成）
 * 2. UserCreationTests - 用户创建逻辑测试（租户管理员角色分配）
 */
@ExtendWith(MockitoExtension.class)
class LingjiSsoServiceTest {

    @Mock
    private LingjiSsoProperties lingjiSsoProperties;

    @Mock
    private TenantService tenantService;

    @Mock
    private TenantPackageService tenantPackageService;

    @Mock
    private UserService userService;

    @Mock
    private OAuth2TokenService oauth2TokenService;

    @Mock
    private LoginLogService loginLogService;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private LingjiSsoServiceImpl lingjiSsoService;

    private OAuth2AccessTokenDO mockToken;

    @BeforeEach
    void setUp() {
        mockToken = new OAuth2AccessTokenDO();
        mockToken.setAccessToken("mockAccessToken123");
        mockToken.setRefreshToken("mockRefreshToken123");
        mockToken.setExpiresTime(LocalDateTime.now().plusHours(2));
    }

    // ========== 登录流程测试 ==========

    @Nested
    @DisplayName("登录流程测试")
    class LoginTests {

        /**
         * 测试配置未启用
         */
        @Test
        @DisplayName("配置未启用 - 抛出异常")
        void testLogin_ConfigDisabled() {
            when(lingjiSsoProperties.isEnabled()).thenReturn(false);

            assertThrows(Exception.class, () -> lingjiSsoService.login("testCode", null));
        }

        /**
         * 测试获取用户信息失败 - 签名错误
         */
        @Test
        @DisplayName("获取用户信息失败 - 签名错误")
        void testLogin_GetUserInfoFailed_SignatureError() {
            setupMockConfig();

            try (MockedStatic<OkHttpClientUtils> mockedHttp = mockStatic(OkHttpClientUtils.class)) {
                mockedHttp.when(() -> OkHttpClientUtils.sendRequest(any(), anyBoolean()))
                        .thenReturn("{\"status\":\"1\",\"message\":\"签名错误\"}");

                assertThrows(Exception.class, () -> lingjiSsoService.login("testCode", null));
            }
        }

        /**
         * 测试获取用户信息失败 - id_token为空
         */
        @Test
        @DisplayName("获取用户信息失败 - id_token为空")
        void testLogin_GetUserInfoFailed_EmptyToken() {
            setupMockConfig();

            try (MockedStatic<OkHttpClientUtils> mockedHttp = mockStatic(OkHttpClientUtils.class)) {
                mockedHttp.when(() -> OkHttpClientUtils.sendRequest(any(), anyBoolean()))
                        .thenReturn("{\"status\":\"0\",\"id_token\":\"\"}");

                assertThrows(Exception.class, () -> lingjiSsoService.login("testCode", null));
            }
        }

        /**
         * 测试成功登录 - 已有租户和用户
         */
        @Test
        @DisplayName("成功登录 - 已有租户和用户")
        void testLogin_Success_ExistingTenantAndUser() {
            setupMockConfig();

            // 准备租户
            TenantDO tenant = createMockTenant(1L);

            // 准备已有用户
            AdminUserDO existingUser = new AdminUserDO();
            existingUser.setId(100L);
            existingUser.setUsername("TEST001");
            existingUser.setNickname("测试用户");
            existingUser.setEmail("test@example.com");

            // Mock API 响应
            String mockResponse = createMockJwtResponse();

            // 配置 mock
            when(tenantService.getTenant(anyLong())).thenReturn(tenant);
            when(userService.getUserByUsername("TEST001")).thenReturn(existingUser);
            when(oauth2TokenService.createAccessToken(anyLong(), anyInt(), anyString(), any())).thenReturn(mockToken);

            try (MockedStatic<OkHttpClientUtils> mockedHttp = mockStatic(OkHttpClientUtils.class)) {
                mockedHttp.when(() -> OkHttpClientUtils.sendRequest(any(), anyBoolean()))
                        .thenReturn(mockResponse);

                AuthLoginRespVO result = lingjiSsoService.login("testCode", null);

                assertNotNull(result);
                assertEquals("mockAccessToken123", result.getAccessToken());
                assertEquals(1L, result.getTenantId());
                verify(tenantService, never()).createTenant(any());
                verify(userService, never()).createUser(any());
            }
        }

        /**
         * 测试成功登录 - 自动创建租户和用户
         */
        @Test
        @DisplayName("成功登录 - 自动创建租户和用户")
        void testLogin_Success_CreateTenantAndUser() {
            setupMockConfig();

            // 准备套餐
            TenantPackageDO mockPackage = new TenantPackageDO();
            mockPackage.setId(1L);

            // 准备租户管理员角色
            RoleDO tenantAdminRole = new RoleDO();
            tenantAdminRole.setId(100L);
            tenantAdminRole.setCode(RoleCodeEnum.TENANT_ADMIN.getCode());

            // Mock API 响应
            String mockResponse = createMockJwtResponse();

            // 配置 mock
            when(tenantPackageService.getTenantPackageByCode(anyString())).thenReturn(mockPackage);
            when(tenantPackageService.getTenantPackageListByStatus(anyInt())).thenReturn(Collections.singletonList(mockPackage));
            when(tenantService.createTenant(any())).thenReturn(2L);
            when(tenantService.getTenant(2L)).thenReturn(createMockTenant(2L));
            when(userService.getUserByUsername("TEST001")).thenReturn(null);
            when(userService.createUser(any())).thenReturn(100L);
            when(oauth2TokenService.createAccessToken(anyLong(), anyInt(), anyString(), any())).thenReturn(mockToken);
            when(roleService.getRoleByCode(RoleCodeEnum.TENANT_ADMIN.getCode())).thenReturn(tenantAdminRole);

            try (MockedStatic<OkHttpClientUtils> mockedHttp = mockStatic(OkHttpClientUtils.class)) {
                mockedHttp.when(() -> OkHttpClientUtils.sendRequest(any(), anyBoolean()))
                        .thenReturn(mockResponse);

                AuthLoginRespVO result = lingjiSsoService.login("testCode", null);

                assertNotNull(result);
                assertEquals("mockAccessToken123", result.getAccessToken());
                assertEquals(2L, result.getTenantId());
                verify(tenantService, times(1)).createTenant(any());
                verify(userService, times(1)).createUser(any());
            }
        }
    }

    // ========== 用户创建逻辑测试 ==========

    @Nested
    @DisplayName("用户创建逻辑测试")
    class UserCreationTests {

        /**
         * 测试创建用户 - 成功分配租户管理员角色
         */
        @Test
        @DisplayName("创建用户 - 成功分配租户管理员角色")
        void testCreateUser_AssignTenantAdminRole() {
            // 准备用户信息
            AdminUserDO userInfo = new AdminUserDO();
            userInfo.setUsername("NEWUSER001");
            userInfo.setNickname("新用户");
            userInfo.setMobile("13800138001");
            userInfo.setEmail("newuser@example.com");
            userInfo.setUserType(UserTypeEnum.CORP.getValue());

            // 准备租户管理员角色
            RoleDO tenantAdminRole = new RoleDO();
            tenantAdminRole.setId(100L);
            tenantAdminRole.setCode(RoleCodeEnum.TENANT_ADMIN.getCode());
            tenantAdminRole.setName("租户管理员");

            // 配置 mock
            when(roleService.getRoleByCode(RoleCodeEnum.TENANT_ADMIN.getCode())).thenReturn(tenantAdminRole);
            when(userService.createUser(any(UserInsertReqVO.class))).thenAnswer(invocation -> {
                UserInsertReqVO reqVO = invocation.getArgument(0);
                // 验证角色已设置
                assertNotNull(reqVO.getRoleIds());
                assertTrue(reqVO.getRoleIds().contains(100L));
                assertEquals("NEWUSER001", reqVO.getUsername());
                assertEquals("新用户", reqVO.getNickname());
                assertEquals("13800138001", reqVO.getMobile());
                assertEquals("newuser@example.com", reqVO.getEmail());
                assertEquals(UserTypeEnum.CORP.getValue(), reqVO.getUserType());
                return 200L;
            });

            // 通过反射调用私有方法进行测试
            AdminUserDO result = invokeCreateUser(userInfo);

            assertNotNull(result);
            assertEquals(200L, result.getId());
            verify(roleService, times(1)).getRoleByCode(RoleCodeEnum.TENANT_ADMIN.getCode());
            verify(userService, times(1)).createUser(any(UserInsertReqVO.class));
        }

        /**
         * 测试创建用户 - 租户管理员角色不存在
         */
        @Test
        @DisplayName("创建用户 - 租户管理员角色不存在，用户无角色")
        void testCreateUser_TenantAdminRoleNotExists() {
            // 准备用户信息
            AdminUserDO userInfo = new AdminUserDO();
            userInfo.setUsername("NEWUSER002");
            userInfo.setNickname("新用户2");
            userInfo.setMobile("13800138002");
            userInfo.setEmail("newuser2@example.com");
            userInfo.setUserType(UserTypeEnum.CORP.getValue());

            // 配置 mock - 角色不存在
            when(roleService.getRoleByCode(RoleCodeEnum.TENANT_ADMIN.getCode())).thenReturn(null);
            when(userService.createUser(any(UserInsertReqVO.class))).thenAnswer(invocation -> {
                UserInsertReqVO reqVO = invocation.getArgument(0);
                // 验证角色未设置
                assertTrue(reqVO.getRoleIds() == null || reqVO.getRoleIds().isEmpty());
                return 201L;
            });

            AdminUserDO result = invokeCreateUser(userInfo);

            assertNotNull(result);
            assertEquals(201L, result.getId());
            verify(roleService, times(1)).getRoleByCode(RoleCodeEnum.TENANT_ADMIN.getCode());
            verify(userService, times(1)).createUser(any(UserInsertReqVO.class));
        }

        /**
         * 测试查找或创建用户 - 用户已存在且信息无变化
         */
        @Test
        @DisplayName("查找或创建用户 - 用户已存在且信息无变化")
        void testFindOrCreateUser_ExistingUserNoChanges() {
            // 准备已有用户
            AdminUserDO existingUser = new AdminUserDO();
            existingUser.setId(100L);
            existingUser.setUsername("EXISTING001");
            existingUser.setNickname("现有用户");
            existingUser.setEmail("existing@example.com");
            existingUser.setMobile("13800138000");

            // 准备用户信息（相同）
            AdminUserDO userInfo = new AdminUserDO();
            userInfo.setUsername("EXISTING001");
            userInfo.setNickname("现有用户");
            userInfo.setEmail("existing@example.com");
            userInfo.setMobile("13800138000");

            when(userService.getUserByUsername("EXISTING001")).thenReturn(existingUser);

            AdminUserDO result = invokeFindOrCreateUser(userInfo);

            assertEquals(100L, result.getId());
            verify(userService, never()).updateUser(any());
            verify(userService, never()).createUser(any());
        }

        /**
         * 测试查找或创建用户 - 用户已存在但信息有变化
         */
        @Test
        @DisplayName("查找或创建用户 - 用户已存在但信息有变化")
        void testFindOrCreateUser_ExistingUserWithChanges() {
            // 准备已有用户
            AdminUserDO existingUser = new AdminUserDO();
            existingUser.setId(100L);
            existingUser.setUsername("EXISTING002");
            existingUser.setNickname("旧昵称");
            existingUser.setEmail("old@example.com");
            existingUser.setMobile("13800138000");

            // 准备用户信息（有变化）
            AdminUserDO userInfo = new AdminUserDO();
            userInfo.setUsername("EXISTING002");
            userInfo.setNickname("新昵称");
            userInfo.setEmail("new@example.com");
            userInfo.setMobile("13800138000");

            when(userService.getUserByUsername("EXISTING002")).thenReturn(existingUser);

            AdminUserDO result = invokeFindOrCreateUser(userInfo);

            assertEquals(100L, result.getId());
            verify(userService, times(1)).updateUser(any());
            verify(userService, never()).createUser(any());
        }

        /**
         * 测试查找或创建用户 - 用户不存在，创建新用户
         */
        @Test
        @DisplayName("查找或创建用户 - 用户不存在，创建新用户")
        void testFindOrCreateUser_NewUser() {
            // 准备用户信息
            AdminUserDO userInfo = new AdminUserDO();
            userInfo.setUsername("NEWUSER003");
            userInfo.setNickname("新用户3");
            userInfo.setEmail("newuser3@example.com");
            userInfo.setMobile("13800138003");
            userInfo.setUserType(UserTypeEnum.CORP.getValue());

            // 准备租户管理员角色
            RoleDO tenantAdminRole = new RoleDO();
            tenantAdminRole.setId(100L);
            tenantAdminRole.setCode(RoleCodeEnum.TENANT_ADMIN.getCode());

            when(userService.getUserByUsername("NEWUSER003")).thenReturn(null);
            when(roleService.getRoleByCode(RoleCodeEnum.TENANT_ADMIN.getCode())).thenReturn(tenantAdminRole);
            when(userService.createUser(any(UserInsertReqVO.class))).thenReturn(300L);

            AdminUserDO result = invokeFindOrCreateUser(userInfo);

            assertNotNull(result);
            assertEquals(300L, result.getId());
            verify(userService, times(1)).createUser(any(UserInsertReqVO.class));
        }

        // ========== 辅助方法 - 通过反射调用私有方法 ==========

        private AdminUserDO invokeCreateUser(AdminUserDO userInfo) {
            try {
                var method = LingjiSsoServiceImpl.class.getDeclaredMethod("createUser", AdminUserDO.class);
                method.setAccessible(true);
                return (AdminUserDO) method.invoke(lingjiSsoService, userInfo);
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke createUser method", e);
            }
        }

        private AdminUserDO invokeFindOrCreateUser(AdminUserDO userInfo) {
            try {
                var ssoUserInfo = new com.cmsr.onebase.module.system.vo.auth.LingjiSsoUserInfoVO();
                var method = LingjiSsoServiceImpl.class.getDeclaredMethod(
                    "findOrCreateUser", AdminUserDO.class,
                    com.cmsr.onebase.module.system.vo.auth.LingjiSsoUserInfoVO.class
                );
                method.setAccessible(true);
                return (AdminUserDO) method.invoke(lingjiSsoService, userInfo, ssoUserInfo);
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke findOrCreateUser method", e);
            }
        }
    }

    // ========== 辅助方法 ==========

    private void setupMockConfig() {
        when(lingjiSsoProperties.isEnabled()).thenReturn(true);
        when(lingjiSsoProperties.getSourceId()).thenReturn("5570132830");
        when(lingjiSsoProperties.getSourceKey()).thenReturn("JhxsQ3whI26YTMvt");
        when(lingjiSsoProperties.getUserInfoUrl()).thenReturn("http://mock-server/api/userInfo");
        when(lingjiSsoProperties.isHttpDebugLogEnabled()).thenReturn(true);
    }

    private TenantDO createMockTenant(Long id) {
        TenantDO tenant = new TenantDO();
        tenant.setId(id);
        tenant.setName("测试租户" + id);
        return tenant;
    }

    /**
     * 创建模拟的 JWT 响应
     * JWT 格式: header.payload.signature
     * payload 需要包含用户信息
     */
    private String createMockJwtResponse() {
        JSONObject payload = new JSONObject();
        payload.set("sub", "13800138000");
        payload.set("userName", "测试用户");
        payload.set("staffCode", "TEST001");
        payload.set("email", "test@example.com");
        payload.set("enterpriseId", "testEnterprise");
        payload.set("enterpriseCode", "TEST_ENT");
        payload.set("insider", 1);

        String payloadBase64 = cn.hutool.core.codec.Base64.encode(payload.toString());
        String mockJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." + payloadBase64 + ".mockSignature";

        JSONObject response = new JSONObject();
        response.set("status", "0");
        response.set("id_token", mockJwt);

        return response.toString();
    }
}