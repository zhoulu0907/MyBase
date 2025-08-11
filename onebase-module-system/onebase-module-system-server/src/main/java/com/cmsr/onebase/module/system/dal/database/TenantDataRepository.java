package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 租户数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class TenantDataRepository extends DataRepositoryNew<TenantDO> {

    public TenantDataRepository() {
        super(TenantDO.class);
    }

    /**
     * 根据名称查找租户
     *
     * @param name 租户名称
     * @return 租户对象
     */
    public TenantDO findOneByName(String name) {
        return findOne(new DefaultConfigStore().and(Compare.EQUAL, TenantDO.NAME, name));
    }

    /**
     * 根据网站域名查找租户
     *
     * @param website 网站域名
     * @return 租户对象
     */
    public TenantDO findOneByWebsite(String website) {
        return findOne(new DefaultConfigStore().and(Compare.EQUAL, TenantDO.WEBSITE, website));
    }

    /**
     * 根据套餐ID统计租户数量
     *
     * @param packageId 套餐ID
     * @return 租户数量
     */
    public Long countByPackageId(Long packageId) {
        return countByConfig(new DefaultConfigStore().and(Compare.EQUAL, TenantDO.PACKAGE_ID, packageId));
    }

    /**
     * 根据状态统计租户数量
     *
     * @param status 状态
     * @return 租户数量
     */
    public Integer countByStatus(Integer status) {
        return (int) countByConfig(new DefaultConfigStore().and(Compare.EQUAL, TenantDO.STATUS, status));
    }

    /**
     * 根据套餐ID查询租户列表
     *
     * @param packageId 套餐ID
     * @return 租户列表
     */
    public List<TenantDO> findListByPackageId(Long packageId) {
        return findAllByConfig(new DefaultConfigStore().and(Compare.EQUAL, TenantDO.PACKAGE_ID, packageId));
    }

    /**
     * 根据状态查询租户列表
     *
     * @param status 状态
     * @return 租户列表
     */
    public List<TenantDO> findListByStatus(Integer status) {
        return findAllByConfig(new DefaultConfigStore().and(Compare.EQUAL, TenantDO.STATUS, status));
    }

    /**
     * 分页查询租户
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<TenantDO> findPage(TenantPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        
        if (pageReqVO.getName() != null) {
            configStore.and(Compare.LIKE, TenantDO.NAME, pageReqVO.getName());
        }
        if (pageReqVO.getContactName() != null) {
            configStore.and(Compare.LIKE, TenantDO.CONTACT_NAME, pageReqVO.getContactName());
        }
        if (pageReqVO.getContactMobile() != null) {
            configStore.and(Compare.LIKE, TenantDO.CONTACT_MOBILE, pageReqVO.getContactMobile());
        }
        if (pageReqVO.getStatus() != null) {
            configStore.and(Compare.EQUAL, TenantDO.STATUS, pageReqVO.getStatus());
        }
        if (pageReqVO.getCreateTime() != null && pageReqVO.getCreateTime().length == 2) {
            configStore.and(Compare.GREAT_EQUAL, "create_time", pageReqVO.getCreateTime()[0])
                       .and(Compare.LESS_EQUAL, "create_time", pageReqVO.getCreateTime()[1]);
        }
        
        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }
}
