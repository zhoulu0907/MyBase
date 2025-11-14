package com.cmsr.onebase.module.bpm.runtime.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 流程发起请求VO
 *
 * @author liyang
 * @date 2025/10/19
 */

@Data
@Schema(description = "流程发起请求VO")
public class BpmSubmitReqVO {
    /**
     * 是否草稿
     */
    @JsonProperty("isDraft")
    private boolean isDraft;

    /**
     * 业务ID
     */
    @NotBlank(message = "业务ID不能为空")
    private Long businessId;

    /**
     * 表单名称
     */
    @NotBlank(message = "表单名称不能为空")
    private String formName = "表单名称";

    /**
     * 实体数据
     */
    @Valid
    @NotNull
    private EntityVO entity;
}