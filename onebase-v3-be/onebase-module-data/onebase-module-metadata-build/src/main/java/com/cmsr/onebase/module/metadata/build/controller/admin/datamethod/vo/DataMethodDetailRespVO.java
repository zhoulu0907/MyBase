package com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 数据方法详情响应VO
 *
 * @author bty418
 * @date 2025-09-10
 */
@Data
@Schema(description = "数据方法详情响应VO")
public class DataMethodDetailRespVO {

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
    private List<DataMethodDetailParameterVO> inputParameters;

    @Schema(description = "输出参数")
    private DataMethodDetailOutputParameterVO outputParameters;

    @Schema(description = "请求示例")
    private String requestExample;

    @Schema(description = "响应示例")
    private String responseExample;
}
