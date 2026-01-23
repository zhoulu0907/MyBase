package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create Flow Connector Response VO
 *
 * @author zhoulu
 * @since 2026-01-22
 */
@Schema(description = "Automation Workflow - Connector Create Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFlowConnectorRespVO {

    @Schema(description = "Connector ID (database primary key)")
    private Long id;

    @Schema(description = "Connector UUID (business unique identifier)")
    private String connectorUuid;

}
