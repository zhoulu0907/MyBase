package com.cmsr.onebase.module.system.dal.flex.repo;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.enums.XFromSceneTypeEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemUsersMapper;
import com.cmsr.onebase.module.system.enums.dept.DeptTypeEnum;
import com.cmsr.onebase.module.system.enums.user.UserStatusEnum;
import com.cmsr.onebase.module.system.vo.user.UserAppPageSearchReqVO;
import com.cmsr.onebase.module.system.vo.user.UserByDeptPageReqVO;
import com.cmsr.onebase.module.system.vo.user.UserPageReqVO;
import com.cmsr.onebase.module.system.vo.user.UserSimplePageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO.DEPT_ID;
import static com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO.NICKNAME;
import static com.cmsr.onebase.module.system.dal.flex.table.SystemUsersTableDef.SYSTEM_USERS;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;

/**
 * 管理员用户数据访问层
 * <p>
 * 负责管理员用户相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-18
 */
@Slf4j
@Repository
public class UserDataRepository extends BaseDataRepository<SystemUsersMapper, AdminUserDO> {

    /**
     * 获取登录用户其用户所处的场景类型：平台/空间/企业
     *
     * @return
     */
    private String getSceneByUserType() {
        String userSceneType = SecurityFrameworkUtils.getSceneByUserType();
        if (StringUtils.isBlank(userSceneType)) {
            throw exception(USER_TYPE_EXCEPTION, SecurityFrameworkUtils.getLoginUserType());
        }
        return userSceneType;
    }

    private QueryWrapper buildUserQueryWrapper() {
        QueryWrapper queryWrapper = new QueryWrapper();
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null || loginUser.getId() == null) {
            // 立即失败，抛出异常，防止数据越权
            throw exception(USER_NOT_EXISTS);
        }

        String fromSceneType = getSceneByUserType();
        if (XFromSceneTypeEnum.PLATFORM.getCode().equals(fromSceneType)) {
            queryWrapper.eq(AdminUserDO.USER_TYPE, UserTypeEnum.PLATFORM.getValue());
        } else if (XFromSceneTypeEnum.TENANT.getCode().equals(fromSceneType)) {
            queryWrapper.eq(AdminUserDO.USER_TYPE, UserTypeEnum.TENANT.getValue());
        } else if (XFromSceneTypeEnum.CORP.getCode().equals(fromSceneType)) {
            Long corpId = loginUser.getCorpId();
            if (null == corpId) {
                // 立即失败，抛出异常，防止数据越权
                throw exception(CORP_ID_NULL);
            }
            queryWrapper.eq(AdminUserDO.CORP_ID,  corpId);
            queryWrapper.eq(AdminUserDO.USER_TYPE,  UserTypeEnum.CORP.getValue());

        } else if (XFromSceneTypeEnum.THIRD.getCode().equals(fromSceneType)) {
            queryWrapper.eq(AdminUserDO.USER_TYPE,  UserTypeEnum.THIRD.getValue());
        } else if (XFromSceneTypeEnum.ALL.getCode().equals(fromSceneType)) {
            // 全部类型，不做任何处理
        } else {
            // 立即失败，抛出异常，防止数据越权
            throw exception(USER_TYPE_EXCEPTION, fromSceneType);
        }
        return queryWrapper;
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    public AdminUserDO findByUsername(String username) {
        return getOne(query().eq(AdminUserDO.USERNAME, username));
    }

    /**
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return 用户对象
     */
    public AdminUserDO findByMobile(String mobile) {
        return getOne(query().eq(AdminUserDO.MOBILE, mobile));
    }

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户对象
     */
    public AdminUserDO findByEmail(String email) {
        return getOne(query().eq(AdminUserDO.EMAIL, email));
    }

    /**
     * 根据部门ID列表查询用户
     *
     * @param deptIds 部门ID列表
     * @return 用户列表
     */
    public List<AdminUserDO> findAllByDeptIds(Collection<Long> deptIds, Integer userType) {
        if (deptIds == null || deptIds.isEmpty()) {
            return Collections.emptyList();
        }
        return list(query()
                .in(DEPT_ID, deptIds)
                .eq(AdminUserDO.USER_TYPE, userType, userType != null));
    }

    /**
     * 查询没有部门的用户
     *
     * @return 用户列表
     */
    public List<AdminUserDO> findNullDeptUser() {
        return list(query().isNull(DEPT_ID));
    }


    public List<AdminUserDO> findUserByUserType(Integer userType) {
        return list(query()
                .isNull(DEPT_ID)
                .eq(AdminUserDO.USER_TYPE, userType));
    }

    /**
     * 根据昵称模糊查询用户
     *
     * @param nickname 昵称
     * @return 用户列表
     */
    public List<AdminUserDO> findAllByNicknameLike(String nickname, Integer userType) {
        return list(buildUserQueryWrapper().like(NICKNAME, nickname)
                .eq(AdminUserDO.USER_TYPE, userType, userType != null));
    }

    /**
     * 根据状态查询用户列表
     *
     * @param status 状态
     * @return 用户列表
     */
    public List<AdminUserDO> findAllByStatus(Integer status, String userNickName) {
        return list(buildUserQueryWrapper()
                .eq(AdminUserDO.STATUS, status)
                .like(NICKNAME, userNickName, StringUtils.isNotBlank(userNickName))
                .orderBy(AdminUserDO.ADMIN_TYPE, true)
                .orderBy(BaseDO.CREATE_TIME, false));
    }

    /**
     * 根据状态统计用户数量
     *
     * @param status 状态
     * @return 用户数量
     */
    public long countByStatus(Integer status) {
        return count(query().eq(AdminUserDO.STATUS, status));
    }

    /**
     * 根据corpId统计用户数量
     *
     * @param corpId 企业id
     * @return 用户数量
     */
    public long getUserCountByCorpId(Long corpId) {
        return count(query().eq(AdminUserDO.CORP_ID, corpId));
    }

    /**
     * 分页查询用户
     *
     * @param reqVO              分页查询条件
     * @param deptIds            部门ID集合（用于部门权限控制）
     * @param includeRoleUserIds 包含角色的用户ID集合（用于包含角色的用户查询)
     * @param excludeRoleUserIds 排除角色的用户ID集合（用于排除角色的用户查询）
     * @return 分页结果
     */
    public PageResult<AdminUserDO> findPage(UserPageReqVO reqVO, Collection<Long> deptIds, Collection<Long> includeRoleUserIds, Collection<Long> excludeRoleUserIds) {

        QueryWrapper queryWrapper = buildUserQueryWrapper();
        queryWrapper.like(NICKNAME, reqVO.getNickname(), StringUtils.isNotBlank(reqVO.getNickname()))
                .like(AdminUserDO.MOBILE, reqVO.getMobile(), StringUtils.isNotBlank(reqVO.getMobile()))
                .like(AdminUserDO.EMAIL, reqVO.getEmail(), StringUtils.isNotBlank(reqVO.getEmail()))
                .eq(AdminUserDO.STATUS, reqVO.getStatus(), reqVO.getStatus() != null)
                .in(DEPT_ID, deptIds, deptIds != null && !deptIds.isEmpty())
                .in(BaseDO.ID, includeRoleUserIds, CollectionUtils.isNotEmpty(includeRoleUserIds))
                .notIn(BaseDO.ID, excludeRoleUserIds, CollectionUtils.isNotEmpty(excludeRoleUserIds))
                .orderBy(AdminUserDO.ADMIN_TYPE, true)
                .orderBy(BaseDO.CREATE_TIME, false);

        // 创建时间范围查询
        if (reqVO.getCreateTime() != null && reqVO.getCreateTime().length == 2) {
            queryWrapper.ge(BaseDO.CREATE_TIME, reqVO.getCreateTime()[0], reqVO.getCreateTime()[0] != null);
            queryWrapper.le(BaseDO.CREATE_TIME, reqVO.getCreateTime()[1], reqVO.getCreateTime()[1] != null);
        }
        // 根据关键词模糊查询
        if (StringUtils.isNotBlank(reqVO.getKeyword())) {
            queryWrapper.and(SYSTEM_USERS.NICKNAME.like(reqVO.getKeyword())
                    .or(SYSTEM_USERS.USERNAME.like(reqVO.getKeyword()))
                    .or(SYSTEM_USERS.EMAIL.like(reqVO.getKeyword()))
                    .or(SYSTEM_USERS.MOBILE.like(reqVO.getKeyword()))
            );
        }

        Page<AdminUserDO> pageResult = page(Page.of(reqVO.getPageNo(), reqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    /**
     * 简单分页查询启用状态的用户
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<AdminUserDO> findSimpleEnablePage(UserSimplePageReqVO reqVO) {
        QueryWrapper queryWrapper =buildUserQueryWrapper().eq(AdminUserDO.STATUS, CommonStatusEnum.ENABLE.getStatus())
                .like(NICKNAME, reqVO.getKeywords(), StringUtils.isNotBlank(reqVO.getKeywords()))
                .orderBy(AdminUserDO.ADMIN_TYPE, true)
                .orderBy(BaseDO.CREATE_TIME, false);
        Page<AdminUserDO> pageResult = page(Page.of(reqVO.getPageNo(), reqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    /**
     * 简单分页查询指定部门ID集合的启用状态用户
     *
     * @param reqVO   分页查询条件
     * @param deptIds 部门ID集合
     * @return 分页结果
     */
    public PageResult<AdminUserDO> findEnableUserPageByDeptIds(UserByDeptPageReqVO reqVO, Set<Long> deptIds) {
        log.info("[findEnableUserPageByDeptIds][deptIds({}) reqVO({})]", deptIds, reqVO);
        QueryWrapper queryWrapper = buildUserQueryWrapper().eq(AdminUserDO.STATUS, CommonStatusEnum.ENABLE.getStatus())
                .like(NICKNAME, reqVO.getKeywords(), StringUtils.isNotBlank(reqVO.getKeywords()))
                .in(DEPT_ID, deptIds, CollectionUtils.isNotEmpty(deptIds))
                .orderBy(AdminUserDO.ADMIN_TYPE, true)
                .orderBy(BaseDO.CREATE_TIME, false);
        Page<AdminUserDO> pageResult = page(Page.of(reqVO.getPageNo(), reqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }

    public List<AdminUserDO> findEnableUserByIds(Set<Long> userIds, String keyword, Integer status) {
        QueryWrapper queryWrapper =buildUserQueryWrapper()
                .in(AdminUserDO.COL_ID, userIds)
                .eq(AdminUserDO.STATUS, status)
                .orderBy(AdminUserDO.ADMIN_TYPE, true);
        // 根据关键词模糊查询
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.and(SYSTEM_USERS.USERNAME.like(keyword)
                    .or(SYSTEM_USERS.NICKNAME.like(keyword)));
        }
        queryWrapper.orderBy(BaseDO.CREATE_TIME, false);
        return list(queryWrapper);
    }

    public List<AdminUserDO> findPlatformEnableUserByIds(Set<Long> userIds) {
        QueryWrapper queryWrapper = query()
                .in(AdminUserDO.COL_ID, userIds)
                .eq(AdminUserDO.STATUS, UserStatusEnum.NORMAL.getStatus())
                .orderBy(AdminUserDO.ADMIN_TYPE, true)
                .orderBy(BaseDO.CREATE_TIME, false);
        return list(queryWrapper);
    }


    public List<AdminUserDO> getTenantExistUserCountByIds(List<Long> tenantIds) {
        // 平台获取用户统计，不需要分用户类型
        QueryWrapper queryWrapper = query()
                .eq(AdminUserDO.STATUS, UserStatusEnum.NORMAL.getStatus())
                .in(AdminUserDO.TENANT_ID, tenantIds)
                .orderBy(AdminUserDO.ADMIN_TYPE, true)
                .orderBy(BaseDO.CREATE_TIME, false);
        return list(queryWrapper);
    }

    public List<AdminUserDO> getCorpExistUserCountByCorpIds(List<Long> corpIds) {
        // 平台获取用户统计，不需要分用户类型
        QueryWrapper queryWrapper = query()
                .eq(AdminUserDO.STATUS, UserStatusEnum.NORMAL.getStatus())
                .in(AdminUserDO.CORP_ID, corpIds)
                .orderBy(BaseDO.CREATE_TIME, false);
        return list(queryWrapper);
    }

    public List<AdminUserDO> findAllByStatusAndDeptIds(Integer status, Set<Long> deptIds) {
        QueryWrapper queryWrapper =  buildUserQueryWrapper();
        queryWrapper.eq(AdminUserDO.STATUS, status)
                .in(DEPT_ID, deptIds)
                .orderBy(AdminUserDO.ADMIN_TYPE, true)
                .orderBy(BaseDO.CREATE_TIME, false);
        return list(queryWrapper);
    }

    public List<AdminUserDO> getPlatformUserByUsernames(Set<String> usernames) {
        QueryWrapper queryWrapper = query()
                .eq(AdminUserDO.STATUS, UserStatusEnum.NORMAL.getStatus())
                .in(AdminUserDO.USERNAME, usernames)
                .eq(AdminUserDO.USER_TYPE, UserTypeEnum.PLATFORM.getValue())
                .orderBy(AdminUserDO.ADMIN_TYPE, true)
                .orderBy(BaseDO.CREATE_TIME, false);
        return list(queryWrapper);
    }

    public PageResult<AdminUserDO> getThirdUserPage(UserAppPageSearchReqVO userAppPageReqVO) {
        QueryWrapper queryWrapper = query()
                .eq(AdminUserDO.USER_TYPE, UserTypeEnum.THIRD.getValue())
                // 根据关键词查询
                .eq(DEPT_ID, userAppPageReqVO.getDeptId(), userAppPageReqVO.getDeptId() != null)
                .eq(AdminUserDO.STATUS, userAppPageReqVO.getStatus(), userAppPageReqVO.getStatus() != null)
                .like(AdminUserDO.USERNAME, userAppPageReqVO.getUserName(), StringUtils.isNotBlank(userAppPageReqVO.getUserName()))
                // 添加排序
                .orderBy(AdminUserDO.ADMIN_TYPE, true)
                .orderBy(BaseDO.CREATE_TIME, false);

        Page<AdminUserDO> pageResult = page(Page.of(userAppPageReqVO.getPageNo(), userAppPageReqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}
