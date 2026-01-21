package com.cmsr.onebase.module.system.dal.database.dept;

import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.system.api.dept.dto.DeptPageApiReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemDeptMapper;
import com.cmsr.onebase.module.system.enums.corp.CorpConstant;
import com.cmsr.onebase.module.system.enums.dept.DeptTypeEnum;
import com.cmsr.onebase.module.system.vo.dept.DeptUpdateReqVO;
import com.cmsr.onebase.module.system.vo.user.UserAdminOrDirectorUpdateReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;

/**
 * 部门数据访问层
 * <p>
 * 负责部门相关的数据操作，基于 MyBatis-Flex 实现。
 *
 * @author matianyu
 * @date 2025-08-07
 */
@Repository
public class DeptDataRepository extends BaseDataRepository<SystemDeptMapper, DeptDO> {

    /**
     * 获取登录用户其用户所处的场景类型：平台/空间/企业
     *
     * @return
     */
    private UserTypeEnum getLoginUserType() {
        Integer userType = SecurityFrameworkUtils.getLoginUserType();
        if (userType == null) {
            throw exception(USER_TYPE_EXCEPTION, SecurityFrameworkUtils.getLoginUserType());
        }
        return UserTypeEnum.valueOf(userType);
    }

    private QueryWrapper buildLoginDeptQueryWrapper() {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null || loginUser.getId() == null) {
            // 立即失败，抛出异常，防止数据越权
            throw exception(USER_NOT_EXISTS);
        }

        UserTypeEnum userTypeEnum = getLoginUserType();
        QueryWrapper queryWrapper = new QueryWrapper();
        if (userTypeEnum == UserTypeEnum.TENANT) {
            queryWrapper.eq(DeptDO.DEPT_TYPE, DeptTypeEnum.TENANT.getCode());
        } else if (userTypeEnum == UserTypeEnum.CORP) {
            Long corpId = loginUser.getCorpId();
            if (corpId != null) {
                queryWrapper.eq(DeptDO.CORP_ID, corpId);
                queryWrapper.eq(DeptDO.DEPT_TYPE, DeptTypeEnum.CORP.getCode());
            } else {
                // 立即失败，抛出异常，防止数据越权
                throw exception(CORP_ID_NULL);
            }
        } else if (userTypeEnum == UserTypeEnum.THIRD) {
            queryWrapper.eq(DeptDO.DEPT_TYPE, DeptTypeEnum.THIRD.getCode());
        } else {
            // 立即失败，抛出异常，防止数据越权
            throw exception(USER_TYPE_EXCEPTION, userTypeEnum);
        }
        return queryWrapper;
    }

    /**
     * 根据父部门ID查询所有子部门
     *
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    public List<DeptDO> findAllByParentId(Long parentId) {
        return list(buildLoginDeptQueryWrapper().eq(DeptDO.PARENT_ID, parentId));
    }

    /**
     * 根据父部门ID+类型查询所有子部门
     *
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    public List<DeptDO> findAllByParentIdAndType(Long parentId, String deptType) {
        return list(buildQueryWrapperByType(deptType).eq(DeptDO.PARENT_ID, parentId));
    }

    /**
     * 根据父部门ID和部门名查询部门
     *
     * @param parentId 父部门ID
     * @param name     部门名称
     * @return 部门对象
     */
    public DeptDO findOneByParentIdAndName(Long parentId, String name) {
        return getOne(buildLoginDeptQueryWrapper()
                .eq(DeptDO.PARENT_ID, parentId)
                .eq(DeptDO.NAME, name));
    }

    /**
     * 根据部门名查询部门
     *
     * @param name 部门名称
     * @return 部门对象
     */
    public DeptDO findOneByName(String name) {
        return getOne(buildLoginDeptQueryWrapper().eq(DeptDO.NAME, name));
    }

    /**
     * 根据领导用户ID查询部门列表
     *
     * @param leaderUserId 领导用户ID
     * @return 部门列表
     */
    public List<DeptDO> findAllByLeaderUserId(Long leaderUserId) {
        return list(buildLoginDeptQueryWrapper().eq(DeptDO.LEADER_USER_ID, leaderUserId));
    }

    /**
     * 根据父部门ID集合查询所有子部门
     *
     * @param parentIds 父部门ID集合
     * @return 子部门列表
     */
    public List<DeptDO> findAllByParentIds(Collection<Long> parentIds) {
        if (parentIds == null || parentIds.isEmpty()) {
            return Collections.emptyList();
        }
        return list(buildLoginDeptQueryWrapper().in(DeptDO.PARENT_ID, parentIds));
    }

    /**
     * 根据部门名和状态查询部门列表
     *
     * @param name   部门名称（可为空）
     * @param status 状态（可为空）
     * @return 部门列表
     */
    public List<DeptDO> findAllByNameAndStatus(String name, Integer status) {
        QueryWrapper queryWrapper = buildLoginDeptQueryWrapper()
                .like(DeptDO.NAME, name, name != null)
                .eq(DeptDO.STATUS, status, status != null)
                .orderBy(DeptDO.SORT, true);
        return list(queryWrapper);
    }

    public DeptDO findDeptByCodeAndType(DeptDO deptRespVO) {
        return getOne(query()
                .eq(DeptDO.DEPT_TYPE, deptRespVO.getDeptType(), StringUtils.isNotBlank(deptRespVO.getDeptType()))
                .eq(DeptDO.DEPT_CODE, deptRespVO.getDeptCode(), StringUtils.isNotBlank(deptRespVO.getDeptCode()))
                .orderBy(DeptDO.SORT, true));
    }

    public List<DeptDO> getDefaultThirdDeptByDefaultCode(String deptCode, Integer status) {
        return list(query()
                .eq(DeptDO.DEPT_CODE, deptCode, StringUtils.isNotBlank(deptCode))
                .eq(DeptDO.STATUS, status, status != null)
                .orderBy(DeptDO.SORT, true));
    }

    public List<DeptDO> findDeptListByDeptType(String deptType) {
        return list(buildQueryWrapperByType(deptType)
                .eq(DeptDO.STATUS, CommonStatusEnum.ENABLE.getStatus()));
    }

    public List<DeptDO> findDeptListByNameAndDeptType(String keywords, String deptType) {
        return list(buildQueryWrapperByType(deptType)
                .like(DeptDO.NAME, keywords, keywords != null)
                .orderBy(DeptDO.SORT, true));
    }

    @NotNull
    private QueryWrapper buildQueryWrapperByType(String deptType) {
        if (StringUtils.isBlank(deptType)) {
            // 1. 如果前端给用户类型为空，则按照登录用户类型过滤。
            return buildLoginDeptQueryWrapper();
        } else {
            // 2. 如果前端指定了用户类型，则按照前端传递的用户类型过滤。
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq(DeptDO.DEPT_TYPE, deptType);
            return queryWrapper;
        }
    }


    public PageResult<DeptDO> selectPage(Integer status, DeptPageApiReqVO pageReqVO) {
        QueryWrapper queryWrapper = buildLoginDeptQueryWrapper().eq(DeptDO.STATUS, status)
                .like(DeptDO.NAME, pageReqVO.getKeywords(), StringUtils.isNotBlank(pageReqVO.getKeywords()))
                .notIn(DeptDO.P_ID, pageReqVO.getExcludeDeptIds(), CollectionUtils.isNotEmpty(pageReqVO.getExcludeDeptIds()))
                .orderBy(BaseDO.CREATE_TIME, false);
        Page<DeptDO> pageResult = page(Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);
        return new PageResult<DeptDO>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    /**
     * 更新部门
     *
     * @param updateReqVO
     */
    public boolean updateDept(DeptUpdateReqVO updateReqVO) {
        UpdateChain<DeptDO> updateChain = this.updateChain();
        if (StringUtils.isNotBlank(updateReqVO.getName())) {
            updateChain.set(DeptDO::getName, updateReqVO.getName());
        }
        if (updateReqVO.getParentId() != null) {
            updateChain.set(DeptDO::getParentId, updateReqVO.getParentId());
        }
        // leaderUserId 即使为 null 也要更新
        updateChain.set(DeptDO::getLeaderUserId, updateReqVO.getLeaderUserId());
        updateChain.set(DeptDO::getAdminUserIds, updateReqVO.getAdminUserIds());
        return updateChain.where(DeptDO::getId).eq(updateReqVO.getId()).update();
    }

    /**
     * 更新部门主管或接口人,可以为null
     */
    public void updateDeptLeaderOrDirector(UserAdminOrDirectorUpdateReqVO updateObj) {
        UpdateChain<DeptDO> updateChain = this.updateChain();
       if (updateObj.getUpdateType().equals(CorpConstant.LEADER_USER_ID)){
           updateChain.set(DeptDO::getAdminUserIds, updateObj.getAdminUserIds());
       } else {
           Set<Long> adminUserIds = updateObj.getAdminUserIds();
           if (CollectionUtils.isNotEmpty(adminUserIds)) {
               Long firstAdminUserId = CollUtil.getFirst(adminUserIds);
               updateChain.set(DeptDO::getLeaderUserId, firstAdminUserId);
           } else {
               updateChain.set(DeptDO::getLeaderUserId, null);
           }
       }
        updateChain.where(DeptDO::getId).eq(updateObj.getDeptId()).update();
    }
}


