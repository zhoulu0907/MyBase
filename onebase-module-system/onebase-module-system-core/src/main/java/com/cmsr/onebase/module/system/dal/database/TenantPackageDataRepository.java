package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.vo.tenant.TenantPackagePageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantPackageDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 租户套餐数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class TenantPackageDataRepository extends DataRepository<TenantPackageDO> {

    public TenantPackageDataRepository() {
        super(TenantPackageDO.class);
    }

    /**
     * 根据名称查找租户套餐
     *
     * @param name 套餐名称
     * @return 租户套餐
     */
    public TenantPackageDO findOneByName(String name) {
        return findOne(new DefaultConfigStore().and(Compare.EQUAL, TenantPackageDO.NAME, name));
    }

    /**
     * 根据套餐编码查找租户套餐
     *
     * @param code 套餐编码
     * @return 租户套餐
     */
    public TenantPackageDO findOneByCode(String code) {
        return findOne(new DefaultConfigStore().and(Compare.EQUAL, TenantPackageDO.CODE, code));
    }

    /**
     * 根据状态查询租户套餐列表
     *
     * @param status 状态
     * @return 租户套餐列表
     */
    public List<TenantPackageDO> findListByStatus(Integer status) {
        return findAllByConfig(new DefaultConfigStore().and(Compare.EQUAL, TenantPackageDO.STATUS, status));
    }

    /**
     * 分页查询租户套餐
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<TenantPackageDO> findPage(TenantPackagePageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (pageReqVO.getName() != null) {
            configStore.and(Compare.LIKE, TenantPackageDO.NAME, pageReqVO.getName());
        }
        if (pageReqVO.getStatus() != null) {
            configStore.and(Compare.EQUAL, TenantPackageDO.STATUS, pageReqVO.getStatus());
        }

        configStore.order("id", "DESC");
        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }
}
