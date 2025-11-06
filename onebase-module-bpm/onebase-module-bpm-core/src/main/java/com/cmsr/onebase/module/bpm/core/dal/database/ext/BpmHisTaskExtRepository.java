package com.cmsr.onebase.module.bpm.core.dal.database.ext;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.core.dto.BpmDoneTaskDTO;
import com.cmsr.onebase.module.bpm.core.vo.BpmDoneTaskPageReqVO;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowHisTaskRepository;
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

    public PageResult<BpmDoneTaskDTO> getDoneTaskPage(BpmDoneTaskPageReqVO reqVO, Long userId) {
        // 构建基础SQL
        String baseSql = buildBaseSql(userId);
        // 构建动态条件
        ConfigStore condition = buildDynamicCondition(reqVO);
        // 执行查询
        DataSet dataSet = hisTaskRepository.querys(baseSql, condition);
        return new PageResult<>(
                dataSet.entitys(BpmDoneTaskDTO.class).stream().toList(),
                dataSet.total()
        );
    }

    private String buildBaseSql(Long userId) {
        return String.format("""
                select
                    t3.business_title,
                    t3.initiator_id,
                    t3.initiator_name,
                    t3.initiator_avatar,
                    t3.initiator_dept_id,
                    t3.initiator_dept_name,
                    t3.submit_Time,
                    t3.form_Summary,
                    t3.form_Name,
                    t.*,
                    t4.form_path as business_id
                FROM bpm_flow_his_task t
                LEFT JOIN bpm_flow_instance t1 ON t.instance_id = t1.id
                left join bpm_flow_instance_biz_ext t3 on t.instance_id = t3.instance_id
                left join bpm_flow_definition t4 on t4.id = t.definition_id
                WHERE
                t.approver = '%d'
                and t1.deleted = 0
                and t.deleted = 0
                and t3.deleted = 0
                and t.node_type in ('1','3','4')
                """, userId);
    }
    private ConfigStore buildDynamicCondition(BpmDoneTaskPageReqVO reqVO){
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
        if (reqVO.getSubmitTimeStart() != null ) {
            condition.and(Compare.GREAT_EQUAL, "t3.submit_time", reqVO.getSubmitTimeStart());
        }
        if (reqVO.getSubmitTimeEnd() != null ) {
            condition.and(Compare.LESS_EQUAL, "t3.submit_time", reqVO.getSubmitTimeEnd());
        }
        if (reqVO.getBusinessId() != null && !reqVO.getBusinessId().isEmpty()) {
            condition.and(Compare.EQUAL, "t4.form_path", reqVO.getBusinessId());
        }
        if (reqVO.getFlowStatus() != null && !reqVO.getFlowStatus().isEmpty() && !"ALL".equals(reqVO.getFlowStatus()) ) {
            condition.and(Compare.EQUAL, "t.flow_status", reqVO.getFlowStatus());
        }
        if (reqVO.getNodeCode() != null && !reqVO.getNodeCode().isEmpty()) {
            condition.and(Compare.EQUAL, "t.node_code", reqVO.getNodeCode());
        }
        // 设置排序
        if("asc".equals(reqVO.getSortType())){
            condition.order("t.update_time asc");
        }else{
            condition.order("t.update_time desc");
        }
        return condition;
    }
}


