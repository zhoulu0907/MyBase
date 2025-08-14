package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色数据访问层
 *
 * @author matianyu
 * @date 2025-01-27
 */
@Repository
public class RoleDataRepository extends DataRepositoryNew<RoleDO> {

    public RoleDataRepository() {
        super(RoleDO.class);
    }

    /**
     * 根据角色名称查找角色
     *
     * @param name 角色名称
     * @return 角色对象
     */
    public RoleDO findOneByName(String name) {
        return findOne(new DefaultConfigStore().and(Compare.EQUAL, RoleDO.NAME, name));
    }

    /**
     * 根据角色编码查找角色
     *
     * @param code 角色编码
     * @return 角色对象
     */
    public RoleDO findOneByCode(String code) {
        return findOne(new DefaultConfigStore().and(Compare.EQUAL, RoleDO.CODE, code));
    }

    /**
     * 根据状态查询角色列表
     *
     * @param status 状态列表
     * @return 角色列表
     */
    public List<RoleDO> findListByStatus(Integer status) {
        ConfigStore configStore = new DefaultConfigStore().and(Compare.EQUAL, RoleDO.STATUS, status);
        // 内置角色靠前，其次是sort，其次是createTime
        configStore.order(RoleDO.TYPE, Order.TYPE.ASC).order(RoleDO.SORT, Order.TYPE.ASC).order(RoleDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 分页查询角色
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    public PageResult<RoleDO> findPage(RolePageReqVO reqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (reqVO.getName() != null) {
            configStore.and(Compare.LIKE, RoleDO.NAME, reqVO.getName());
        }
        if (reqVO.getCode() != null) {
            configStore.and(Compare.LIKE, RoleDO.CODE, reqVO.getCode());
        }
        if (reqVO.getStatus() != null) {
            configStore.and(Compare.EQUAL, RoleDO.STATUS, reqVO.getStatus());
        }
        if (reqVO.getCreateTime() != null) {
            configStore.and(Compare.EQUAL, RoleDO.CREATE_TIME, reqVO.getCreateTime());
        }
        // 内置角色靠前，其次是sort，其次是createTime
        configStore.order(RoleDO.TYPE, Order.TYPE.ASC).order(RoleDO.SORT, Order.TYPE.ASC).order(RoleDO.CREATE_TIME, Order.TYPE.DESC);

        return findPageWithConditions(configStore, reqVO.getPageNo(), reqVO.getPageSize());
    }
}
