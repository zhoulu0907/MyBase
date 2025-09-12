package com.cmsr.onebase.module.system.vo.dept;

import com.cmsr.onebase.module.system.vo.user.UserSimpleRespVO;
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
public class DeptAndUsersRespVO {

    @Schema(description = "当前部门信息")
    private DeptRespVO deptInfo;

    @Schema(description = "下级部门列表")
    private List<DeptRespVO> deptList;

    @Schema(description = "当前部门下直属用户列表")
    private List<UserSimpleRespVO> userList;

}
