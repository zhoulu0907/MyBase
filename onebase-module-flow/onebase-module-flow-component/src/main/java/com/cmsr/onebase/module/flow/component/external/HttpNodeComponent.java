package com.cmsr.onebase.module.flow.component.external;

import com.cmsr.onebase.module.flow.component.SkippableNodeComponent;
import com.cmsr.onebase.module.flow.component.external.service.HttpExecuteService;
import com.cmsr.onebase.module.flow.component.external.service.HttpRequest;
import com.cmsr.onebase.module.flow.component.external.service.HttpServiceResponse;
import com.cmsr.onebase.module.flow.component.utils.VariableProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.HttpNodeData;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTTP 请求节点组件
 * 用于在流程中执行 HTTP 请求，支持变量替换和动态配置
 *
 * <p>核心功能：
 * <ul>
 *   <li>支持 GET/POST/PUT/DELETE/PATCH 方法</li>
 *   <li>支持 URL、Header、Body 中的变量替换</li>
 *   <li>SSRF 防护（禁止访问内网地址）</li>
 *   <li>智能响应解析（自动解析 JSON）</li>
 *   <li>完整的输出结构（statusCode/headers/body/rawBody/duration）</li>
 * </ul>
 *
 * @author zhoulu
 * @since 2026-01-15
 */
@Slf4j
@Setter
@LiteflowComponent("api_http")
public class HttpNodeComponent extends SkippableNodeComponent {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    @Autowired
    private HttpExecuteService httpExecuteService;

    @Override
    public void process() throws Exception {
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("HTTP 请求节点开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        HttpNodeData nodeData = (HttpNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        Map<String, Object> expressionContext = VariableProvider.resolveLoopVariables(
                this, inLoopDepth, variableContext.getNodeVariables()
        );

        String resolvedUrl = replaceVariables(nodeData.getUrl(), expressionContext);
        String resolvedBodyContent = replaceVariables(nodeData.getBodyContent(), expressionContext);

        if (nodeData.getHeaders() != null) {
            for (HttpNodeData.Header header : nodeData.getHeaders()) {
                header.setValue(replaceVariables(header.getValue(), expressionContext));
            }
        }

        HttpRequest request = new HttpRequest();
        request.setUrl(resolvedUrl);
        request.setMethod(nodeData.getMethod());
        request.setHeaders(nodeData.getHeaders());
        request.setBodyType(nodeData.getBodyType());
        request.setBodyContent(resolvedBodyContent);
        request.setTimeout(nodeData.getTimeout() != null ? nodeData.getTimeout() : 5000);
        request.setRetry(nodeData.getRetry() != null ? nodeData.getRetry() : 0);

        HttpServiceResponse serviceResponse = httpExecuteService.execute(request);

        Map<String, Object> output = new HashMap<>();
        output.put("statusCode", serviceResponse.getStatusCode());
        output.put("headers", serviceResponse.getHeaders());
        output.put("body", serviceResponse.getBody());
        output.put("rawBody", serviceResponse.getRawBody());
        output.put("duration", serviceResponse.getDuration());

        executeContext.addLog(String.format(
                "HTTP 请求执行成功 - 方法: %s, URL: %s, 状态码: %d, 耗时: %dms",
                request.getMethod(), request.getUrl(),
                serviceResponse.getStatusCode(), serviceResponse.getDuration()
        ));

        variableContext.putNodeVariables(this.getTag(), output);
    }

    private String replaceVariables(String template, Map<String, Object> context) {
        if (template == null || template.isEmpty()) {
            return template;
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object value = context.get(variableName);
            if (value != null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(value.toString()));
            } else {
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group()));
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }
}
