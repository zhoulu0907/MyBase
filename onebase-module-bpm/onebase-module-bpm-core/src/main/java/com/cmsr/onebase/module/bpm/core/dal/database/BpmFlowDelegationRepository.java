package com.cmsr.onebase.module.bpm.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowDelegationDO;
import com.cmsr.onebase.module.bpm.core.dto.BpmInstanceDTO;
import org.anyline.data.param.ConfigStore;
import org.anyline.entity.DataSet;
import org.springframework.stereotype.Repository;

/**
 * BPM流程代理仓库
 *
 * @author liyang
 * @date 2025-11-10
 */
@Repository
public class BpmFlowDelegationRepository extends DataRepository<BpmFlowDelegationDO> {
    public BpmFlowDelegationRepository() {
        super(BpmFlowDelegationDO.class);
    }
    public PageResult<BpmFlowDelegationDO> getDelegationPage(ConfigStore condition) {
        // 构建基础SQL
        String baseSql = buildBaseSql();

        // 执行查询
        DataSet dataSet = querys(baseSql, condition);
        return new PageResult<>(
                dataSet.entitys(BpmFlowDelegationDO.class).stream().toList(),
                dataSet.total()
        );
    }
    private String buildBaseSql() {
        return """
                select
                    t.id,
                	t.app_id,
                	t.principal_id,
                	t.delegate_id,
                	t.start_time,
                	t.end_time,
                	t.revoker_id,
                	t.revoked_time,
                	t.creator,
                	t.create_time,
                	t.updater,
                	t.update_time,
                	t.revoker_id,
                    t.revoked_time
                from  bpm_flow_delegation t
                left join system_users t1 on t.delegate_id  = t1.id
                left join system_users t2 on t.principal_id  = t2.id
                where t.deleted =0 and t1.deleted =0 and t2.deleted =0
                """;
    }
}
