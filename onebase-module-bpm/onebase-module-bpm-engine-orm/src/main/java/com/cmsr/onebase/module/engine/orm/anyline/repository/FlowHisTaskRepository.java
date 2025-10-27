package com.cmsr.onebase.module.engine.orm.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.engine.orm.anyline.dataobject.FlowTaskDO;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowHisTask;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowTask;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowDoneTaskPageReqVO;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.DataSet;
import org.anyline.entity.DefaultPageNavi;
import org.anyline.entity.PageNavi;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 历史任务 仓储
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowHisTaskRepository extends DataRepository<FlowHisTask> {
    @Resource
    private AnylineService<?> service;
    public FlowHisTaskRepository() {
        super(FlowHisTask.class);
    }
    public PageResult<FlowHisTask> getDoneTaskPage(BpmFlowDoneTaskPageReqVO reqVO, Long userId) {
        // 构建基础SQL
        String baseSql = buildBaseSql(userId);
        // 设置分页参数
        PageNavi navi = new DefaultPageNavi();
        navi.setCurPage(reqVO.getPageNo());
        navi.setPageRows(reqVO.getPageSize());
        // 构建动态条件
        ConfigStore condition = buildDynamicCondition(reqVO);
        // 执行查询
        DataSet dataSet = service.querys(baseSql, condition, navi);
        return new PageResult<>(
                dataSet.entitys(FlowHisTask.class).stream().toList(),
                dataSet.total()
        );
    }
    private String buildBaseSql(Long userId) {
        return "select \n" +
                "    t.id,\n" +
                "    t.skip_type,\n" +
                "    t1.ext,\n" +
                "    t.update_time,\n" +
                "    t.create_time\n" +
                "FROM (\n" +
                "    SELECT *,\n" +
                "           ROW_NUMBER() OVER (PARTITION BY instance_id ORDER BY id DESC) as rn\n" +
                "    FROM bpm_flow_his_task\n" +
                "    WHERE deleted  = 0 \n" +
                "    and approver = '" + userId + "'\n" +
                "   ) t\n" +
                "LEFT JOIN bpm_flow_instance t1 ON t.instance_id = t1.id\n" +
                "WHERE t.rn = 1 \n" +
                "and t1.deleted  = 0";
    }
    private ConfigStore buildDynamicCondition(BpmFlowDoneTaskPageReqVO reqVO){
        DefaultConfigStore condition = new DefaultConfigStore();
        // 动态添加其他查询条件
        if (reqVO.getProcessTitle() != null && !reqVO.getProcessTitle().isEmpty()) {
            condition.and(Compare.LIKE, "t1.ext::json->'processInfo'->>'processTitle'", reqVO.getProcessTitle());
        }
        if (reqVO.getInitiator() != null && !reqVO.getInitiator().isEmpty()) {
            condition.and(Compare.LIKE, "t1.ext::json->'processInfo'->>'initiator'", reqVO.getInitiator());
        }
        if (reqVO.getFormSummary() != null && !reqVO.getFormSummary().isEmpty()) {
            condition.and(Compare.LIKE, "t1.ext::json->'processInfo'->>'formSummary'", reqVO.getFormSummary());
        }
        if (reqVO.getHandleTime() != null && reqVO.getHandleTime().length == 2) {
            condition.and(Compare.BETWEEN, "t.update_time",reqVO.getHandleTime()[0], reqVO.getHandleTime()[1]);
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
    public List<FlowHisTask> getHisTaskByInstanceId(Long instanceId){
        ConfigStore configStore = new DefaultConfigStore();
        configStore.in(FlowHisTask.INSTANCE_ID, instanceId);
        return findAllByConfig(configStore);
    }
}


