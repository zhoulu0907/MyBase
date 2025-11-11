package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.flow.component.SkippableNodeComponent;
import com.cmsr.onebase.module.flow.component.utils.VariableProvider;
import com.cmsr.onebase.module.flow.context.ConditionsProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.express.ExpressionItem;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.DataAddNodeData;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.InsertDataReqDTO;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
public class DataAddNodeComponent extends SkippableNodeComponent {

    @Autowired
    private DataMethodApi dataMethodApi;

    @Autowired
    private ConditionsProvider conditionsProvider;

    @Override
    public void process() throws Exception {
        // 获取上下文和节点数据
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("数据新增节点开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        DataAddNodeData nodeData = (DataAddNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        Map<String, Object> expressionContext = VariableProvider.resolveLoopVariables(this, inLoopDepth, variableContext.getNodeVariables());

        // 执行数据添加操作
        InsertDataReqDTO reqDTO = new InsertDataReqDTO();
        reqDTO.setTraceId(executeContext.getTraceId());
        //
        if (StringUtils.equalsIgnoreCase("mainEntity", nodeData.getAddType())) {
            reqDTO.setEntityId(nodeData.getMainEntityId());
        } else if (StringUtils.equalsIgnoreCase("subEntity", nodeData.getAddType())) {
            reqDTO.setEntityId(nodeData.getSubEntityId());
        } else {
            throw new IllegalArgumentException("数据添加addType类型错误: " + nodeData.getAddType());
        }
        boolean batchType = nodeData.getBatchType();
        List<ConditionItem> conditionItems = nodeData.getFields();
        List<Map<Long, Object>> reqData;
        if (batchType) {
            reqData = buildBatchReqData(nodeData, variableContext, conditionItems);
        } else {
            reqData = buildSingleReqData(conditionItems, expressionContext);
        }
        if (CollectionUtils.isEmpty(reqData)) {
            executeContext.addLog("数据添加节点结束执行, 未包含请求数据");
            return;
        }
        reqDTO.setData(reqData);
        try {
            executeContext.addLog("数据添加节点开始执行");
            List<List<EntityFieldDataRespDTO>> respDTOSS = TenantUtils.executeIgnore(() -> dataMethodApi.insertData(reqDTO));
            executeContext.addLog("数据添加节点结束执行, 响应结果数量: " + respDTOSS.size());
            // 处理响应结果
            processResponse(respDTOSS, variableContext, batchType);
        } catch (Exception e) {
            throw e; // 重新抛出异常，保持原有行为
        }
    }

    private List<Map<Long, Object>> buildSingleReqData(List<ConditionItem> conditionItems, Map<String, Object> expressionContext) {
        List<Map<Long, Object>> reqData = new ArrayList<>();
        List<ExpressionItem> expressionItems = conditionsProvider.formatConditionItemsForValue(conditionItems, expressionContext);
        Map<Long, Object> data = new HashMap<>();
        for (ExpressionItem expressionItem : expressionItems) {
            data.put(NumberUtils.toLong(expressionItem.getKey().toString()), expressionItem.getValue());
        }
        reqData.add(data);
        return reqData;
    }

    private List<Map<Long, Object>> buildBatchReqData(DataAddNodeData nodeData, VariableContext variableContext, List<ConditionItem> conditionItems) {
        String dataNodeId = nodeData.getDataNodeId();
        List<Map<String, Object>> dataList = variableContext.getListVariableByTag(dataNodeId);
        List<Map<Long, Object>> reqData = new ArrayList<>();
        for (Map<String, Object> dataMap : dataList) {
            Map<Long, Object> data = new HashMap<>();
            for (ConditionItem conditionItem : conditionItems) {
                ExpressionItem expressionItem = conditionsProvider.formatConditionItemForValue(conditionItem, dataMap);
                data.put(NumberUtils.toLong(expressionItem.getKey().toString()), expressionItem.getValue());
            }
            reqData.add(data);
        }
        return reqData;
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
            variableContext.putNodeVariables(this.getTag(), DataMethodApiHelper.convertToListMap(respDTOSS));
        } else {
            variableContext.putNodeVariables(this.getTag(), DataMethodApiHelper.convertToMap(respDTOSS.get(0)));
        }
        log.debug("DataAddNodeComponent processResponse - 响应结果已添加到变量上下文中");
    }

}
