package com.cmsr.onebase.module.system.vo.dictdata;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * 批量字典数据列表 Request VO
 *
 * @author matianyu
 * @date 2025-12-30
 */
@Schema(description = "管理后台 - 批量字典数据列表 Request VO")
@Data
public class DictDataListByTypesReqVO {

    @Schema(description = "字典类型列表", example = "gender,status")
    private List<String> dictTypes;

    @Schema(description = "字典类型ID列表", example = "1,2")
    private List<Long> dictTypeIds;

}
