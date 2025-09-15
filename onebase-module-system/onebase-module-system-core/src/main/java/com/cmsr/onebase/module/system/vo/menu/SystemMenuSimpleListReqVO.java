package com.cmsr.onebase.module.system.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 权限列表 Request VO")
@Data
public class SystemMenuSimpleListReqVO {

    @Schema(description = "权限名称，模糊匹配", example = "用户查询")
    private String name;

    @Schema(description = "权限编码，模糊匹配", example = "system:user:query")
    private String code;

}
