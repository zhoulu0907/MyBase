package com.cmsr.onebase.module.bpm.runtime.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 实体数据
 *
 * @author liyang
 * @date 2025-10-28
 */
@Data
public class EntityVO {
    @NotBlank(message = "表名不能为空")
    private String tableName;

    /**
     * 数据内容
     */
    @NotEmpty(message = "实体数据不能为空")
    private Map<String, Object> data = new HashMap<>();
}
