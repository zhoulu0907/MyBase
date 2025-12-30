package com.cmsr.onebase.module.system.api.dept.dto;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collection;

/**
 * 管理后台 - 部门和用户查询 Request VO
 *
 * @author ggq
 * @date 2025-12-30
 */
@Schema(description = "管理后台 - 部门和用户查询 Request VO")
@Data
public class DeptAndUsersApiReqVO extends PageParam {


    @Schema(description = "搜索关键词", example = "onebase")
    private String keywords;

    @Schema(description = "排除的userIDs", example = "100")
    private Collection<Long> excludeUserIds;

    @Schema(description = "排除的deptIds", example = "100")
    private Collection<Long> excludeDeptIds;

}
