package com.cmsr.onebase.module.flow.component.external.connector;

import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorConfigException;
import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorException;
import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorExecutionException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 连接器抽象基类
 * 使用模板方法模式，提供统一的执行流程
 *
 * <p>核心特性：
 * <ul>
 *   <li>统一的执行流程：验证 → 执行 → 记录</li>
 *   <li>自动异常处理和包装</li>
 *   <li>提供通用的结果构建方法</li>
 *   <li>子类只需实现具体的执行逻辑</li>
 * </ul>
 *
 * @author zhoulu
 * @since 2026-01-10
 */
@Slf4j
public abstract class AbstractConnector implements ConnectorExecutor {

    /**
     * 模板方法：定义统一的执行流程
     * 子类不需要重写此方法
     *
     * @param actionType 动作类型
     * @param config 配置
     * @return 执行结果
     * @throws Exception 执行异常
     */
    @Override
    public final Map<String, Object> execute(String actionType, Map<String, Object> config) throws Exception {
        // 1. 验证配置
        if (!validateConfig(config)) {
            throw new ConnectorConfigException(getConnectorType(), "连接器配置无效");
        }

        // 2. 执行操作
        try {
            Map<String, Object> result = doExecute(actionType, config);
            log.info("连接器执行成功: type={}, action={}", getConnectorType(), actionType);
            return result;
        } catch (ConnectorException e) {
            // 连接器异常，直接抛出
            throw e;
        } catch (Exception e) {
            // 其他异常，包装为连接器执行异常
            throw new ConnectorExecutionException(getConnectorType(), actionType, "连接器执行失败", e);
        }
    }

    /**
     * 执行具体操作（模板方法）
     * 子类必须实现此方法
     *
     * @param actionType 动作类型
     * @param config 配置
     * @return 执行结果
     * @throws Exception 执行异常
     */
    protected abstract Map<String, Object> doExecute(String actionType, Map<String, Object> config) throws Exception;

    /**
     * 构建成功结果
     *
     * @param message 成功消息
     * @return 结果Map
     */
    protected Map<String, Object> buildSuccessResult(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        return result;
    }

    /**
     * 构建成功结果（带数据）
     *
     * @param message 成功消息
     * @param data 数据Map
     * @return 结果Map
     */
    protected Map<String, Object> buildSuccessResult(String message, Map<String, Object> data) {
        Map<String, Object> result = buildSuccessResult(message);
        if (data != null) {
            result.putAll(data);
        }
        return result;
    }

    /**
     * 构建失败结果
     * 注意：此方法仅用于构建返回数据，不会抛出异常
     * 如需抛出异常，请使用 ConnectorExecutionException
     *
     * @param message 失败消息
     * @return 结果Map
     */
    protected Map<String, Object> buildFailureResult(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        return result;
    }

    /**
     * 从配置中获取inputData
     *
     * @param config 配置
     * @return inputData，如果不存在返回空Map
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> getInputData(Map<String, Object> config) {
        Object inputData = config.get("inputData");
        if (inputData instanceof Map) {
            return (Map<String, Object>) inputData;
        }
        return new HashMap<>();
    }

    /**
     * 从inputData中获取字符串值
     *
     * @param inputData 输入数据
     * @param key 键
     * @return 字符串值，如果不存在返回空字符串
     */
    protected String getStringValue(Map<String, Object> inputData, String key) {
        Object value = inputData.get(key);
        return value != null ? value.toString() : "";
    }

    /**
     * 从inputData中获取字符串值（带默认值）
     *
     * @param inputData 输入数据
     * @param key 键
     * @param defaultValue 默认值
     * @return 字符串值
     */
    protected String getStringValue(Map<String, Object> inputData, String key, String defaultValue) {
        Object value = inputData.get(key);
        return value != null ? value.toString() : defaultValue;
    }
}
