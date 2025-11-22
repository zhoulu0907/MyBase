package com.cmsr.onebase.module.bpm.core.dal.database.ext;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.core.dto.BpmTodoTaskDTO;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowTaskRepository;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.anyline.data.param.ConfigStore;
import org.anyline.entity.DataSet;
import org.springframework.stereotype.Repository;

/**
 * WarmFlow
 *
 * @author liyang
 * @date 2025-10-10
 */
@Getter
@Repository
public class BpmTaskExtRepository {
    @Resource
    private FlowTaskRepository flowTaskRepository;

    public PageResult<BpmTodoTaskDTO> getTodoTaskPage(ConfigStore condition, String userId) {
        // 构建基础SQL
        String baseSql = buildBaseSql(userId);

        // 去重
        condition.eq("rn", 1);

        // 执行查询
        DataSet dataSet = flowTaskRepository.querys(baseSql, condition);
        return new PageResult<>(
                dataSet.entitys(BpmTodoTaskDTO.class).stream().toList(),
                dataSet.total()
        );
    }
    private String buildBaseSql(String userId) {
        return String.format("""
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
                     t.id,
                     t.instance_id,
                     t.flow_status,
                     t.create_time,
                     t1.processed_by,
                     t1.type as user_type,
                     t2.node_code,
                     t4.agent_id as agent_id,
                     t4.agent_name as agent_name,
                     ROW_NUMBER() OVER (
                         PARTITION BY t.id
                         ORDER BY CASE
                                WHEN t1.processed_by = '%s' THEN 1
                                WHEN t4.agent_id = '%s' THEN 2
                                ELSE 3
                                END
                     ) as rn
                     from bpm_flow_task t
                     left join bpm_flow_user t1 on t.id = t1.associated
                     left join bpm_flow_instance t2 on t.instance_id = t2.id
                     left join bpm_flow_instance_biz_ext t3 on t.instance_id = t3.instance_id
                     left join bpm_flow_agent_ins t4 ON t.id = t4.task_id AND t1.processed_by = t4.principal_id and t4.deleted = 0
                     where t.node_type = 1 and t.flow_status != 'draft'
                     and t.deleted = 0 and t1.deleted = 0 and t2.deleted = 0 and t3.deleted = 0
                     and (t1.processed_by = '%s' or t4.agent_id = '%s')
                ) tf
                """,
                userId,
                userId,
                userId,
                userId
        );
    }
}