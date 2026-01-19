package com.cmsr.onebase.module.system.service.tenant;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.module.system.dal.database.TenantDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.cmsr.onebase.module.system.enums.permission.PackageTypeEnum;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import com.cmsr.onebase.module.system.service.permission.MenuService;
import com.cmsr.onebase.module.system.service.permission.PermissionService;
import com.cmsr.onebase.module.system.service.permission.RoleService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.tenant.TenantInsertReqVO;
import com.cmsr.onebase.module.system.vo.tenant.TenantUpdateReqVO;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * TenantService 单元测试
 *
 * 测试租户服务的核心功能:
 * 1. 创建租户
 * 2. 更新租户
 * 3. 查询租户
 * 4. License限制验证
 *
 * @author Test
 * @date 2025-01-18
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
public class TenantServiceTest {

    @Resource
    private TenantService tenantService;

    @Resource
    private TenantDataRepository tenantDataRepository;

    @MockBean
    private TenantPackageService tenantPackageService;

    @MockBean
    private LicenseService licenseService;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private MenuService menuService;

    @MockBean
    private PermissionService permissionService;

    private TenantPackageDO testPackage;
    private LicenseDO testLicense;

    /**
     * 初始化测试数据
     */
    @BeforeEach
    public void setUp() {
        // 创建测试租户套餐
        testPackage = createTestPackage();

        // 创建测试License
        testLicense = createTestLicense();

        // Mock租户套餐服务
        when(tenantPackageService.getTenantPackageByCode(PackageTypeEnum.ALL.getCode()))
            .thenReturn(testPackage);

        // Mock License服务
        when(licenseService.getLatestActiveLicense())
            .thenReturn(testLicense);

        // Mock用户数量
        when(userService.getUserCountByStatus(any()))
            .thenReturn(0);
    }

    /**
     * 测试: 根据名称查询租户 - 成功场景
     */
    @Test
    public void testGetTenantByName_Success() {
        // 准备测试数据
        TenantDO tenant = createAndSaveTenant("测试租户", "test-tenant");

        // 执行查询
        TenantDO result = tenantService.getTenantByName("测试租户");

        // 验证结果
        assertNotNull(result, "应该查询到租户");
        assertEquals("测试租户", result.getName(), "租户名称应该匹配");
        assertEquals("test-tenant", result.getWebsite(), "域名应该匹配");
    }

    /**
     * 测试: 根据名称查询租户 - 不存在
     */
    @Test
    public void testGetTenantByName_NotFound() {
        // 执行查询
        TenantDO result = tenantService.getTenantByName("不存在的租户");

        // 验证结果
        assertNull(result, "不应该查询到租户");
    }

    /**
     * 测试: 根据域名查询租户 - 成功场景
     */
    @Test
    public void testGetTenantByWebsite_Success() {
        // 准备测试数据
        TenantDO tenant = createAndSaveTenant("测试租户", "test-tenant");

        // 执行查询
        TenantDO result = tenantService.getTenantByWebsite("test-tenant");

        // 验证结果
        assertNotNull(result, "应该查询到租户");
        assertEquals("test-tenant", result.getWebsite(), "域名应该匹配");
    }

    /**
     * 测试: 根据域名查询租户 - 禁用状态的租户
     */
    @Test
    public void testGetTenantByWebsite_Disabled() {
        // 准备测试数据 - 创建禁用状态的租户
        TenantDO tenant = createAndSaveTenant("禁用租户", "disabled-tenant");
        tenant.setStatus(CommonStatusEnum.DISABLE.getStatus());
        tenantDataRepository.update(tenant);

        // 执行查询
        TenantDO result = tenantService.getTenantByWebsite("disabled-tenant");

        // 验证结果 - 禁用的租户也应该能查询到(由Controller层过滤)
        assertNotNull(result, "应该查询到租户(包括禁用的)");
    }

    /**
     * 测试: 获取指定状态的租户列表
     */
    @Test
    public void testGetTenantListByStatus() {
        // 准备测试数据
        createAndSaveTenant("启用租户1", "enabled1", CommonStatusEnum.ENABLE.getStatus());
        createAndSaveTenant("启用租户2", "enabled2", CommonStatusEnum.ENABLE.getStatus());
        createAndSaveTenant("禁用租户", "disabled", CommonStatusEnum.DISABLE.getStatus());

        // 执行查询 - 查询启用状态的租户
        var enabledTenants = tenantService.getTenantListByStatus(CommonStatusEnum.ENABLE.getStatus());

        // 验证结果
        assertNotNull(enabledTenants, "应该返回租户列表");
        assertEquals(2, enabledTenants.size(), "应该有2个启用的租户");
        assertTrue(enabledTenants.stream().allMatch(t ->
            t.getStatus().equals(CommonStatusEnum.ENABLE.getStatus())),
            "所有租户都应该是启用状态");
    }

    /**
     * 测试: 校验租户 - 正常租户
     */
    @Test
    public void testValidTenant_Success() {
        // 准备测试数据
        TenantDO tenant = createAndSaveTenant("正常租户", "normal-tenant");
        tenant.setExpireTime(LocalDateTime.now().plusDays(30)); // 未过期
        tenantDataRepository.update(tenant);

        // 执行校验 - 不应该抛出异常
        assertDoesNotThrow(() -> tenantService.validTenant(tenant.getId()),
            "正常租户校验应该通过");
    }

    /**
     * 测试: 校验租户 - 租户不存在
     */
    @Test
    public void testValidTenant_NotExists() {
        // 执行校验 - 应该抛出异常
        assertThrows(Exception.class, () -> tenantService.validTenant(99999L),
            "不存在的租户应该抛出异常");
    }

    /**
     * 测试: 校验租户 - 租户已禁用
     */
    @Test
    public void testValidTenant_Disabled() {
        // 准备测试数据
        TenantDO tenant = createAndSaveTenant("禁用租户", "disabled-tenant");
        tenant.setStatus(CommonStatusEnum.DISABLE.getStatus());
        tenantDataRepository.update(tenant);

        // 执行校验 - 应该抛出异常
        assertThrows(Exception.class, () -> tenantService.validTenant(tenant.getId()),
            "禁用的租户应该抛出异常");
    }

    /**
     * 测试: 校验租户 - 租户已过期
     */
    @Test
    public void testValidTenant_Expired() {
        // 准备测试数据
        TenantDO tenant = createAndSaveTenant("过期租户", "expired-tenant");
        tenant.setExpireTime(LocalDateTime.now().minusDays(1)); // 已过期
        tenantDataRepository.update(tenant);

        // 执行校验 - 应该抛出异常
        assertThrows(Exception.class, () -> tenantService.validTenant(tenant.getId()),
            "过期的租户应该抛出异常");
    }

    /**
     * 测试: 获取可分配账号数量
     */
    @Test
    public void testGetAvailableAccountCount() {
        // Mock用户数量
        when(userService.getUserCountByStatus(any())).thenReturn(50);

        // 执行查询
        Long availableCount = tenantService.getAvailableAccountCount();

        // 验证结果 (License限制100,已使用50,剩余50)
        assertNotNull(availableCount, "应该返回可分配数量");
        assertEquals(50L, availableCount, "可分配数量应该正确计算");
    }

    /**
     * 测试: 获取可分配账号数量 - 超出限制
     */
    @Test
    public void testGetAvailableAccountCount_Exceeded() {
        // Mock用户数量超过License限制
        when(userService.getUserCountByStatus(any())).thenReturn(150);

        // 执行查询
        Long availableCount = tenantService.getAvailableAccountCount();

        // 验证结果
        assertEquals(0L, availableCount, "超出限制时应该返回0");
    }

    /**
     * 测试: 获取可分配账号数量 - 无License
     */
    @Test
    public void testGetAvailableAccountCount_NoLicense() {
        // Mock无License
        when(licenseService.getLatestActiveLicense()).thenReturn(null);

        // 执行查询
        Long availableCount = tenantService.getAvailableAccountCount();

        // 验证结果
        assertEquals(0L, availableCount, "无License时应该返回0");
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建并保存测试租户
     */
    private TenantDO createAndSaveTenant(String name, String website) {
        return createAndSaveTenant(name, website, CommonStatusEnum.ENABLE.getStatus());
    }

    /**
     * 创建并保存测试租户(指定状态)
     */
    private TenantDO createAndSaveTenant(String name, String website, Integer status) {
        TenantDO tenant = new TenantDO();
        tenant.setName(name);
        tenant.setWebsite(website);
        tenant.setStatus(status);
        tenant.setPackageId(1L);
        tenant.setAccountCount(10);
        tenant.setExpireTime(LocalDateTime.now().plusYears(1));
        return tenant;
        //return tenantDataRepository.insert(tenant);
    }

    /**
     * 创建测试租户套餐
     */
    private TenantPackageDO createTestPackage() {
        TenantPackageDO packageDO = new TenantPackageDO();
        packageDO.setId(1L);
        packageDO.setName("全量套餐");
        packageDO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        packageDO.setCode(PackageTypeEnum.ALL.getCode());
        return packageDO;
    }

    /**
     * 创建测试License
     */
    private LicenseDO createTestLicense() {
        LicenseDO license = new LicenseDO();
        license.setId(1L);
        license.setUserLimit(100);  // 用户限制100
        license.setTenantLimit(10); // 租户限制10
        license.setStatus("active"); // License状态为active
        license.setExpireTime(LocalDateTime.now().plusYears(1));
        return license;
    }
}
