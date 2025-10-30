package com.cmsr.onebase.module.engine.orm.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseEntity;
import com.cmsr.onebase.module.engine.orm.anyline.dataobject.ext.FlowHisTaskExt;
import com.cmsr.onebase.module.engine.orm.anyline.dataobject.ext.FlowInstanceExt;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowInstance;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowDoneTaskPageReqVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowTodoTaskPageReqVO;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmMyCreatedPageReqVO;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.*;
import org.springframework.stereotype.Repository;

/**
 * WarmFlow 流程实例 Repository
 *
 * @author liyang
 * @date 2025-10-10
 */
@Repository
public class FlowInstanceRepository extends DataRepository<FlowInstance> {

    public FlowInstanceRepository() {
        super(FlowInstance.class);
    }

    public PageResult<FlowInstanceExt> getMyCreatePage(BpmMyCreatedPageReqVO reqVO, Long userId) {
        // 构建基础SQL
        String baseSql = buildBaseSql();
        // 构建动态条件
        ConfigStore condition = buildDynamicCondition(reqVO, userId);
        // 执行查询
        DataSet dataSet = querys(baseSql, condition);
        return new PageResult<>(
                dataSet.entitys(FlowInstanceExt.class).stream().toList(),
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
                      t3.initiator_dept_id,
                      t3.initiator_dept_name,
                      t3.submit_Time,
                      t3.form_Summary,
                      t3.form_Name,
                      t.*
                from bpm_flow_instance t
                inner join bpm_flow_instance_biz_ext t3 on t.id = t3.instance_id
                """;
    }

    private ConfigStore buildDynamicCondition(BpmMyCreatedPageReqVO reqVO, Long userId) {
        DefaultConfigStore condition = new DefaultConfigStore();
        // 设置分页参数
        PageNavi navi = new DefaultPageNavi();
        navi.setCurPage(reqVO.getPageNo());
        navi.setPageRows(reqVO.getPageSize());
        condition.setPageNavi(navi);
        condition.and(Compare.EQUAL, "(t3.app_id)::varchar", reqVO.getAppId());
        condition.and(Compare.EQUAL, "t.creator", userId);
        if (reqVO.getProcessTitle() != null && !reqVO.getProcessTitle().isEmpty()) {
            condition.and(Compare.LIKE, "t3.business_title", reqVO.getProcessTitle());
        }
        if (reqVO.getInitiator() != null && !reqVO.getInitiator().isEmpty()) {
            condition.and(Compare.LIKE, "t3.initiator_name", reqVO.getInitiator());
        }
        if (reqVO.getFormSummary() != null && !reqVO.getFormSummary().isEmpty()) {
            condition.and(Compare.LIKE, "t3.form_summary", reqVO.getFormSummary());
        }
        if (reqVO.getSubmitTimeStart() != null ) {
            condition.and(Compare.GREAT_EQUAL, "t3.submit_time", reqVO.getSubmitTimeStart());
        }
        if (reqVO.getSubmitTimeStart() != null ) {
            condition.and(Compare.LESS_EQUAL, "t3.submit_time", reqVO.getSubmitTimeEnd());
        }
        if (reqVO.getFlowStatus() != null && !reqVO.getFlowStatus().isEmpty()){
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