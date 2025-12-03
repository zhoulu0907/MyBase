package com.cmsr.onebase.module.app.build.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/7 14:48
 */
@Data
@Schema(description = "应用管理 - 视图 Response VO")
public class AuthViewVO {

    @Schema(description = "主键Id")
    private Long id;

    @Schema(description = "实体uuid")
    private String viewUuid;

    @Schema(description = "实体名称")
    private String viewDisplayName;

    @Schema(description = "是否可访问")
    private Integer isAllowed;

}
