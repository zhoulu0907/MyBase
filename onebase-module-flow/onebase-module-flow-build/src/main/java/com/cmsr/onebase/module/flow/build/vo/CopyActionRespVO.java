package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 复制动作响应VO
 *
 * @author kanten
 * @since 2026-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "复制动作响应VO")
public class CopyActionRespVO {

    @Schema(description = "动作ID", example = "action-uuid-002")
    private String actionId;

    @Schema(description = "动作名称", example = "获取用户信息(副本)")
    private String actionName;

    @Schema(description = "动作编码", example = "GET_USER_COPY")
    private String actionCode;
}
