package com.cmsr.onebase.module.system.dal.database.dept;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.enums.XFromSceneTypeEnum;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.enums.dept.DeptTypeEnum;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;

import java.util.Collection;
import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;

/**
 * 部门数据访问层
 * <p>
 * 负责部门相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-07
 */
public abstract class AbstractDeptDataRepository extends DataRepository<DeptDO> {
    /**
     * 构造方法，指定默认实体类
     */
    public AbstractDeptDataRepository() {
        super(DeptDO.class);
    }

    /**
     * 获取登录用户其用户所处的场景类型：平台/空间/企业
     *
     * @return
     */
    public abstract String getXFromSceneType();

    // /**
    //  * 获取登录用户其用户所处的场景类型：平台/空间/企业
    //  *
    //  * @return
    //  */
    // public String getXFromSceneType(){
    //     String userSceneType = SecurityFrameworkUtils.getXFromSceneType();
    //     if (StringUtils.isBlank(userSceneType)) {
    //         throw exception(USER_TYPE_EXCEPTION, "类型为空");
    //     }
    //     return userSceneType;
    // }
    private DefaultConfigStore buildDeptConfigStore() {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null || loginUser.getId() == null) {
            // 立即失败，抛出异常，防止数据越权
            throw exception(USER_NOT_EXISTS);
        }
        
        String fromSceneType = getXFromSceneType();
        DefaultConfigStore configStore = new DefaultConfigStore();
        if (XFromSceneTypeEnum.TENANT.getCode().equals(fromSceneType)) {
             configStore.and(Compare.EQUAL, DeptDO.DEPT_TYPE, DeptTypeEnum.TENANT.getCode());
        } else if (XFromSceneTypeEnum.CORP.getCode().equals(fromSceneType)) {
            Long corpId = loginUser.getCorpId();
            if (null != corpId) {
                configStore.and(Compare.EQUAL, DeptDO.CORP_ID, corpId);
                configStore.and(Compare.EQUAL, DeptDO.DEPT_TYPE, DeptTypeEnum.CORP.getCode());
            } else {
                // 立即失败，抛出异常，防止数据越权
                throw exception(CORP_ID_NULL);
            }
        } else if (XFromSceneTypeEnum.ALL.getCode().equals(fromSceneType)) {
            // 不做任何处理，全量数据
        } else {
            // 立即失败，抛出异常，防止数据越权
            throw exception(USER_TYPE_EXCEPTION, fromSceneType);
        }
        return configStore;
    }
    
    /**
     * 根据父部门ID查询所有子部门
     *
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    public List<DeptDO> findAllByParentId(Long parentId) {
        DefaultConfigStore configs = buildDeptConfigStore();
        configs.and(Compare.EQUAL, DeptDO.PARENT_ID, parentId);
        return findAllByConfig(configs);
    }

    /**
     * 根据父部门ID和部门名查询部门
     *
     * @param parentId 父部门ID
     * @param name     部门名称
     * @return 部门对象
     */
    public DeptDO findOneByParentIdAndName(Long parentId, String name) {
        DefaultConfigStore configs = buildDeptConfigStore();
        configs.and(Compare.EQUAL, DeptDO.PARENT_ID, parentId);
        configs.and(Compare.EQUAL, DeptDO.NAME, name);
        return findOne(configs);
    }

    /**
     * 根据部门名查询部门
     *
     * @param name 部门名称
     * @return 部门对象
     */
    public DeptDO findOneByName(String name) {
        DefaultConfigStore configs = buildDeptConfigStore();
        configs.and(Compare.EQUAL, DeptDO.NAME, name);
        return findOne(configs);
    }

    /**
     * 根据领导用户ID查询部门列表
     *
     * @param leaderUserId 领导用户ID
     * @return 部门列表
     */
    public List<DeptDO> findAllByLeaderUserId(Long leaderUserId) {
        DefaultConfigStore configs = buildDeptConfigStore();
        configs.and(Compare.EQUAL, DeptDO.LEADER_USER_ID, leaderUserId);
        return findAllByConfig(configs);
    }

    /**
     * 根据父部门ID集合查询所有子部门
     *
     * @param parentIds 父部门ID集合
     * @return 子部门列表
     */
    public List<DeptDO> findAllByParentIds(Collection<Long> parentIds) {
        DefaultConfigStore configs = buildDeptConfigStore();
        configs.and(Compare.IN, DeptDO.PARENT_ID, parentIds);
        return findAllByConfig(configs);
    }

    /**
     * 根据部门名和状态查询部门列表
     *
     * @param name   部门名称（可为空）
     * @param status 状态（可为空）
     * @return 部门列表
     */
    public List<DeptDO> findAllByNameAndStatus(String name, Integer status) {
        DefaultConfigStore configs = buildDeptConfigStore();
        if (name != null) {
            configs.and(Compare.LIKE, DeptDO.NAME, name);
        }
        if (status != null) {
            configs.and(Compare.EQUAL, DeptDO.STATUS, status);
        }
        configs.order(DeptDO.SORT, org.anyline.entity.Order.TYPE.ASC);
        return findAllByConfig(configs);
    }

}
