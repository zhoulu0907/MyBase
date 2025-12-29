package com.cmsr.onebase.module.system.vo.user;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;


@Schema(description = "管理后台 - 修改部门管理员/主管 Req VO")
@Data
@ExcelIgnoreUnannotated
public class UserAdminOrDirectorUpdateReqVO {

    @Schema(description = "修改类型类型", example = "admin/director")
    @NotNull(message = "修改类型不能为空")
    private String updateType;

    @NotNull(message = "部门ID不能为空")
    @Schema(description = "部门Id", example = "2")
    private Long deptId;

    @Schema(description = " 接口人UserIds", example = "")
    private Set<Long> adminUserIds;

}
