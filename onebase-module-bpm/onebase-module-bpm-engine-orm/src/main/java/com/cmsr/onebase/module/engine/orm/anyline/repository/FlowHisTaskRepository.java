package com.cmsr.onebase.module.engine.orm.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.engine.orm.anyline.dataobject.ext.FlowHisTaskExt;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowHisTask;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowDoneTaskPageReqVO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 历史任务 仓储
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowHisTaskRepository extends DataRepository<FlowHisTask> {
    public FlowHisTaskRepository() {
        super(FlowHisTask.class);
    }
    public PageResult<FlowHisTaskExt> getDoneTaskPage(BpmFlowDoneTaskPageReqVO reqVO, Long userId) {
        // 构建基础SQL
        String baseSql = buildBaseSql(userId);
        // 构建动态条件
        ConfigStore condition = buildDynamicCondition(reqVO);
        // 执行查询
        DataSet dataSet = querys(baseSql, condition);
        return new PageResult<>(
                dataSet.entitys(FlowHisTaskExt.class).stream().toList(),
                dataSet.total()
        );
    }
    private String buildBaseSql(Long userId) {
        return String.format("""
                select
                    t3.business_title,
                    t3.initiator_id,
                    t3.initiator_name,
                    t3.initiator_dept_id,
                    t3.initiator_dept_name,
                    t3.submit_Time,
                    t3.form_Summary,
                    t3.form_Name,
                    t.*
                FROM (
                    SELECT *,
                       ROW_NUMBER() OVER (PARTITION BY instance_id ORDER BY id DESC) as rn
                    FROM bpm_flow_his_task
                    WHERE deleted = 0
                    and approver = '%d'
                    ) t
                LEFT JOIN bpm_flow_instance t1 ON t.instance_id = t1.id
                left join bpm_flow_instance_biz_ext t3 on t.instance_id = t3.instance_id
                WHERE t.rn = 1
                and t1.deleted = 0
                """, userId);
    }
    private ConfigStore buildDynamicCondition(BpmFlowDoneTaskPageReqVO reqVO){
        DefaultConfigStore condition = new DefaultConfigStore();
        // 设置分页参数
        PageNavi navi = new DefaultPageNavi();
        navi.setCurPage(reqVO.getPageNo());
        navi.setPageRows(reqVO.getPageSize());
        condition.setPageNavi(navi);
        condition.and(Compare.EQUAL, "(t3.app_id)::varchar", reqVO.getAppId());
        // 动态添加其他查询条件
        if (reqVO.getProcessTitle() != null && !reqVO.getProcessTitle().isEmpty()) {
            condition.and(Compare.LIKE, "t3.business_title", reqVO.getProcessTitle());
        }
        if (reqVO.getInitiator() != null && !reqVO.getInitiator().isEmpty()) {
            condition.and(Compare.LIKE, "t3.initiator_name", reqVO.getInitiator());
        }
        if (reqVO.getFormSummary() != null && !reqVO.getFormSummary().isEmpty()) {
            condition.and(Compare.LIKE, "t3.form_summary", reqVO.getFormSummary());
        }
        if (reqVO.getHandleTimeStart() != null ) {
            condition.and(Compare.GREAT_EQUAL, "t.update_time", reqVO.getHandleTimeStart());
        }
        if (reqVO.getHandleTimeEnd() != null ) {
            condition.and(Compare.LESS_EQUAL, "t.update_time", reqVO.getHandleTimeEnd());
        }
        if (reqVO.getSkipType() != null && !reqVO.getSkipType().isEmpty()) {
            condition.and(Compare.EQUAL, "t.skip_type", reqVO.getSkipType());
        }
        // 设置排序
        if("asc".equals(reqVO.getSortType())){
            condition.order("t.create_time asc");
        }else{
            condition.order("t.create_time desc");
        }
        return condition;
    }
    public List<FlowHisTask> getHisTaskByInstanceId(Long instanceId,String appId){
        ConfigStore configStore = new DefaultConfigStore();
        configStore.and(FlowHisTask.INSTANCE_ID, instanceId);
        configStore.and(Compare.EQUAL,"ext::json->>'appId'",appId);
        configStore.order("update_time", Order.TYPE.ASC);
        return findAllByConfig(configStore);
    }
}


