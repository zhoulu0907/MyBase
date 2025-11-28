package com.cmsr.onebase.module.metadata.runtime.semantic.vo;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Schema(description = "合并请求体：顶层键为业务字段或连接器名称，用于创建/更新")
@Data
/**
 * 合并请求体 VO
 *
 * <p>用于创建/更新场景，顶层键为业务字段或连接器名称。
 * 通过 {@link #set(String, Object)} 动态收集未知键。</p>
 */
public class SemanticMergeBodyVO {

    private Map<String, Object> properties = new HashMap<>();

    /**
     * 动态接收任意顶层键值（Jackson 反序列化）
     * @param key 顶层键（字段名/连接器名）
     * @param value 对应值
     */
    @JsonAnySetter
    public void set(String key, Object value) {
        properties.put(key, value);
    }

    /**
     * 暴露全部已收集的顶层属性（Jackson 序列化）
     * @return 顶层属性
     */
    @JsonAnyGetter
    public Map<String, Object> any() {
        return properties;
    }
}
