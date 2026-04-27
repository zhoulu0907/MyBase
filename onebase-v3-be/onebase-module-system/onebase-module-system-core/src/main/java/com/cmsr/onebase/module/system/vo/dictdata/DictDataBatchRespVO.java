package com.cmsr.onebase.module.system.vo.dictdata;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 字典数据批量操作 Response VO
 *
 * @author bty418
 * @date 2025-10-23
 */
@Schema(description = "管理后台 - 字典数据批量操作 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictDataBatchRespVO {

    @Schema(description = "新增成功的字典数据ID列表")
    private List<Long> createdIds;

    @Schema(description = "更新成功的字典数据ID列表")
    private List<Long> updatedIds;

    @Schema(description = "删除成功的字典数据ID列表")
    private List<Long> deletedIds;

    @Schema(description = "新增成功的数量")
    private Integer createCount;

    @Schema(description = "更新成功的数量")
    private Integer updateCount;

    @Schema(description = "删除成功的数量")
    private Integer deleteCount;

}

