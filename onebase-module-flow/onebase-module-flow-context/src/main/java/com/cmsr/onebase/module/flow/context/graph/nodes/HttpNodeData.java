package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.NodeType;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * HTTP 请求节点数据模型
 *
 * <p>该类定义了 HTTP 请求节点的配置数据，用于在流程中调用外部 API。
 * 使用 @NodeType("api_http") 注解注册为 HTTP 节点类型，与 HttpNodeComponent 配合使用。
 *
 * <p>节点注册流程：
 * <ol>
 *   <li>在流程设计器中创建 HTTP 节点（nodeCode = "api_http"）</li>
 *   <li>配置节点的各项参数（URL、方法、头、体等）</li>
 *   <li>流程执行时，HttpNodeComponent 读取此配置</li>
 *   <li>执行 HTTP 请求并将结果存储到变量上下文</li>
 * </ol>
 *
 * <p>变量替换支持：
 * - url: 支持 ${variableName} 格式的变量替换
 * - headers.value: 支持 ${variableName} 格式的变量替换
 * - bodyContent: 支持 ${variableName} 格式的变量替换
 *
 * <p>配置示例：
 * <pre>{@code
 * {
 *   "nodeCode": "api_http",
 *   "url": "https://api.example.com/users/${userId}",
 *   "method": "GET",
 *   "headers": [
 *     {"key": "Authorization", "value": "Bearer ${token}"}
 *   ],
 *   "timeout": 5000,
 *   "retry": 2
 * }
 * }</pre>
 *
 * @author zhoulu
 * @since 2026-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NodeType("api_http")
public class HttpNodeData extends NodeData implements Serializable {

    /**
     * 请求 URL
     *
     * <p>HTTP 请求的目标地址，支持 http 和 https 协议。
     *
     * <p>变量替换：
     * 支持 ${variableName} 格式的变量占位符，在节点执行时被替换为实际值。
     *
     * <p>示例：
     * <pre>{@code
     * // 静态 URL
     * url = "https://api.example.com/users"
     *
     * // 带变量的 URL
     * url = "https://api.example.com/users/${userId}"
     * // 如果 userId = "123"，替换后为 "https://api.example.com/users/123"
     *
     * // 多个变量
     * url = "${protocol}://api.example.com/users/${userId}/posts/${postId}"
     * // 如果 protocol="https", userId="123", postId="456"
     * // 替换后为 "https://api.example.com/users/123/posts/456"
     * }</pre>
     *
     * <p>安全限制（SSRF 防护）：
     * - 禁止访问内网地址（10.x.x.x, 192.168.x.x, 172.16-31.x.x）
     * - 禁止访问 localhost（127.0.0.1, ::1, localhost）
     * - 只允许访问公网地址
     */
    private String url;

    /**
     * HTTP 请求方法
     *
     * <p>指定 HTTP 请求的方法类型。
     *
     * <p>支持的值：
     * <ul>
     *   <li>GET: 获取资源（通常用于查询数据）</li>
     *   <li>POST: 创建资源（通常用于提交数据）</li>
     *   <li>PUT: 更新资源（全量更新）</li>
     *   <li>DELETE: 删除资源</li>
     *   <li>PATCH: 更新资源（部分更新）</li>
     * </ul>
     *
     * <p>示例：
     * <pre>{@code
     * // 查询用户信息
     * method = "GET"
     * url = "https://api.example.com/users/${userId}"
     *
     * // 创建用户
     * method = "POST"
     * url = "https://api.example.com/users"
     * bodyContent = '{"name":"John","email":"john@example.com"}'
     *
     * // 更新用户
     * method = "PUT"
     * url = "https://api.example.com/users/${userId}"
     * bodyContent = '{"name":"John Updated"}'
     *
     * // 删除用户
     * method = "DELETE"
     * url = "https://api.example.com/users/${userId}"
     * }</pre>
     *
     * <p>注意：方法不区分大小写，会被统一转换为大写处理。
     */
    private String method;

    /**
     * 请求头列表
     *
     * <p>用于设置 HTTP 请求头，可以包含多个请求头。
     *
     * <p>常见的请求头用途：
     * <ul>
     *   <li>Content-Type: 指定请求体的内容类型</li>
     *   <li>Authorization: 携带认证信息（如 Bearer token）</li>
     *   <li>Accept: 指定期望的响应类型</li>
     *   <li>User-Agent: 标识客户端类型</li>
     *   <li>Custom-Header: 自定义业务头</li>
     * </ul>
     *
     * <p>变量替换：
     * headers 中每个 header 的 value 字段都支持变量替换。
     *
     * <p>示例：
     * <pre>{@code
     * headers = [
     *   // 设置 Content-Type
     *   Header(key = "Content-Type", value = "application/json"),
     *
     *   // 携带认证 token
     *   Header(key = "Authorization", value = "Bearer ${token}"),
     *
     *   // 自定义请求头
     *   Header(key = "X-Tenant-Id", value = "${tenantId}"),
     *
     *   // 设置 API 版本
     *   Header(key = "API-Version", value = "v1")
     * ]
     * }</pre>
     *
     * <p>注意：某些请求头（如 Host、Content-Length）会由 HttpClient 自动设置，
     * 不需要手动配置。
     */
    private List<Header> headers;

    /**
     * 请求体类型
     *
     * <p>标识请求体的格式类型，用于指导请求体的编码和处理。
     *
     * <p>支持的值：
     * <ul>
     *   <li>JSON: JSON 格式（application/json）</li>
     *   <li>FORM: 表单格式（application/x-www-form-urlencoded）</li>
     *   <li>RAW: 原始文本（text/plain 或其他）</li>
     * </ul>
     *
     * <p>示例：
     * <pre>{@code
     * // JSON 格式
     * bodyType = "JSON"
     * bodyContent = '{"name":"John","age":30}'
     *
     * // 表单格式
     * bodyType = "FORM"
     * bodyContent = "name=John&age=30"
     *
     * // 原始文本
     * bodyType = "RAW"
     * bodyContent = "Any raw text content"
     * }</pre>
     *
     * <p>注意：当前版本中此字段为预留标识，实际请求体直接使用 bodyContent 的字符串值。
     * 未来可能根据此字段自动设置 Content-Type 头或进行请求体编码。
     */
    private String bodyType;

    /**
     * 请求体内容
     *
     * <p>包含要发送的 HTTP 请求体数据。
     *
     * <p>变量替换：
     * 支持 ${variableName} 格式的变量占位符。
     *
     * <p>使用场景：
     * <ul>
     *   <li>POST/PUT/PATCH: 发送 JSON 或表单数据</li>
     *   <li>GET/DELETE: 通常为空或不使用</li>
     * </ul>
     *
     * <p>示例：
     * <pre>{@code
     * // JSON 格式请求体
     * bodyContent = '{"name":"${userName}","age":${userAge}}'
     * // 如果 userName="John", userAge=30
     * // 结果: {"name":"John","age":30}
     *
     * // 表单格式请求体
     * bodyContent = "name=${userName}&age=${userAge}"
     * // 如果 userName="John", userAge=30
     * // 结果: name=John&age=30
     *
     * // 复杂嵌套 JSON
     * bodyContent = '{"user":{"name":"${userName}"},"settings":{"theme":"${theme}"}}'
     * }</pre>
     *
     * <p>注意：
     * - 变量替换发生在节点执行时，不是在保存配置时
     * - 确保请求体格式与 Content-Type 头一致
     * - 对于复杂的 JSON，注意转义字符的正确使用
     */
    private String bodyContent;

    /**
     * 请求超时时间（毫秒）
     *
     * <p>设置 HTTP 请求的超时时间，超过此时间请求将被中断。
     *
     * <p>默认值：5000 毫秒（5 秒）
     *
     * <p>建议值：
     * <ul>
     *   <li>快速 API: 3000-5000ms</li>
     *   <li>普通查询: 5000-10000ms</li>
     *   <li>文件上传: 30000-60000ms</li>
     *   <li>批量操作: 60000-120000ms</li>
     * </ul>
     *
     * <p>示例：
     * <pre>{@code
     * // 默认超时（5 秒）
     * timeout = null 或不设置
     *
     * // 快速 API（3 秒）
     * timeout = 3000
     *
     * // 文件上传（60 秒）
     * timeout = 60000
     * }</pre>
     *
     * <p>超时行为：
     * - 如果配置了重试（retry > 0），超时会触发重试机制
     * - 如果未配置重试或重试次数用完，抛出 HttpTimeoutException
     * - 超时时间包括：连接时间 + 读取时间
     */
    private Integer timeout;

    /**
     * 重试次数
     *
     * <p>当请求失败时，自动重试的次数。
     *
     * <p>默认值：0（不重试）
     *
     * <p>重试触发条件：
     * <ul>
     *   <li>网络异常（连接超时、读取超时、连接拒绝）</li>
     *   <li>HTTP 5xx 服务器错误（可能是临时故障）</li>
     *   <li>HTTP 429 请求过于频繁（需要等待后重试）</li>
     * </ul>
     *
     * <p>重试策略（指数退避）：
     * <ul>
     *   <li>第 1 次重试: 等待 100ms</li>
     *   <li>第 2 次重试: 等待 200ms</li>
     *   <li>第 3 次重试: 等待 400ms</li>
     *   <li>最大退避时间: 5000ms</li>
     * </ul>
     *
     * <p>示例：
     * <pre>{@code
     * // 不重试
     * retry = 0 或不设置
     *
     * // 重试 1 次（总共尝试 2 次）
     * retry = 1
     *
     * // 重试 3 次（总共尝试 4 次）
     * retry = 3
     * }</pre>
     *
     * <p>注意事项：
     * <ul>
     *   <li>4xx 客户端错误（除了 429）不会重试</li>
     *   <li>每次重试都会增加总耗时</li>
     *   <li>重试会消耗服务端资源，合理设置</li>
     *   <li>对于幂等操作（GET、PUT、DELETE），可以适当重试</li>
     *   <li>对于非幂等操作（POST），谨慎使用重试</li>
     * </ul>
     */
    private Integer retry;

    /**
     * 连接器UUID
     * 用于标识所属的连接器，从流程定义中传入
     */
    private String connectorUuid;

    /**
     * 动作名称
     * 用于从 action_config.properties 中按名称索引动作配置
     */
    private String actionName;

    /**
     * 环境名称
     * 用于从 connector.config.properties 中按名称查找环境配置
     * 为空时 fallback 取第一个环境
     */
    private String envName;

    /**
     * 连接器配置
     * 运行时从flow_connector表加载的配置
     * 包含baseUrl、全局认证信息等
     */
    private transient java.util.Map<String, Object> connectorConfig;

    /**
     * HTTP动作配置
     * 运行时从flow_connector_http表加载的配置
     * 包含requestMethod、requestPath等
     */
    private transient java.util.Map<String, Object> actionConfig;

    /**
     * HTTP 请求头
     *
     * <p>表示单个 HTTP 请求头的键值对。
     *
     * <p>使用场景：
     * <ul>
     *   <li>设置 Content-Type 指定请求体格式</li>
     *   <li>设置 Authorization 携带认证信息</li>
     *   <li>设置自定义业务头</li>
     *   <li>设置 Accept 指定期望的响应格式</li>
     * </ul>
     *
     * <p>示例：
     * <pre>{@code
     * // 设置 Content-Type
     * Header(key = "Content-Type", value = "application/json")
     *
     * // 设置 Bearer Token 认证
     * Header(key = "Authorization", value = "Bearer ${token}")
     *
     * // 设置自定义租户 ID
     * Header(key = "X-Tenant-Id", value = "${tenantId}")
     *
     * // 设置 API Key
     * Header(key = "X-API-Key", value = "your-api-key-12345")
     * }</pre>
     *
     * <p>变量替换：
     * value 字段支持 ${variableName} 格式的变量占位符。
     * 在节点执行时，变量会被替换为实际值。
     *
     * <p>注意：
     * - key 字段不支持变量替换（必须是静态字符串）
     * - 同一个 key 可以出现多次（如 Set-Cookie）
     * - 某些请求头会被 HttpClient 自动管理（如 Host、Content-Length）
     */
    @Data
    public static class Header implements Serializable {
        /**
         * 请求头名称
         *
         * <p>HTTP 请求头的字段名，不区分大小写。
         *
         * <p>常见的请求头名称：
         * <ul>
         *   <li>Content-Type: 请求体的内容类型</li>
         *   <li>Authorization: 认证信息</li>
         *   <li>Accept: 期望的响应类型</li>
         *   <li>User-Agent: 用户代理</li>
         *   <li>Cache-Control: 缓存控制</li>
         * </ul>
         *
         * <p>示例：
         * <pre>{@code
         * key = "Content-Type"
         * key = "Authorization"
         * key = "X-Custom-Header"
         * }</pre>
         *
         * <p>注意：此字段为静态值，不支持变量替换。
         * 如需动态值，请在 value 字段中使用变量占位符。
         */
        private String key;

        /**
         * 请求头值
         *
         * <p>HTTP 请求头的字段值。
         *
         * <p>变量替换：
         * 支持 ${variableName} 格式的变量占位符。
         * 在节点执行时，变量会被替换为实际值。
         *
         * <p>注意：
         * - 实际数据库存储中字段名可能为 fieldValue，通过 @JsonAlias 兼容
         */
        @JsonAlias("fieldValue")
        private String value;
    }
}