package com.cmsr.onebase.module.bpm.core.vo.instance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 流程发起响应VO
 *
 * @author liyang
 * @date 2025-10-27
 */

@Data
@Schema(description = "流程发起响应VO")
public class BpmSubmitRespVO {

    /**
     * 实体数据ID
     */
    private String entityDataId;

    /**
     * 流程实例ID
     */
    private Long instanceId;
}