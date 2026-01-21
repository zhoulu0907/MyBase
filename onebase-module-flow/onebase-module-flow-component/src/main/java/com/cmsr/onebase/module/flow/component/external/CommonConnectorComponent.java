package com.cmsr.onebase.module.flow.component.external;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.component.SkippableNodeComponent;
import com.cmsr.onebase.module.flow.component.utils.VariableProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.express.ExpressionItem;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.CommonNodeData;
import com.cmsr.onebase.module.flow.context.provider.FlowConditionsProvider;
import com.cmsr.onebase.module.flow.component.external.connector.ConnectorExecutor;
import com.cmsr.onebase.module.flow.component.external.connector.ConnectorRegistry;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通用连接器组件
 * 基于双Map架构，支持动态配置的连接器节点
 * 参考 ScriptNodeComponent 的简洁实现风格
 *
 * @author zhoulu
 * @since 2025-01-10
 */
@Slf4j
@Setter
@LiteflowComponent("common")
public class CommonConnectorComponent extends SkippableNodeComponent {

    @Autowired
    private FlowConditionsProvider flowConditionsProvider;

    @Autowired
    private ConnectorRegistry connectorRegistry;

    @Override
    public void process() throws Exception {
        // 1. 初始化上下文
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("通用连接器节点开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        CommonNodeData nodeData = (CommonNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        Map<String, Object> expressionContext = VariableProvider.resolveLoopVariables(this, inLoopDepth, variableContext.getNodeVariables());

        // 2. 准备输入参数
        List<ConditionItem> conditionItems = extractConditionItems(nodeData);
        if (conditionItems == null) {
            executeContext.addLog("通用连接器节点执行失败，未找到输入参数");
            throw new RuntimeException("通用连接器节点执行失败，未找到输入参数");
        }

        setJdbcType(conditionItems);
        List<ExpressionItem> expressionItems = flowConditionsProvider.formatConditionItemsForValue(conditionItems, expressionContext);
        Map<String, Object> inputData = expressionItems.stream()
                .collect(Collectors.toMap(ExpressionItem::getFieldKey, ExpressionItem::getFieldValue));

        // 3. 合并配置并执行连接器
        String connectorCode = nodeData.getConnectorCode();
        String actionType = nodeData.getActionType();

        Map<String, Object> mergedConfig = new HashMap<>();
        mergedConfig.putAll(nodeData.getConnectorConfig());
        mergedConfig.putAll(nodeData.getActionConfig());
        mergedConfig.put("inputData", inputData);

        // 4. 执行连接器
        Map<String, Object> result = executeConnector(connectorCode, actionType, mergedConfig);

        executeContext.addLog("通用连接器节点执行成功，输出: " + JsonUtils.toJsonString(result));
        variableContext.putNodeVariables(this.getTag(), result);
    }

    /**
     * 从节点数据中提取输入参数
     */
    private List<ConditionItem> extractConditionItems(CommonNodeData nodeData) {
        List<ConditionItem> conditionItems = (List<ConditionItem>) nodeData.getComponentContext().get("inputParameters");
        if (conditionItems == null) {
            conditionItems = (List<ConditionItem>) nodeData.getActionConfig().get("inputParameters");
        }
        return conditionItems;
    }

    /**
     * 执行连接器（动态注册版 - 消除硬编码）
     * 新增连接器无需修改此代码
     */
    private Map<String, Object> executeConnector(String connectorCode, String actionType, Map<String, Object> config) throws Exception {
        // 动态获取连接器，消除硬编码
        ConnectorExecutor connector = connectorRegistry.getConnector(connectorCode);
        return connector.execute(actionType, config);
    }

    /**
     * 设置字段类型为 TEXT
     */
    private void setJdbcType(List<ConditionItem> conditionItems) {
        for (ConditionItem conditionItem : conditionItems) {
            conditionItem.setFieldTypeEnum(SemanticFieldTypeEnum.TEXT);
        }
    }
}