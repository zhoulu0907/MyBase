package com.cmsr.onebase.module.app.build.vo.version;

import com.cmsr.onebase.framework.common.pojo.PageParam;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导出记录分页查询请求VO
 *
 * @author zhoumingji
 * @date 2026-01-27
 */
@Schema(description = "导出记录分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ExportPageReqVO extends PageParam {
    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "导出状态 0-未知 1-导出中 2-导出成功 3-导出失败")
    private Integer exportStatus;

}
