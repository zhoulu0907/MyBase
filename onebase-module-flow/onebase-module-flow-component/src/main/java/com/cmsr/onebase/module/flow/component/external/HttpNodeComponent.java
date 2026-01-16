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
     * 执行流程：
     * 1. 初始化上下文 - 获取执行上下文、变量上下文和节点数据
     * 2. 解析循环变量 - 处理在循环中的变量引用（如 item.index, item.value）
     * 3. 变量替换 - 将 URL、Body、Headers 中的占位符替换为实际值
     * 4. 构建请求 - 创建 HttpRequest 对象并设置所有参数
     * 5. 执行请求 - 调用 HttpExecuteService 发送 HTTP 请求
     * 6. 构建输出 - 将响应数据转换为标准输出格式
     * 7. 保存结果 - 将输出保存到变量上下文供后续节点使用
     *
     * @throws Exception 请求执行过程中的任何异常
     */
    @Override
    public void process() throws Exception {
        // ========== 步骤 1: 初始化上下文 ==========
        // 获取执行上下文，用于记录日志和获取节点数据
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("HTTP 请求节点开始执行");

        // 获取变量上下文，用于存储和获取变量
        VariableContext variableContext = this.getContextBean(VariableContext.class);

        // 获取当前节点的配置数据（从流程定义中读取）
        HttpNodeData nodeData = (HttpNodeData) executeContext.getNodeData(this.getTag());

        // 获取循环深度信息，用于处理循环中的变量
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();

        // ========== 步骤 2: 解析循环变量 ==========
        // 如果节点在循环中，需要解析循环变量（如 forEach 中的 item）
        // VariableProvider 会将循环变量合并到表达式上下文中
        Map<String, Object> expressionContext = VariableProvider.resolveLoopVariables(
                this, inLoopDepth, variableContext.getNodeVariables()
        );

        // ========== 步骤 3: 获取配置（支持动态加载和内联配置） ==========
        Map<String, Object> connectorConfig;
        Map<String, Object> actionConfig;

        // 如果有connectorUuid/httpUuid，使用动态加载的配置
        if (nodeData.getConnectorConfig() != null && nodeData.getActionConfig() != null) {
            connectorConfig = nodeData.getConnectorConfig();
            actionConfig = nodeData.getActionConfig();
        } else {
            // 否则使用内联配置（向后兼容）
            connectorConfig = new HashMap<>();
            actionConfig = new HashMap<>();
            actionConfig.put("requestMethod", nodeData.getMethod());
            actionConfig.put("requestPath", nodeData.getUrl());
            actionConfig.put("requestHeaders", nodeData.getHeaders());
            actionConfig.put("requestBodyTemplate", nodeData.getBodyContent());
            actionConfig.put("timeout", nodeData.getTimeout());
            actionConfig.put("retryCount", nodeData.getRetry());
        }

        // ========== 步骤 4: 构建完整URL ==========
        String baseUrl = (String) connectorConfig.get("baseUrl");
        String requestPath = (String) actionConfig.get("requestPath");
        String resolvedPath = replaceVariables(requestPath, expressionContext);
        String fullUrl = baseUrl != null ? baseUrl + resolvedPath : resolvedPath;

        // ========== 步骤 5: 构建请求对象 ==========
        HttpRequest request = new HttpRequest();
        request.setUrl(fullUrl);
        request.setMethod((String) actionConfig.get("requestMethod"));

        // 处理请求头
        @SuppressWarnings("unchecked")
        java.util.List<HttpNodeData.Header> headersFromConfig = (java.util.List<HttpNodeData.Header>) actionConfig.get("requestHeaders");
        java.util.List<HttpNodeData.Header> headers = headersFromConfig != null ? headersFromConfig : nodeData.getHeaders();
        if (headers != null) {
            for (HttpNodeData.Header header : headers) {
                String resolvedValue = replaceVariables(header.getValue(), expressionContext);
                header.setValue(resolvedValue);
            }
        }
        request.setHeaders(headers);

        // 处理请求体
        String requestBodyTemplate = (String) actionConfig.get("requestBodyTemplate");
        String resolvedBodyContent = replaceVariables(requestBodyTemplate != null ? requestBodyTemplate : nodeData.getBodyContent(), expressionContext);
        request.setBodyContent(resolvedBodyContent);

        // 处理超时和重试
        Integer timeout = (Integer) actionConfig.get("timeout");
        request.setTimeout(timeout != null ? timeout : (nodeData.getTimeout() != null ? nodeData.getTimeout() : 5000));

        Integer retryCount = (Integer) actionConfig.get("retryCount");
        request.setRetry(retryCount != null ? retryCount : (nodeData.getRetry() != null ? nodeData.getRetry() : 0));

        // ========== 步骤 5: 执行 HTTP 请求 ==========
        // 调用 HttpExecuteService 执行实际的 HTTP 请求
        // 该服务会处理：
        // - SSRF 防护检查
        // - 连接管理和超时控制
        // - 重试机制（指数退避）
        // - 响应解析和错误处理
        HttpServiceResponse serviceResponse = httpExecuteService.execute(request);

        // ========== 步骤 6: 构建输出 ==========
        // 将 HTTP 响应转换为标准输出格式，供后续节点使用
        // 输出结构严格按照规范定义：
        Map<String, Object> output = new HashMap<>();
        output.put("statusCode", serviceResponse.getStatusCode());      // HTTP 状态码 (200, 404, 500 等)
        output.put("headers", serviceResponse.getHeaders());            // 响应头 (Map<String, List<String>>)
        output.put("body", serviceResponse.getBody());                  // 响应体（解析后的 JSON 对象或原始字符串）
        output.put("rawBody", serviceResponse.getRawBody());            // 原始响应体（始终为字符串）
        output.put("duration", serviceResponse.getDuration());          // 请求耗时（毫秒）

        // ========== 步骤 7: 记录日志并保存结果 ==========
        // 记录执行日志
        executeContext.addLog(String.format(
                "HTTP 请求执行成功 - 方法: %s, URL: %s, 状态码: %d, 耗时: %dms",
                request.getMethod(), request.getUrl(),
                serviceResponse.getStatusCode(), serviceResponse.getDuration()
        ));

        // 将输出保存到变量上下文，使用节点标签作为键
        // 后续节点可以通过 ${节点标签.statusCode} 等方式访问响应数据
        variableContext.putNodeVariables(this.getTag(), output);
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
        // 使用 StringBuffer 存储替换结果（比 String 更高效，支持频繁修改）
        StringBuffer result = new StringBuffer();

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
