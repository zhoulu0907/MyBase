package com.cmsr.onebase.module.app.build.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/7 12:05
 */
@Data
@Schema(description = "应用管理 - 角色删除部门 Request VO")
public class AuthRoleDeleteMemberReqVO {

    @Schema(description = "角色id")
    @NotNull(message = "角色id不能为空")
    private Long roleId;

    @Schema(description = "成员列表")
    @NotNull(message = "成员不能为空")
    private List<DeleteMember> members;


    @Data
    @Schema(description = "成员")
    public static class DeleteMember {

        @Schema(description = "主键id")
        private Long id;

        @Schema(description = "角色类别")
        private String memberType;
    }
}
