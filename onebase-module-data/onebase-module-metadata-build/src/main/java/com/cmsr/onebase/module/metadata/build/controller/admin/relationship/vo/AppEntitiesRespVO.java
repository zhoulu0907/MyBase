package com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 应用实体和字段信息 Response VO
 *
 * @author matianyu
 * @date 2025-08-09
 */
@Schema(description = "管理后台 - 应用实体和字段信息 Response VO")
@Data
public class AppEntitiesRespVO {

    @Schema(description = "实体列表")
    private List<EntityInfoRespVO> entities;
}
