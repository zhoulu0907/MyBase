package com.cmsr.onebase.module.bpm.runtime.vo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实体数据
 *
 * @author liyang
 * @date 2025-10-28
 */
@Data
public class EntityVO {
    /**
     * 实体ID
     */
    @NotNull(message = "实体ID不能为空")
    private Long entityId;

    /**
     * 数据ID
     */
    private Long id;

    /**
     * 数据内容
     */
    private Map<Long, Object> data = new HashMap<>();

    /**
     * 子实体数据列表
     */
    @Valid
    private List<SubEntityVo> subEntities;

    /**
     * 子实体数据
     *
     * @author liyang
     * @date 2025-10-28
     */
    @Data
    public static class SubEntityVo {
        /**
         * 子实体ID
         */
        @NotNull(message = "子实体ID不能为空")
        private Long subEntityId;

        /**
         * 子实体数据
         */
        @NotEmpty(message = "子实体数据不能为空")
        private List<Map<Long, Object>> subEntityData;
    }
}
