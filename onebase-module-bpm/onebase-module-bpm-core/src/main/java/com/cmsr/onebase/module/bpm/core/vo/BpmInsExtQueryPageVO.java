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

    @Schema(description = "关键词：模糊匹配流程标题、发起人、表单摘要", example = "物品领用申请")
    @Size(max = 200, message = "流程标题长度不能超过200个字符")
    private String keyword;

    @Schema(description = "应用ID", example = "1332334434343")
    @NotNull(message = "应用ID不能为空")
    private Long appId;

    @Schema(description = "业务id，实际对应pageSetId", example = "32636263636323")
    private String businessId;

    @Schema(description = "排序方式：desc-最新处理的, asc-最早处理的",
            example = "desc", defaultValue = "desc")
    private String sortType;

}