package com.cmsr.onebase.module.bpm.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowCcRecordDO;
import com.cmsr.onebase.module.bpm.core.dto.BpmCcRecordDTO;
import org.anyline.data.param.ConfigStore;
import org.anyline.entity.DataSet;
import org.springframework.stereotype.Repository;

@Repository
public class BpmFlowCcRecordRepository extends DataRepository<BpmFlowCcRecordDO> {
    public BpmFlowCcRecordRepository() {
        super(BpmFlowCcRecordDO.class);
    }
    public PageResult<BpmCcRecordDTO> getCcPage(ConfigStore condition) {
        // 构建基础SQL
        String baseSql = buildBaseSql();

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
                       t.update_time
                   from
                       bpm_flow_cc_record t
                       left join bpm_flow_instance t2 on t.instance_id = t2.id
                       left join bpm_flow_instance_biz_ext t3 on t.instance_id = t3.instance_id
                   where  t.deleted = 0  and t2.deleted = 0 and t3.deleted = 0
                ) tf
                """;
    }
}
