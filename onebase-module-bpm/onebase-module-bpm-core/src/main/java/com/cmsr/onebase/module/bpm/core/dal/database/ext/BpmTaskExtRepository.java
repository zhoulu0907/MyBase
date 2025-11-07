package com.cmsr.onebase.module.bpm.core.dal.database.ext;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.core.dto.BpmTodoTaskDTO;
import com.cmsr.onebase.module.bpm.core.vo.BpmTodoTaskPageReqVO;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowTaskRepository;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.DataSet;
import org.anyline.entity.DefaultPageNavi;
import org.anyline.entity.PageNavi;
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

    public PageResult<BpmTodoTaskDTO> getTodoTaskPage(BpmTodoTaskPageReqVO reqVO, String permission) {
        // 构建基础SQL
        String baseSql = buildBaseSql();

        // 构建动态条件
        ConfigStore condition = buildDynamicCondition(reqVO,permission);
        // 执行查询
        DataSet dataSet = flowTaskRepository.querys(baseSql, condition);
        return new PageResult<>(
                dataSet.entitys(BpmTodoTaskDTO.class).stream().toList(),
                dataSet.total()
        );
    }
    private String buildBaseSql() {
        return """
                  select
                    distinct
                    t3.business_title,
                    t3.initiator_id,
                    t3.initiator_name,
                    t3.initiator_avatar,
                    t3.initiator_dept_id,
                    t3.initiator_dept_name,
                    t3.submit_Time,
                    t3.form_Summary,
                    t3.form_Name,
                    t.id,
                    t.instance_id,
                    t.flow_status,
                    t.create_time,
                    t4.form_path as business_id
                    from  bpm_flow_task t
                    left join bpm_flow_user t1 on t.id = t1.associated
                    left join bpm_flow_instance t2 on t.instance_id = t2.id
                    left join bpm_flow_instance_biz_ext t3 on t.instance_id = t3.instance_id
                    left join bpm_flow_definition t4 on t4.id = t.definition_id
                    where t.node_type = 1 and t.flow_status !='draft'
                    and t.deleted = 0 and t1.deleted = 0 and t2.deleted = 0 and t3.deleted = 0 and  t4.deleted = 0
                """;
    }

    private ConfigStore buildDynamicCondition(BpmTodoTaskPageReqVO reqVO, String permission) {
        DefaultConfigStore condition = new DefaultConfigStore();
        // 设置分页参数
        PageNavi navi = new DefaultPageNavi();
        navi.setCurPage(reqVO.getPageNo());
        navi.setPageRows(reqVO.getPageSize());
        condition.setPageNavi(navi);
        condition.and(Compare.EQUAL, "t3.app_id", reqVO.getAppId());
       // 动态添加其他查询条件
        if (reqVO.getKeyword() != null && !reqVO.getKeyword().isEmpty()) {
            ConfigStore orCondition = new DefaultConfigStore();
            orCondition.or(Compare.LIKE, "t3.business_title", reqVO.getKeyword());
            orCondition.or(Compare.LIKE, "t3.initiator_name", reqVO.getKeyword());
            orCondition.or(Compare.LIKE, "t3.form_summary", reqVO.getKeyword());
            condition.and(orCondition);
        }
        if (reqVO.getBusinessId() != null && !reqVO.getBusinessId().isEmpty()) {
            condition.and(Compare.EQUAL, "t4.form_path", reqVO.getBusinessId());
        }
        if (reqVO.getNodeCode() != null && !reqVO.getNodeCode().isEmpty()) {
            condition.and(Compare.EQUAL, "t.node_code", reqVO.getNodeCode());
        }
        if (reqVO.getSubmitTimeStart() != null ) {
            condition.and(Compare.GREAT_EQUAL, "t3.submit_time", reqVO.getSubmitTimeStart());
        }
        if (reqVO.getSubmitTimeEnd() != null ) {
            condition.and(Compare.LESS_EQUAL, "t3.submit_time", reqVO.getSubmitTimeEnd());
        }
        if (permission != null ) {
            condition.and(Compare.EQUAL, "t1.processed_by", permission);
        }
        if (reqVO.getFlowStatus() != null && !reqVO.getFlowStatus().isEmpty() && !"ALL".equals(reqVO.getFlowStatus()) ) {
            condition.and(Compare.EQUAL, "t.flow_status", reqVO.getFlowStatus());
        }
        // 设置排序
        if("asc".equals(reqVO.getSortType())){
            condition.order("t.create_time asc");
        } else{
            condition.order("t.create_time desc");
        }

        return condition;
    }
}