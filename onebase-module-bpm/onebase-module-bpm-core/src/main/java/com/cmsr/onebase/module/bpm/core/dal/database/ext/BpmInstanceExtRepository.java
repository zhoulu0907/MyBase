package com.cmsr.onebase.module.bpm.core.dal.database.ext;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.core.dto.BpmInstanceDTO;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowInstanceRepository;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.anyline.data.param.ConfigStore;
import org.anyline.entity.DataSet;
import org.springframework.stereotype.Repository;

/**
 * 对FlowInstanceRepository的扩展
 *
 * @author liyang
 * @date 2025-10-10
 */
@Getter
@Repository
public class BpmInstanceExtRepository {

    @Resource
    private FlowInstanceRepository flowInstanceRepository;

    public PageResult<BpmInstanceDTO> getMyCreatePage(ConfigStore condition) {
        // 构建基础SQL
        String baseSql = buildBaseSql();

        // 执行查询
        DataSet dataSet = flowInstanceRepository.querys(baseSql, condition);
        return new PageResult<>(
                dataSet.entitys(BpmInstanceDTO.class).stream().toList(),
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
                          t3.initiator_dept_id,
                          t3.initiator_dept_name,
                          t3.submit_Time,
                          t3.form_summary,
                          t3.form_name,
                          t3.binding_view_id,
                          t.id,
                          t.definition_id,
                          t.node_type,
                          t.node_code,
                          t.node_name,
                          t.flow_status,
                          t.creator,
                          t.create_time,
                          t.updater,
                          t.update_time
                    from bpm_flow_instance t
                    inner join bpm_flow_instance_biz_ext t3 on t.id = t3.instance_id
                    where t.deleted = 0 and t3.deleted = 0
                ) tf
                """;
    }
}