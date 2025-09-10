package com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 数据方法详情 Response VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 数据方法详情 Response VO")
@Data
public class DataMethodDetailRespVO {

    @Schema(description = "方法名称", example = "新增单条数据")
    private String methodName;

    @Schema(description = "方法编码", example = "create_single")
    private String methodCode;

    @Schema(description = "方法类型", example = "CREATE")
    private String methodType;

    @Schema(description = "URL地址", example = "/api/data/user_info/create")
    private String url;

    @Schema(description = "HTTP方法", example = "POST")
    private String httpMethod;

    @Schema(description = "描述信息", example = "新增单条用户信息记录")
    private String description;

    @Schema(description = "输入参数列表")
    private List<DataMethodDetailParameterVO> inputParameters;

    @Schema(description = "输出参数信息")
    private DataMethodDetailOutputParameterVO outputParameters;

    @Schema(description = "请求示例")
    private Object requestExample;

    @Schema(description = "响应示例")
    private Object responseExample;

} 