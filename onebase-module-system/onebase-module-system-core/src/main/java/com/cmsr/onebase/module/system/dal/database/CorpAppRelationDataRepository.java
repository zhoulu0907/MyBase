package com.cmsr.onebase.module.system.dal.database;


import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.module.system.api.corpapprelation.dto.CorpAppRelationPageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.applicationauthtenant.CorpAppRelationDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

/**
 * 企业应用关联 数据仓储接口
 */
@Repository
public class CorpAppRelationDataRepository extends DataRepository<CorpAppRelationDO> {

    public CorpAppRelationDataRepository() {
        super(CorpAppRelationDO.class);
    }

    public PageResult<CorpAppRelationDO> selectPage(CorpAppRelationPageReqVO pageReqVO) {
        // 构建查询条件
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq("tenant_id", TenantContextHolder.getRequiredTenantId());
        configStore.eq("corp_id", pageReqVO.getCorpId());
        // 按创建时间倒序排列
        configStore.order("create_time", Order.TYPE.DESC);
        // 执行分页查询
        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());

    }
}