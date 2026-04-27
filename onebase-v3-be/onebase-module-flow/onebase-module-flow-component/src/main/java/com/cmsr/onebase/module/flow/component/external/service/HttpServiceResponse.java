package com.cmsr.onebase.module.flow.component.external.service;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * HTTP 响应封装对象
 *
 * <p>该类用于封装 HTTP 响应的所有信息，包括状态码、响应头、响应体等。
 * 由 HttpExecuteService 返回，HttpNodeComponent 将其转换为输出格式存储到变量上下文。
 *
 * <p>响应字段说明：
 * - statusCode: HTTP 状态码（如 200、404、500）
 * - headers: 响应头（可能有多个同名 header）
 * - body: 响应体（智能解析，JSON 会被解析为 Map/List）
 * - rawBody: 原始响应体（始终为字符串，未经解析）
 * - duration: 请求耗时（毫秒）
 * - errorMessage: 错误信息（如果请求失败）
 *
 * @author zhoulu
 * @since 2026-01-15
 */
@Data
public class HttpServiceResponse implements Serializable {

    /**
     * HTTP 状态码
     *
     * <p>标准的 HTTP 状态码，表示请求的处理结果。
     *
     * <p>常见状态码：
     * <ul>
     *   <li>2xx (成功): 请求成功处理
     *     <ul>
     *       <li>200 OK: 请求成功（GET/POST/PATCH）
     *       <li>201 Created: 资源创建成功（POST/PUT）
     *       <li>204 No Content: 请求成功但无返回内容（DELETE）
     *     </ul>
     *   <li>3xx (重定向): 需要进一步操作
     *     <ul>
     *       <li>301 Moved Permanently: 资源已永久移动
     *       <li>302 Found: 资源临时移动
     *     </ul>
     *   <li>4xx (客户端错误): 请求包含错误或无法处理
     *     <ul>
     *       <li>400 Bad Request: 请求格式错误
     *       <li>401 Unauthorized: 未授权
     *       <li>403 Forbidden: 禁止访问
     *       <li>404 Not Found: 资源不存在
     *       <li>429 Too Many Requests: 请求过于频繁
     *     </ul>
     *   <li>5xx (服务器错误): 服务器处理请求时发生错误
     *     <ul>
     *       <li>500 Internal Server Error: 服务器内部错误
     *       <li>502 Bad Gateway: 网关或代理服务器错误
     *       <li>503 Service Unavailable: 服务暂时不可用
     *     </ul>
     * </ul>
     *
     * <p>可以通过 {@link #isSuccess()} 方法快速判断请求是否成功（2xx）。
     */
    private Integer statusCode;

    /**
     * HTTP 响应头
     *
     * <p>包含服务器返回的所有响应头信息。
     *
     * <p>数据结构为 Map<String, List<String>>：
     * - Key: 响应头名称（如 "Content-Type"、"Set-Cookie"）
     * - Value: 响应头值列表（可能有多个同名 header）
     *
     * <p>常见的响应头：
     * <ul>
     *   <li>Content-Type: 响应体的内容类型（如 application/json）
     *   <li>Content-Length: 响应体的字节长度
     *   <li>Set-Cookie: 设置的 Cookie（可能有多个）
     *   <li>Cache-Control: 缓存策略
     *   <li>ETag: 资源版本标识
     * </ul>
     *
     * <p>示例：
     * <pre>{@code
     * headers = {
     *   "Content-Type": ["application/json"],
     *   "Set-Cookie": ["session=abc", "token=xyz"],
     *   "Cache-Control": ["no-cache"]
     * }
     * }</pre>
     */
    private Map<String, List<String>> headers;

    /**
     * 响应体（已解析）
     *
     * <p>根据 Content-Type 进行智能解析：
     * <ul>
     *   <li>如果是 JSON (application/json):
     *       解析为 Map 或 List 对象，方便后续访问
     *   <li>如果不是 JSON:
     *       返回原始字符串（如 text/plain, HTML 等）
     * </ul>
     *
     * <p>解析示例：
     * <pre>{@code
     * // JSON 响应
     * // 原始: {"name":"John","age":30}
     * // body: {name: "John", age: 30}
     *
     * // JSON 数组响应
     * // 原始: [{"id":1},{"id":2}]
     * // body: [{id: 1}, {id: 2}]
     *
     * // 文本响应
     * // 原始: Hello World
     * // body: "Hello World"
     * }</pre>
     *
     * <p>访问示例：
     * <pre>{@code
     * // 在流程中访问 JSON 字段
     * ${httpNode.body.data.name}
     * ${httpNode.body.users[0].id}
     * }</pre>
     *
     * <p>注意：如果 JSON 解析失败，会回退到原始字符串。
     * 始终可以通过 {@link #rawBody} 获取未经解析的原始响应。
     */
    private Object body;

    /**
     * 原始响应体（未经解析）
     *
     * <p>始终包含服务器返回的原始响应体字符串，无论内容类型如何。
     *
     * <p>使用场景：
     * <ul>
     *   <li>当 JSON 解析失败时，作为备用数据
     *   <li>当需要处理非文本响应（如二进制数据）时
     *   <li>当需要完整保留原始响应时
     * </ul>
     *
     * <p>与 {@link #body} 的区别：
     * <ul>
     *   <li>rawBody: 始终为字符串，未经解析
     *   <li>body: 智能解析，JSON 被解析为对象，否则为字符串
     * </ul>
     *
     * <p>示例：
     * <pre>{@code
     * // JSON 响应
     * rawBody = '{"name":"John","age":30}'
     * body = {name: "John", age: 30}
     *
     * // 文本响应
     * rawBody = "Hello World"
     * body = "Hello World"
     * }</pre>
     */
    private String rawBody;

    /**
     * 请求耗时（毫秒）
     *
     * <p>从请求发出到收到响应的总耗时，包括：
     * <ul>
     *   <li>建立连接的时间</li>
     *   <li>发送请求的时间</li>
     *   <li>服务器处理时间</li>
     *   <li>接收响应的时间</li>
     *   <li>如果发生重试，包括所有重试的耗时</li>
     * </ul>
     *
     * <p>使用场景：
     * <ul>
     *   <li>性能监控和日志记录</li>
     *   <li>超时告警</li>
     *   <li>API 性能分析</li>
     * </ul>
     *
     * <p>示例值：
     * <ul>
     *   <li>快速 API: 50-200ms</li>
     *   <li>普通查询: 200-1000ms</li>
     *   <li>复杂查询: 1000-5000ms</li>
     *   <li>批量操作: 5000-30000ms</li>
     * </ul>
     */
    private Long duration;

    /**
     * 错误信息
     *
     * <p>当请求失败（状态码 >= 400）时，包含错误详情。
     *
     * <p>错误信息格式："{错误类型}: {错误详情}"
     *
     * <p>错误类型：
     * <ul>
     *   <li>请求过于频繁: HTTP 429</li>
     *   <li>服务器错误: HTTP 5xx</li>
     *   <li>客户端错误: HTTP 4xx（除 429 外）</li>
     *   <li>未知错误: 其他情况</li>
     * </ul>
     *
     * <p>错误详情：
     * <ul>
     *   <li>优先从 JSON 响应中提取 message 或 error 字段</li>
     *   <li>如果没有，返回截断的响应体（最多 100 字符）</li>
     *   <li>如果响应体为空，返回"无响应体"</li>
     * </ul>
     *
     * <p>示例：
     * <pre>{@code
     * // 404 错误
     * errorMessage = "客户端错误: 无响应体"
     *
     * // 500 错误，服务器返回错误详情
     * errorMessage = "服务器错误: Database connection failed"
     *
     * // 429 错误
     * errorMessage = "请求过于频繁: Rate limit exceeded"
     * }</pre>
     *
     * <p>可以通过检查此字段是否为 null 来判断请求是否失败。
     * 也可以使用 {@link #isSuccess()} 方法进行快速判断。
     */
    private String errorMessage;

    /**
     * 判断 HTTP 请求是否成功
     *
     * <p>基于 HTTP 状态码判断请求是否成功：
     * <ul>
     *   <li>返回 true: 状态码为 2xx (200-299)</li>
     *   <li>返回 false: 状态码不是 2xx，或 statusCode 为 null</li>
     * </ul>
     *
     * <p>使用场景：
     * <ul>
     *   <li>快速判断请求结果</li>
     *   <li>条件分支处理</li>
     *   <li>错误处理</li>
     * </ul>
     *
     * <p>示例：
     * <pre>{@code
     * HttpServiceResponse response = httpExecuteService.execute(request);
     *
     * if (response.isSuccess()) {
     *     // 处理成功响应
     *     Object data = response.getBody();
     * } else {
     *     // 处理错误响应
     *     String error = response.getErrorMessage();
     * }
     * }</pre>
     *
     * @return 如果状态码为 2xx 返回 true，否则返回 false
     */
    public boolean isSuccess() {
        return statusCode != null && statusCode >= 200 && statusCode < 300;
    }
}