package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.system.enums.tenant.SortEnum;
import com.cmsr.onebase.module.system.vo.tenant.TenantPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.enums.tenant.TenantCodeEnum;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 租户数据访问层
 *
 * 负责租户相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-18
 */
@Repository
public class TenantDataRepository extends DataRepository<TenantDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public TenantDataRepository() {
        super(TenantDO.class);
    }

    /**
     * 根据租户名称查询租户
     *
     * @param name 租户名称
     * @return 租户对象
     */
    public TenantDO findByName(String name) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(TenantDO.NAME, name);
        return findOne(configStore);
    }

    /**
     * 根据网站域名查询租户
     *
     * @param website 网站域名
     * @return 租户对象
     */
    public TenantDO findByWebsite(String website) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(TenantDO.WEBSITE, website);
        return findOne(configStore);
    }

    /**
     * 根据套餐ID统计租户数量
     *
     * @param packageId 套餐ID
     * @return 租户数量
     */
    public long countByPackageId(Long packageId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(TenantDO.PACKAGE_ID, packageId);
        return countByConfig(configStore);
    }

    /**
     * 根据状态统计租户数量
     *
     * @param status 状态
     * @return 租户数量
     */
    public long countByStatus(Integer status) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(TenantDO.STATUS, status);
        return countByConfig(configStore);
    }

    /**
     * 根据套餐ID查询租户列表
     *
     * @param packageId 套餐ID
     * @return 租户列表
     */
    public List<TenantDO> findAllByPackageId(Long packageId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(TenantDO.PACKAGE_ID, packageId);
        return findAllByConfig(configStore);
    }

    /**
     * 根据状态查询租户列表
     *
     * @param status 状态
     * @return 租户列表
     */
    public List<TenantDO> findAllByStatus(Integer status) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(TenantDO.STATUS, status)
                .order(TenantDO.CREATE_TIME, Order.TYPE.DESC);
        return findAllByConfig(configStore);
    }

    /**
     * 查询所有租户列表
     *
     * @return 租户列表
     */
    public List<TenantDO> findAll() {
        return findAllByConfig(new DefaultConfigStore());
    }

    /**
     * 根据状态和租户编码查询租户数量（排除平台租户）
     *
     * @param status 状态
     * @param excludeTenantId 排除的租户ID（可为null）
     * @return 租户数量
     */
    public long countByStatusExcludePlatform(Integer status, Long excludeTenantId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(TenantDO.STATUS, status)
                .and(Compare.NOT_EQUAL, TenantDO.TENANT_CODE, TenantCodeEnum.PLATFORM_TENANT.getCode());

        if (excludeTenantId != null) {
            configStore.and(Compare.NOT_EQUAL, TenantDO.ID, excludeTenantId);
        }

        return countByConfig(configStore);
    }

    /**
     * 根据状态查询租户列表（排除平台租户）
     *
     * @param status 状态
     * @param excludeTenantId 排除的租户ID（可为null）
     * @return 租户列表
     */
    public List<TenantDO> findAllByStatusExcludePlatform(Integer status, Long excludeTenantId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(TenantDO.STATUS, status)
                .and(Compare.NOT_EQUAL, TenantDO.TENANT_CODE, TenantCodeEnum.PLATFORM_TENANT.getCode());

        if (excludeTenantId != null) {
            configStore.and(Compare.NOT_EQUAL, TenantDO.ID, excludeTenantId);
        }

        return findAllByConfig(configStore);
    }

    /**
     * 分页查询租户
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */

    public PageResult<TenantDO> findPage(TenantPageReqVO pageReqVO) {
        Integer status = pageReqVO.getStatus();
        DefaultConfigStore configStore = new DefaultConfigStore();
        // 按照关键词模糊查询
        if (pageReqVO.getKeyword() != null && !pageReqVO.getKeyword().trim().isEmpty()) {
            configStore.and(new DefaultConfigStore()
                    .or(Compare.LIKE, TenantDO.NAME, pageReqVO.getKeyword())
                    .or(Compare.LIKE, TenantDO.TENANT_CODE, pageReqVO.getKeyword()));
        }

        // 按照状态查询
        if (status != null) {
            configStore.eq(TenantDO.STATUS, status);
        }

        // 排除平台租户
        configStore.and(Compare.NOT_EQUAL, TenantDO.TENANT_CODE, TenantCodeEnum.PLATFORM_TENANT.getCode());

        if(pageReqVO.getSortType()!=null && pageReqVO.getSortType().equals(SortEnum.DESC.getValue())){
            configStore.order(BaseDO.CREATE_TIME, Order.TYPE.DESC);
        }else{
            configStore.order(BaseDO.CREATE_TIME, Order.TYPE.ASC);
        }
        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }
}
