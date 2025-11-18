package com.cmsr.onebase.module.system.api.user.dto;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "查询部门下用户分页")
@Data
public class UserByDeptPageReqDTO extends PageParam {

    @Schema(description = "模糊查询关键词", example = "IT 部")
    private Long deptId;

    @Schema(description = "isRecurseSub 如果为true，那么把deptId以及它所有下级部门的用户分页返回", example = "true")
    private Boolean isRecurseSub;

    @Schema(description = "模糊查询关键词", example = "张某")
    private String keywords;

}
