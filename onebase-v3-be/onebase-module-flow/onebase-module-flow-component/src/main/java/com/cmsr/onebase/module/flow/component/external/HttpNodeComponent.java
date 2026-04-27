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

import java.util.*;
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
@LiteflowComponent("http")
public class HttpNodeComponent extends SkippableNodeComponent {

    /**
     * 变量替换正则表达式
     * 匹配格式: ${variableName}
     * 示例: https://api.example.com/users/${userId} -> userId 会被匹配
     *
     * Pattern 说明:
     * - \$\{ : 匹配字面量 "${"
     * ([^}]+) : 捕获组 1，匹配一个或多个非 "}" 字符（变量名）
     * \} : 匹配字面量 "}"
     */
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    @Autowired
    private HttpExecuteService httpExecuteService;

    /**
     * HTTP 请求节点处理方法
     * 这是 LiteFlow 组件的核心执行方法，在流程执行到该节点时被调用
     *
     * @throws Exception 请求执行过程中的任何异常
     */
    @Override
    public void process() throws Exception {
        // ========== 步骤 1: 初始化上下文 ==========
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        Long processId = executeContext.getProcessId();
        String traceId = executeContext.getTraceId();
        String executionUuid = executeContext.getExecutionUuid();
        String nodeId = this.getTag();

        log.info("[FLOW-TRACE] HTTP节点开始执行: processId={}, traceId={}, executionUuid={}, nodeId={}",
                processId, traceId, executionUuid, nodeId);
        executeContext.addLog("HTTP 请求节点开始执行");

        VariableContext variableContext = this.getContextBean(VariableContext.class);
        HttpNodeData nodeData = (HttpNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();

        // ========== 步骤 2: 解析循环变量 ==========
        Map<String, Object> expressionContext = VariableProvider.resolveLoopVariables(
                this, inLoopDepth, variableContext.getNodeVariables()
        );

        // ========== 步骤 3: 配置校验（早返回） ==========
        Map<String, Object> connectorConfig = nodeData.getConnectorConfig();
        Map<String, Object> actionConfig = nodeData.getActionConfig();

        if (connectorConfig == null || actionConfig == null) {
            String errorMsg = String.format(
                "HTTP节点配置缺失：connectorConfig=%s, actionConfig=%s, nodeId=%s",
                connectorConfig != null ? "已加载" : "null",
                actionConfig != null ? "已加载" : "null",
                nodeId);
            log.error("[FLOW-TRACE] {}", errorMsg);
            executeContext.addLog(errorMsg);
            throw new IllegalStateException(errorMsg);
        }

        log.debug("[FLOW-TRACE] HTTP节点使用动态加载配置: nodeId={}", nodeId);

        String method = (String) actionConfig.get("method");
        String requestPath = (String) actionConfig.get("url");
        log.debug("[FLOW-TRACE] 从actionConfig获取到: method={}, url={}", method, requestPath);

        Map<String, Object> paramTabs = getNestedMap(actionConfig, "tabs");
        if (paramTabs == null) {
            String errorMsg = String.format(
                "HTTP节点配置错误：缺少请求参数配置。请检查连接器动作配置中的 tabs。nodeId=%s, actionConfig=%s",
                nodeId, actionConfig);
            log.error(errorMsg);
            executeContext.addLog(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        log.debug("[FLOW-TRACE] 使用参数来源: tabs, paramTabs中包含的keys: {}",
                paramTabs.keySet());

        // ========== 步骤 4: 构建请求各部分 ==========
        List<HttpNodeData.Header> headers = buildHeaders(paramTabs, connectorConfig, expressionContext, nodeId);
        String bodyContent = buildRequestBody(paramTabs);
        String finalUrl = buildFullUrl(requestPath, paramTabs, expressionContext, nodeId);
        int timeout = 5000;
        int retryCount = nodeData.getRetry() != null ? nodeData.getRetry() : 0;

        // ========== 步骤 5: 组装 HttpRequest ==========
        HttpRequest request = new HttpRequest();
        request.setProcessId(String.valueOf(processId));
        request.setTraceId(traceId);
        request.setExecutionUuid(executionUuid);
        request.setNodeId(nodeId);
        request.setUrl(finalUrl);
        request.setMethod(method);
        request.setTimeout(timeout);
        request.setRetry(retryCount);

        String resolvedBody = replaceVariables(bodyContent != null ? bodyContent : "", expressionContext);
        request.setBodyContent(resolvedBody);
        request.setHeaders(headers);

        log.debug("[FLOW-TRACE] HTTP请求头: nodeId={}, headerCount={}", nodeId, headers.size());
        log.debug("[FLOW-TRACE] HTTP请求体: nodeId={}, bodyLength={}",
                nodeId, resolvedBody != null ? resolvedBody.length() : 0);

        // ========== 步骤 6: 执行请求 ==========
        executeRequest(request, executeContext, variableContext, nodeId);
    }

    /**
     * 构建请求头列表
     * 从 paramTabs.requestHeaders 获取自定义请求头，并从 connectorConfig 获取认证信息
     *
     * @param paramTabs        请求参数配置
     * @param connectorConfig  连接器配置
     * @param expressionContext 变量替换上下文
     * @param nodeId           节点 ID（用于日志）
     * @return 经过变量替换的请求头列表
     */
    private List<HttpNodeData.Header> buildHeaders(Map<String, Object> paramTabs,
            Map<String, Object> connectorConfig, Map<String, Object> expressionContext, String nodeId) {
        List<HttpNodeData.Header> headers = new ArrayList<>();

        // 从 paramTabs.requestHeaders 获取自定义请求头
        List<?> requestHeadersList = (List<?>) paramTabs.get("requestHeaders");
        if (requestHeadersList != null && !requestHeadersList.isEmpty()) {
            for (Object item : requestHeadersList) {
                if (item instanceof Map<?, ?> headerMap) {
                    HttpNodeData.Header h = new HttpNodeData.Header();
                    h.setKey((String) headerMap.get("key"));
                    Object val = headerMap.get("fieldValue");
                    if (val == null) {
                        val = headerMap.get("value");
                    }
                    h.setValue(val != null ? val.toString() : "");
                    headers.add(h);
                }
            }
        }
        log.debug("[FLOW-TRACE] 构建的请求头数量: nodeId={}, headers={}", nodeId, headers.size());

        // 认证处理 — 从 connectorConfig.properties.headers.Authorization 读取
        Map<String, Object> connectorProps = getNestedMap(connectorConfig, "properties");
        Map<String, Object> headersConfig = connectorProps != null ? getNestedMap(connectorProps, "headers") : null;
        Map<String, Object> authHeaderConfig = headersConfig != null ? getNestedMap(headersConfig, "Authorization") : null;
        if (authHeaderConfig != null) {
            String token = (String) authHeaderConfig.get("value");
            log.debug("[FLOW-TRACE] 从环境配置获取到认证token: nodeId={}, tokenLength={}", nodeId,
                    token != null ? token.length() : 0);
            if (token != null && !token.isEmpty()) {
                HttpNodeData.Header authHeader = new HttpNodeData.Header();
                authHeader.setKey("Authorization");
                authHeader.setValue(token);
                headers.add(authHeader);
            }
        }

        // 变量替换 headers
        List<HttpNodeData.Header> resolvedHeaders = new ArrayList<>();
        for (HttpNodeData.Header header : headers) {
            HttpNodeData.Header resolved = new HttpNodeData.Header();
            resolved.setKey(header.getKey());
            resolved.setValue(replaceVariables(header.getValue(), expressionContext));
            resolvedHeaders.add(resolved);
        }
        return resolvedHeaders;
    }

    /**
     * 构建完整的请求 URL
     * 对 URL 模板进行变量替换，然后替换 pathParams 和拼接 queryParams
     *
     * @param requestPath       原始请求路径模板
     * @param paramTabs         请求参数配置（含 pathParams、queryParams）
     * @param expressionContext  变量替换上下文
     * @param nodeId            节点 ID（用于日志）
     * @return 完整的请求 URL
     */
    private String buildFullUrl(String requestPath, Map<String, Object> paramTabs,
            Map<String, Object> expressionContext, String nodeId) {
        // 变量替换
        String fullUrl = replaceVariables(requestPath != null ? requestPath : "", expressionContext);
        log.debug("[FLOW-TRACE] URL变量替换后: nodeId={}, fullUrl={}", nodeId, fullUrl);

        // pathParams 替换 — 从 paramTabs.pathParams 获取并替换 URL 中的 {paramName}
        Object pathParamsObj = paramTabs.get("pathParams");
        if (pathParamsObj instanceof List<?> pathParamsList) {
            for (Object item : pathParamsList) {
                if (item instanceof Map<?, ?> paramMap) {
                    String paramName = (String) paramMap.get("key");
                    Object paramValue = paramMap.get("fieldValue");
                    if (paramName != null && paramValue != null) {
                        String resolvedParamValue = replaceVariables(paramValue.toString(), expressionContext);
                        fullUrl = fullUrl.replace("{" + paramName + "}", resolvedParamValue);
                    }
                }
            }
        }

        // queryParams 拼接
        String finalUrl = appendQueryParams(fullUrl, paramTabs, expressionContext);
        log.debug("[FLOW-TRACE] 添加queryParams后的最终URL: nodeId={}, finalUrl={}", nodeId, finalUrl);
        return finalUrl;
    }

    /**
     * 执行 HTTP 请求并保存结果
     */
    private void executeRequest(HttpRequest request, ExecuteContext executeContext,
                                VariableContext variableContext, String nodeId) throws Exception {
        Long processId = executeContext.getProcessId();
        try {
            log.info("[FLOW-TRACE] HTTP请求准备: processId={}, nodeId={}, method={}, url={}",
                    processId, nodeId, request.getMethod(), request.getUrl());

            HttpServiceResponse serviceResponse = httpExecuteService.execute(request);

            log.info("[FLOW-TRACE] HTTP响应接收: processId={}, nodeId={}, statusCode={}, duration={}ms",
                    processId, nodeId, serviceResponse.getStatusCode(), serviceResponse.getDuration());

            // rawBody 截断日志
            String rawBody = serviceResponse.getRawBody();
            log.debug("[FLOW-TRACE] HTTP响应体: nodeId={}, rawBodyLength={}", nodeId,
                    rawBody != null ? rawBody.length() : 0);

            // 构建输出
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

            log.debug("[FLOW-TRACE] HTTP节点执行完成: processId={}, nodeId={}", processId, nodeId);
        } catch (Exception e) {
            log.error("[FLOW-TRACE] HTTP节点执行异常: processId={}, nodeId={}, method={}, url={}, error={}",
                    processId, nodeId, request.getMethod(), request.getUrl(), e.getMessage(), e);
            executeContext.addLog(String.format("HTTP 请求执行失败 - 方法: %s, URL: %s, 错误: %s",
                    request.getMethod(), request.getUrl(), e.getMessage()));
            throw e;
        }
    }

    /**
     * 从嵌套 Map 中安全获取子 Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getNestedMap(Map<String, Object> parent, String key) {
        if (parent == null) return null;
        Object value = parent.get(key);
        return value instanceof Map ? (Map<String, Object>) value : null;
    }

    /**
     * 从 request.requestBody 数组构建请求体 JSON
     */
    private String buildRequestBody(Map<String, Object> requestConfig) {
        if (requestConfig == null) return null;
        Object requestBodyObj = requestConfig.get("requestBody");
        if (requestBodyObj == null) return null;

        if (requestBodyObj instanceof String) {
            return (String) requestBodyObj;
        }

        if (requestBodyObj instanceof List<?> bodyList) {
            if (bodyList.isEmpty()) return null;
            // 将 [{key: "paramKey", fieldValue: "val"}, ...] 转换为 JSON 对象
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Object item : bodyList) {
                if (item instanceof Map<?, ?> bodyField) {
                    String key = (String) bodyField.get("key");
                    Object fieldValue = bodyField.get("fieldValue");
                    if (key != null) {
                        if (!first) sb.append(",");
                        sb.append("\"").append(key).append("\":");
                        if (fieldValue instanceof String) {
                            sb.append("\"").append(fieldValue).append("\"");
                        } else {
                            sb.append(fieldValue);
                        }
                        first = false;
                    }
                }
            }
            sb.append("}");
            return sb.toString();
        }

        return null;
    }

    /**
     * 拼接 queryParams 到 URL
     */
    private String appendQueryParams(String url, Map<String, Object> paramTabs,
                                     Map<String, Object> expressionContext) {
        List<String> queryParts = new ArrayList<>();

        // 从 paramTabs.queryParams 获取
        if (paramTabs != null) {
            Object queryParams = paramTabs.get("queryParams");
            if (queryParams instanceof List<?> paramsList) {
                log.debug("[FLOW-TRACE] 处理queryParams: paramsCount={}", paramsList.size());
                collectQueryParams(paramsList, queryParts, expressionContext);
            }
        }

        if (queryParts.isEmpty()) {
            log.debug("[FLOW-TRACE] 无queryParams需要添加");
            return url;
        }

        String separator = url.contains("?") ? "&" : "?";
        String result = url + separator + String.join("&", queryParts);
        log.debug("[FLOW-TRACE] 拼接queryParams后的URL: url={}", result);
        return result;
    }

    private void collectQueryParams(List<?> paramsList, List<String> queryParts,
                                    Map<String, Object> expressionContext) {
        for (Object item : paramsList) {
            if (item instanceof Map<?, ?> paramMap) {
                String paramName = (String) paramMap.get("paramName");
                if (paramName == null) paramName = (String) paramMap.get("key");
                Object paramValue = paramMap.get("fieldValue");
                if (paramValue == null) paramValue = paramMap.get("value");
                if (paramName != null && paramValue != null) {
                    String resolvedValue = replaceVariables(paramValue.toString(), expressionContext);
                    queryParts.add(paramName + "=" + resolvedValue);
                }
            }
        }
    }

    /**
     * 变量替换方法
     * 将模板字符串中的 ${variableName} 占位符替换为实际值
     *
     * <p>工作原理：
     * 1. 使用正则表达式查找所有 ${...} 格式的占位符
     * 2. 对于每个占位符，从上下文中查找对应的变量值
     * 3. 如果变量存在，替换为实际值；否则保持原样
     * 4. 支持任意类型的变量值（通过 toString() 转换）
     *
     * <p>示例：
     * <pre>{@code
     * // 输入
     * template = "https://api.example.com/users/${userId}/posts/${postId}"
     * context = {"userId": "123", "postId": "456"}
     *
     * // 输出
     * "https://api.example.com/users/123/posts/456"
     *
     * // 如果变量不存在，保持原样
     * template = "https://api.example.com/users/${nonExistentVar}"
     * context = {"userId": "123"}
     *
     * // 输出
     * "https://api.example.com/users/${nonExistentVar}"
     * }</pre>
     *
     * @param template 模板字符串，包含 ${variableName} 格式的占位符
     * @param context 变量上下文，包含变量名到值的映射
     * @return 替换后的字符串，如果模板为 null 或空则直接返回
     */
    private String replaceVariables(String template, Map<String, Object> context) {
        // 边界检查：如果模板为 null 或空，直接返回
        if (template == null || template.isEmpty()) {
            return template;
        }

        // 创建正则匹配器，用于查找所有变量占位符
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        // 使用 StringBuilder 存储替换结果
        StringBuilder result = new StringBuilder();

        // 遍历所有匹配的占位符
        while (matcher.find()) {
            // 提取变量名（捕获组 1）
            // 示例: "${userId}" -> "userId"
            String variableName = matcher.group(1);

            // 从上下文中获取变量值
            Object value = context.get(variableName);

            if (value != null) {
                // 变量存在：替换为实际值
                // 使用 Matcher.quoteReplacement() 转义特殊字符，防止被当作正则表达式
                // 示例: "$100.00" 会被正确处理而不是当作正则组引用
                matcher.appendReplacement(result, Matcher.quoteReplacement(value.toString()));
            } else {
                // 变量不存在：保持原样（方便调试）
                // 这样用户可以看到哪些变量没有被正确设置
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group()));
            }
        }

        // 添加最后一次匹配之后的剩余内容
        matcher.appendTail(result);

        return result.toString();
    }
}
