package com.cmsr.onebase.module.system.service.auth;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.permission.RoleCodeEnum;
import com.cmsr.onebase.module.system.service.permission.RoleService;
import com.cmsr.onebase.module.system.service.tenant.TenantPackageService;
import com.cmsr.onebase.module.system.service.tenant.TenantService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.auth.LingjiSsoUserInfoVO;
import com.cmsr.onebase.module.system.vo.tenant.TenantAdminUserReqVO;
import com.cmsr.onebase.module.system.vo.tenant.TenantInsertReqVO;
import com.cmsr.onebase.module.system.vo.user.UserInsertReqVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 灵畿 SSO 用户创建逻辑测试
 *
 * 测试重点：
 * 1. 创建租户 - 验证租户创建流程
 * 2. 创建用户 - 验证用户创建流程
 * 3. 设置管理员角色 - 验证租户管理员角色分配
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
    private RoleService roleService;

    @InjectMocks
    private LingjiSsoServiceImpl lingjiSsoService;

    // ========== 创建租户测试 ==========

    @Nested
    @DisplayName("创建租户测试")
    class CreateTenantTests {

        /**
         * 测试创建租户 - 使用默认套餐
         */
        @Test
        @DisplayName("创建租户 - 使用默认套餐")
        void testCreateTenant_WithDefaultPackage() throws Exception {
            // 准备套餐
            TenantPackageDO mockPackage = new TenantPackageDO();
            mockPackage.setId(1L);
            mockPackage.setName("默认套餐");

            // 准备用户信息
            LingjiSsoUserInfoVO userInfo = createMockUserInfo();

            // 配置 mock
            when(tenantPackageService.getTenantPackageByCode("default")).thenReturn(mockPackage);
            when(tenantService.createTenant(any(TenantInsertReqVO.class))).thenAnswer(invocation -> {
                TenantInsertReqVO reqVO = invocation.getArgument(0);
                // 验证租户创建参数
                assertNotNull(reqVO.getName());
                assertTrue(reqVO.getName().contains("测试用户"));
                assertEquals(CommonStatusEnum.ENABLE.getStatus(), reqVO.getStatus());
                assertEquals(1L, reqVO.getPackageId());
                // 验证管理员信息
                assertNotNull(reqVO.getTenantAdminUserReqVOList());
                assertEquals(1, reqVO.getTenantAdminUserReqVOList().size());
                TenantAdminUserReqVO adminUser = reqVO.getTenantAdminUserReqVOList().get(0);
                assertEquals("TEST001", adminUser.getAdminUserName());
                assertEquals("测试用户", adminUser.getAdminNickName());
                return 2L;
            });
            when(tenantService.getTenant(2L)).thenReturn(createMockTenant(2L));

            // 执行测试
            TenantDO result = invokeCreateTenant(userInfo);

            // 验证
            assertNotNull(result);
            assertEquals(2L, result.getId());
            verify(tenantPackageService, times(1)).getTenantPackageByCode("default");
            verify(tenantService, times(1)).createTenant(any(TenantInsertReqVO.class));
        }

        /**
         * 测试创建租户 - 默认套餐不存在，使用第一个可用套餐
         */
        @Test
        @DisplayName("创建租户 - 默认套餐不存在，使用第一个可用套餐")
        void testCreateTenant_DefaultPackageNotExists() throws Exception {
            // 准备备用套餐
            TenantPackageDO fallbackPackage = new TenantPackageDO();
            fallbackPackage.setId(2L);
            fallbackPackage.setName("备用套餐");

            // 准备用户信息
            LingjiSsoUserInfoVO userInfo = createMockUserInfo();

            // 配置 mock
            when(tenantPackageService.getTenantPackageByCode("default")).thenReturn(null);
            when(tenantPackageService.getTenantPackageListByStatus(CommonStatusEnum.ENABLE.getStatus()))
                    .thenReturn(Collections.singletonList(fallbackPackage));
            when(tenantService.createTenant(any(TenantInsertReqVO.class))).thenAnswer(invocation -> {
                TenantInsertReqVO reqVO = invocation.getArgument(0);
                assertEquals(2L, reqVO.getPackageId()); // 使用备用套餐
                return 3L;
            });
            when(tenantService.getTenant(3L)).thenReturn(createMockTenant(3L));

            TenantDO result = invokeCreateTenant(userInfo);

            assertNotNull(result);
            verify(tenantPackageService, times(1)).getTenantPackageByCode("default");
            verify(tenantPackageService, times(1)).getTenantPackageListByStatus(anyInt());
        }
    }

    // ========== 创建用户测试 ==========

    @Nested
    @DisplayName("创建用户测试")
    class CreateUserTests {

        /**
         * 测试创建用户 - 成功分配租户管理员角色
         */
        @Test
        @DisplayName("创建用户 - 成功分配租户管理员角色")
        void testCreateUser_AssignTenantAdminRole() throws Exception {
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
                // 验证用户创建参数
                assertEquals("NEWUSER001", reqVO.getUsername());
                assertEquals("新用户", reqVO.getNickname());
                assertEquals("13800138001", reqVO.getMobile());
                assertEquals("newuser@example.com", reqVO.getEmail());
                assertEquals(UserTypeEnum.CORP.getValue(), reqVO.getUserType());
                assertEquals(CommonStatusEnum.ENABLE.getStatus(), reqVO.getStatus());
                // 验证角色已设置
                assertNotNull(reqVO.getRoleIds());
                assertTrue(reqVO.getRoleIds().contains(100L), "应该包含租户管理员角色ID");
                return 200L;
            });

            // 执行测试
            AdminUserDO result = invokeCreateUser(userInfo);

            // 验证
            assertNotNull(result);
            assertEquals(200L, result.getId());
            verify(roleService, times(1)).getRoleByCode(RoleCodeEnum.TENANT_ADMIN.getCode());
            verify(userService, times(1)).createUser(any(UserInsertReqVO.class));
        }

        /**
         * 测试创建用户 - 租户管理员角色不存在，用户无角色
         */
        @Test
        @DisplayName("创建用户 - 租户管理员角色不存在")
        void testCreateUser_TenantAdminRoleNotExists() throws Exception {
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
                assertTrue(reqVO.getRoleIds() == null || reqVO.getRoleIds().isEmpty(),
                        "角色不存在时，用户应该没有角色");
                return 201L;
            });

            AdminUserDO result = invokeCreateUser(userInfo);

            assertNotNull(result);
            assertEquals(201L, result.getId());
            verify(roleService, times(1)).getRoleByCode(RoleCodeEnum.TENANT_ADMIN.getCode());
            verify(userService, times(1)).createUser(any(UserInsertReqVO.class));
        }

        /**
         * 测试创建用户 - 使用工号作为用户名
         */
        @Test
        @DisplayName("创建用户 - 优先使用工号作为用户名")
        void testCreateUser_UseStaffCodeAsUsername() throws Exception {
            // 准备用户信息（有工号）
            AdminUserDO userInfo = new AdminUserDO();
            userInfo.setUsername("STAFF123"); // 工号
            userInfo.setNickname("员工用户");
            userInfo.setMobile("13800138003");
            userInfo.setUserType(UserTypeEnum.CORP.getValue());

            // 准备角色
            RoleDO tenantAdminRole = new RoleDO();
            tenantAdminRole.setId(100L);
            tenantAdminRole.setCode(RoleCodeEnum.TENANT_ADMIN.getCode());

            when(roleService.getRoleByCode(RoleCodeEnum.TENANT_ADMIN.getCode())).thenReturn(tenantAdminRole);
            when(userService.createUser(any(UserInsertReqVO.class))).thenAnswer(invocation -> {
                UserInsertReqVO reqVO = invocation.getArgument(0);
                assertEquals("STAFF123", reqVO.getUsername());
                return 300L;
            });

            AdminUserDO result = invokeCreateUser(userInfo);

            assertNotNull(result);
            assertEquals(300L, result.getId());
        }
    }

    // ========== 查找或创建用户测试 ==========

    @Nested
    @DisplayName("查找或创建用户测试")
    class FindOrCreateUserTests {

        /**
         * 测试查找用户 - 用户已存在且信息无变化
         */
        @Test
        @DisplayName("查找用户 - 用户已存在且信息无变化")
        void testFindOrCreateUser_ExistingUserNoChanges() throws Exception {
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
         * 测试查找用户 - 用户已存在但信息有变化
         */
        @Test
        @DisplayName("查找用户 - 用户已存在但信息有变化")
        void testFindOrCreateUser_ExistingUserWithChanges() throws Exception {
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
         * 测试创建用户 - 用户不存在，创建新用户
         */
        @Test
        @DisplayName("创建用户 - 用户不存在，创建新用户")
        void testFindOrCreateUser_NewUser() throws Exception {
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
    }

    // ========== 辅助方法 ==========

    private LingjiSsoUserInfoVO createMockUserInfo() {
        LingjiSsoUserInfoVO userInfo = new LingjiSsoUserInfoVO();
        userInfo.setSub("13800138000");
        userInfo.setUserName("测试用户");
        userInfo.setStaffCode("TEST001");
        userInfo.setEmail("test@example.com");
        userInfo.setEnterpriseId("testEnterprise");
        userInfo.setEnterpriseCode("TEST_ENT");
        userInfo.setInsider(1);
        return userInfo;
    }

    private TenantDO createMockTenant(Long id) {
        TenantDO tenant = new TenantDO();
        tenant.setId(id);
        tenant.setName("测试租户" + id);
        return tenant;
    }

    /**
     * 通过反射调用私有方法 createTenant
     */
    private TenantDO invokeCreateTenant(LingjiSsoUserInfoVO userInfo) throws Exception {
        var method = LingjiSsoServiceImpl.class.getDeclaredMethod("createTenant", LingjiSsoUserInfoVO.class);
        method.setAccessible(true);
        return (TenantDO) method.invoke(lingjiSsoService, userInfo);
    }

    /**
     * 通过反射调用私有方法 createUser
     */
    private AdminUserDO invokeCreateUser(AdminUserDO userInfo) throws Exception {
        var method = LingjiSsoServiceImpl.class.getDeclaredMethod("createUser", AdminUserDO.class);
        method.setAccessible(true);
        return (AdminUserDO) method.invoke(lingjiSsoService, userInfo);
    }

    /**
     * 通过反射调用私有方法 findOrCreateUser
     */
    private AdminUserDO invokeFindOrCreateUser(AdminUserDO userInfo) throws Exception {
        var ssoUserInfo = new LingjiSsoUserInfoVO();
        var method = LingjiSsoServiceImpl.class.getDeclaredMethod(
                "findOrCreateUser", AdminUserDO.class, LingjiSsoUserInfoVO.class
        );
        method.setAccessible(true);
        return (AdminUserDO) method.invoke(lingjiSsoService, userInfo, ssoUserInfo);
    }
}