package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.enums.XFromSceneTypeEnum;
import com.cmsr.onebase.framework.security.core.LoginUser;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.enums.dept.DeptTypeEnum;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * 部门数据访问层
 * <p>
 * 负责部门相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-07
 */
@Repository
public class DeptDataRepository extends DataRepository<DeptDO> {
    /**
     * 构造方法，指定默认实体类
     */
    public DeptDataRepository() {
        super(DeptDO.class);
    }

    /**
     * 根据父部门ID查询所有子部门
     *
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    public List<DeptDO> findAllByParentId(Long parentId) {
        DefaultConfigStore configs = getCorpConfigStore();
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
        DefaultConfigStore configs = getCorpConfigStore();
        ;
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
        DefaultConfigStore configs = getCorpConfigStore();
        ;
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
        DefaultConfigStore configs = getCorpConfigStore();
        ;
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
        DefaultConfigStore configs = getCorpConfigStore();
        ;
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
        DefaultConfigStore configs = getCorpConfigStore();
        if (name != null) {
            configs.and(Compare.LIKE, DeptDO.NAME, name);
        }
        if (status != null) {
            configs.and(Compare.EQUAL, DeptDO.STATUS, status);
        }
        configs.order(DeptDO.SORT, org.anyline.entity.Order.TYPE.ASC);
        return findAllByConfig(configs);
    }

    public DefaultConfigStore getCorpConfigStore() {
        DefaultConfigStore configStore = new DefaultConfigStore();
        String fromSceneType = WebFrameworkUtils.getXFromSceneType();
        if (XFromSceneTypeEnum.TENANT.getCode().equals(fromSceneType)) {
            configStore.and(Compare.EQUAL, DeptDO.DEPT_TYPE, DeptTypeEnum.TENANT.getCode());
        } else {
            LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
            if (null != loginUser) {
                Long corpId = loginUser.getCorpId();

                if (null != corpId) {
                    configStore.and(Compare.EQUAL, DeptDO.CORP_ID, corpId);
                    configStore.and(Compare.EQUAL, DeptDO.DEPT_TYPE, DeptTypeEnum.CORP.getCode());
                } else {
                    // TODO  改造未成功之前，默认取全部，
                    configStore.and(Compare.EQUAL, DeptDO.DEPT_TYPE, DeptTypeEnum.TENANT.getCode());
                }
            } else {
                // TODO  改造未成功之前，默认取全部，
                configStore.and(Compare.EQUAL, DeptDO.DEPT_TYPE, DeptTypeEnum.TENANT.getCode());
            }
        }
        return configStore;
    }
}
