package com.cmsr.onebase.module.app.build.vo.version;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 导出记录分页查询响应VO
 *
 * @author zhoumingji
 * @date 2026-01-27
 */
@Schema(description = "导出记录分页查询 Response VO")
@Data
public class ExportPageRespVO {

    @Schema(description = "导出记录ID")
    private Long id;

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "资源在S3中的ID")
    private String objectId;

    @Schema(description = "导出状态 0-未知 1-导出中 2-导出成功 3-导出失败")
    private Integer exportStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    private String creatorName;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人")
    private String updaterName;

}
