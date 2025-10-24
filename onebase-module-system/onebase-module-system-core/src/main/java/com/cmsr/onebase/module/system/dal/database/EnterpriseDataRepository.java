package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.system.api.enterprise.dto.EnterprisePageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.enterprise.EnterpriseDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;
import com.cmsr.onebase.framework.common.pojo.PageResult;

/**
 * 企业数据访问层
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Repository
public class EnterpriseDataRepository extends DataRepository<EnterpriseDO> {

    public EnterpriseDataRepository() {
        super(EnterpriseDO.class);
    }

    public PageResult<EnterpriseDO> selectPage(EnterprisePageReqVO pageReqVO) {
        // 构建查询条件
        DefaultConfigStore configStore = new DefaultConfigStore();

        // 按照企业名称模糊查询
        if (pageReqVO.getEnterpriseName() != null && !pageReqVO.getEnterpriseName().isEmpty()) {
            configStore.like("enterprise_name", pageReqVO.getEnterpriseName());
        }

        // 按照企业编码精确查询
        if (pageReqVO.getEnterpriseCode() != null && !pageReqVO.getEnterpriseCode().isEmpty()) {
            configStore.eq("enterprise_code", pageReqVO.getEnterpriseCode());
        }

        // 按照状态查询
        if (pageReqVO.getStatus() != null) {
            configStore.eq("status", pageReqVO.getStatus());
        }
        // 按照行业类型查询
        if (pageReqVO.getIndustryType() != null) {
            configStore.eq("industry_type", pageReqVO.getIndustryType());
        }
        // 只查询未删除的记录
        configStore.eq("deleted", 0);
        // 按创建时间倒序排列
        configStore.order("create_time", Order.TYPE.DESC);

        // 执行分页查询
        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    public Long getTenantEnterpriseCount(Long tenantId){
        // 构建查询条件
        DefaultConfigStore configStore = new DefaultConfigStore();
        // 添加租户ID条件
        if (tenantId != null) {
            configStore.eq("tenant_id", tenantId);
        }
        // 只查询未删除的记录
        configStore.eq("deleted", 0);
        // 执行计数查询
        return countByConfig(configStore);

    }

    public EnterpriseDO findByName(String name) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq("enterprise_name", name);
        // 只查询未删除的记录
        configStore.eq("deleted", 0);
        return findOne(configStore);
    }
}