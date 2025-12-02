package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import lombok.Data;

@Data
public class DataSelectionConfig {
    //todo 增加关联关系主键id
    private Long relationId;

    /**
     * 关联的目标实体id
     */
    private Long targetEntityId;

    /**
     * 关联的目标字段id
     */
    private Long targetFieldId;

}
