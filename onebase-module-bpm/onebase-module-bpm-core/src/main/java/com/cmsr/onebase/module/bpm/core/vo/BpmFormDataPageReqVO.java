package com.cmsr.onebase.module.bpm.core.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticPageConditionVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static com.cmsr.onebase.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
/**
 * 获取列表信息请求VO
 */
public class BpmFormDataPageReqVO extends BpmInsExtQueryPageVO {

    @Schema(description = "发起时间 - 开始（包含）", example = "2025-08-01 00:00:00")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime submitTimeStart;

    @Schema(description = "发起时间 - 结束（包含）", example = "2025-08-18 23:59:59")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime submitTimeEnd;

    @Schema(description = "发起人，支持多个值，格式：1,2,3", example = "3")
    private String initiatorId;

    @Schema(description = "实体表名", example = "sog_main")
    @NotBlank(message = "实体表名不能为空")
    private String tableName;

    @Schema(description = "动态分页查询条件")
    private SemanticPageConditionVO entityFilters;
    //========================== 内部使用，前端不用传 ==============
    /**
     * 发起人列表
     */
    private List<String> initiatorIdList;

}
