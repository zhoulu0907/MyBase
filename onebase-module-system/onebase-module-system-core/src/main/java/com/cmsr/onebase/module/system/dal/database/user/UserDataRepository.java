package com.cmsr.onebase.module.system.dal.database.user;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.enums.XFromSceneTypeEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.enums.user.UserStatusEnum;
import com.cmsr.onebase.module.system.vo.user.UserByDeptPageReqVO;
import com.cmsr.onebase.module.system.vo.user.UserPageReqVO;
import com.cmsr.onebase.module.system.vo.user.UserSimplePageReqVO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.Order;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
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
public class UserDataRepository extends DataRepository<AdminUserDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public UserDataRepository() {
        super(AdminUserDO.class);
    }


    /**
     * 获取登录用户其用户所处的场景类型：平台/空间/企业
     *
     * @return
     */
    public String getSceneByUserType() {
        String userSceneType = SecurityFrameworkUtils.getSceneByUserType();
        if (StringUtils.isBlank(userSceneType)) {
            throw exception(USER_TYPE_EXCEPTION, SecurityFrameworkUtils.getLoginUserType());
        }
        return userSceneType;
    }

    private DefaultConfigStore buildUserConfigStore() {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null || loginUser.getId() == null) {
            // 立即失败，抛出异常，防止数据越权
            throw exception(USER_NOT_EXISTS);
        }

        String fromSceneType = getSceneByUserType();

        DefaultConfigStore configStore = new DefaultConfigStore();
        if (XFromSceneTypeEnum.PLATFORM.getCode().equals(fromSceneType)) {
            configStore.and(Compare.EQUAL, AdminUserDO.USER_TYPE, UserTypeEnum.PLATFORM.getValue());
        } else if (XFromSceneTypeEnum.TENANT.getCode().equals(fromSceneType)) {
            configStore.and(Compare.EQUAL, AdminUserDO.USER_TYPE, UserTypeEnum.TENANT.getValue());
        } else if (XFromSceneTypeEnum.CORP.getCode().equals(fromSceneType)) {
            Long corpId = loginUser.getCorpId();
            if (null == corpId) {
                // 立即失败，抛出异常，防止数据越权
                throw exception(CORP_ID_NULL);
            }
            configStore.and(Compare.EQUAL, DeptDO.CORP_ID, corpId);
            configStore.and(Compare.EQUAL, AdminUserDO.USER_TYPE, UserTypeEnum.CORP.getValue());
        } else if (XFromSceneTypeEnum.ALL.getCode().equals(fromSceneType)) {
            // 全部类型，不做任何处理
        } else {
            // 立即失败，抛出异常，防止数据越权
            throw exception(USER_TYPE_EXCEPTION, fromSceneType);
        }
        return configStore;
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    public AdminUserDO findByUsername(String username) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(AdminUserDO.USERNAME, username);
        return findOne(configStore);
    }

    /**
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return 用户对象
     */
    public AdminUserDO findByMobile(String mobile) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(AdminUserDO.MOBILE, mobile);
        return findOne(configStore);
    }

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户对象
     */
    public AdminUserDO findByEmail(String email) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(AdminUserDO.EMAIL, email);
        return findOne(configStore);
    }

    /**
     * 根据部门ID列表查询用户
     *
     * @param deptIds 部门ID列表
     * @return 用户列表
     */
    public List<AdminUserDO> findAllByDeptIds(Collection<Long> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return Collections.emptyList();
        }
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in(AdminUserDO.DEPT_ID, deptIds);
        return findAllByConfig(configStore);
    }

    /**
     * 查询没有部门的用户
     *
     * @return 用户列表
     */
    public List<AdminUserDO> findAllNoDept() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.isNull(AdminUserDO.DEPT_ID);
        return findAllByConfig(configStore);
    }

    /**
     * 根据昵称模糊查询用户
     *
     * @param nickname 昵称
     * @return 用户列表
     */
    public List<AdminUserDO> findAllByNicknameLike(String nickname) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.like(AdminUserDO.NICKNAME, nickname);
        return findAllByConfig(configStore);
    }

    /**
     * 根据状态查询用户列表
     *
     * @param status 状态
     * @return 用户列表
     */
    public List<AdminUserDO> findAllByStatus(Integer status, String userNickName) {
        DefaultConfigStore configStore = buildUserConfigStore();
        if (StringUtils.isNotBlank(userNickName)) {
            configStore.like(AdminUserDO.NICKNAME, userNickName);
        }
        configStore.eq(AdminUserDO.STATUS, status)
                .order(AdminUserDO.ADMIN_TYPE, Order.TYPE.ASC)
                .order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 根据状态统计用户数量
     *
     * @param status 状态
     * @return 用户数量
     */
    public long countByStatus(Integer status) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(AdminUserDO.STATUS, status);
        return countByConfig(configStore);
    }

    /**
     * 根据corpId统计用户数量
     *
     * @param corpId 企业id
     * @return 用户数量
     */
    public long getUserCountByCorpId(Long corpId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(AdminUserDO.CORP_ID, corpId);
        return countByConfig(configStore);
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
        DefaultConfigStore configStore = buildUserConfigStore();
        // 根据关键词模糊查询
        if (reqVO.getKeyword() != null && !reqVO.getKeyword().trim().isEmpty()) {
            configStore.and(new DefaultConfigStore()
                            .or(Compare.LIKE, AdminUserDO.USERNAME, reqVO.getKeyword())
                            .or(Compare.LIKE, AdminUserDO.EMAIL, reqVO.getKeyword()))
                    .or(Compare.LIKE, AdminUserDO.MOBILE, reqVO.getKeyword())
                    .or(Compare.LIKE, AdminUserDO.NICKNAME, reqVO.getKeyword());
        }

        // 用户名模糊查询
        if (reqVO.getNickname() != null && !reqVO.getNickname().trim().isEmpty()) {
            configStore.like(AdminUserDO.NICKNAME, reqVO.getNickname());
        }

        // 手机号模糊查询
        if (reqVO.getMobile() != null && !reqVO.getMobile().trim().isEmpty()) {
            configStore.like(AdminUserDO.MOBILE, reqVO.getMobile());
        }

        // 邮箱模糊查询
        if (reqVO.getEmail() != null && !reqVO.getEmail().trim().isEmpty()) {
            configStore.like(AdminUserDO.EMAIL, reqVO.getEmail());
        }

        // 状态精确查询
        if (reqVO.getStatus() != null) {
            configStore.eq(AdminUserDO.STATUS, reqVO.getStatus());
        }

        // 创建时间范围查询
        if (reqVO.getCreateTime() != null && reqVO.getCreateTime().length == 2) {
            if (reqVO.getCreateTime()[0] != null) {
                configStore.ge(BaseDO.CREATE_TIME, reqVO.getCreateTime()[0]);
            }
            if (reqVO.getCreateTime()[1] != null) {
                configStore.le(BaseDO.CREATE_TIME, reqVO.getCreateTime()[1]);
            }
        }

        // 部门ID条件
        if (deptIds != null && !deptIds.isEmpty()) {
            configStore.in(AdminUserDO.DEPT_ID, deptIds);
        }

        // 包含角色的情况
        if (CollectionUtils.isNotEmpty(includeRoleUserIds)) {
            configStore.in(BaseDO.ID, includeRoleUserIds);

        }
        // 排除角色的情况
        if (CollectionUtils.isNotEmpty(excludeRoleUserIds)) {
            configStore.notIn(BaseDO.ID, excludeRoleUserIds);
        }


        // 添加排序
        configStore.order(AdminUserDO.ADMIN_TYPE, Order.TYPE.ASC).order(BaseDO.CREATE_TIME, Order.TYPE.DESC);

        return findPageWithConditions(configStore, reqVO.getPageNo(), reqVO.getPageSize());
    }

    /**
     * 简单分页查询启用状态的用户
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<AdminUserDO> findSimpleEnablePage(UserSimplePageReqVO reqVO) {
        DefaultConfigStore configStore = buildUserConfigStore();
        configStore.eq(AdminUserDO.STATUS, CommonStatusEnum.ENABLE.getStatus()); // 启用状态

        // 根据关键词模糊查询
        if (reqVO.getKeywords() != null && !reqVO.getKeywords().trim().isEmpty()) {
            configStore.like(AdminUserDO.NICKNAME, reqVO.getKeywords());
        }

        // 添加排序
        configStore.order(AdminUserDO.ADMIN_TYPE, Order.TYPE.ASC).order(BaseDO.CREATE_TIME, Order.TYPE.DESC);

        return findPageWithConditions(configStore, reqVO.getPageNo(), reqVO.getPageSize());
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
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(AdminUserDO.STATUS, CommonStatusEnum.ENABLE.getStatus()); // 启用状态
        configStore.in(AdminUserDO.DEPT_ID, deptIds); // 指定部门ID集合

        // 根据关键词模糊查询
        if (StringUtils.isNotBlank(reqVO.getKeywords())) {
            configStore.like(AdminUserDO.NICKNAME, reqVO.getKeywords());
        }

        // 添加排序
        configStore.order(AdminUserDO.ADMIN_TYPE, Order.TYPE.ASC).order(BaseDO.CREATE_TIME, Order.TYPE.DESC);

        return findPageWithConditions(configStore, reqVO.getPageNo(), reqVO.getPageSize());
    }

    public List<AdminUserDO> findEnableUserByIds(Set<Long> userIds, String keyword, Integer status) {
        DefaultConfigStore configStore = buildUserConfigStore();
        configStore.in(AdminUserDO.ID, userIds)
                .eq(AdminUserDO.STATUS, status)
                .order(AdminUserDO.ADMIN_TYPE, Order.TYPE.ASC);
        // 根据关键词模糊查询
        if (StringUtils.isNotBlank(keyword)) {
            configStore.and(new DefaultConfigStore()
                    .or(Compare.LIKE, AdminUserDO.USERNAME, keyword)
                    .or(Compare.LIKE, AdminUserDO.NICKNAME, keyword));
        }
        configStore.order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    public List<AdminUserDO> findPlatformEnableUserByIds(Set<Long> userIds) {
        DefaultConfigStore configStore = buildUserConfigStore();
        configStore.in(AdminUserDO.ID, userIds)
                .eq(AdminUserDO.STATUS, UserStatusEnum.NORMAL.getStatus())
                .order(AdminUserDO.ADMIN_TYPE, Order.TYPE.ASC)
                .order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }


    public List<AdminUserDO> getTenantExistUserCountByIds(List<Long> tenantIds) {
        // 平台获取用户统计，不需要分用户类型
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore
                .eq(AdminUserDO.STATUS, UserStatusEnum.NORMAL.getStatus())
                .in(AdminUserDO.TENANT_ID, tenantIds)
                .order(AdminUserDO.ADMIN_TYPE, Order.TYPE.ASC)
                .order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    public List<AdminUserDO> getCorpExistUserCountByCorpIds(List<Long> corpIds) {
        // 平台获取用户统计，不需要分用户类型
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore
                .eq(AdminUserDO.STATUS, UserStatusEnum.NORMAL.getStatus())
                .in(AdminUserDO.CORP_ID, corpIds)
                .order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    public List<AdminUserDO> findAllByStatusAndDeptIds(Integer status, Set<Long> deptIds) {
        DefaultConfigStore configStore = buildUserConfigStore();
        configStore.eq(AdminUserDO.STATUS, status)
                .in(AdminUserDO.DEPT_ID, deptIds)
                .order(AdminUserDO.ADMIN_TYPE, Order.TYPE.ASC)
                .order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }
}
