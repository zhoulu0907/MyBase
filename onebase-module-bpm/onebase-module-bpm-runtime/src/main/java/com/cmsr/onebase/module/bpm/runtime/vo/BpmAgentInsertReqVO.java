package com.cmsr.onebase.module.bpm.runtime.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.cmsr.onebase.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "流程代理创建 VO")
@Data
public class BpmAgentInsertReqVO {
    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "应用ID不能为空")
    private Long appId;

    @Schema(description = "代理人ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "代理人ID不能为空")
    private String agentId;

    @Schema(description = "代理人名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "代理人名称不能为空")
    private String agentName;

    @Schema(description = "被代理人ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String principalId;

    @Schema(description = "被代理人名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String principalName;

    @Schema(description = "代理开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-10-29 00:00:00")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @NotNull(message = "代理开始时间不能为空")
    private LocalDateTime startTime;

    @Schema(description = "代理结束时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-10-30 00:00:00")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @NotNull(message = "代理结束时间不能为空")
    private LocalDateTime endTime;
}
