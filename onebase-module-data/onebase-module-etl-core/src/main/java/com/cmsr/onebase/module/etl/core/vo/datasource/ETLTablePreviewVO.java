package com.cmsr.onebase.module.etl.core.vo.datasource;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ETLTablePreviewVO {

    @NotNull
    private Long datasourceId;

    @NotNull
    private Long tableId;

}
