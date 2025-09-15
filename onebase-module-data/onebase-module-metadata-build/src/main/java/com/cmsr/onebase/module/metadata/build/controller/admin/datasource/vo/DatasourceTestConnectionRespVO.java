package com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 数据源连接测试结果 Response VO")
@Data
public class DatasourceTestConnectionRespVO {

    @Schema(description = "测试结果", example = "true")
    private Boolean success;

    @Schema(description = "错误信息", example = "连接超时")
    private String message;

    @Schema(description = "耗时（毫秒）", example = "1500")
    private Long duration;

    public static DatasourceTestConnectionRespVO success(Long duration) {
        DatasourceTestConnectionRespVO respVO = new DatasourceTestConnectionRespVO();
        respVO.setSuccess(true);
        respVO.setMessage("连接成功");
        respVO.setDuration(duration);
        return respVO;
    }

    public static DatasourceTestConnectionRespVO failed(String message) {
        DatasourceTestConnectionRespVO respVO = new DatasourceTestConnectionRespVO();
        respVO.setSuccess(false);
        respVO.setMessage(message);
        return respVO;
    }

}
