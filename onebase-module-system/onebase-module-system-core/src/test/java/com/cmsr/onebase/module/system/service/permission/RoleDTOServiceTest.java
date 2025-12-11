package com.cmsr.onebase.module.system.service.permission;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.database.RoleDataRepository;
import com.cmsr.onebase.module.system.dal.database.UserRoleDataRepository;
import com.cmsr.onebase.module.system.dal.database.user.UserDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.enums.permission.DataScopeEnum;
import com.cmsr.onebase.module.system.enums.permission.RoleCodeEnum;
import com.cmsr.onebase.module.system.enums.permission.RoleTypeEnum;
import com.cmsr.onebase.module.system.vo.role.RoleInsertReqVO;
import com.cmsr.onebase.module.system.vo.role.RolePageReqVO;
import com.cmsr.onebase.module.system.vo.role.RoleUpdateReqVO;
import jakarta.annotation.Resource;
import org.anyline.data.param.init.DefaultConfigStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * RoleService 单元测试
 * <p>
 * 测试角色服务的各种功能，包括角色的创建、更新、删除、查询等核心操作
 *
 * @author matianyu
 * @date 2025-08-06
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
public class RoleDTOServiceTest {

    @Resource
    private RoleService roleService;

    @Resource
    private RoleDataRepository roleDataRepository;

    @Resource
    private UserRoleDataRepository userRoleDataRepository;

    @Resource
    private UserDataRepository userDataRepository;

    /**
     * 每个测试后清理数据
     */
    @AfterEach
    public void tearDown() {
        // 清理测试数据
        userRoleDataRepository.deleteByConfig(new DefaultConfigStore());
        roleDataRepository.deleteByConfig(new DefaultConfigStore());
        userDataRepository.deleteByConfig(new DefaultConfigStore());
    }

    /**
     * 测试createRole方法 - 正常创建角色
     */
    @Test
    public void testCreateRole_Success() {
        // 准备数据
        RoleInsertReqVO createReqVO = new RoleInsertReqVO();
        createReqVO.setName("测试角色");
        createReqVO.setCode("TEST_ROLE");
        createReqVO.setSort(1);
        createReqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        createReqVO.setRemark("测试角色描述");

        // 执行测试
        Long roleId = roleService.createRole(createReqVO, RoleTypeEnum.CUSTOM.getType());

        // 验证结果
        assertNotNull(roleId, "角色ID不应该为空");

        RoleDO role = roleService.getRole(roleId);
        assertNotNull(role, "创建的角色应该存在");
        assertEquals("测试角色", role.getName(), "角色名称应该一致");
        assertEquals("TEST_ROLE", role.getCode(), "角色编码应该一致");
        assertEquals(RoleTypeEnum.CUSTOM.getType(), role.getType(), "角色类型应该一致");
        assertEquals(CommonStatusEnum.ENABLE.getStatus(), role.getStatus(), "角色状态应该一致");
        assertEquals(DataScopeEnum.ALL.getScope(), role.getDataScope(), "默认数据权限应该是全部");
    }

    /**
     * 测试createRole方法 - 角色名称重复
     */
    @Test
    public void testCreateRole_NameDuplicate() {
        // 准备数据 - 先创建一个角色
        createTestRole(null, "重复角色", "DUPLICATE_ROLE", CommonStatusEnum.ENABLE.getStatus());

        // 准备创建重复名称的角色
        RoleInsertReqVO createReqVO = new RoleInsertReqVO();
        createReqVO.setName("重复角色");
        createReqVO.setCode("ANOTHER_ROLE");

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class,
            () -> roleService.createRole(createReqVO, RoleTypeEnum.CUSTOM.getType()));

        assertEquals(ROLE_NAME_DUPLICATE.getCode(), exception.getCode(), "应该抛出角色名称重复异常");
    }

    /**
     * 测试createRole方法 - 角色编码重复
     */
    @Test
    public void testCreateRole_CodeDuplicate() {
        // 准备数据 - 先创建一个角色
        createTestRole(null, "角色1", "DUPLICATE_CODE", CommonStatusEnum.ENABLE.getStatus());

        // 准备创建重复编码的角色
        RoleInsertReqVO createReqVO = new RoleInsertReqVO();
        createReqVO.setName("角色2");
        createReqVO.setCode("DUPLICATE_CODE");

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class,
            () -> roleService.createRole(createReqVO, RoleTypeEnum.CUSTOM.getType()));

        assertEquals(ROLE_CODE_DUPLICATE.getCode(), exception.getCode(), "应该抛出角色编码重复异常");
    }

    /**
     * 测试createRole方法 - 超级管理员编码不允许创建
     */
    @Test
    public void testCreateRole_SuperAdminCode() {
        // 准备数据
        RoleInsertReqVO createReqVO = new RoleInsertReqVO();
        createReqVO.setName("超级管理员");
        createReqVO.setCode(RoleCodeEnum.SUPER_ADMIN.getCode());

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class,
            () -> roleService.createRole(createReqVO, RoleTypeEnum.CUSTOM.getType()));

        assertEquals(ROLE_ADMIN_CODE_ERROR.getCode(), exception.getCode(), "不允许创建超级管理员角色");
    }

    /**
     * 测试updateRole方法 - 正常更新角色
     */
    @Test
    public void testUpdateRole_Success() {
        // 准备数据 - 先创建一个角色
        RoleDO role = createTestRole(null, "原始角色", "ORIGINAL_ROLE", CommonStatusEnum.ENABLE.getStatus());

        // 准备更新数据
        RoleUpdateReqVO updateReqVO = new RoleUpdateReqVO();
        updateReqVO.setId(role.getId());
        updateReqVO.setName("更新后角色");
        updateReqVO.setCode("UPDATED_ROLE");
        updateReqVO.setSort(2);
        updateReqVO.setStatus(CommonStatusEnum.DISABLE.getStatus());
        updateReqVO.setRemark("更新后描述");

        // 执行测试
        roleService.updateRole(updateReqVO);

        // 验证结果
        RoleDO updatedRole = roleService.getRole(role.getId());
        assertNotNull(updatedRole, "更新后的角色应该存在");
        assertEquals("更新后角色", updatedRole.getName(), "角色名称应该已更新");
        assertEquals("UPDATED_ROLE", updatedRole.getCode(), "角色编码应该已更新");
        assertEquals(CommonStatusEnum.DISABLE.getStatus(), updatedRole.getStatus(), "角色状态应该已更新");
    }

    /**
     * 测试updateRole方法 - 更新不存在的角色
     */
    @Test
    public void testUpdateRole_NotExists() {
        // 准备数据
        RoleUpdateReqVO updateReqVO = new RoleUpdateReqVO();
        updateReqVO.setId(999L);
        updateReqVO.setName("不存在的角色");
        updateReqVO.setCode("NOT_EXISTS");

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class,
            () -> roleService.updateRole(updateReqVO));

        assertEquals(ROLE_NOT_EXISTS.getCode(), exception.getCode(), "应该抛出角色不存在异常");
    }

    /**
     * 测试updateRole方法 - 不允许更新系统角色
     */
    @Test
    public void testUpdateRole_SystemRole() {
        // 准备数据 - 创建系统角色
        RoleDO role = createTestRole(null, "系统角色", "SYSTEM_ROLE", CommonStatusEnum.ENABLE.getStatus());
        role.setType(RoleTypeEnum.SYSTEM.getType());
        roleDataRepository.update(role);

        // 准备更新数据
        RoleUpdateReqVO updateReqVO = new RoleUpdateReqVO();
        updateReqVO.setId(role.getId());
        updateReqVO.setName("尝试更新系统角色");
        updateReqVO.setCode("TRY_UPDATE_SYSTEM");

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class,
            () -> roleService.updateRole(updateReqVO));

        assertEquals(ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE.getCode(), exception.getCode(), "不允许更新系统角色");
    }

    /**
     * 测试updateRoleDataScope方法 - 更新角色数据权限
     */
    @Test
    public void testUpdateRoleDataScope() {
        // 准备数据
        RoleDO role = createTestRole(null, "测试角色", "TEST_ROLE", CommonStatusEnum.ENABLE.getStatus());
        Set<Long> dataScopeDeptIds = new HashSet<>(Arrays.asList(1L, 2L, 3L));

        // 执行测试
        roleService.updateRoleDataScope(role.getId(), DataScopeEnum.DEPT_CUSTOM.getScope(), dataScopeDeptIds);

        // 验证结果
        RoleDO updatedRole = roleService.getRole(role.getId());
        assertEquals(DataScopeEnum.DEPT_CUSTOM.getScope(), updatedRole.getDataScope(), "数据权限范围应该已更新");
        assertEquals(dataScopeDeptIds.size(), updatedRole.getDataScopeDeptIds().size(), "数据权限部门数量应该一致");

        // 处理可能的类型转换问题：将获取到的数据转换为Long类型的Set
        Set<Long> actualDeptIds = new HashSet<>();
        if (updatedRole.getDataScopeDeptIds() != null) {
            for (Object id : updatedRole.getDataScopeDeptIds()) {
                if (id instanceof String) {
                    actualDeptIds.add(Long.valueOf((String) id));
                } else if (id instanceof Long) {
                    actualDeptIds.add((Long) id);
                } else if (id instanceof Number) {
                    actualDeptIds.add(((Number) id).longValue());
                }
            }
        }

        // 验证数据内容是否完全一致
        assertEquals(dataScopeDeptIds, actualDeptIds, "数据权限部门应该完全一致");

        // 双向验证确保数据完整性
        assertTrue(actualDeptIds.containsAll(dataScopeDeptIds), "应该包含所有设置的部门ID");
        assertTrue(dataScopeDeptIds.containsAll(actualDeptIds), "不应该包含额外的部门ID");
    }

    /**
     * 测试deleteRole方法 - 正常删除角色
     */
    @Test
    public void testDeleteRole_Success() {
        // 准备数据
        RoleDO role = createTestRole(null, "待删除角色", "DELETE_ROLE", CommonStatusEnum.ENABLE.getStatus());

        // 执行测试
        roleService.deleteRole(role.getId());

        // 验证结果
        RoleDO deletedRole = roleService.getRole(role.getId());
        assertNull(deletedRole, "删除后角色应该不存在");
    }

    /**
     * 测试deleteRole方法 - 删除不存在的角色
     */
    @Test
    public void testDeleteRole_NotExists() {
        // 执行测试并验证���常
        ServiceException exception = assertThrows(ServiceException.class,
            () -> roleService.deleteRole(999L));

        assertEquals(ROLE_NOT_EXISTS.getCode(), exception.getCode(), "应该抛出角色不存在异常");
    }

    /**
     * 测试deleteRole方法 - 不允许删除系统角色
     */
    @Test
    public void testDeleteRole_SystemRole() {
        // 准备数据 - 创建系统角色
        RoleDO role = createTestRole(null, "系统角色", "SYSTEM_ROLE", CommonStatusEnum.ENABLE.getStatus());
        role.setType(RoleTypeEnum.SYSTEM.getType());
        roleDataRepository.update(role);

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class,
            () -> roleService.deleteRole(role.getId()));

        assertEquals(ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE.getCode(), exception.getCode(), "不允许删除系统角色");
    }

    /**
     * 测试getRole方法 - 根据ID获取角色
     */
    @Test
    public void testGetRole() {
        // 准备数据
        RoleDO role = createTestRole(null, "测试角色", "TEST_ROLE", CommonStatusEnum.ENABLE.getStatus());

        // 执行测试
        RoleDO result = roleService.getRole(role.getId());

        // 验证结果
        assertNotNull(result, "角色应该存在");
        assertEquals(role.getId(), result.getId(), "角色ID应该一致");
        assertEquals("测试角色", result.getName(), "角色名称应该一致");
        assertEquals("TEST_ROLE", result.getCode(), "角色编码应该一致");
    }

    /**
     * 测试getRoleIdsByCode方法 - 根据编码获取角色
     */
    @Test
    public void testGetRoleIdsByCode() {
        // 准备数据
        RoleDO role = createTestRole(null, "测试角色", "TEST_ROLE", CommonStatusEnum.ENABLE.getStatus());

        // 执行测试
        RoleDO result = roleService.getRoleIdsByCode("TEST_ROLE");

        // 验证结果
        assertNotNull(result, "角色应该存在");
        assertEquals(role.getId(), result.getId(), "角色ID应该一致");
        assertEquals("TEST_ROLE", result.getCode(), "角色编码应该一致");
    }

    /**
     * 测试getRoleListByStatus方法 - 根据状态获取角色列表
     */
    @Test
    public void testGetRoleListByStatus() {
        // 准备数据
        createTestRole(null, "启用角色1", "ENABLE_ROLE_1", CommonStatusEnum.ENABLE.getStatus());
        createTestRole(null, "启用角色2", "ENABLE_ROLE_2", CommonStatusEnum.ENABLE.getStatus());
        createTestRole(null, "禁用角色", "DISABLE_ROLE", CommonStatusEnum.DISABLE.getStatus());

        // 执行测试
        List<RoleDO> enabledRoles = roleService.getRoleListByStatus(CommonStatusEnum.ENABLE.getStatus());

        // 验证结果
        assertEquals(2, enabledRoles.size(), "应该有2个启用的角色");
        assertTrue(enabledRoles.stream().allMatch(role ->
            CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus())), "所有角色都应该是启用状态");
    }

    /**
     * 测试getRoleList方法 - 获取所有角色列表
     */
    @Test
    public void testGetRoleList() {
        // 准备数据
        createTestRole(null, "角色1", "ROLE_1", CommonStatusEnum.ENABLE.getStatus());
        createTestRole(null, "角色2", "ROLE_2", CommonStatusEnum.DISABLE.getStatus());

        // 执行测试
        List<RoleDO> allRoles = roleService.getRoleList();

        // 验证结果
        assertEquals(2, allRoles.size(), "应该有2个角色");
    }

    /**
     * 测试getRoleList方法 - 根据ID集合获取角色列表
     */
    @Test
    public void testGetRoleListByIds() {
        // 准备数据
        RoleDO role1 = createTestRole(null, "角色1", "ROLE_1", CommonStatusEnum.ENABLE.getStatus());
        RoleDO role2 = createTestRole(null, "角色2", "ROLE_2", CommonStatusEnum.ENABLE.getStatus());
        createTestRole(null, "角色3", "ROLE_3", CommonStatusEnum.ENABLE.getStatus());

        Collection<Long> ids = Arrays.asList(role1.getId(), role2.getId());

        // 执行测试
        List<RoleDO> roles = roleService.getRoleList(ids);

        // 验证结果
        assertEquals(2, roles.size(), "应该返回2个角色");
        assertTrue(roles.stream().anyMatch(role -> role.getId().equals(role1.getId())), "应该包含角色1");
        assertTrue(roles.stream().anyMatch(role -> role.getId().equals(role2.getId())), "应该包含角色2");
    }

    /**
     * 测试getRolePage方法 - 分页查询角色
     */
    @Test
    public void testGetRolePage() {
        // 准备数据
        createTestRole(null, "管理员角色", "ADMIN_ROLE", CommonStatusEnum.ENABLE.getStatus());
        createTestRole(null, "普通角色", "USER_ROLE", CommonStatusEnum.ENABLE.getStatus());
        createTestRole(null, "禁用角色", "DISABLE_ROLE", CommonStatusEnum.DISABLE.getStatus());

        // 准备查询条件
        RolePageReqVO reqVO = new RolePageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(10);
        reqVO.setName("角色");
        reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // 执行测试
        PageResult<RoleDO> pageResult = roleService.findRolePageOnlyTenant(reqVO);

        // 验证结果
        assertNotNull(pageResult, "分页结果不应该为空");
        assertEquals(2, pageResult.getTotal(), "符合条件的角色应该有2个");
        assertTrue(pageResult.getList().stream().allMatch(role ->
            role.getName().contains("角色") &&
            CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus())),
            "所有结果都应该符合查询条件");
    }

    /**
     * 测试hasAnySuperAdmin方法 - 检查是否包含超级管理员
     */
    @Test
    public void testHasAnySuperAdmin() {
        // 准备数据 - 创建超级管理员角色
        RoleDO superAdminRole = createTestRole(null, "超级管理员", RoleCodeEnum.SUPER_ADMIN.getCode(),
            CommonStatusEnum.ENABLE.getStatus());
        RoleDO normalRole = createTestRole(null, "普通角色", "NORMAL_ROLE", CommonStatusEnum.ENABLE.getStatus());

        // 执行测试
        boolean hasSuperAdmin = roleService.hasAnySuperOrTenantAdmin(Arrays.asList(superAdminRole.getId(), normalRole.getId()));
        boolean hasNoSuperAdmin = roleService.hasAnySuperOrTenantAdmin(Collections.singleton(normalRole.getId()));

        // 验证结果
        assertTrue(hasSuperAdmin, "应该包含超级管理员");
        assertFalse(hasNoSuperAdmin, "不应该包含超级管理员");
    }

    /**
     * 测试validateRoleList方法 - 校验角色列表有效性
     */
    @Test
    public void testValidateRoleList_Success() {
        // 准备数据 - 创建有效角色
        RoleDO role1 = createTestRole(null, "角色1", "ROLE_1", CommonStatusEnum.ENABLE.getStatus());
        RoleDO role2 = createTestRole(null, "角色2", "ROLE_2", CommonStatusEnum.ENABLE.getStatus());

        Collection<Long> roleIds = Arrays.asList(role1.getId(), role2.getId());

        // 执行测试 - 不应该抛出异常
        assertDoesNotThrow(() -> roleService.validateRoleList(roleIds), "有��的角色列表不应该抛出异常");
    }

    /**
     * 测试validateRoleList方法 - 角色不存在
     */
    @Test
    public void testValidateRoleList_NotExists() {
        // 准备数据 - 使用不存在的角色ID
        Collection<Long> roleIds = Collections.singleton(999L);

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class,
            () -> roleService.validateRoleList(roleIds));

        assertEquals(ROLE_NOT_EXISTS.getCode(), exception.getCode(), "应该抛出角色不存在异常");
    }

    /**
     * 测试validateRoleList方法 - 角色被禁用
     */
    @Test
    public void testValidateRoleList_Disabled() {
        // 准备数据 - 创建禁用的角色
        RoleDO disabledRole = createTestRole(null, "禁用角色", "DISABLED_ROLE", CommonStatusEnum.DISABLE.getStatus());

        Collection<Long> roleIds = Collections.singleton(disabledRole.getId());

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class,
            () -> roleService.validateRoleList(roleIds));

        assertEquals(ROLE_IS_DISABLE.getCode(), exception.getCode(), "应该抛出角色被禁用异常");
    }

    // ========== 辅助方法 ==========

    /**
     * 创建测试角色
     */
    private RoleDO createTestRole(Long roleId, String name, String code, Integer status) {
        RoleDO role = new RoleDO();
        // 完全不设置ID，让Anyline使用SnowflakeIdGenerator自动生成
        role.setName(name);
        role.setCode(code);
        role.setSort(1);
        role.setStatus(status);
        role.setType(RoleTypeEnum.CUSTOM.getType());
        role.setDataScope(DataScopeEnum.ALL.getScope());
        role.setRemark("测试角色");
        role.setTenantId(0L);
        RoleDO saved = roleDataRepository.insert(role);
        return saved;
    }
}

