package com.cmsr.onebase.module.etl.build.service.etl.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkflowUpdateVO extends WorkflowCreateVO {

    @NotNull
    private Long id;

}
