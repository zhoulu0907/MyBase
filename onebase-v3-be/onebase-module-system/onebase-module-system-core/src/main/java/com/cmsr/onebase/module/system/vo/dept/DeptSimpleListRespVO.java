package com.cmsr.onebase.module.system.vo.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 部门精简信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeptSimpleListRespVO {

    @Schema(description = "部门id",   example = "1024")
    @NotNull(message = "部门id不能为空")
    private Long deptId;

    @Schema(description = "直属标识",  example = "默认true，获取自己，false 获取自己及其下属")
    private Boolean directFlag;

}
