package com.cmsr.onebase.module.bpm.core.dal.database.ext;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.core.dto.BpmInstanceDTO;
import com.cmsr.onebase.module.bpm.core.vo.BpmMyCreatedPageReqVO;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowInstanceRepository;
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

    public PageResult<BpmInstanceDTO> getMyCreatePage(BpmMyCreatedPageReqVO reqVO, Long userId) {
        // 构建基础SQL
        String baseSql = buildBaseSql();
        // 构建动态条件
        ConfigStore condition = buildDynamicCondition(reqVO, userId);
        // 执行查询
        DataSet dataSet = flowInstanceRepository.querys(baseSql, condition);
        return new PageResult<>(
                dataSet.entitys(BpmInstanceDTO.class).stream().toList(),
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
                      t3.form_summary,
                      t3.form_name,
                      t.*
                from bpm_flow_instance t
                left join bpm_flow_task t1 on t.id = t1.instance_id
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
        condition.and(Compare.EQUAL, "t3.app_id", reqVO.getAppId());
        condition.and(Compare.EQUAL, "t.creator", userId);
        // 动态添加其他查询条件
        if (reqVO.getKeyword() != null && !reqVO.getKeyword().isEmpty()) {
            ConfigStore orCondition = new DefaultConfigStore();
            orCondition.or(Compare.LIKE, "t3.business_title", reqVO.getKeyword());
            orCondition.or(Compare.LIKE, "t3.initiator_name", reqVO.getKeyword());
            orCondition.or(Compare.LIKE, "t3.form_summary", reqVO.getKeyword());
            condition.and(orCondition);
        }
        if (reqVO.getBusinessId() != null && !reqVO.getBusinessId().isEmpty()) {
            condition.and(Compare.EQUAL, "t.business_id", reqVO.getBusinessId());
        }
        if (reqVO.getNodeCode() != null && !reqVO.getNodeCode().isEmpty()) {
            condition.and(Compare.EQUAL, "t1.node_code", reqVO.getNodeCode());
        }
        if (reqVO.getFlowStatus() != null && !reqVO.getFlowStatus().isEmpty()){
            condition.and(Compare.EQUAL, "t.flow_status", reqVO.getFlowStatus());
        }
        if (reqVO.getCreateTimeStart() != null ) {
            condition.and(Compare.GREAT_EQUAL, "t.create_time", reqVO.getCreateTimeStart());
        }
        if (reqVO.getCreateTimeEnd() != null ) {
            condition.and(Compare.LESS_EQUAL, "t.create_time", reqVO.getCreateTimeEnd());
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