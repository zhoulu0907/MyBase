package com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 数据方法输出参数 VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 数据方法输出参数 VO")
@Data
public class DataMethodOutputParameterVO {

    @Schema(description = "参数类型", example = "OBJECT")
    private String type;

    @Schema(description = "参数描述", example = "创建成功的用户信息对象")
    private String description;

} 