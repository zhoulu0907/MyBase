package com.cmsr.onebase.module.system.api.dept.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collection;

/**
 * 管理后台 - 部门和用户查询 Request VO
 *
 * @author matianyu
 * @date 2025-01-27
 */
@Schema(description = "管理后台 - 部门和用户查询 Request VO")
@Data
public class DeptAndUsersReqDTO {

    @Schema(description = "部门ID", example = "1024")
    private Long deptId;

    @Schema(description = "搜索关键词", example = "onebase")
    private String keywords;

    @Schema(description = "排除的userIDs", example = "100")
    private Collection<Long> excludeUserIds;

    @Schema(description = "排除的roleIDs", example = "100")
    private Collection<Long> excludeRoleIds;

    @Schema(description = "用户类型", example = "onebase")
    private  Integer userType;
}
