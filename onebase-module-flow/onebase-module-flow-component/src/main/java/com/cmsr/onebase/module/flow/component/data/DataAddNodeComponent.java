package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.flow.component.SkippableNodeComponent;
import com.cmsr.onebase.module.flow.component.utils.VariableProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.express.ExpressionItem;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.DataAddNodeData;
import com.cmsr.onebase.module.flow.context.provider.ConditionsProvider;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticMergeConditionVO;
import com.mybatisflex.core.tenant.TenantManager;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
    private SemanticDynamicDataApi semanticDynamicDataApi;

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
        SemanticMergeConditionVO reqDTO = new SemanticMergeConditionVO();
        reqDTO.setTraceId(executeContext.getTraceId());
        reqDTO.setTableName(nodeData.resolveTargetTableName());
        boolean batchType = nodeData.getBatchType();
        List<ConditionItem> conditionItems = nodeData.getFields();
        List<Map<String, Object>> reqDataList;
        if (batchType) {
            reqDataList = buildBatchReqData(nodeData, variableContext, conditionItems);
        } else {
            reqDataList = buildSingleReqData(conditionItems, expressionContext);
        }
        if (CollectionUtils.isEmpty(reqDataList)) {
            executeContext.addLog("数据添加节点结束执行, 未包含请求数据");
            return;
        }
        List<SemanticEntityValueDTO> respDTOSS = new ArrayList<SemanticEntityValueDTO>();
        try {
            executeContext.addLog("数据添加节点开始执行");
            for (Map<String, Object> reqData : reqDataList) {
                reqDTO.setData(reqData);
                SemanticEntityValueDTO respDTO = TenantManager.withoutTenantCondition(() -> ApplicationManager.withApplicationIdAndVersionTag(
                        executeContext.getApplicationId(),
                        executeContext.getVersionTag(),
                        () -> semanticDynamicDataApi.insertData(reqDTO)));
                respDTOSS.add(respDTO);
            }
            executeContext.addLog("数据添加节点结束执行, 响应结果数量: " + respDTOSS.size());
            // 处理响应结果
            processResponse(respDTOSS, variableContext, batchType);
        } catch (Exception e) {
            throw e; // 重新抛出异常，保持原有行为
        }
    }

    private List<Map<String, Object>> buildSingleReqData(List<ConditionItem> conditionItems, Map<String, Object> expressionContext) {
        List<Map<String, Object>> reqData = new ArrayList<>();
        List<ExpressionItem> expressionItems = conditionsProvider.formatConditionItemsForValue(conditionItems, expressionContext);
        Map<String, Object> data = new HashMap<>();
        for (ExpressionItem expressionItem : expressionItems) {
            data.put(expressionItem.getFieldKey(), expressionItem.getFieldValue());
        }
        reqData.add(data);
        return reqData;
    }

    private List<Map<String, Object>> buildBatchReqData(DataAddNodeData nodeData, VariableContext variableContext, List<ConditionItem> conditionItems) {
        String dataNodeId = nodeData.getDataNodeId();
        List<Map<String, Object>> dataList = variableContext.getListVariableByTag(dataNodeId);
        List<Map<String, Object>> reqData = new ArrayList<>();
        for (Map<String, Object> dataMap : dataList) {
            Map<String, Object> data = new HashMap<>();
            for (ConditionItem conditionItem : conditionItems) {
                ExpressionItem expressionItem = conditionsProvider.formatConditionItemForValue(conditionItem, dataMap);
                data.put(expressionItem.getFieldKey(), expressionItem.getFieldValue());
            }
            reqData.add(data);
        }
        return reqData;
    }

    /**
     * 处理响应结果
     */
    private void processResponse(List<SemanticEntityValueDTO> respDTOSS, VariableContext variableContext, boolean batchType) {
        if (CollectionUtils.isEmpty(respDTOSS)) {
            log.warn("DataAddNodeComponent processResponse - 响应结果为空");
            return;
        }
        if (batchType) {
            List<Map<String, Object>> result = DataMethodApiHelper.convertToListMap(respDTOSS);
            variableContext.putNodeVariables(this.getTag(), result);
        } else {
            Map<String, Object> result = DataMethodApiHelper.convertToMap(respDTOSS.get(0));
            variableContext.putNodeVariables(this.getTag(), result);
        }
        log.debug("DataAddNodeComponent processResponse - 响应结果已添加到变量上下文中");
    }

}
