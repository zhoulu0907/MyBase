package com.cmsr.onebase.module.bpm.runtime.vo;

import com.cmsr.onebase.module.bpm.core.vo.UserBasicInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
/**
 * 获取列表信息响应VO
 */
public class BpmFormDataPageRespVO {
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "流程标题")
    private String processTitle;

    @Schema(description = "发起人")
    private UserBasicInfoVO initiator;

    @Schema(description = "当前节点状态")
    private String flowStatus;

    @Schema(description = "发起时间")
    private LocalDateTime submitTime;

    @Schema(description = "流程节点编码")
    private String nodeCode;

    @Schema(description = "流程节点名称")
    private String nodeName;

    /**
     * 实体数据
     */
    private Map<String, Object> data;
}
