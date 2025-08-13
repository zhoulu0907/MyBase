package com.cmsr.onebase.module.system.controller.admin.dept.vo.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 部门和用户查询 Request VO
 *
 * @author matianyu
 * @date 2025-01-27
 */
@Schema(description = "管理后台 - 部门和用户查询 Request VO")
@Data
public class DeptAndUsersReqVO {

    @Schema(description = "部门ID", example = "1024")
    private Long deptId;

    @Schema(description = "搜索关键词", example = "onebase")
    private String keywords;

}
