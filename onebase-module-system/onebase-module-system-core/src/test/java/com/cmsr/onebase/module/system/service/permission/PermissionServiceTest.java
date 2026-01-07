package com.cmsr.onebase.module.system.service.permission;

import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.framework.common.biz.system.permission.dto.DeptDataPermissionRespDTO;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.module.system.dal.database.MenuDataRepository;
import com.cmsr.onebase.module.system.dal.database.PermissionDataRepository;
import com.cmsr.onebase.module.system.dal.database.RoleDataRepository;
import com.cmsr.onebase.module.system.dal.database.UserRoleDataRepository;
import com.cmsr.onebase.module.system.dal.database.dept.DeptDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.MenuDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleMenuDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.UserRoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.dal.flex.repo.UserDataRepository;
import com.cmsr.onebase.module.system.enums.permission.DataScopeEnum;
import jakarta.annotation.Resource;
import org.anyline.data.param.init.DefaultConfigStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PermissionService 单元测试
 * <p>
 * 测试权限服务的各种功能，包括用户权限检查、角色菜单管理、用户角色管理等
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
public class PermissionServiceTest {

    @Resource
    private PermissionService permissionService;

    @Resource
    private PermissionDataRepository permissionDataRepository;
    @Resource
    private UserRoleDataRepository   userRoleDataRepository;
    @Resource
    private RoleDataRepository roleDataRepository;
    @Resource
    private DeptDataRepository deptDataRepository;
    @Resource
    private MenuDataRepository menuDataRepository;
    @Resource
    private UserDataRepository userDataRepository;

    /**
     * 每个测试后清理数据
     */
    @AfterEach
    public void tearDown() {
        // 清理测试数据
        permissionDataRepository.deleteByConfig(new DefaultConfigStore());
        userRoleDataRepository.deleteByConfig(new DefaultConfigStore());
        roleDataRepository.deleteByConfig(new DefaultConfigStore());
        deptDataRepository.deleteByConfig(new DefaultConfigStore());
        menuDataRepository.deleteByConfig(new DefaultConfigStore());
    }

    /**
     * 测试hasAnyPermissions方法 - 正常情况
     */
    @Test
    public void testHasAnyPermissions_Normal() {
        // 准备数据
        String permission = "tenant:user:query";

        // 创建用户
        AdminUserDO user = createTestUser(null, "testuser");

        // 创建角色
        RoleDO role = createTestRole(null, "TEST_ROLE", CommonStatusEnum.ENABLE.getStatus());

        // 创建菜单
        MenuDO menu = createTestMenu(null, "用户查询", permission);

        // 创建用户角色关联
        createUserRole(user.getId(), role.getId());

        // 创建角色菜单关联
        createRoleMenu(role.getId(), menu.getId());

        // 执行测试
        boolean result = permissionService.hasAnyPermissions(user.getId(), permission);

        // 验证结果
        assertTrue(result, "用户应该拥有指定权限");
    }

    /**
     * 测试hasAnyPermissions方法 - 用户无权限
     */
    @Test
    public void testHasAnyPermissions_NoPermission() {
        // 准备数据
        String permission = "tenant:user:query";

        // 创建用户（但不分配任何角色）
        AdminUserDO user = createTestUser(null, "testuser");

        // 执行测试
        boolean result = permissionService.hasAnyPermissions(user.getId(), permission);

        // 验证结果
        assertFalse(result, "用户没有角色时应该没有权限");
    }

    /**
     * 测试hasAnyRoles方法 - 正常情况
     */
    @Test
    public void testHasAnyRoles_Normal() {
        // 准备数据
        String roleCode = "TEST_ROLE";

        // 创建用户
        AdminUserDO user = createTestUser(null, "testuser");

        // 创建角色
        RoleDO role = createTestRole(null, roleCode, CommonStatusEnum.ENABLE.getStatus());

        // 创建用户角色关联
        createUserRole(user.getId(), role.getId());

        // 执行测试
        boolean result = permissionService.hasAnyRoles(user.getId(), roleCode);

        // 验证结果
        assertTrue(result, "用户应该拥有指定角色");
    }

    /**
     * 测试assignRoleMenu方法 - 分配角色菜单
     */
    @Test
    public void testAssignRoleMenu() {
        // 准备数据
        Set<Long> menuIds = new HashSet<>();

        // 创建角色
        RoleDO role = createTestRole(null, "TEST_ROLE", CommonStatusEnum.ENABLE.getStatus());

        // 创建菜单
        for (int i = 1; i <= 3; i++) {
            MenuDO menu = createTestMenu(null, "菜单" + i, "permission:" + i);
            menuIds.add(menu.getId());
        }

        // 执行测试
        permissionService.assignRoleMenu(role.getId(), menuIds);

        // 验证结果
        Set<Long> assignedMenuIds = permissionService.getRoleMenuListByRoleId(role.getId());
        assertEquals(menuIds.size(), assignedMenuIds.size(), "分配的菜单数量应该一致");
        assertTrue(assignedMenuIds.containsAll(menuIds), "应该包含所有分配的菜单");
    }

    /**
     * 测试assignUserRoles方法 - 分配用户角色
     */
    @Test
    public void testAssignUserRoles() {
        // 准备数据
        Set<Long> roleIds = new HashSet<>();

        // 创建用户
        AdminUserDO user = createTestUser(null, "testuser");

        // 创建角色
        for (int i = 1; i <= 2; i++) {
            RoleDO role = createTestRole(null, "ROLE_" + i, CommonStatusEnum.ENABLE.getStatus());
            roleIds.add(role.getId());
        }

        // 执行测试
        permissionService.assignUserRoles(user.getId(), roleIds);

        // 验证结果
        Set<Long> assignedRoleIds = permissionService.getRoleIdsListByUserId(user.getId());
        assertEquals(roleIds.size(), assignedRoleIds.size(), "分配的角色数量应该一致");
        assertTrue(assignedRoleIds.containsAll(roleIds), "应该包含所有分配的角色");
    }

    /**
     * 测试processRoleDeleted方法 - 删除角色时清理关联数据
     */
    @Test
    public void testProcessRoleDeleted() {
        // 准备数据
        AdminUserDO user = createTestUser(null, "testuser");
        RoleDO role = createTestRole(null, "TEST_ROLE", CommonStatusEnum.ENABLE.getStatus());
        MenuDO menu = createTestMenu(null, "测试菜单", "test:permission");

        createUserRole(user.getId(), role.getId());
        createRoleMenu(role.getId(), menu.getId());

        // 验证删除前数据存在
        assertFalse(permissionService.getRoleIdsListByUserId(user.getId()).isEmpty(), "删除前应该有用户角色关联");
        assertFalse(permissionService.getRoleMenuListByRoleId(role.getId()).isEmpty(), "删除前应该有角色菜单关联");

        // 执行测试
        permissionService.processRoleDeleted(role.getId());

        // 验证结果
        assertTrue(permissionService.getRoleIdsListByUserId(user.getId()).isEmpty(), "删除后应该清理用户角色关联");
        assertTrue(permissionService.getRoleMenuListByRoleId(role.getId()).isEmpty(), "删除后应该清理角色菜单关联");
    }

    /**
     * 测试processMenuDeleted方法 - 删除菜单时清理关联数据
     */
    @Test
    public void testProcessMenuDeleted() {
        // 准备数据
        RoleDO role = createTestRole(null, "TEST_ROLE", CommonStatusEnum.ENABLE.getStatus());
        MenuDO menu = createTestMenu(null, "测试菜单", "test:permission");

        createRoleMenu(role.getId(), menu.getId());

        // 验证删除前数据存在
        assertFalse(permissionService.getRoleMenuListByRoleId(role.getId()).isEmpty(), "删除前应该有角色菜单关联");

        // 执行测试
        permissionService.processMenuDeleted(menu.getId());

        // 验证结果
        assertTrue(permissionService.getRoleMenuListByRoleId(role.getId()).isEmpty(), "删除后应该清理角色菜单关联");
    }

    /**
     * 测试assignRoleUsers方法 - 为角色分配用户
     */
    @Test
    public void testAddRoleUsers() {
        // 准备数据
        Set<Long> userIds = new HashSet<>();

        // 创建角色
        RoleDO role = createTestRole(null, "TEST_ROLE", CommonStatusEnum.ENABLE.getStatus());

        // 创建用户
        for (int i = 1; i <= 3; i++) {
            AdminUserDO user = createTestUser(null, "user" + i);
            userIds.add(user.getId());
        }

        // 执行测试
        long affectedRows = permissionService.addRoleUsers(role.getId(), userIds);

        // 验证结果
        assertEquals(userIds.size(), affectedRows, "影响的行数应该等于用户数量");
        Set<Long> assignedUserIds = permissionService.getUserIdsListByRoleIds(Collections.singleton(role.getId()));
        assertEquals(userIds.size(), assignedUserIds.size(), "分配的用户数量应该一致");
        assertTrue(assignedUserIds.containsAll(userIds), "应该包含所有分配的用户");
    }

    /**
     * 测试deleteRoleUsers方法 - 从角色中移除用户
     */
    @Test
    public void testDeleteRoleUsers() {
        // 准备数据
        RoleDO role = createTestRole(null, "TEST_ROLE", CommonStatusEnum.ENABLE.getStatus());
        AdminUserDO user1 = createTestUser(null, "user1");
        AdminUserDO user2 = createTestUser(null, "user2");

        Set<Long> userIds = new HashSet<>(Arrays.asList(user1.getId(), user2.getId()));
        Set<Long> deleteUserIds = new HashSet<>(Arrays.asList(user1.getId()));

        // 创建用户角色关联
        for (Long userId : userIds) {
            createUserRole(userId, role.getId());
        }

        // 验证删除前数据存在 - 检查每个用户的角色
        for (Long userId : userIds) {
            Set<Long> userRoles = permissionService.getRoleIdsListByUserId(userId);
            assertTrue(userRoles.contains(role.getId()), "用户应该拥有指定角色");
        }

        // 执行测试
        boolean deleted = permissionService.deleteRoleUsers(role.getId(), deleteUserIds);
        assertTrue(deleted, "删除操作应该返回 true");

        // 检查被删除的用户不再拥有该角色
        Set<Long> user1Roles = permissionService.getRoleIdsListByUserId(user1.getId());
        System.out.println("User1 Roles: " + user1Roles);
        assertFalse(user1Roles.contains(role.getId()), "被删除的用户不应该再拥有该角色");

        // 检查未删除的用户仍然拥有该角色
        Set<Long> user2Roles = permissionService.getRoleIdsListByUserId(user2.getId());
        System.out.println("User2 Roles: " + user2Roles);
        assertTrue(user2Roles.contains(role.getId()), "未删除的用户应该仍然拥有该角色");
    }

    /**
     * 测试getDeptDataPermission方法 - 获取部门数据权限
     */
    @Test
    public void testGetDeptDataPermission() {
        // 准备数据
        DeptDO dept = createTestDept(null, "测试部门");

        // 创建用户并关联部门
        AdminUserDO user = createTestUser(null, "testuser");
        user.setDeptId(dept.getId());
        // permissionDataRepository.update(user);

        // 创建角色并设置数据权限为本部门
        RoleDO role = createTestRole(null, "TEST_ROLE", CommonStatusEnum.ENABLE.getStatus());
        role.setDataScope(DataScopeEnum.DEPT_ONLY.getScope());
        roleDataRepository.update(role);

        // 创建用户角色关联
        createUserRole(user.getId(), role.getId());

        // 执行测试
        DeptDataPermissionRespDTO result = permissionService.getDeptDataPermission(user.getId());

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertFalse(result.getAll(), "不应该有全部数据权限");
        assertFalse(result.getSelf(), "不应该只有自己的数据权限");
        assertTrue(CollUtil.isNotEmpty(result.getDeptIds()), "应该有部门权限");
        assertTrue(result.getDeptIds().contains(dept.getId()), "应该包含用户所在部门");
    }

    // ========== 辅助方法 ==========

    /**
     * 创建测试用户
     */
    private AdminUserDO createTestUser(Long userId, String username) {
        AdminUserDO user = new AdminUserDO();
        user.setUsername(username);
        user.setNickname(username);
        user.setPassword("password");
        user.setStatus(CommonStatusEnum.ENABLE.getStatus());
        user.setEmail(username + "@test.com");
        user.setMobile("1388888888" + (userId != null ? userId % 10 : 0));
        user.setTenantId(0L);
        return userDataRepository.insertReturn(user);
    }

    /**
     * 创建测试角色
     */
    private RoleDO createTestRole(Long roleId, String code, Integer status) {
        RoleDO role = new RoleDO();
        role.setName(code);
        role.setCode(code);
        role.setSort(1);
        role.setStatus(status);
        role.setType(1);
        role.setDataScope(DataScopeEnum.ALL.getScope());
        role.setTenantId(0L);
        return roleDataRepository.insertReturn(role);
    }

    /**
     * 创建测试菜单
     */
    private MenuDO createTestMenu(Long menuId, String name, String permission) {
        MenuDO menu = new MenuDO();
        menu.setName(name);
        menu.setPermission(permission);
        menu.setType(2);
        menu.setSort(1);
        menu.setParentId(0L);
        menu.setStatus(CommonStatusEnum.ENABLE.getStatus());
        return menuDataRepository.insertReturn(menu);
    }

    /**
     * 创建测试部门
     */
    private DeptDO createTestDept(Long deptId, String name) {
        DeptDO dept = new DeptDO();
        dept.setName(name);
        dept.setParentId(0L);
        dept.setSort(1);
        dept.setStatus(CommonStatusEnum.ENABLE.getStatus());
        dept.setTenantId(0L);
        return deptDataRepository.insertReturn(dept);
    }

    /**
     * 创建用户角色关联
     */
    private void createUserRole(Long userId, Long roleId) {
        UserRoleDO userRole = new UserRoleDO();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setTenantId(0L);
        userRoleDataRepository.insert(userRole);
    }

    /**
     * 创建角色菜单关联
     */
    private void createRoleMenu(Long roleId, Long menuId) {
        RoleMenuDO roleMenu = new RoleMenuDO();
        roleMenu.setRoleId(roleId);
        roleMenu.setMenuId(menuId);
        roleMenu.setTenantId(0L);
        permissionDataRepository.insert(roleMenu);
    }
}
