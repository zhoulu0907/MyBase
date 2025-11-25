package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import lombok.Data;

@Data
public class DataSelectionConfig {

    /**
     * 关联的目标实体id
     */
    private Long targetEntityId;

    /**
     * 关联的目标字段id
     */
    private Long targetFieldId;

}
