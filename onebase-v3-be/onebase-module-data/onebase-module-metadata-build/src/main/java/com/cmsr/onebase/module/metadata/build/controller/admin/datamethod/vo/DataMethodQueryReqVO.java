package com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据方法查询请求 VO
 *
 * @author matianyu
 * @date 2025-09-10
 */
@Schema(description = "管理后台 - 数据方法查询请求 VO")
@Data
public class DataMethodQueryReqVO {

    @Schema(description = "实体UUID", example = "entity-xxxx-xxxx-xxxx")
    private String entityUuid;

    @Schema(description = "实体ID（兼容旧版，与entityUuid二选一）", example = "164329365983232001")
    private String entityId;

    @Schema(description = "方法类型", example = "query")
    private String methodType;

    @Schema(description = "关键词", example = "用户")
    private String keyword;
}
