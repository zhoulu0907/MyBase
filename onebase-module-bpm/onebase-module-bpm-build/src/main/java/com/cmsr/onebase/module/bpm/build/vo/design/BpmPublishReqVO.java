package com.cmsr.onebase.module.bpm.build.vo.design;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 流程发布请求VO
 *
 * @author liyang
 * @date 2025-10-24
 */
@Data
public class BpmPublishReqVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 流程ID
     */
    @Schema(description = "流程ID", required = true, example = "1")
    @NotNull(message = "流程ID不能为空")
    private Long id;
}
