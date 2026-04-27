package com.cmsr.onebase.module.system.vo.dictdata;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 字典数据创建/修改 Request VO")
@Data
public class DictDataUpdateStatusVO {

    @Schema(description = "字典数据编号", example = "1024")
    @NotNull(message = "data id不能为空")
    private Long id;

    @Schema(description = "状态,见 CommonStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @InEnum(value = CommonStatusEnum.class, message = "修改状态必须是 {value}")
    @NotNull(message = "status不能为空")
    private Integer status;

}
