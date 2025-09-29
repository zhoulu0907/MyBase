package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.flow.component.NormalNodeComponent;
import com.cmsr.onebase.module.flow.component.utils.ConditionsProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.Condition;
import com.cmsr.onebase.module.flow.context.condition.RuleItem;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.InsertDataReqDTO;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:24
 */
@Slf4j
@Setter
@LiteflowComponent("dataAdd")
public class DataAddNodeComponent extends NormalNodeComponent {

    @Autowired
    private DataMethodApi dataMethodApi;

    @Autowired
    private DataMethodApiHelper dataMethodApiHelper;

    @Autowired
    private ConditionsProvider conditionsProvider;

    @Override
    public void process() throws Exception {
        log.info("DataAddNodeComponent process - 开始处理节点数据添加操作");
        // 获取上下文和节点数据
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        NodeData nodeData = executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        // 参数校验
        if (MapUtils.isEmpty(nodeData)) {
            log.warn("DataAddNodeComponent process - 节点数据为空，跳过处理");
            return;
        }

        Long entityId = MapUtils.getLong(nodeData, "mainEntityId");
        if (entityId == null) {
            throw new IllegalArgumentException("实体ID不能为空");
        }
        boolean batchType = nodeData.getBooleanValue("batchType", false);
        List<Map<String, Object>> fields = (List<Map<String, Object>>) MapUtils.getObject(nodeData, "fields");
        List<RuleItem> ruleItems = Condition.createRuleItems(fields);
        List<Map<Long, Object>> reqData = new ArrayList<>();
        if (!batchType) {
            ruleItems = conditionsProvider.formatRuleItemsForExpression(this, ruleItems, inLoopDepth);
            ruleItems = conditionsProvider.formatRuleItemsForValue(ruleItems, variableContext);
            Map<Long, Object> data = new HashMap<>();
            for (RuleItem ruleItem : ruleItems) {
                data.put(NumberUtils.toLong(ruleItem.getFieldId()), ruleItem.getValue());
            }
            reqData.add(data);
        } else {
            String dataNodeId = nodeData.getString("dataNodeId");
            int dataSize = variableContext.getVariableSizeByTag(dataNodeId);

            for (int i = 0; i < dataSize; i++) {
                Map<Long, Object> data = new HashMap<>();
                for (RuleItem ruleItem : ruleItems) {
                    ruleItem = conditionsProvider.formatRuleItemForExpression(i, ruleItem);
                    ruleItem = conditionsProvider.formatRuleItemForValue(ruleItem, variableContext);
                    data.put(NumberUtils.toLong(ruleItem.getFieldId()), ruleItem.getValue());
                }
                reqData.add(data);
            }
        }

        if (CollectionUtils.isEmpty(reqData)) {
            return;
        }
        // 执行数据添加操作
        InsertDataReqDTO reqDTO = new InsertDataReqDTO();
        reqDTO.setEntityId(entityId);
        reqDTO.setData(reqData);

        try {
            List<List<EntityFieldDataRespDTO>> respDTOSS = TenantUtils.executeIgnore(() -> dataMethodApi.insertData(reqDTO));
            // 处理响应结果
            processResponse(respDTOSS, variableContext, batchType);
        } catch (Exception e) {
            throw e; // 重新抛出异常，保持原有行为
        }
    }

    /**
     * 处理响应结果
     */
    private void processResponse(List<List<EntityFieldDataRespDTO>> respDTOSS, VariableContext variableContext, boolean batchType) {
        if (CollectionUtils.isEmpty(respDTOSS)) {
            log.warn("DataAddNodeComponent processResponse - 响应结果为空");
            return;
        }
        if (batchType) {
            variableContext.putNodeVariables(this.getTag(), dataMethodApiHelper.convertToListMap(respDTOSS));
        } else {
            variableContext.putNodeVariables(this.getTag(), dataMethodApiHelper.convertToMap(respDTOSS.get(0)));
        }
        log.debug("DataAddNodeComponent processResponse - 响应结果已添加到变量上下文中");
    }

}
