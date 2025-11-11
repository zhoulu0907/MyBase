package com.cmsr.onebase.module.etl.build.service.preview.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TablePreviewVO {

    @NotNull
    private Long datasourceId;

    @NotNull
    private Long tableId;

}
