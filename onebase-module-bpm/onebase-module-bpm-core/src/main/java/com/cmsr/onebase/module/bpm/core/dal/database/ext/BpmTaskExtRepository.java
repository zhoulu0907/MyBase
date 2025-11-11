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

    public PageResult<BpmTodoTaskDTO> getTodoTaskPage(ConfigStore condition) {
        // 构建基础SQL
        String baseSql = buildBaseSql();

        // 执行查询
        DataSet dataSet = flowTaskRepository.querys(baseSql, condition);
        return new PageResult<>(
                dataSet.entitys(BpmTodoTaskDTO.class).stream().toList(),
                dataSet.total()
        );
    }
    private String buildBaseSql() {
        return """
               select * from (
                  select
                    distinct
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
                    t2.node_code
                    from bpm_flow_task t
                    left join bpm_flow_user t1 on t.id = t1.associated
                    left join bpm_flow_instance t2 on t.instance_id = t2.id
                    left join bpm_flow_instance_biz_ext t3 on t.instance_id = t3.instance_id
                    where t.node_type = 1 and t.flow_status !='draft'
                    and t1.type in ('1','2','3')
                    and t.deleted = 0 and t1.deleted = 0 and t2.deleted = 0 and t3.deleted = 0
               ) tf
               """;
    }
}