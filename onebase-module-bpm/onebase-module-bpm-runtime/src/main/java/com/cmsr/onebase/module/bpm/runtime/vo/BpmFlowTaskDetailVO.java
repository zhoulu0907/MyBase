package com.cmsr.onebase.module.bpm.runtime.vo;

import com.cmsr.onebase.module.bpm.api.dto.node.base.BaseNodeBtnCfgDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "流程详情VO")
@Data
public class BpmFlowTaskDetailVO {
    @Schema(description = "当前状态")
    private String currentStatus;

    @Schema(description = "发起人ID")
    private Long initiatorId;

    @Schema(description = "发起人")
    private String initiatorName;

    @Schema(description = "发起部门ID")
    private Long initiatorDeptId;

    @Schema(description = "发起部门名称（冗余字段，方便查询显示）")
    private String initiatorDeptName;

    @Schema(description = "流程版本号")
    private String bpmVersion;

    @Schema(description = "发起时间")
    private LocalDateTime submitTime;

    @Schema(description = "按钮信息")
    List<BaseNodeBtnCfgDTO> buttonConfigs;

    @Schema(description = "form信息")
    private Map<String, Object> formData;
}
