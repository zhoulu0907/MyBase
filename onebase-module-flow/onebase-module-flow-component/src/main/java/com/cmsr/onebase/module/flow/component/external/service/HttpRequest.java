package com.cmsr.onebase.module.flow.component.external.service;

import com.cmsr.onebase.module.flow.context.graph.nodes.HttpNodeData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * HTTP 请求封装对象
 *
 * <p>该类用于封装 HTTP 请求的所有配置信息，在 HttpNodeComponent 和 HttpExecuteService 之间传递。
 * 所有字段中的变量占位符（${var}）都已在前一阶段被替换为实际值。
 *
 * <p>使用场景：
 * - HttpNodeComponent 创建该对象并设置请求参数
 * - HttpExecuteService 接收该对象并执行实际的 HTTP 请求
 *
 * <p>请求参数说明：
 * - url: 请求的目标 URL（支持 http 和 https）
 * - method: HTTP 方法类型（GET/POST/PUT/DELETE/PATCH）
 * - headers: 请求头列表（如 Content-Type、Authorization 等）
 * - bodyType: 请求体类型标识（预留字段，用于未来扩展）
 * - bodyContent: 请求体内容（JSON 字符串、表单数据等）
 * - timeout: 请求超时时间（毫秒，默认 5000ms）
 * - retry: 失败重试次数（默认 0，即不重试）
 *
 * @author zhoulu
 * @since 2026-01-15
 */
@Data
public class HttpRequest implements Serializable {

    /**
     * 流程追踪信息
     * 用于全流程日志关联，方便问题排查
     */
    private String processId;
    private String traceId;
    private String executionUuid;
    private String nodeId;

    /**
     * 请求 URL（已解析变量）
     *
     * <p>支持 http 和 https 协议。
     * 变量替换已在 HttpNodeComponent 中完成。
     *
     * <p>示例：
     * <pre>{@code
     * // 替换前: https://api.example.com/users/${userId}
     * // 替换后: https://api.example.com/users/123
     * }</pre>
     *
     * <p>SSRF 防护：
     * - 禁止访问内网地址（10.x.x.x, 192.168.x.x, 172.16-31.x.x）
     * - 禁止访问 localhost（127.0.0.1, ::1, localhost）
     */
    private String url;

    /**
     * HTTP 请求方法
     *
     * <p>支持的 HTTP 方法：
     * - GET: 获取资源
     * - POST: 创建资源
     * - PUT: 更新资源（全量）
     * - DELETE: 删除资源
     * - PATCH: 更新资源（部分）
     *
     * <p>不区分大小写，会被统一转换为大写处理。
     */
    private String method;

    /**
     * 请求头列表（已解析变量）
     *
     * <p>用于设置 HTTP 请求头，常见的请求头包括：
     * - Content-Type: 请求体的内容类型（如 application/json）
     * - Authorization: 认证信息（如 Bearer token）
     * - Accept: 期望的响应类型（如 application/json）
     * - User-Agent: 用户代理标识
     *
     * <p>每个请求头是一个键值对，键和值都已完成变量替换。
     *
     * <p>示例：
     * <pre>{@code
     * // 设置 Content-Type 和 Authorization
     * headers = [
     *   Header(key = "Content-Type", value = "application/json"),
     *   Header(key = "Authorization", value = "Bearer ${token}")
     * ]
     * }</pre>
     */
    private List<HttpNodeData.Header> headers;

    /**
     * 请求体类型（预留字段）
     *
     * <p>用于标识请求体的格式类型，可能的值：
     * - JSON: JSON 格式（application/json）
     * - FORM: 表单格式（application/x-www-form-urlencoded）
     * - RAW: 原始文本（text/plain）
     *
     * <p>注意：当前版本中此字段为预留，实际请求体直接使用 bodyContent 的字符串值。
     * 未来可能根据此字段自动设置 Content-Type 头或进行请求体编码。
     */
    private String bodyType;

    /**
     * 请求体内容（已解析变量）
     *
     * <p>包含要发送的请求体数据，通常为 JSON 字符串。
     *
     * <p>对于 GET 和 DELETE 请求，此字段通常为空。
     * 对于 POST、PUT 和 PATCH 请求，此字段包含要发送的数据。
     *
     * <p>示例：
     * <pre>{@code
     * // JSON 格式请求体
     * bodyContent = '{"name":"John","age":30,"email":"john@example.com"}'
     *
     * // 表单格式请求体
     * bodyContent = "name=John&age=30&email=john@example.com"
     * }</pre>
     *
     * <p>变量替换已在 HttpNodeComponent 中完成：
     * <pre>{@code
     * // 替换前: '{"userId":"${userId}","userName":"${userName}"}'
     * // 替换后: '{"userId":"123","userName":"John"}'
     * }</pre>
     */
    private String bodyContent;

    /**
     * 请求超时时间（毫秒）
     *
     * <p>设置 HTTP 请求的超时时间，包括：
     * - 连接超时：建立连接的最大等待时间
     * - 读取超时：读取响应数据的最大等待时间
     *
     * <p>默认值：5000 毫秒（5 秒）
     *
     * <p>建议值：
     * - 快速 API: 3000-5000ms
     * - 普通查询: 5000-10000ms
     * - 文件上传: 30000-60000ms
     * - 批量操作: 60000-120000ms
     *
     * <p>超时后会发生什么：
     * - 如果配置了重试，会触发重试机制
     * - 如果未配置重试或重试次数用完，会抛出 HttpTimeoutException
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
     * - 网络异常（连接超时、读取超时、连接拒绝）
     * - HTTP 5xx 服务器错误（可能是临时故障）
     * - HTTP 429 请求过于频繁（需要等待后重试）
     *
     * <p>重试策略：
     * - 使用指数退避算法
     * - 第 1 次重试: 100ms
     * - 第 2 次重试: 200ms
     * - 第 3 次重试: 400ms
     * - 最大退避时间: 5000ms
     *
     * <p>示例：
     * <pre>{@code
     * // 不重试
     * retry = 0
     *
     * // 重试 2 次（总共尝试 3 次）
     * retry = 2
     * }</pre>
     *
     * <p>注意：
     * - 4xx 客户端错误（除了 429）不会重试
     * - 每次重试都会增加总耗时
     * - 建议根据业务场景合理设置重试次数
     */
    private Integer retry;
}