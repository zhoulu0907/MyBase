package com.cmsr.onebase.module.engine.orm.anyline.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.cmsr.onebase.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "我创建的流程 - 分页列表请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class BpmMyCreatedPageReqVO extends PageParam {

    @Schema(description = "流程标题，模糊匹配", example = "物品领用申请")
    @Size(max = 200, message = "流程标题长度不能超过200个字符")
    private String processTitle;

    @Schema(description = "发起人，模糊匹配", example = "王少青")
    @Size(max = 100, message = "发起人长度不能超过100个字符")
    private String initiator;

    @Schema(description = "表单摘要，模糊匹配", example = "铅笔")
    @Size(max = 500, message = "表单摘要长度不能超过500个字符")
    private String formSummary;

    @Schema(description = "流程状态：ALL-全部, DRAFT-草稿, APPROVING-审批中, APPROVED-已通过, REJECTED-已拒绝, REVOKED-已撤回, TERMINATED-已终止",
            example = "ALL", defaultValue = "ALL")
    private String flowStatus;

    @Schema(description = "发起时间范围", example = "[2025-08-01 00:00:00,2025-08-18 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] submitTime;

    @Schema(description = "排序方式：desc-最新处理的, asc-最早处理的",
            example = "desc", defaultValue = "desc")
    private String sortType;

    @Schema(description = "应用ID", example = "1332334434343")
    @NotBlank(message = "应用ID不能为空")
    private String appId;
}
