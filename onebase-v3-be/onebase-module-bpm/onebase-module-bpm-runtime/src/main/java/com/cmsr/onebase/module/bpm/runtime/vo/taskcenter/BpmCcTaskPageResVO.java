package com.cmsr.onebase.module.bpm.runtime.vo.taskcenter;

import com.cmsr.onebase.module.bpm.core.vo.UserBasicInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 待办任务VO
 */
@Schema(description = "待办任务VO")
@Data
public class BpmCcTaskPageResVO  {
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "流程标题")
    private String processTitle;

    @Schema(description = "发起人")
    private UserBasicInfoVO initiator;

    @Schema(description = "流程状态")
    private String flowStatus;


    @Schema(description = "到达时间")
    private LocalDateTime arrivalTime;


    @Schema(description = "任务id")
    private Long taskId;

    @Schema(description = "流程实例id")
    private Long instanceId;

    @Schema(description = "流程表单，对应menuUuid")
    private String businessUuid;

    @Schema(description = "页面集Id")
    private Long pageSetId;

    @Schema(description = "已阅 0，否 1，是")
    private boolean viewed;
}
