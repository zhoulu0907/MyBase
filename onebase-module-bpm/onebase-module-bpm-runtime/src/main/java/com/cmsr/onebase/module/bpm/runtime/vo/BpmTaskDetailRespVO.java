package com.cmsr.onebase.module.bpm.runtime.vo;

import com.cmsr.onebase.module.bpm.core.dto.PageViewDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeBtnCfgDTO;
import com.cmsr.onebase.module.bpm.core.vo.UserBasicInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "流程详情响应VO")
@Data
public class BpmTaskDetailRespVO {
    @Schema(description = "当前状态")
    private String currentStatus;

    @Deprecated
    @Schema(description = "发起人ID")
    private String initiatorId;

    @Deprecated
    @Schema(description = "发起人")
    private String initiatorName;

    /**
     * 发起人
     */
    @Schema(description = "发起人")
    private UserBasicInfoVO initiator;

    @Schema(description = "发起部门ID")
    private Long initiatorDeptId;

    @Schema(description = "发起部门名称（冗余字段，方便查询显示）")
    private String initiatorDeptName;

    @Schema(description = "流程版本号")
    private String bpmVersion;

    @Schema(description = "发起时间")
    private LocalDateTime submitTime;

    @Schema(description = "当前待办任务ID")
    private Long taskId;

    @Schema(description = "流程实例ID")
    private Long instanceId;

    @Schema(description = "按钮信息")
    List<BaseNodeBtnCfgDTO> buttonConfigs;

    @Schema(description = "form信息")
    private Map<String, Object> formData;

    @Schema(description = "页面视图信息")
    private PageViewDTO pageView;

    @Schema(description = "流程标题")
    private String processTitle;

    @Schema(description = "代理人名称")
    private String agentName;

    @Schema(description = "代理人ID")
    private String agentId;
}
