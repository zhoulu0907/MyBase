package com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * ProcessedSubEntityVo
 */
@Data
@Schema(description = "处理过的子表VO，用于全流程CRUD操作")
public class ProcessedSubEntityVo {

    private Long menuId; //菜单id，来源于父表crud操作的menuId

    private String traceId; //追踪id，来源于父表crud操作的traceId

    private String id; //删除/修改操作时的数据行id

    private String subEntityId; //子表对应的实体UUID

    private Map subData; //子表数据

}
