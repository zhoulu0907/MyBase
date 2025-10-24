package com.cmsr.onebase.module.app.build.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/14 13:15
 */
@Data
@Schema(description = "应用管理 - 视图权限 Response VO")
public class AuthDetailViewVO {

    @Schema(description = "所有视图可访问")
    private Integer isAllViewsAllowed;

    @Schema(description = "实体访问权限")
    private List<AuthViewVO> authViews;
}
