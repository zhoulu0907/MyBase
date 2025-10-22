package com.cmsr.onebase.module.app.build.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/14 13:17
 */
@Data
@Schema(description = "应用管理 - 字段权限 Response VO")
public class AuthDetailFieldPermissionVO {

    @Schema(description = "所有字段可操作")
    private Integer isAllFieldsAllowed;

    @Schema(description = "字段权限")
    private List<AuthFieldVO> authFieldsRD;

    @Schema(description = "字段权限")
    private List<AuthFieldVO> authFieldsDL;

}
