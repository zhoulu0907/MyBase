package com.cmsr.onebase.module.system.dal.database.dept;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.XFromSceneTypeEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.system.api.dept.dto.DeptAndUsersApiReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemDeptMapper;
import com.cmsr.onebase.module.system.enums.dept.DeptTypeEnum;
import com.cmsr.onebase.module.system.vo.dept.DeptRespVO;
import com.cmsr.onebase.module.system.vo.dept.DeptSaveReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO.NICKNAME;
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
     * @return 场景类型
     */
    public String getSceneByUserType() {
        String userSceneType = SecurityFrameworkUtils.getSceneByUserType();
        if (StringUtils.isBlank(userSceneType)) {
            throw exception(USER_TYPE_EXCEPTION, SecurityFrameworkUtils.getLoginUserType());
        }
        return userSceneType;
    }

    private QueryWrapper buildDeptQueryWrapper() {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null || loginUser.getId() == null) {
            // 立即失败，抛出异常，防止数据越权
            throw exception(USER_NOT_EXISTS);
        }

        String fromSceneType = getSceneByUserType();
        QueryWrapper queryWrapper = query();
        if (XFromSceneTypeEnum.TENANT.getCode().equals(fromSceneType)) {
            queryWrapper.eq(DeptDO.DEPT_TYPE, DeptTypeEnum.TENANT.getCode());
        } else if (XFromSceneTypeEnum.CORP.getCode().equals(fromSceneType)) {
            Long corpId = loginUser.getCorpId();
            if (corpId != null) {
                queryWrapper.eq(DeptDO.CORP_ID, corpId);
                queryWrapper.eq(DeptDO.DEPT_TYPE, DeptTypeEnum.CORP.getCode());
            } else {
                // 立即失败，抛出异常，防止数据越权
                throw exception(CORP_ID_NULL);
            }
        } else if (XFromSceneTypeEnum.THIRD.getCode().equals(fromSceneType)) {
            queryWrapper.eq(DeptDO.DEPT_TYPE, DeptTypeEnum.THIRD.getCode());
        } else if (XFromSceneTypeEnum.ALL.getCode().equals(fromSceneType)) {
            // 不做任何处理，全量数据
        } else {
            // 立即失败，抛出异常，防止数据越权
            throw exception(USER_TYPE_EXCEPTION, fromSceneType);
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
        return list(buildDeptQueryWrapper().eq(DeptDO.PARENT_ID, parentId));
    }

    /**
     * 根据父部门ID和部门名查询部门
     *
     * @param parentId 父部门ID
     * @param name     部门名称
     * @return 部门对象
     */
    public DeptDO findOneByParentIdAndName(Long parentId, String name) {
        return getOne(buildDeptQueryWrapper()
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
        return getOne(buildDeptQueryWrapper().eq(DeptDO.NAME, name));
    }

    /**
     * 根据领导用户ID查询部门列表
     *
     * @param leaderUserId 领导用户ID
     * @return 部门列表
     */
    public List<DeptDO> findAllByLeaderUserId(Long leaderUserId) {
        return list(buildDeptQueryWrapper().eq(DeptDO.LEADER_USER_ID, leaderUserId));
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
        return list(buildDeptQueryWrapper().in(DeptDO.PARENT_ID, parentIds));
    }

    /**
     * 根据部门名和状态查询部门列表
     *
     * @param name   部门名称（可为空）
     * @param status 状态（可为空）
     * @return 部门列表
     */
    public List<DeptDO> findAllByNameAndStatus(String name, Integer status) {
        QueryWrapper queryWrapper = buildDeptQueryWrapper()
                .like(DeptDO.NAME, name, name != null)
                .eq(DeptDO.STATUS, status, status != null)
                .orderBy(DeptDO.SORT, true);
        return list(queryWrapper);
    }

    public DeptDO findDeptByCodeAndType(DeptSaveReqVO deptRespVO) {
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
        return list(query()
                .eq(DeptDO.STATUS, CommonStatusEnum.ENABLE.getStatus())
                .eq(DeptDO.DEPT_TYPE, deptType));
    }

    public List<DeptDO> findDeptListByNameAndDeptType(String keywords, String deptType) {
        return null;
    }




    public PageResult<DeptDO> selectPage(Integer status, DeptAndUsersApiReqVO pageReqVO) {
        QueryWrapper queryWrapper = buildDeptQueryWrapper().eq(DeptDO.STATUS, status)
                .like(DeptDO.NAME, pageReqVO.getKeywords(), StringUtils.isNotBlank(pageReqVO.getKeywords()))
                .notIn(DeptDO.ID, pageReqVO.getExcludeDeptIds(), CollectionUtils.isNotEmpty(pageReqVO.getExcludeDeptIds()))
                .orderBy(BaseDO.CREATE_TIME, false);
        Page<DeptDO> pageResult = page(Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);
        return new PageResult<DeptDO>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}
