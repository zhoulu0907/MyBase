package com.cmsr.onebase.module.bpm.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowCcRecordDO;
import com.cmsr.onebase.module.bpm.core.dto.BpmCcRecordDTO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataSet;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BpmFlowCcRecordRepository extends DataRepository<BpmFlowCcRecordDO> {
    public BpmFlowCcRecordRepository() {
        super(BpmFlowCcRecordDO.class);
    }
    public PageResult<BpmCcRecordDTO> getCcPage(ConfigStore condition) {
        // 构建基础SQL
        String baseSql = buildBaseSql();

        // todo：优化sql
        // 去重
        condition.eq("rn", 1);

        // 执行查询
        DataSet dataSet = querys(baseSql, condition);
        return new PageResult<>(
                dataSet.entitys(BpmCcRecordDTO.class).stream().toList(),
                dataSet.total()
        );
    }

    private String buildBaseSql() {
        return """
                select * from (
                   select
                       t3.app_id,
                       t3.bpm_title,
                       t3.initiator_id,
                       t3.initiator_name,
                       t3.initiator_avatar,
                       t3.initiator_dept_id,
                       t3.initiator_dept_name,
                       t3.submit_time,
                       t3.form_summary,
                       t3.form_name,
                       t3.binding_view_id,
                       t2.flow_status,
                       t2.node_type,
                       t.id,
                       t.instance_id,
                       t.task_id,
                       t.viewed,
                       t.viewed_time,
                       t.user_id,
                       t.creator,
                       t.create_time,
                       t.updater,
                       t.update_time,
                       t4.agent_id as agent_id,
                       t4.agent_name as agent_name,
                       ROW_NUMBER() OVER (
                        PARTITION BY t.id
                         ORDER BY CASE
                                WHEN t.user_id = #{userId} THEN 1
                                WHEN t4.agent_id = #{userId} THEN 2
                                ELSE 3
                                END
                       ) as rn
                   from
                       bpm_flow_cc_record t
                       left join bpm_flow_instance t2 on t.instance_id = t2.id
                       left join bpm_flow_instance_biz_ext t3 on t.instance_id = t3.instance_id
                       left join bpm_flow_agent_ins t4 ON t.task_id = t4.task_id AND t.user_id = t4.principal_id and t4.deleted = 0
                   where
                   t.deleted = 0 and t2.deleted = 0 and t3.deleted = 0
                   and (t.user_id = #{userId} or t4.agent_id = #{userId})
                ) tf
                """;
    }

    public List<BpmFlowCcRecordDO> findAllByInstanceId(Long instanceId) {
        ConfigStore configs = new DefaultConfigStore();
        configs.eq(BpmCcRecordDTO.INSTANCE_ID, instanceId);

        return findAllByConfig(configs);
    }
}
