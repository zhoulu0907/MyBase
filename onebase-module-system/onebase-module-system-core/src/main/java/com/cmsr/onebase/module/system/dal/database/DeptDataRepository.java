package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
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
        DefaultConfigStore configs = CorpDeptUserHelper.getCorpConfigStore(DeptDO.DEPT_TYPE);
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
        DefaultConfigStore configs = CorpDeptUserHelper.getCorpConfigStore(DeptDO.DEPT_TYPE);
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
        DefaultConfigStore configs = CorpDeptUserHelper.getCorpConfigStore(DeptDO.DEPT_TYPE);
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
        DefaultConfigStore configs = CorpDeptUserHelper.getCorpConfigStore(DeptDO.DEPT_TYPE);
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
        DefaultConfigStore configs = CorpDeptUserHelper.getCorpConfigStore(DeptDO.DEPT_TYPE);
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
        DefaultConfigStore configs = CorpDeptUserHelper.getCorpConfigStore(DeptDO.DEPT_TYPE);
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
