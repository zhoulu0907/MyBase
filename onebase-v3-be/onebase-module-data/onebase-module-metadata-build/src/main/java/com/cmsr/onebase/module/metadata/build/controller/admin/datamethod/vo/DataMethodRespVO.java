package com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 数据方法响应VO
 *
 * @author bty418
 * @date 2025-09-10
 */
@Data
@Schema(description = "数据方法响应VO")
public class DataMethodRespVO {

    @Schema(description = "方法ID")
    private String id;

    @Schema(description = "方法名称")
    private String methodName;

    @Schema(description = "方法编码")
    private String methodCode;

    @Schema(description = "方法类型")
    private String methodType;

    @Schema(description = "请求URL")
    private String url;

    @Schema(description = "HTTP方法")
    private String httpMethod;

    @Schema(description = "方法描述")
    private String description;

    @Schema(description = "输入参数列表")
    private List<DataMethodParameterVO> inputParameters;

    @Schema(description = "输出参数")
    private DataMethodOutputParameterVO outputParameters;
}
