package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.system.enums.user.UserStatusEnum;
import com.cmsr.onebase.module.system.vo.role.RolePageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.events.Event;

import java.util.Collection;
import java.util.List;

/**
 * 角色数据访问层
 *
 * 负责角色相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-18
 */
@Repository
public class RoleDataRepository extends DataRepository<RoleDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public RoleDataRepository() {
        super(RoleDO.class);
    }

    /**
     * 根据角色名称查询角色
     *
     * @param name 角色名称
     * @return 角色对象
     */
    public RoleDO findOneByName(String name) {
        return findOne(new DefaultConfigStore().and(Compare.EQUAL, RoleDO.NAME, name));
    }

    /**
     * 根据角色编码查询角色
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
     * @param status 状态
     * @return 角色列表
     */
    public List<RoleDO> findListByStatus(Integer status) {
        ConfigStore configStore = new DefaultConfigStore().and(Compare.EQUAL, RoleDO.STATUS, status);
        // 内置角色靠前，其次是sort，其次是createTime
        configStore.order(RoleDO.TYPE, Order.TYPE.ASC).order(RoleDO.SORT, Order.TYPE.ASC).order(RoleDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 查询所有角色列表
     *
     * @return 角色列表
     */
    public List<RoleDO> findAllRoles() {
        return findAllByConfig(new DefaultConfigStore());
    }

    /**
     * 分页查询角色
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<RoleDO> findPage(RolePageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        // 按名称模糊查询
        if (pageReqVO.getName() != null && !pageReqVO.getName().trim().isEmpty()) {
            configStore.like(RoleDO.NAME, pageReqVO.getName());
        }

        // 按编码模糊查询
        if (pageReqVO.getCode() != null && !pageReqVO.getCode().trim().isEmpty()) {
            configStore.like(RoleDO.CODE, pageReqVO.getCode());
        }

        // 按状态查询
        if (pageReqVO.getStatus() != null) {
            configStore.eq(RoleDO.STATUS, pageReqVO.getStatus());
        }

        // 按创建时间范围查询
        if (pageReqVO.getCreateTime() != null && pageReqVO.getCreateTime().length == 2) {
            if (pageReqVO.getCreateTime()[0] != null) {
                configStore.ge(BaseDO.CREATE_TIME, pageReqVO.getCreateTime()[0]);
            }
            if (pageReqVO.getCreateTime()[1] != null) {
                configStore.le(BaseDO.CREATE_TIME, pageReqVO.getCreateTime()[1]);
            }
        }

        // 排序
        configStore.order(RoleDO.SORT, Order.TYPE.ASC)
                .order(BaseDO.CREATE_TIME, Order.TYPE.DESC);

        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    /**
     * 根据角色编码列表查询角色
     *
     * @param codes 角色编码列表
     * @return 角色列表
     */
    public List<RoleDO> findAllByCodes(Collection<String> codes) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in(RoleDO.CODE, codes);
        return findAllByConfig(configStore);
    }

    public RoleDO getRoleIdsByCodeAndTenantId(String code,Long tenandID) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in(RoleDO.CODE, code);
        configStore.eq(RoleDO.TENANT_ID, tenandID);
        return findOne(configStore);

    }
    public RoleDO getRoleByCodeIgnoreTenant(String codes) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in(RoleDO.CODE, codes);
        return findOne(configStore);
    }

}
