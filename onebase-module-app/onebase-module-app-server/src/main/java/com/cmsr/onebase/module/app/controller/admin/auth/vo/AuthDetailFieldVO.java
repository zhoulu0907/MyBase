package com.cmsr.onebase.module.app.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/14 13:17
 */
@Data
@Schema(description = "应用管理 - 字段权限 Response VO")
public class AuthDetailFieldVO {

    @Schema(description = "所有字段可操作")
    private Boolean allFieldsAllowed = Boolean.TRUE;

    @Schema(description = "字段权限")
    private List<AuthFieldVO> authFields;
}
