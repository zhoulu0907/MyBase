package com.cmsr.onebase.module.app.build.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/20 12:27
 */
@Data
public class EntityFieldVO {

    @Schema(description = "字段ID", example = "1024")
    private Long id;

    @Schema(description = "显示名称", example = "用户名")
    private String displayName;
}
