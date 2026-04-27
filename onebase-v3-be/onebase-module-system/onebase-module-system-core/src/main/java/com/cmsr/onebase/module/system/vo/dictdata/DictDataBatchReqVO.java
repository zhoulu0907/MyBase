package com.cmsr.onebase.module.system.vo.dictdata;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

/**
 * 字典数据批量操作 Request VO
 *
 * @author bty418
 * @date 2025-10-23
 */
@Schema(description = "管理后台 - 字典数据批量操作 Request VO")
@Data
public class DictDataBatchReqVO {

    @Schema(description = "批量新增的字典数据列表")
    @Valid
    private List<DictDataInsertReqVO> createList;

    @Schema(description = "批量更新的字典数据列表")
    @Valid
    private List<DictDataUpdateReqVO> updateList;

    @Schema(description = "批量删除的字典数据ID列表")
    private List<Long> deleteIds;

}

