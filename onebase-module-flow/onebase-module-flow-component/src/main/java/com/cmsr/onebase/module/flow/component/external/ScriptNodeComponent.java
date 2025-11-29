package com.cmsr.onebase.module.flow.component.external;

import com.cmsr.onebase.module.flow.component.SkippableNodeComponent;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.graph.nodes.ScriptNodeData;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import kong.unirest.core.ContentType;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONObject;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.net.http.HttpClient;

@Slf4j
@Setter
@LiteflowComponent("javascript")
public class ScriptNodeComponent extends SkippableNodeComponent {
    private static final String INVOKE_SUFFIX_URI = "/api/exec";

    @Value("${liteflow.js-server-address}")
    private String jsServerAddress;

    @Override
    public void process() throws Exception {
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("脚本节点开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        ScriptNodeData nodeData = (ScriptNodeData) executeContext.getNodeData(this.getTag());

        String invokeUrl = jsServerAddress + INVOKE_SUFFIX_URI;
        HttpResponse<JsonNode> nodeHttpResponse = Unirest.post(invokeUrl)
                .contentType(ContentType.APPLICATION_JSON)
                .version(HttpClient.Version.HTTP_1_1)
                .asJson();

        if (nodeHttpResponse.isSuccess()) {
            System.out.println("node服务器调用成功");
            JSONObject result = nodeHttpResponse.getBody().getObject().getJSONObject("data");
        } else {
            int httpStatus = nodeHttpResponse.getStatus();
            String errMsg = nodeHttpResponse.getBody().getObject().getString("msg");
            executeContext.addLog("脚本节点执行失败, 响应码: " + httpStatus + ", 异常信息: " + errMsg);
        }
        // TODO:
    }
}
