package com.cmsr.onebase.module.engine.orm.anyline.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class BpmFlowDoneTaskPageReqVO extends PageParam {

    @Schema(description = "流程标题，模糊匹配")
    private String processTitle;

    @Schema(description = "发起人，模糊匹配")
    private String initiator;

    @Schema(description = "处理操作：已同意、已拒绝、已退回、已转交、已委派、已会签")
    private String handleOperation;

    @Schema(description = "处理时间范围")
    private LocalDateTime[] handleTime;

    @Schema(description = "排序方式：desc-最新处理的, asc-最早处理的",
            example = "desc", defaultValue = "desc")
    private String sortType;
}