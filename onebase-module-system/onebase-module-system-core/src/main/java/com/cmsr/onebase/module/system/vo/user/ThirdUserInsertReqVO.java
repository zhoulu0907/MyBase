package com.cmsr.onebase.module.system.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ThirdUserInsertReqVO extends UserInsertReqVO{
    @Schema(description = "创建来源", example = "")
    private String createSource;
}
