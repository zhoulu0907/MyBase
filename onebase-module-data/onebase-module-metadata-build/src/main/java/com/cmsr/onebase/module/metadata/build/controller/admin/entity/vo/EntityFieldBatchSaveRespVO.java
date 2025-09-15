package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理后台 - 批量保存（增删改）实体字段 Response VO
 */
@Schema(description = "管理后台 - 批量保存（增删改）实体字段 Response VO")
@Data
public class EntityFieldBatchSaveRespVO {

    @Schema(description = "新增成功的字段ID列表")
    private List<String> createdIds = new ArrayList<>();

    @Schema(description = "更新成功的字段ID列表")
    private List<String> updatedIds = new ArrayList<>();

    @Schema(description = "删除成功的字段ID列表")
    private List<String> deletedIds = new ArrayList<>();

    @Schema(description = "总成功数量")
    public int getSuccessCount() {
        return (createdIds != null ? createdIds.size() : 0)
            + (updatedIds != null ? updatedIds.size() : 0)
            + (deletedIds != null ? deletedIds.size() : 0);
    }
}
