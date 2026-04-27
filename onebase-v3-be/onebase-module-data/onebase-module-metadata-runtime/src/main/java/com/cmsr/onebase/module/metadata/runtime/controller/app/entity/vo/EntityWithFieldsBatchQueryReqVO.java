package com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 运行态 - 批量查询实体及字段信息 Request VO
 *
 * @author matianyu
 * @date 2025-12-15
 */
@Schema(description = "运行态 - 批量查询实体及字段信息 Request VO")
@Data
public class EntityWithFieldsBatchQueryReqVO {

    @Schema(description = "实体UUID列表（与tableNames二选一）", example = "[\"01onal1s-0000-0000-0000-000000000001\", \"01onal1s-0000-0000-0000-000000000002\"]")
    private List<String> entityUuids;

    @Schema(description = "表名列表（与entityUuids二选一）", example = "[\"user_info\", \"order_info\"]")
    private List<String> tableNames;
}
