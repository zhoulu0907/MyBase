package com.cmsr.onebase.module.bpm.build.vo.vermgmt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 流程修改请求VO
 *
 * @author liyang
 * @date 2025-10-20
 */
@Data
public class BpmUpdateReqVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 流程ID
     */
    @Schema(description = "流程ID", required = true, example = "1")
    @NotNull(message = "流程ID不能为空")
    private Long id;

    /**
     * 流程名称
     */
    @Schema(description = "流程版本备注", required = true, example = "流程版本备注V1")
    @NotNull(message = "流程版本备注不能为空")
    private String versionAlias;

}
