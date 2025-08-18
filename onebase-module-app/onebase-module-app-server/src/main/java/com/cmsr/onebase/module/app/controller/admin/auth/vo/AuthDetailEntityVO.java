package com.cmsr.onebase.module.app.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/14 13:15
 */
@Data
@Schema(description = "应用管理 - 实体权限 Response VO")
public class AuthDetailEntityVO {

    @Schema(description = "所有实体可访问")
    private Boolean allEntitiesAllowed = Boolean.TRUE;

    @Schema(description = "实体访问权限")
    private List<AuthEntityVO> authEntities;
}
