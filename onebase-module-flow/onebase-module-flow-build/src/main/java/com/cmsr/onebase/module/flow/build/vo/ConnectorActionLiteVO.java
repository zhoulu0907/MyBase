package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 连接器动作精简响应VO
 * <p>
 * 用于列表展示，只包含列表页必需的字段，不包含详细配置
 *
 * @author onebase
 * @since 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "连接器动作精简响应")
public class ConnectorActionLiteVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "动作名称", example = "getUserInfo")
    private String actionName;

    @Schema(description = "动作描述", example = "获取用户信息")
    private String description;

    @Schema(description = "状态：1-已发布，2-已下架", example = "1")
    private String status;

    @Schema(description = "创建时间", example = "2026-02-05 16:30:56")
    private String createTime;

    @Schema(description = "更新时间", example = "2026-02-05 18:31:26")
    private String updateTime;
}
