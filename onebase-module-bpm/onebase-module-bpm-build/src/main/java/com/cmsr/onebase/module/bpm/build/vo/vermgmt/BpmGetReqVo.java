package com.cmsr.onebase.module.bpm.build.vo.vermgmt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 获取流程版本管理列表VO
 *
 * @author liyang
 * @date 2025-10-20
 */
@Data
public class BpmGetReqVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 流程ID
     */
    @Schema(description = "表单ID", required = true, example = "113771690916872193")
    @NotNull(message = "表单ID不能为空")
    private String businessId;
}
