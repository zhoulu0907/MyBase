package com.cmsr.onebase.module.bpm.core.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程实例查询条件VO
 *
 * @author liyang
 * @date 2025-11-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BpmInsExtQueryPageVO extends PageParam  {

    @Schema(description = "流程标题，模糊匹配", example = "物品领用申请")
    @Size(max = 200, message = "流程标题长度不能超过200个字符")
    private String processTitle;

    @Schema(description = "发起人，模糊匹配", example = "王少青")
    @Size(max = 100, message = "发起人长度不能超过100个字符")
    private String initiator;

    @Schema(description = "表单摘要，模糊匹配", example = "铅笔")
    @Size(max = 500, message = "表单摘要长度不能超过500个字符")
    private String formSummary;

    @Schema(description = "应用ID", example = "1332334434343")
    @NotNull(message = "应用ID不能为空")
    private Long appId;
}