package com.cmsr.onebase.module.system.service.dept;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.module.system.dal.database.dept.DeptDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.dal.flex.repo.UserDataRepository;
import com.cmsr.onebase.module.system.enums.user.UserStatusEnum;
import com.cmsr.onebase.module.system.vo.dept.DeptListReqVO;
import com.cmsr.onebase.module.system.vo.dept.DeptRespVO;
import com.cmsr.onebase.module.system.vo.dept.DeptSaveReqVO;
import com.mybatisflex.core.query.QueryWrapper;
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
 * DeptService 单元测试
 * <p>
 * 测试部门服务的各种功能，特别是部门人数统计功能
 *
 * @author matianyu
 * @date 2025-01-27
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
public class DeptServiceTest {

    @Resource
    private DeptService deptService;

    @Resource
    private DeptDataRepository deptDataRepository;

    @Resource
    private UserDataRepository userDataRepository;

    /**
     * 每个测试后清理数据
     */
    @AfterEach
    public void tearDown() {
        // 清理测试数据
        userDataRepository.remove(new QueryWrapper());
        deptDataRepository.deleteByConfig(new DefaultConfigStore());
    }

    /**
     * 测试getDeptListWithUserCount方法 - 正常情况
     */
    @Test
    public void testGetDeptListWithUserCount_Normal() {
        // 准备数据 - 创建部门
        DeptDO dept1 = createTestDept("技术部");
        DeptDO dept2 = createTestDept("销售部");
        DeptDO dept3 = createTestDept("财务部");

        // 创建用户并分配到不同部门
        // 技术部3个人
        createTestUser("user1", dept1.getId());
        createTestUser("user2", dept1.getId());
        createTestUser("user3", dept1.getId());

        // 销售部2个人
        createTestUser("user4", dept2.getId());
        createTestUser("user5", dept2.getId());

        // 财务部1个人
        createTestUser("user6", dept3.getId());

        // 执行测试
        DeptListReqVO reqVO = new DeptListReqVO();
        List<DeptRespVO> result = deptService.getDeptListWithUserCount(reqVO);

        System.out.println("testGetDeptListWithUserCount_Normal: "+result);
        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(3, result.size(), "应该返回3个部门");

        // 按部门名称查找并验证人数
        DeptRespVO techDept = result.stream()
            .filter(dept -> "技术部".equals(dept.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(techDept, "应该找到技术部");
        assertEquals(3, techDept.getUserCount(), "技术部应该有3个人");

        DeptRespVO salesDept = result.stream()
            .filter(dept -> "销售部".equals(dept.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(salesDept, "应该找到销售部");
        assertEquals(2, salesDept.getUserCount(), "销售部应该有2个人");

        DeptRespVO financeDept = result.stream()
            .filter(dept -> "财务部".equals(dept.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(financeDept, "应该找到财务部");
        assertEquals(1, financeDept.getUserCount(), "财务部应该有1个人");
    }

    /**
     * 测试getDeptListWithUserCount方法 - 部门无用户
     */
    @Test
    public void testGetDeptListWithUserCount_NoUsers() {
        // 准备数据 - 创建部门但不分配用户
        DeptDO dept1 = createTestDept("空部门1");
        DeptDO dept2 = createTestDept("空部门2");

        // 执行测试
        DeptListReqVO reqVO = new DeptListReqVO();
        List<DeptRespVO> result = deptService.getDeptListWithUserCount(reqVO);
        System.out.println("testGetDeptListWithUserCount_NoUsers: "+result);

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(2, result.size(), "应该返回2个部门");

        // 验证所有部门人数都为0
        result.forEach(dept -> {
            assertEquals(0, dept.getUserCount(), "空部门的人数应该为0");
        });
    }

    /**
     * 测试getDeptListWithUserCount方法 - 按状态过滤部门
     */
    @Test
    public void testGetDeptListWithUserCount_FilterByStatus() {
        // 准备数据 - 创建启用和禁用的部门
        DeptDO enabledDept = createTestDept("启用部门");

        DeptDO disabledDept = createTestDept("禁用部门");
        disabledDept.setStatus(CommonStatusEnum.DISABLE.getStatus());
        deptDataRepository.update(disabledDept);

        // 为两个部门都分配用户
        createTestUser("user1", enabledDept.getId());
        createTestUser("user2", disabledDept.getId());

        // 执行测试 - 只查询启用状态的部门
        DeptListReqVO reqVO = new DeptListReqVO();
        reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        List<DeptRespVO> result = deptService.getDeptListWithUserCount(reqVO);
        System.out.println("testGetDeptListWithUserCount_FilterByStatus: "+result);

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(1, result.size(), "应该只返回启用状态的部门");

        DeptRespVO dept = result.get(0);
        assertEquals("启用部门", dept.getName(), "应该是启用部门");
        assertEquals(1, dept.getUserCount(), "启用部门应该有1个人");
    }

    /**
     * 测试getDeptListWithUserCount方法 - 空结果
     */
    @Test
    public void testGetDeptListWithUserCount_EmptyResult() {
        // 不创建任何部门

        // 执行测试
        DeptListReqVO reqVO = new DeptListReqVO();
        List<DeptRespVO> result = deptService.getDeptListWithUserCount(reqVO);

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertTrue(result.isEmpty(), "应该返回空列表");
    }

    /**
     * 测试getDeptListWithUserCount方法 - 统计所有用户（包括禁用状态）
     */
    @Test
    public void testGetDeptListWithUserCount_IncludeDisabledUsers() {
        // 准备数据 - 创建部门
        DeptDO dept = createTestDept("测试部门");

        // 创建启用状态的用户
        AdminUserDO enabledUser1 = createTestUser("enabled1", dept.getId());
        AdminUserDO enabledUser2 = createTestUser("enabled2", dept.getId());

        // 创建禁用状态的用户
        AdminUserDO disabledUser = createTestUser("disabled1", dept.getId());
        disabledUser.setStatus(UserStatusEnum.DISABLE.getStatus());
        userDataRepository.update(disabledUser);

        // 执行测试
        DeptListReqVO reqVO = new DeptListReqVO();
        List<DeptRespVO> result = deptService.getDeptListWithUserCount(reqVO);
        System.out.println("testGetDeptListWithUserCount_IncludeDisabledUsers: "+result);

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(1, result.size(), "应该返回1个部门");

        DeptRespVO testDept = result.get(0);
        assertEquals("测试部门", testDept.getName(), "应该是测试部门");
        assertEquals(3, testDept.getUserCount(), "应该统计所有用户（包括禁用状态），共3个人");
    }

    /**
     * 测试createDept方法 - 正常创建
     */
    @Test
    public void testCreateDept_Success() {
        // 准备数据
        DeptSaveReqVO reqVO = new DeptSaveReqVO();
        reqVO.setName("新部门");
        reqVO.setParentId(0L);
        reqVO.setSort(1);
        reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // 执行测试
        Long deptId = deptService.createDept(reqVO);

        // 验证结果
        assertNotNull(deptId, "创建的部门ID不应该为空");

        DeptDO dept = deptService.getDept(deptId);
        assertNotNull(dept, "创建的部门应该存在");
        assertEquals("新部门", dept.getName(), "部门名称应该匹配");
        assertEquals(0L, dept.getParentId(), "父部门ID应该匹配");
        assertEquals(CommonStatusEnum.ENABLE.getStatus(), dept.getStatus(), "部门状态应该匹配");
    }

    /**
     * 测试createDept方法 - 父部门不存在
     */
    @Test
    public void testCreateDept_ParentNotExists() {
        // 准备数据
        DeptSaveReqVO reqVO = new DeptSaveReqVO();
        reqVO.setName("子部门");
        reqVO.setParentId(999L); // 不存在的父部门ID
        reqVO.setSort(1);
        reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deptService.createDept(reqVO);
        });
        assertEquals(DEPT_PARENT_NOT_EXITS.getCode(), exception.getCode(), "应该抛出父部门不存在的异常");
    }

    /**
     * 测试createDept方法 - 部门名称重复
     */
    @Test
    public void testCreateDept_NameDuplicate() {
        // 准备数据 - 先创建一个部门
        DeptDO existingDept = createTestDept("重复部门");

        // 尝试创建同名部门
        DeptSaveReqVO reqVO = new DeptSaveReqVO();
        reqVO.setName("重复部门");
        reqVO.setParentId(0L);
        reqVO.setSort(1);
        reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deptService.createDept(reqVO);
        });
        assertEquals(DEPT_NAME_DUPLICATE.getCode(), exception.getCode(), "应该抛出部门名称重复的异常");
    }

    /**
     * 测试updateDept方法 - 正常更新
     */
    @Test
    public void testUpdateDept_Success() {
        // 准备数据 - 创建部门
        DeptDO dept = createTestDept("原部门名");

        // 执行更新
        DeptSaveReqVO reqVO = new DeptSaveReqVO();
        reqVO.setId(dept.getId());
        reqVO.setName("新部门名");
        reqVO.setParentId(0L);
        reqVO.setSort(2);
        reqVO.setStatus(CommonStatusEnum.DISABLE.getStatus());

        deptService.updateDept(reqVO);

        // 验证结果
        DeptDO updatedDept = deptService.getDept(dept.getId());
        assertNotNull(updatedDept, "更新后的部门应该存在");
        assertEquals("新部门名", updatedDept.getName(), "部门名称应该更新");
        assertEquals(CommonStatusEnum.DISABLE.getStatus(), updatedDept.getStatus(), "部门状态应该更新");
    }

    /**
     * 测试updateDept方法 - 部门不存在
     */
    @Test
    public void testUpdateDept_DeptNotExists() {
        // 准备数据
        DeptSaveReqVO reqVO = new DeptSaveReqVO();
        reqVO.setId(999L); // 不存在的部门ID
        reqVO.setName("更新部门");
        reqVO.setParentId(0L);

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deptService.updateDept(reqVO);
        });
        assertEquals(DEPT_NOT_FOUND.getCode(), exception.getCode(), "应该抛出部门不存在的异常");
    }

    /**
     * 测试updateDept方法 - 设置自己为父部门
     */
    @Test
    public void testUpdateDept_ParentIsSelf() {
        // 准备数据 - 创建部门
        DeptDO dept = createTestDept("测试部门");

        // 尝试设置自己为父部门
        DeptSaveReqVO reqVO = new DeptSaveReqVO();
        reqVO.setId(dept.getId());
        reqVO.setName("测试部门");
        reqVO.setParentId(dept.getId()); // 设置自己为父部门

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deptService.updateDept(reqVO);
        });
        assertEquals(DEPT_PARENT_ERROR.getCode(), exception.getCode(), "应该抛出设置父部门错误的异常");
    }

    /**
     * 测试deleteDept方法 - 正常删除
     */
    @Test
    public void testDeleteDept_Success() {
        // 准备数据 - 创建部门
        DeptDO dept = createTestDept("待删除部门");

        // 执行删除
        deptService.deleteDept(dept.getId());

        // 验证结果
        DeptDO deletedDept = deptService.getDept(dept.getId());
        assertNull(deletedDept, "删除后的部门应该不存在");
    }

    /**
     * 测试deleteDept方法 - 部门不存在
     */
    @Test
    public void testDeleteDept_DeptNotExists() {
        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deptService.deleteDept(999L);
        });
        assertEquals(DEPT_NOT_FOUND.getCode(), exception.getCode(), "应该抛出部门不存在的异常");
    }

    /**
     * 测试deleteDept方法 - 存在子部门
     */
    @Test
    public void testDeleteDept_HasChildren() {
        // 准备数据 - 创建父子部门
        DeptDO parentDept = createTestDept("父部门");
        DeptDO childDept = createTestDept("子部门");
        childDept.setParentId(parentDept.getId());
        deptDataRepository.update(childDept);

        // 尝试删除有子部门的父部门
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deptService.deleteDept(parentDept.getId());
        });
        assertEquals(DEPT_EXITS_CHILDREN.getCode(), exception.getCode(), "应该抛出存在子部门的异常");
    }

    /**
     * 测试getDept方法 - 正常获取
     */
    @Test
    public void testGetDept_Success() {
        // 准备数据
        DeptDO dept = createTestDept("测试部门");

        // 执行测试
        DeptDO result = deptService.getDept(dept.getId());

        // 验证结果
        assertNotNull(result, "部门应该存在");
        assertEquals(dept.getId(), result.getId(), "部门ID应该匹配");
        assertEquals("测试部门", result.getName(), "部门名称应该匹配");
    }

    /**
     * 测试getDept方法 - 部门不存在
     */
    @Test
    public void testGetDept_NotExists() {
        // 执行测试
        DeptDO result = deptService.getDept(999L);

        // 验证结果
        assertNull(result, "不存在的部门应该返回null");
    }

    /**
     * 测试getDeptList方法 - 通过ID集合查询
     */
    @Test
    public void testGetDeptList_ByIds() {
        // 准备数据
        DeptDO dept1 = createTestDept("部门1");
        DeptDO dept2 = createTestDept("部门2");
        DeptDO dept3 = createTestDept("部门3");

        // 执行测试
        List<Long> ids = Arrays.asList(dept1.getId(), dept2.getId());
        List<DeptDO> result = deptService.getDeptList(ids);

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(2, result.size(), "应该返回2个部门");

        Set<Long> resultIds = result.stream().map(DeptDO::getId).collect(HashSet::new, HashSet::add, HashSet::addAll);
        assertTrue(resultIds.contains(dept1.getId()), "应该包含部门1");
        assertTrue(resultIds.contains(dept2.getId()), "应该包含部门2");
        assertFalse(resultIds.contains(dept3.getId()), "不应该包含部门3");
    }

    /**
     * 测试getDeptList方法 - 空ID集合
     */
    @Test
    public void testGetDeptList_EmptyIds() {
        // 执行测试
        List<DeptDO> result = deptService.getDeptList(Collections.emptyList());

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertTrue(result.isEmpty(), "空ID集合应该返回空列表");
    }

    /**
     * 测试getDeptList方法 - 通过条件查询
     */
    @Test
    public void testGetDeptList_ByCondition() {
        // 准备数据
        DeptDO enabledDept = createTestDept("启用部门");

        DeptDO disabledDept = createTestDept("禁用部门");
        disabledDept.setStatus(CommonStatusEnum.DISABLE.getStatus());
        deptDataRepository.update(disabledDept);

        // 测试按状态查询
        DeptListReqVO reqVO = new DeptListReqVO();
        reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        List<DeptDO> result = deptService.getDeptList(reqVO);

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertTrue(result.size() >= 1, "应该至少有1个启用部门");
        result.forEach(dept -> {
            assertEquals(CommonStatusEnum.ENABLE.getStatus(), dept.getStatus(), "所有返回的部门都应该是启用状态");
        });
    }

    /**
     * 测试getChildDeptList方法 - 获取子部门
     */
    @Test
    public void testGetChildDeptList_Success() {
        // 准备数据 - 创建部门层级结构
        DeptDO rootDept = createTestDept("根部门");

        DeptDO childDept1 = createTestDept("子部门1");
        childDept1.setParentId(rootDept.getId());
        deptDataRepository.update(childDept1);

        DeptDO childDept2 = createTestDept("子部门2");
        childDept2.setParentId(rootDept.getId());
        deptDataRepository.update(childDept2);

        DeptDO grandChildDept = createTestDept("孙部门");
        grandChildDept.setParentId(childDept1.getId());
        deptDataRepository.update(grandChildDept);

        // 执行测试
        List<DeptDO> result = deptService.getChildDeptList(rootDept.getId());

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(3, result.size(), "应该返回3个子部门（包括孙部门）");

        Set<Long> resultIds = result.stream().map(DeptDO::getId).collect(HashSet::new, HashSet::add, HashSet::addAll);
        assertTrue(resultIds.contains(childDept1.getId()), "应该包含子部门1");
        assertTrue(resultIds.contains(childDept2.getId()), "应该包含子部门2");
        assertTrue(resultIds.contains(grandChildDept.getId()), "应该包含孙部门");
    }

    /**
     * 测试getDeptListByLeaderUserId方法 - 根据管理员查询
     */
    @Test
    public void testGetDeptListByLeaderUserId_Success() {
        // 准备数据 - 创建用户和部门
        AdminUserDO leader = createTestUser("leader", null);

        DeptDO dept1 = createTestDept("部门1");
        dept1.setLeaderUserId(leader.getId());
        deptDataRepository.update(dept1);

        DeptDO dept2 = createTestDept("部门2");
        dept2.setLeaderUserId(leader.getId());
        deptDataRepository.update(dept2);

        DeptDO dept3 = createTestDept("部门3"); // 不设置管理员

        // 执行测试
        List<DeptDO> result = deptService.getDeptListByLeaderUserId(leader.getId());

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(2, result.size(), "应该返回2个部门");

        Set<Long> resultIds = result.stream().map(DeptDO::getId).collect(HashSet::new, HashSet::add, HashSet::addAll);
        assertTrue(resultIds.contains(dept1.getId()), "应该包含部门1");
        assertTrue(resultIds.contains(dept2.getId()), "应该包含部门2");
        assertFalse(resultIds.contains(dept3.getId()), "不应该包含部门3");
    }

    /**
     * 测试getChildDeptIdListFromCache方法 - 缓存获取子部门ID
     */
    @Test
    public void testGetChildDeptIdListFromCache_Success() {
        // 准备数据
        DeptDO parentDept = createTestDept("父部门");

        DeptDO childDept1 = createTestDept("子部门1");
        childDept1.setParentId(parentDept.getId());
        deptDataRepository.update(childDept1);

        DeptDO childDept2 = createTestDept("子部门2");
        childDept2.setParentId(parentDept.getId());
        deptDataRepository.update(childDept2);

        // 执行测试
        Set<Long> result = deptService.getChildDeptIdListFromCache(parentDept.getId());

        // 验证结果
        assertNotNull(result, "结果不应该为空");
        assertEquals(2, result.size(), "应该返回2个子部门ID");
        assertTrue(result.contains(childDept1.getId()), "应该包含子部门1的ID");
        assertTrue(result.contains(childDept2.getId()), "应该包含子部门2的ID");
    }

    /**
     * 测试validateDeptList方法 - 正常验证
     */
    @Test
    public void testValidateDeptList_Success() {
        // 准备数据 - 创建启用状态的部门
        DeptDO dept1 = createTestDept("部门1");
        DeptDO dept2 = createTestDept("部门2");

        // 执行测试 - 不应该抛出异常
        assertDoesNotThrow(() -> {
            deptService.validateDeptList(Arrays.asList(dept1.getId(), dept2.getId()));
        });
    }

    /**
     * 测试validateDeptList方法 - 部门不存在
     */
    @Test
    public void testValidateDeptList_DeptNotExists() {
        // 准备数据
        DeptDO dept1 = createTestDept("部门1");

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deptService.validateDeptList(Arrays.asList(dept1.getId(), 999L));
        });
        assertEquals(DEPT_NOT_FOUND.getCode(), exception.getCode(), "应该抛出部门不存在的异常");
    }

    /**
     * 测试validateDeptList方法 - 部门被禁用
     */
    @Test
    public void testValidateDeptList_DeptDisabled() {
        // 准备数据 - 创建禁用状态的部门
        DeptDO disabledDept = createTestDept("禁用部门");
        disabledDept.setStatus(CommonStatusEnum.DISABLE.getStatus());
        deptDataRepository.update(disabledDept);

        // 执行测试并验证异常
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deptService.validateDeptList(Arrays.asList(disabledDept.getId()));
        });
        assertEquals(DEPT_NOT_ENABLE.getCode(), exception.getCode(), "应该抛出部门被禁用的异常");
    }

    /**
     * 测试validateDeptList方法 - 空列表
     */
    @Test
    public void testValidateDeptList_EmptyList() {
        // 执行测试 - 空列表不应该抛出异常
        assertDoesNotThrow(() -> {
            deptService.validateDeptList(Collections.emptyList());
        });
    }

    // ========== 辅助方法 ==========

    /**
     * 创建测试部门
     *
     * @param name 部门名称
     * @return 创建的部门
     */
    private DeptDO createTestDept(String name) {
        DeptDO dept = new DeptDO();
        dept.setName(name);
        dept.setParentId(0L);
        dept.setSort(1);
        dept.setStatus(CommonStatusEnum.ENABLE.getStatus());
        dept.setTenantId(0L);
        return deptDataRepository.insertReturn(dept);
    }

    /**
     * 创建测试用户
     *
     * @param username 用户名
     * @param deptId   部门ID
     * @return 创建的用户
     */
    private AdminUserDO createTestUser(String username, Long deptId) {
        AdminUserDO user = new AdminUserDO();
        user.setUsername(username);
        user.setNickname(username);
        user.setPassword("password");
        user.setStatus(UserStatusEnum.NORMAL.getStatus());
        user.setEmail(username + "@test.com");
        user.setMobile("138888888" + (username.hashCode() % 90 + 10)); // 生成不同的手机号
        user.setDeptId(deptId);
        user.setTenantId(0L);
        userDataRepository.save(user);
        return user;
    }
}

