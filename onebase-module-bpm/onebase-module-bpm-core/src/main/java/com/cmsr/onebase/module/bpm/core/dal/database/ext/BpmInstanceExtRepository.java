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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

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
        if (StringUtils.isNotBlank(reqVO.getKeyword())) {
            ConfigStore orCondition = new DefaultConfigStore();
            orCondition.or(Compare.LIKE, "t3.bpm_title", reqVO.getKeyword());
            orCondition.or(Compare.LIKE, "t3.initiator_name", reqVO.getKeyword());
            orCondition.or(Compare.LIKE, "t3.form_summary", reqVO.getKeyword());
            condition.and(orCondition);
        }

        if (StringUtils.isNotBlank(reqVO.getBusinessId())) {
            condition.and(Compare.EQUAL, "t.binding_view_id", reqVO.getBusinessId());
        }

        // 流程状态条件（支持多个值）
        List<String> flowStatusList = reqVO.getFlowStatusList();
        if (CollectionUtils.isNotEmpty(flowStatusList)) {
            if (flowStatusList.size() == 1) {
                condition.and(Compare.EQUAL, "t.flow_status", flowStatusList.get(0));
            } else {
                condition.and(Compare.IN, "t.flow_status", flowStatusList);
            }
        }

        // 节点编码条件（支持多个值）
        List<String> nodeCodeList = reqVO.getNodeCodeList();
        if (CollectionUtils.isNotEmpty(nodeCodeList)) {
            if (nodeCodeList.size() == 1) {
                condition.and(Compare.EQUAL, "t.node_code", nodeCodeList.get(0));
            } else {
                condition.and(Compare.IN, "t.node_code", nodeCodeList);
            }
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