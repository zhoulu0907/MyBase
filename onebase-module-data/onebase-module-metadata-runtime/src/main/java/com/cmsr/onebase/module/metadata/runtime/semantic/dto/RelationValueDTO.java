package com.cmsr.onebase.module.metadata.runtime.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "关联值 DTO")
@Data
public class RelationValueDTO {
    @Schema(description = "ONE 基数时的单行值")
    private RowValueDTO row;

    @Schema(description = "MANY 基数时的多行值")
    private List<RowValueDTO> rows;
}
