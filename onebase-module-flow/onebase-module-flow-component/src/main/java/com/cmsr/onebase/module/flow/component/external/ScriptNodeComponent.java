package com.cmsr.onebase.module.flow.component.external;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.component.SkippableNodeComponent;
import com.cmsr.onebase.module.flow.component.utils.PropertyDefine;
import com.cmsr.onebase.module.flow.component.utils.SchemaParser;
import com.cmsr.onebase.module.flow.component.utils.VariableProvider;
import com.cmsr.onebase.module.flow.context.ConditionsProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.enums.JdbcTypeEnum;
import com.cmsr.onebase.module.flow.context.express.ExpressionItem;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.ScriptNodeData;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import kong.unirest.core.ContentType;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONObject;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Setter
@LiteflowComponent("javascript")
public class ScriptNodeComponent extends SkippableNodeComponent {
    private static final String INVOKE_SUFFIX_URI = "/api/exec";

    @Value("${liteflow.js-server-address}")
    private String jsServerAddress;

    @Autowired
    private ConditionsProvider conditionsProvider;

    @Override
    public void process() throws Exception {
        // 1. 初始化上下文
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("脚本节点开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        ScriptNodeData nodeData = (ScriptNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        Map<String, Object> expressionContext = VariableProvider.resolveLoopVariables(this, inLoopDepth, variableContext.getNodeVariables());

        // 2. 从空间中读取变量
        List<ConditionItem> conditionItems = nodeData.getInputParameterFields();
        settingToJdbcType(conditionItems);
        List<ExpressionItem> expressionItems = conditionsProvider.formatConditionItemsForValue(conditionItems, expressionContext);

        Map<String, Object> inputData = expressionItems.stream().collect(Collectors.toMap(ExpressionItem::getKey, ExpressionItem::getValue));
        // 3. 执行Http调用
        JsRequest jsRequest = new JsRequest();
        jsRequest.setScript(nodeData.getScript());
        List<PropertyDefine> inputDef = JsonUtils.parseArray(nodeData.getInputSchema(), PropertyDefine.class);
        Map<String, Object> parsedInputParams = SchemaParser.parseBySchemaDef(inputData, inputDef);
        jsRequest.setInputJson(JsonUtils.toJsonString(parsedInputParams));

        String invokeUrl = jsServerAddress + INVOKE_SUFFIX_URI;
        HttpResponse<JsonNode> nodeHttpResponse = Unirest.post(invokeUrl)
                .contentType(ContentType.APPLICATION_JSON)
                .version(HttpClient.Version.HTTP_1_1)
                .body(jsRequest)
                .asJson();

        if (nodeHttpResponse.isSuccess()) {
            JSONObject result = nodeHttpResponse.getBody().getObject().getJSONObject("data");
            List<PropertyDefine> outputDef = JsonUtils.parseArray(nodeData.getOutputSchema(), PropertyDefine.class);
            Map<String, Object> resultMap = result.toMap();
            Map<String, Object> parsedResult = SchemaParser.parseBySchemaDef(resultMap, outputDef);
            executeContext.addLog("脚本节点执行成功，输出: " + JsonUtils.toJsonString(parsedResult));
            variableContext.putNodeVariables(this.getTag(), parsedResult);
        } else {
            int httpStatus = nodeHttpResponse.getStatus();
            String errMsg = nodeHttpResponse.getBody().getObject().getString("msg");
            executeContext.addLog("脚本节点执行失败, 响应码: " + httpStatus + ", 异常信息: " + errMsg);
            throw new RuntimeException("脚本节点执行失败");
        }
    }

    private void settingToJdbcType(List<ConditionItem> conditionItems) {
        for (ConditionItem conditionItem : conditionItems) {
            //String fieldType = conditionItem.getFieldType();
            conditionItem.setJdbcType(JdbcTypeEnum.VARCHAR.getCode());
        }
    }


    @Data
    public static class JsRequest {
        private String inputJson;
        private String script;
    }


}
