package com.cmsr.onebase.module.system.dal.database;


import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterprisePageReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.applicationauthtenant.ApplicationAuthEnterpriseDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

/**
 * 应用授权企业表 数据仓储接口
 */
@Repository
public class ApplicationAuthEnterpriseDataRepository extends DataRepository<ApplicationAuthEnterpriseDO> {

    public ApplicationAuthEnterpriseDataRepository() {
        super(ApplicationAuthEnterpriseDO.class);
    }

    public PageResult<ApplicationAuthEnterpriseDO> selectPage(ApplicationAuthEnterprisePageReqVO pageReqVO) {
        // 构建查询条件
        DefaultConfigStore configStore = new DefaultConfigStore();
        if (pageReqVO.getApplicationId() != null  ) {
            configStore.eq("enterprise_id", pageReqVO.getApplicationId());
        }
        // 只查询未删除的记录
        configStore.eq("deleted", 0L);

        // 按创建时间倒序排列
        configStore.order("create_time", Order.TYPE.DESC);

        // 执行分页查询
        return findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());

}
}