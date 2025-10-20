package com.cmsr.onebase.module.app.build.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/7 9:36
 */
@Data
@Schema(description = "应用管理 - 角色权限 Response VO")
public class AuthDetailFunctionPermissionVO {

    @Schema(description = "页面是否可访问")
    private Integer isPageAllowed = 0;

    @Schema(description = "操作权限")
    private List<String> authOperationTags;

    @Schema(description = "实体访问权限")
    private AuthDetailViewVO authViewVO;

}
