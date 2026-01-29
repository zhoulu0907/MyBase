package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 动作详情VO
 *
 * @author kanten
 * @since 2026-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "动作详情VO")
public class ActionDetailVO {

    @Schema(description = "动作ID", example = "action-uuid-001")
    private String actionId;

    @Schema(description = "动作名称", example = "获取用户信息")
    private String actionName;

    @Schema(description = "动作编码", example = "GET_USER")
    private String actionCode;

    @Schema(description = "动作描述", example = "通过HTTP GET获取用户信息")
    private String description;

    @Schema(description = "状态", example = "draft")
    private String status;

    @Schema(description = "版本号", example = "1")
    private Integer version;

    @Schema(description = "表单结构定义", example = "{\"type\":\"object\",\"properties\":{...}}")
    private JsonNode formSchema;

    @Schema(description = "表单值", example = "{\"url\":\"https://api.example.com\",\"method\":\"GET\"}")
    private JsonNode formValues;

    @Schema(description = "创建人", example = "张三")
    private String creator;

    @Schema(description = "创建人ID", example = "user-uuid-001")
    private String creatorId;

    @Schema(description = "创建时间", example = "2026-01-25T10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2026-01-26T14:30:00")
    private LocalDateTime updateTime;
}
