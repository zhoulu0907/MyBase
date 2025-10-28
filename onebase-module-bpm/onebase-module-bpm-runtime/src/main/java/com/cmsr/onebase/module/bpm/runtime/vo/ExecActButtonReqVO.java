package com.cmsr.onebase.module.bpm.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 流程执行操作按钮请求VO
 *
 * @author liyang
 * @date 2025/10/19
 */

@Data
@Schema(description = "流程执行操作按钮请求VO")
public class ExecActButtonReqVO {

    /**
     * 按钮类型
     */
    @NotBlank(message = "按钮类型不能为空")
    private String buttonType;

    /**
     * 实体数据ID
     */
    private String entityDataId;

    /**
     * 业务ID
     */
    @NotBlank(message = "业务ID不能为空")
    private String businessId;

    /**
     * 实体ID
     */
    @NotNull(message = "实体ID不能为空")
    private Long entityId;

    /**
     * 数据内容
     */
    @NotEmpty(message = "实体数据内容不能为空")
    private Map<Long, Object> entityData;

    /**
     * 子实体数据列表
     */
    @Valid
    private List<SubEntityVo> subEntities;

}