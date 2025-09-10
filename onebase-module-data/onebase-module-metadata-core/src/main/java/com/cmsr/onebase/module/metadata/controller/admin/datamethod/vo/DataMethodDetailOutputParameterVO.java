package com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 数据方法详情输出参数 VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 数据方法详情输出参数 VO")
@Data
public class DataMethodDetailOutputParameterVO {

    @Schema(description = "参数类型", example = "OBJECT")
    private String type;

    @Schema(description = "参数描述", example = "创建成功的用户信息对象")
    private String description;

    @Schema(description = "属性列表")
    private List<DataMethodPropertyVO> properties;

} 