package com.cmsr.onebase.module.app.build.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/19 9:53
 */
@Data
@Schema(description = "应用管理 - 角色下用户列表 Response VO")
public class AuthRoleMembersPageRespVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "成员ID")
    private Long memberId;

    @Schema(description = "成员名称")
    private String name;

    @Schema(description = "成员类型")
    private String type;

    @Schema(description = "成员类型名称")
    private String typeName;

    @Schema(description = "部门名称")
    private String deptName;

}
