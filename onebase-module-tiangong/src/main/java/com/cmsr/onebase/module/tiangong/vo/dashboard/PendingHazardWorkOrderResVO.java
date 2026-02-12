package com.cmsr.onebase.module.tiangong.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "待处理隐患工单 VO")
@Data
public class PendingHazardWorkOrderResVO {

    @Schema(description = "工单ID")
    private Integer id;

    @Schema(description = "工单编号")
    private String workOrderNo;

    @Schema(description = "隐患名称")
    private String hazardName;

    @Schema(description = "创建时间")
    private String createTime;

}