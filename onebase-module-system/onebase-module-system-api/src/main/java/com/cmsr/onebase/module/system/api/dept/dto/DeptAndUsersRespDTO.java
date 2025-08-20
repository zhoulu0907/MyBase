package com.cmsr.onebase.module.system.api.dept.dto;

import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 部门和用户查询 Response VO
 *
 * @author matianyu
 * @date 2025-01-27
 */
@Schema(description = "管理后台 - 部门和用户查询 Response VO")
@Data
public class DeptAndUsersRespDTO {

    @Schema(description = "当前部门信息")
    private DeptRespDTO deptInfo;

    @Schema(description = "下级部门列表")
    private List<DeptRespDTO> deptList;

    @Schema(description = "当前部门下直属用户列表")
    private List<AdminUserRespDTO> userList;

}
