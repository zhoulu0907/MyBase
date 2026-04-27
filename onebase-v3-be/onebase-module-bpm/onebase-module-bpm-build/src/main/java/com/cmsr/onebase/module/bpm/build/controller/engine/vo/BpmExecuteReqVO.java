package com.cmsr.onebase.module.bpm.build.controller.engine.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * bpm execute请求VO
 *
 * @author matianyu
 * @date 2025-09-01
 */
@Data
public class BpmExecuteReqVO {

    /**
     * ID
     */
    @NotBlank(message = "processId不可为空")
    private String processId;

    /**
     * 参数
     */
    private Map<String, Object> params;
}
