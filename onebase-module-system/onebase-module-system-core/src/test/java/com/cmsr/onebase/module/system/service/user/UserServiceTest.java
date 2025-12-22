package com.cmsr.onebase.module.system.service.user;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.database.RoleDataRepository;
import com.cmsr.onebase.module.system.dal.database.UserRoleDataRepository;
import com.cmsr.onebase.module.system.dal.database.dept.DeptDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.UserRoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.dal.flex.repo.UserDataRepository;
import com.cmsr.onebase.module.system.enums.permission.DataScopeEnum;
import com.cmsr.onebase.module.system.service.permission.PermissionService;
import com.cmsr.onebase.module.system.vo.user.UserPageReqVO;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.anyline.data.param.init.DefaultConfigStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AdminUserService 单元测试
 * <p>
 * 测试用户服务的各种功能，特别是getUserPage方法的过滤条件
 *
 * @author matianyu
 * @date 2025-01-15
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
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Resource
    private PermissionService permissionService;

    @Resource
    private UserDataRepository userDataRepository;

    @Resource
    private DeptDataRepository deptDataRepository;

    @Resource
    private RoleDataRepository roleDataRepository;

    @Resource
    private UserRoleDataRepository userRoleDataRepository;

    private AdminUserDO testUser1;
    private AdminUserDO testUser2;
    private AdminUserDO testUser3;
    private DeptDO testDept1;
    private DeptDO testDept2;
    private RoleDO testRole;

    /**
     * 初始化测试数据
     */
    @BeforeEach
    public void setUp() {
        // 创建测试部门
        testDept1 = createTestDept("技术部", 0L);
        testDept2 = createTestDept("产品部", 0L);

        // 创建测试角色
        testRole = createTestRole("TEST_ROLE", "测试角色");

        // 创建测试用户
        LocalDateTime now = LocalDateTime.now();
        testUser1 = createTestUser("user1", "用户1", "13800000001", CommonStatusEnum.ENABLE.getStatus(), testDept1.getId(), now.minus(2, ChronoUnit.DAYS));
        testUser2 = createTestUser("user2", "用户2", "13800000002", CommonStatusEnum.DISABLE.getStatus(), testDept2.getId(), now.minus(1, ChronoUnit.DAYS));
        testUser3 = createTestUser("admin", "管理员", "13900000003", CommonStatusEnum.ENABLE.getStatus(), testDept1.getId(), now);

        // 为testUser1分配角色
        createUserRole(testUser1.getId(), testRole.getId());
    }

    /**
     * 清理测试数据
     */
    @AfterEach
    public void tearDown() {
        userRoleDataRepository.deleteByConfig(new DefaultConfigStore());
        userDataRepository.remove(new QueryWrapper());
        roleDataRepository.deleteByConfig(new DefaultConfigStore());
        deptDataRepository.deleteByConfig(new DefaultConfigStore());
    }

    /**
     * 测试getUserPage方法 - 无过滤条件，查询所有用户
     */
    @Test
    public void testGetUserPage_NoFilter() {
        // 准备请求参数
        UserPageReqVO reqVO = new UserPageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(10);

        // 执行测试
        PageResult<AdminUserDO> result = userService.getUserPage(reqVO);

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(3, result.getTotal(), "应该返回3个用户");
        assertEquals(3, result.getList().size(), "列表应该包含3个用户");
    }

    /**
     * 测试getUserPage方法 - 按用户名过滤
     */
    @Test
    public void testGetUserPage_FilterByUsername() {
        // 准备请求参数
        UserPageReqVO reqVO = new UserPageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(10);
        reqVO.setNickname("user");

        // 执行测试
        PageResult<AdminUserDO> result = userService.getUserPage(reqVO);

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(2, result.getTotal(), "应该返回2个匹配的用户");
        assertTrue(result.getList().stream().allMatch(user ->
            user.getUsername().contains("user")), "所有返回的用户名都应该包含'user'");
    }

    /**
     * 测试getUserPage方法 - 按手机号过滤
     */
    @Test
    public void testGetUserPage_FilterByMobile() {
        // 准备请求参数
        UserPageReqVO reqVO = new UserPageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(10);
        reqVO.setMobile("138");

        // 执行测试
        PageResult<AdminUserDO> result = userService.getUserPage(reqVO);

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(2, result.getTotal(), "应该返回2个匹配的用户");
        assertTrue(result.getList().stream().allMatch(user ->
            user.getMobile().contains("138")), "所有返回的用户手机号都应该包含'138'");
    }

    /**
     * 测试getUserPage方法 - 按状态过滤
     */
    @Test
    public void testGetUserPage_FilterByStatus() {
        // 准备请求参数
        UserPageReqVO reqVO = new UserPageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(10);
        reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // 执行测试
        PageResult<AdminUserDO> result = userService.getUserPage(reqVO);

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(2, result.getTotal(), "应该返回2个启用状态的用户");
        assertTrue(result.getList().stream().allMatch(user ->
            CommonStatusEnum.ENABLE.getStatus().equals(user.getStatus())), "所有返回的用户状态都应该是启用");
    }

    /**
     * 测试getUserPage方法 - 按部门ID过滤
     */
    @Test
    public void testGetUserPage_FilterByDeptId() {
        // 准备请求参数
        UserPageReqVO reqVO = new UserPageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(10);
        reqVO.setDeptId(testDept1.getId());

        // 执行测试
        PageResult<AdminUserDO> result = userService.getUserPage(reqVO);

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(2, result.getTotal(), "应该返回2个技术部的用户");
        assertTrue(result.getList().stream().allMatch(user ->
            testDept1.getId().equals(user.getDeptId())), "所有返回的用户都应该属于技术部");
    }

    /**
     * 测试getUserPage方法 - 按角色ID过滤
     */
    @Test
    public void testGetUserPage_FilterByRoleId() {
        // 准备请求参数
        UserPageReqVO reqVO = new UserPageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(10);
        reqVO.setRoleId(testRole.getId());

        // 执行测试
        PageResult<AdminUserDO> result = userService.getUserPage(reqVO);

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(1, result.getTotal(), "应该返回1个拥有测试角色的用户");
        assertEquals(testUser1.getId(), result.getList().get(0).getId(), "返回的用户应该是testUser1");
    }

    /**
     * 测试getUserPage方法 - 按创建时间范围过滤
     */
    @Test
    public void testGetUserPage_FilterByCreateTime() {
        // 准备请求参数
        LocalDateTime now = LocalDateTime.now();
        UserPageReqVO reqVO = new UserPageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(10);
        reqVO.setCreateTime(new LocalDateTime[]{
            now.minus(2, ChronoUnit.DAYS).minus(1, ChronoUnit.HOURS),
            now.minus(1, ChronoUnit.HOURS)
        });

        // 执行测试
        PageResult<AdminUserDO> result = userService.getUserPage(reqVO);

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(2, result.getTotal(), "应该返回2个在指定时间范围内创建的用户");
    }

    /**
     * 测试getUserPage方法 - 组合过滤条件
     */
    @Test
    public void testGetUserPage_CombinedFilters() {
        // 准备请求参数
        UserPageReqVO reqVO = new UserPageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(10);
        reqVO.setNickname("user");
        reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        reqVO.setDeptId(testDept1.getId());

        // 执行测试
        PageResult<AdminUserDO> result = userService.getUserPage(reqVO);

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(1, result.getTotal(), "应该返回1个满足所有条件的用户");
        AdminUserDO user = result.getList().get(0);
        assertTrue(user.getUsername().contains("user"), "用户名应该包含'user'");
        assertEquals(CommonStatusEnum.ENABLE.getStatus(), user.getStatus(), "状态应该是启用");
        assertEquals(testDept1.getId(), user.getDeptId(), "应该属于技术部");
    }

    /**
     * 测试getUserPage方法 - 不存在的角色ID
     */
    @Test
    public void testGetUserPage_NonExistentRoleId() {
        // 准备请求参数
        UserPageReqVO reqVO = new UserPageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(10);
        reqVO.setRoleId(99999L); // 不存在的角色ID

        // 执行测试
        PageResult<AdminUserDO> result = userService.getUserPage(reqVO);

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(0, result.getTotal(), "不存在的角色应该返回0个用户");
        assertTrue(result.getList().isEmpty(), "列表应该为空");
    }

    /**
     * 测试getUserPage方法 - 分页功能
     */
    @Test
    public void testGetUserPage_Pagination() {
        // 准备请求参数 - 第一页，每页2条
        UserPageReqVO reqVO = new UserPageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(2);

        // 执行测试
        PageResult<AdminUserDO> result = userService.getUserPage(reqVO);

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(3, result.getTotal(), "总记录数应该是3");
        assertEquals(2, result.getList().size(), "第一页应该返回2条记录");

        // 测试第二页
        reqVO.setPageNo(2);
        PageResult<AdminUserDO> result2 = userService.getUserPage(reqVO);
        assertEquals(1, result2.getList().size(), "第二页应该返回1条记录");
    }

    // ========== 辅助方法 ==========

    /**
     * 创建测试用户
     */
    private AdminUserDO createTestUser(String username, String nickname, String mobile, Integer status, Long deptId, LocalDateTime createTime) {
        AdminUserDO user = new AdminUserDO();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setPassword("password");
        user.setStatus(status);
        user.setEmail(username + "@test.com");
        user.setMobile(mobile);
        user.setDeptId(deptId);
        user.setTenantId(0L);
        if (createTime != null) {
            user.setCreateTime(createTime);
        }
        userDataRepository.save(user);
        return user;
    }

    /**
     * 创建测试部门
     */
    private DeptDO createTestDept(String name, Long parentId) {
        DeptDO dept = new DeptDO();
        dept.setName(name);
        dept.setParentId(parentId);
        dept.setSort(1);
        dept.setStatus(CommonStatusEnum.ENABLE.getStatus());
        dept.setTenantId(0L);
        return deptDataRepository.insert(dept);
    }

    /**
     * 创建测试角色
     */
    private RoleDO createTestRole(String code, String name) {
        RoleDO role = new RoleDO();
        role.setName(name);
        role.setCode(code);
        role.setSort(1);
        role.setStatus(CommonStatusEnum.ENABLE.getStatus());
        role.setType(1);
        role.setDataScope(DataScopeEnum.ALL.getScope());
        role.setTenantId(0L);
        return roleDataRepository.insert(role);
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
}

