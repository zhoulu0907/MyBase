package com.cmsr.onebase.module.system.service.dept;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.system.dal.database.dept.DeptDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.dal.redis.RedisKeyConstants;
import com.cmsr.onebase.module.system.enums.corp.CorpConstant;
import com.cmsr.onebase.module.system.enums.dept.DefaultThirdDept;
import com.cmsr.onebase.module.system.enums.dept.DeptTypeEnum;
import com.cmsr.onebase.module.system.enums.dept.IdTypeEnum;
import com.cmsr.onebase.module.system.service.permission.PermissionService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.dept.*;
import com.cmsr.onebase.module.system.vo.user.UserAdminOrDirectorUpdateReqVO;
import com.cmsr.onebase.module.system.vo.user.UserSimpleRespVO;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertSet;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;

/**
 * 部门 Service 实现类
 *
 * @author matianyu
 */
@Slf4j
@Validated
@Service
public class DeptServiceImpl implements DeptService {

    public static final int LOOP_COUNT_LIMIT = 30;
    public static final int LOOP_INIT        = 0;

    @Lazy
    @Resource
    private UserService userService;

    @Lazy
    @Resource
    private PermissionService permissionService;

    @Resource
    private DeptDataRepository deptDataRepository;

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST, allEntries = true, beforeInvocation = true)
    public Long createDept(DeptSaveReqVO createReqVO) {
        if (createReqVO.getParentId() == null) {
            createReqVO.setParentId(DeptDO.PARENT_ID_ROOT);
        }
        // 校验父部门的有效性
        validateParentDept(null, createReqVO.getParentId());
        // 校验部门名的唯一性
        validateDeptNameUnique(null, createReqVO.getParentId(), createReqVO.getName());

        // 插入部门
        DeptDO dept = BeanUtils.toBean(createReqVO, DeptDO.class);
        dept.setStatus(CommonStatusEnum.ENABLE.getStatus());

        Integer loginUserType = SecurityFrameworkUtils.getLoginUserType();
        if(Objects.equals(UserTypeEnum.TENANT.getValue(), loginUserType)){
            dept.setDeptType(DeptTypeEnum.TENANT.getCode());
        }else if(Objects.equals(UserTypeEnum.CORP.getValue(), loginUserType)){
            dept.setDeptType(DeptTypeEnum.CORP.getCode());
            LoginUser user = SecurityFrameworkUtils.getLoginUser();
            if (user == null || user.getCorpId() == null) {
                throw exception(CORP_ID_NULL);
            }
            dept.setCorpId(user.getCorpId());
        }else{
            throw exception(USER_TYPE_EXCEPTION, loginUserType);
        }

        deptDataRepository.insert(dept);

        return dept.getId();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST, allEntries = true, beforeInvocation = true)
    public void updateDept(DeptSaveReqVO updateReqVO) {
        if (updateReqVO.getParentId() == null) {
            updateReqVO.setParentId(DeptDO.PARENT_ID_ROOT);
        }
        // 校验自己存在
        validateDeptExists(updateReqVO.getId());
        // 校验父部门的有效性
        validateParentDept(updateReqVO.getId(), updateReqVO.getParentId());
        // 校验部门名的唯一性
        validateDeptNameUnique(updateReqVO.getId(), updateReqVO.getParentId(), updateReqVO.getName());

        // 更新部门
        DeptDO updateObj = BeanUtils.toBean(updateReqVO, DeptDO.class);
        deptDataRepository.update(updateObj);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST, allEntries = true, beforeInvocation = true)
    public void deleteDept(Long id) {
        // 校验是否存在
        validateDeptExists(id);
        // 校验是否有子部门
        if (getChildDeptCount(id) > 0) {
            throw exception(DEPT_EXITS_CHILDREN);
        }
        // 如果一个部门有用户，则不能删除
        List<AdminUserDO> userListByDeptIds = userService.getUserListByDeptIds(Collections.singleton(id));
        if (CollectionUtils.isNotEmpty(userListByDeptIds)) {
            throw exception(DEPT_DEL_FAILD_EXISTS_USERS);
        }
        // 删除部门
        deptDataRepository.deleteById(id);
    }

    /**
     * 获取子部门数量
     */
    private long getChildDeptCount(Long parentId) {
        return deptDataRepository.findAllByParentId(parentId).size();
    }

    @VisibleForTesting
    void validateDeptExists(Long id) {
        if (id == null) {
            return;
        }
        DeptDO dept = deptDataRepository.findById(id);
        if (dept == null) {
            throw exception(DEPT_NOT_FOUND);
        }
    }

    @VisibleForTesting
    void validateParentDept(Long id, Long parentId) {
        if (parentId == null || DeptDO.PARENT_ID_ROOT.equals(parentId)) {
            return;
        }
        // 1. 不能设置自己为父部门
        if (Objects.equals(id, parentId)) {
            throw exception(DEPT_PARENT_ERROR);
        }
        // 2. 父部门不存在
        DeptDO parentDept = deptDataRepository.findById(parentId);
        if (parentDept == null) {
            throw exception(DEPT_PARENT_NOT_EXITS);
        }
        // 3. 递归校验父部门，如果父部门是自己的子部门，则报错，避免形成环路
        if (id == null) { // id 为空，说明新增，不需要考虑环路
            return;
        }
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            // 3.1 校验环路
            parentId = parentDept.getParentId();
            if (Objects.equals(id, parentId)) {
                throw exception(DEPT_PARENT_IS_CHILD);
            }
            // 3.2 继续递归下一级父部门
            if (parentId == null || DeptDO.PARENT_ID_ROOT.equals(parentId)) {
                break;
            }
            parentDept = deptDataRepository.findById(parentId);
            if (parentDept == null) {
                break;
            }
        }
    }

    @VisibleForTesting
    void validateDeptNameUnique(Long id, Long parentId, String name) {
        DeptDO dept = deptDataRepository.findOneByParentIdAndName(parentId, name);
        if (dept == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的部门
        if (id == null) {
            throw exception(DEPT_NAME_DUPLICATE);
        }
        if (ObjUtil.notEqual(dept.getId(), id)) {
            throw exception(DEPT_NAME_DUPLICATE);
        }
    }

    @Override
    public DeptDO getDept(Long id) {
        if (id == null) {
            return null;
        }
        return deptDataRepository.findById(id);
    }

    @Override
    public List<DeptDO> getDeptList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return deptDataRepository.findAllByIds(new ArrayList<>(ids));
    }

    @Override
    public List<DeptDO> getDeptList(DeptListReqVO reqVO) {
        return deptDataRepository.findAllByNameAndStatus(reqVO.getName(), reqVO.getStatus());
    }

    @Override
    public List<DeptDO> getChildDeptList(Collection<Long> ids) {
        List<DeptDO> children = new LinkedList<>();
        // 遍历每一层
        Collection<Long> parentIds = ids;
        for (int i = 0; i < Short.MAX_VALUE; i++) { // 使用 Short.MAX_VALUE 避免 bug 场景下，存在死循环
            // 查询当前层，所有的子部门
            List<DeptDO> depts = getDeptListByParentIds(parentIds);
            // 1. 如果没有子部门，则结束遍历
            if (CollUtil.isEmpty(depts)) {
                break;
            }
            // 2. 如果有子部门，继续遍历
            children.addAll(depts);
            parentIds = convertSet(depts, DeptDO::getId);
        }
        return children;
    }

    /**
     * 根据父部门ID列表查询子部门
     */
    private List<DeptDO> getDeptListByParentIds(Collection<Long> parentIds) {
        return deptDataRepository.findAllByParentIds(parentIds);
    }

    @Override
    public List<DeptDO> getDeptListByLeaderUserId(Long id) {
        return deptDataRepository.findAllByLeaderUserId(id);
    }

    @Override
    @Cacheable(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST, key = "#id")
    public Set<Long> getChildDeptIdListFromCache(Long id) {
        log.info("[getChildDeptIdListFromCache][deptId({})]", id);
        List<DeptDO> children = getChildDeptList(id);
        return convertSet(children, DeptDO::getId);
    }

    @Override
    public void validateDeptList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得科室信息
        Map<Long, DeptDO> deptMap = getDeptMap(ids);
        // 校验
        ids.forEach(id -> {
            DeptDO dept = deptMap.get(id);
            if (dept == null) {
                throw exception(DEPT_NOT_FOUND);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(dept.getStatus())) {
                throw exception(DEPT_NOT_ENABLE, dept.getName());
            }
        });
    }

    @Override
    public List<DeptRespVO> getDeptListWithUserCount(DeptListReqVO reqVO) {
        // 1. 获取部门列表
        List<DeptDO> deptList = getDeptList(reqVO);
        List<DeptRespVO> respList = BeanUtils.toBean(deptList, DeptRespVO.class);

        if (CollUtil.isEmpty(deptList)) {
            return respList;
        }

        // 2. 提取部门ID和领导用户ID
        List<Long> deptIds = deptList.stream().map(DeptDO::getId).collect(Collectors.toList());
        List<Long> leaderUserIds = deptList.stream().map(DeptDO::getLeaderUserId).filter(Objects::nonNull).collect(Collectors.toList());

        // 1. 批量获取部门人数统计（包含下级部门）
        Map<Long, Integer> deptUserCountMap = userService.getUserCountByDeptIdsIncludeChildren(deptIds);
        // 2. 批量获取领导用户信息
        Map<Long, AdminUserDO> leaderUserMap = userService.getUserMap(leaderUserIds);

        List<Long> directorUserIds = deptList.stream().map(DeptDO::getAdminUserId).filter(Objects::nonNull).collect(Collectors.toList());

        // 2. 批量获取部门主管用户信息
        Map<Long, AdminUserDO> directorUserMap = userService.getUserMap(directorUserIds);

        // 4. 设置每个部门的人数和领导姓名
        respList.forEach(dept -> {
            // 设置人数（包含下级部门）
            Integer userCount = deptUserCountMap.getOrDefault(dept.getId(), 0);
            dept.setUserCount(userCount);

            // 设置领导姓名
            if (dept.getLeaderUserId() != null) {
                AdminUserDO leader = leaderUserMap.get(dept.getLeaderUserId());
                if (leader != null) {
                    dept.setLeaderUserName(leader.getNickname());
                }
            }
            if (dept.getAdminUserId() != null) {
                AdminUserDO deptDirector = directorUserMap.get(dept.getAdminUserId());
                if (deptDirector != null) {
                    dept.setAdminUserName(deptDirector.getNickname());
                }
            }
        });

        return respList;
    }

    @Override
    public DeptRespVO getDeptWithUserCountAndLeader(Long id) {
        DeptDO dept = getDept(id);
        if (dept == null) {
            return null;
        }

        DeptRespVO respVO = BeanUtils.toBean(dept, DeptRespVO.class);

        // 准备批量查询的参数
        List<Long> deptIds = Collections.singletonList(id);
        List<Long> leaderUserIds = dept.getLeaderUserId() != null ? Collections.singletonList(dept.getLeaderUserId()) : Collections.emptyList();

        // 1. 批量获取部门人数统计（包含下级部门）
        Map<Long, Integer> deptUserCountMap = userService.getUserCountByDeptIdsIncludeChildren(deptIds);
        // 2. 批量获取领导用户信息
        Map<Long, AdminUserDO> leaderUserMap = userService.getUserMap(leaderUserIds);
        List<Long> directorUserIds = dept.getAdminUserId() != null ? Collections.singletonList(dept.getLeaderUserId()) : Collections.emptyList();
        // . 批量获取部门主管用户信息
        Map<Long, AdminUserDO> directorUserMap = userService.getUserMap(directorUserIds);

        // 设置部门人数（包含下级部门）
        Integer userCount = deptUserCountMap.getOrDefault(id, 0);
        respVO.setUserCount(userCount);

        // 设置领导姓名
        if (dept.getLeaderUserId() != null) {
            AdminUserDO leader = leaderUserMap.get(dept.getLeaderUserId());
            if (leader != null) {
                respVO.setLeaderUserName(leader.getNickname());
            }
        }
        if (dept.getAdminUserId() != null) {
            AdminUserDO deptDirector = directorUserMap.get(dept.getAdminUserId());
            if (deptDirector != null) {
                respVO.setAdminUserName(deptDirector.getNickname());
            }
        }

        return respVO;
    }

    @Override
    public DeptAndUsersRespVO getDeptAndUsers(DeptAndUsersReqVO reqVO) {
        DeptAndUsersRespVO respVO = new DeptAndUsersRespVO();

        // 判断是否有搜索关键词
        boolean hasKeywords = StrUtil.isNotBlank(reqVO.getKeywords());
        boolean hasDeptId = reqVO.getDeptId() != null && reqVO.getDeptId() > 0;

        if (hasKeywords) {
            // 场景3和4：有搜索关键词时，优先按搜索词处理
            respVO.setDeptInfo(null);

            // 按部门名称模糊搜索部门
            List<DeptDO> matchedDepts = deptDataRepository.findAllByNameAndStatus(reqVO.getKeywords(), null);
            respVO.setDeptList(BeanUtils.toBean(matchedDepts, DeptRespVO.class));

            // 按用户昵称模糊搜索用户
            List<AdminUserDO> matchedUsers = userService.getUserListByNickname(reqVO.getKeywords());
            respVO.setUserList(BeanUtils.toBean(matchedUsers, UserSimpleRespVO.class));

        } else if (hasDeptId) {
            // 场景2：有部门ID，无搜索词
            // 获取当前部门信息
            DeptDO deptInfo = getDept(reqVO.getDeptId());
            respVO.setDeptInfo(BeanUtils.toBean(deptInfo, DeptRespVO.class));

            // 获取直属子部门
            List<DeptDO> childDepts = deptDataRepository.findAllByParentId(reqVO.getDeptId());
            respVO.setDeptList(BeanUtils.toBean(childDepts, DeptRespVO.class));

            // 获取直属用户
            List<AdminUserDO> directUsers = userService.getUserListByDeptIds(Collections.singletonList(reqVO.getDeptId()));
            respVO.setUserList(BeanUtils.toBean(directUsers, UserSimpleRespVO.class));

        } else {
            // 场景1：部门ID和搜索词都为空
            respVO.setDeptInfo(null);

            // 获取所有一级部门（parentId = 0）
            List<DeptDO> rootDepts = deptDataRepository.findAllByParentId(DeptDO.PARENT_ID_ROOT);
            respVO.setDeptList(BeanUtils.toBean(rootDepts, DeptRespVO.class));

            // 获取所有没有部门的用户（dept_id = null）
            List<AdminUserDO> usersWithoutDept = userService.getUserListNoDept();
            respVO.setUserList(BeanUtils.toBean(usersWithoutDept, UserSimpleRespVO.class));
        }

        // 数据处理：排除指定用户和角色用户
        if (CollectionUtils.isNotEmpty(respVO.getUserList())) {
            Collection<Long> excludeRoleUserIds = null;
            // 排除拥有excludRoleId角色的用户
            if (reqVO.getExcludeRoleIds() != null) {
                excludeRoleUserIds = permissionService.getUserIdsListByRoleIds(reqVO.getExcludeRoleIds());
            }
            // 取合集，并去掉重复userID
            Set<Long> excludeUserIds = CollUtil.unionDistinct(reqVO.getExcludeUserIds(), excludeRoleUserIds);
            // 过滤掉排除的用户
            respVO.setUserList(respVO.getUserList().stream().filter(user -> !excludeUserIds.contains(user.getId())).collect(Collectors.toList()));
        }

        return respVO;
    }

    @Override
    public List<DeptDO> getParentDeptsListById(Long id, String idType) {
        if (IdTypeEnum.USER.getCode().equals(idType)) {
            AdminUserDO adminUserDO = userService.getUser(id);
            if (adminUserDO != null && adminUserDO.getDeptId() != null) {
                return getParentDeptsList(adminUserDO.getDeptId());
            }
        } else if (IdTypeEnum.DEPT.getCode().equals(idType)) {
            return getParentDeptsList(id);
        }
        return List.of();
    }


    private List<DeptDO> getParentDeptsList(Long deptId) {
        List<DeptDO> parentDepts = new ArrayList<>();
        if (deptId == null) {
            return parentDepts;
        }
        DeptDO dept = deptDataRepository.findById(deptId);
        parentDepts.add(dept);

        int loopCount = LOOP_INIT;
        while (dept != null && dept.getParentId() != null) {
            DeptDO parentDept = deptDataRepository.findById(dept.getParentId());
            if (parentDept != null) {
                parentDepts.add(parentDept);
                dept = parentDept;
            } else {
                break;
            }
            if (++loopCount > LOOP_COUNT_LIMIT) {
                log.error("获取父部门列表时，出现死循环，deptId = {}，loopCount = {}", deptId, loopCount);
                break;
            }
        }
        return parentDepts;
    }

    @Override
    public void updateAdminOrDirector(UserAdminOrDirectorUpdateReqVO reqVO) {
        // todo 验证部门是否存在/启用；验证空间/企业是否存在此用户
        if (reqVO.getUpdateType().equals(CorpConstant.LEADER_USER_ID)) {
            DataRow row = new DataRow();
            row.put(DeptDO.ADMIN_USER_ID, reqVO.getUserId());
            deptDataRepository.updateByConfig(row, new DefaultConfigStore().eq(DeptDO.ID, reqVO.getDeptId()));
        } else {
            DataRow row = new DataRow();
            row.put(DeptDO.LEADER_USER_ID, reqVO.getUserId());
            deptDataRepository.updateByConfig(row, new DefaultConfigStore().eq(DeptDO.ID, reqVO.getDeptId()));
        }
    }


    @Override
    public DeptDO findDeptByCodeAndType(DeptSaveReqVO deptRespVO) {
       return deptDataRepository.findDeptByCodeAndType(deptRespVO);
    }

    @Override
    public Long createThirdDefaultDept(DeptSaveReqVO deptRespVO) {
        DeptDO dept = BeanUtils.toBean(deptRespVO, DeptDO.class);
       return deptDataRepository.insert(dept).getId();
    }

    @Override
    public List<DeptDO> getDefaultThirdDept() {
        return deptDataRepository.getDefaultThirdDeptByDefaultCode(DefaultThirdDept.DEFAULT_THIRD_DEPT.getCode(), CommonStatusEnum.ENABLE.getStatus());
    }

}
