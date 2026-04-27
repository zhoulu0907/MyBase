package com.cmsr.onebase.module.tiangong.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "隐患工单列表响应 VO")
@Data
public class HazardWorkOrderListResVO {

    @Schema(description = "序号")
    private Integer seqNo;

    @Schema(description = "工单编号")
    private String workOrderNo;

    @Schema(description = "设备ID")
    private String deviceId;

    @Schema(description = "设备型号")
    private String deviceModel;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "预约服务时间")
    private String appointmentTime;

    @Schema(description = "工单状态")
    private String workOrderStatus;

}