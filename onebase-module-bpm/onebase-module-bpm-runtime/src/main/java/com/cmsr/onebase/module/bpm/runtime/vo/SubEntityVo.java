package com.cmsr.onebase.module.bpm.runtime.vo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 子实体数据
 *
 * @author liyang
 * @date 2025-10-28
 */
@Data
public class SubEntityVo {
    /**
     * 子实体ID
     */
    @NotEmpty(message = "子实体ID不能为空")
    private Long subEntityId;

    /**
     * 子实体数据
     */
    @NotEmpty(message = "子实体数据不能为空")
    private List<Map<Long, Object>> subEntityData;
}
