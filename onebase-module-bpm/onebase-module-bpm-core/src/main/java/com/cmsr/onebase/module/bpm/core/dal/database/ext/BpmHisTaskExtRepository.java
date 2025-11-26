package com.cmsr.onebase.module.bpm.core.dal.database.ext;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.core.dto.BpmDoneTaskDTO;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowHisTaskRepository;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.anyline.data.param.ConfigStore;
import org.anyline.entity.DataSet;
import org.springframework.stereotype.Repository;

/**
 *  对FlowHisTaskRepository的扩展
 *
 * @author liyang
 * @date 2025-09-29
 */
@Getter
@Repository
public class BpmHisTaskExtRepository {

    @Resource
    private FlowHisTaskRepository hisTaskRepository;

    public PageResult<BpmDoneTaskDTO> getDoneTaskPage(ConfigStore condition) {
        // 构建基础SQL
        String baseSql = buildBaseSql();

        // todo：优化sql
        // 去重
        condition.eq("rn", 1);

        // 执行查询
        DataSet dataSet = hisTaskRepository.querys(baseSql, condition);
        return new PageResult<>(
                dataSet.entitys(BpmDoneTaskDTO.class).stream().toList(),
                dataSet.total()
        );
    }

    private String buildBaseSql() {
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
                        t.task_id,
                        t.instance_id,
                        t.approver,
                        t.collaborator,
                        t.ext,
                        t.flow_status as task_flow_status,
                        t.create_time,
                        t.update_time,
                        t1.node_code,
                        t1.flow_status,
                        t4.agent_id as agent_id,
                        t4.agent_name as agent_name,
                        ROW_NUMBER() OVER (
                        PARTITION BY t.id
                         ORDER BY CASE
                                WHEN t.approver = #{userId} THEN 1
                                WHEN t4.agent_id = #{userId} THEN 2
                                ELSE 3
                                END
                        ) as rn
                    FROM bpm_flow_his_task t
                    LEFT JOIN bpm_flow_instance t1 ON t.instance_id = t1.id
                    left join bpm_flow_instance_biz_ext t3 on t.instance_id = t3.instance_id
                    left join bpm_flow_agent_ins t4 on t.task_id = t4.task_id and t.approver = t4.principal_id and t4.deleted = 0
                    WHERE
                    t1.deleted = 0
                    and t.deleted = 0
                    and t3.deleted = 0
                    and t.node_type = 1
                    and (t.approver = #{userId} or t4.agent_id = #{userId})
                ) tf
                """
        );
    }
}


