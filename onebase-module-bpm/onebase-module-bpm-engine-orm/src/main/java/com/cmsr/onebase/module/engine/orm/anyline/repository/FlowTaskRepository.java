package com.cmsr.onebase.module.engine.orm.anyline.repository;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.engine.orm.anyline.dataobject.ext.FlowTaskExt;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowTask;
import com.cmsr.onebase.module.engine.orm.anyline.vo.BpmFlowTodoTaskPageReqVO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.DataSet;
import org.anyline.entity.DefaultPageNavi;
import org.anyline.entity.PageNavi;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * WarmFlow 待办任务 Repository
 *
 * @author liyang
 * @date 2025-10-10
 */
@Repository
public class FlowTaskRepository extends DataRepository<FlowTask> {

    public FlowTaskRepository() {
        super(FlowTask.class);
    }
    public PageResult<FlowTaskExt> getTodoTaskPage(BpmFlowTodoTaskPageReqVO reqVO, List<String> permissionList) {
        // 构建基础SQL
        String baseSql = buildBaseSql();

        // 构建动态条件
        ConfigStore condition = buildDynamicCondition(reqVO,permissionList);
        // 执行查询
        DataSet dataSet = querys(baseSql, condition);
        return new PageResult<>(
                dataSet.entitys(FlowTaskExt.class).stream().toList(),
                dataSet.total()
        );
    }
    private String buildBaseSql() {
        return """
                  select 
                    distinct
                     t.id,
                     t.flow_status,
                     t2.ext ,
                     t.create_time
                     from   bpm_flow_task t
                     left join bpm_flow_user t1 on t.id = t1.associated
                     left join bpm_flow_instance t2 on t.instance_id  = t2.id
                     where t.node_type = 1 and t.deleted = 0 and t1.deleted = 0 and t2.deleted = 0
                """;
    }
    private ConfigStore buildDynamicCondition(BpmFlowTodoTaskPageReqVO reqVO,List<String> permissionList) {
        DefaultConfigStore condition = new DefaultConfigStore();
        // 设置分页参数
        PageNavi navi = new DefaultPageNavi();
        navi.setCurPage(reqVO.getPageNo());
        navi.setPageRows(reqVO.getPageSize());
        condition.setPageNavi(navi);
        condition.and(Compare.EQUAL, "t2.ext::json->>'appId'", reqVO.getAppId());//设置appId
       // 动态添加其他查询条件
        if (reqVO.getProcessTitle() != null && !reqVO.getProcessTitle().isEmpty()) {
            condition.and(Compare.LIKE, "t2.ext::json->'processInfo'->>'processTitle'", reqVO.getProcessTitle());
        }
        if (reqVO.getInitiator() != null && !reqVO.getInitiator().isEmpty()) {
            condition.and(Compare.LIKE, "t2.ext::json->'processInfo'->>'initiator'", reqVO.getInitiator());
        }
        if (reqVO.getFormSummary() != null && !reqVO.getFormSummary().isEmpty()) {
            condition.and(Compare.LIKE, "t2.ext::json->'processInfo'->>'formSummary'", reqVO.getFormSummary());
        }
        if (reqVO.getSubmitTime() != null && reqVO.getSubmitTime().length == 2) {
            condition.and(Compare.BETWEEN, "(t2.ext::json->'processInfo'->>'submitTime')::timestamp",reqVO.getSubmitTime()[0], reqVO.getSubmitTime()[1]);
        }

        if(permissionList!= null && !permissionList.isEmpty()){
            condition.and(Compare.IN, "t1.processed_by", permissionList);
        }
        // 设置排序
        if("asc".equals(reqVO.getSortType())){
            condition.order("t.create_time asc");
        }else{
            condition.order("t.create_time desc");
        }
        return condition;
    }
    public List<FlowTask>  getByInsId (Long instanceId){
        ConfigStore configStore = new DefaultConfigStore();
        configStore.in(FlowTask.INSTANCE_ID, instanceId);
        return findAllByConfig(configStore);
    }
}