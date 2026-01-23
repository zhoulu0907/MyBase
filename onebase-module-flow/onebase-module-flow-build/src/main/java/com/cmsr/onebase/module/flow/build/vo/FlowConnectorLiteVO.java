package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Flow Connector Lite VO
 * Lightweight version for listing connector instances without config details
 *
 * @author zhoulu
 * @since 2026-01-22
 */
@Schema(description = "连接器实例轻量级信息VO")
@Data
public class FlowConnectorLiteVO {

    @Schema(description = "连接器UUID")
    private String connectorUuid;

    @Schema(description = "连接器名称")
    private String connectorName;

    @Schema(description = "连接器类型编码")
    private String typeCode;

    @Schema(description = "连接器描述")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
