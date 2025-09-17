package com.cmsr.onebase.module.metadata.api.entity;

import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 实体字段管理 sdk
 *
 * @author matianyu
 * @date 2025-09-10
 */
@Tag(name = "实体字段管理 sdk")
public interface MetadataEntityFieldApi {

    /**
     * 查询指定实体的字段列表
     *
     * @param reqDTO 查询请求参数
     * @return 字段列表
     */
    @Operation(summary = "查询指定实体的字段列表")
    List<EntityFieldRespDTO> getEntityFieldList(@Valid @RequestBody EntityFieldQueryReqDTO reqDTO);

    /**
     * 根据字段ID列表返回对应的JDBC数据类型和字段类型
     * 先查 metadata_entity_field 获取字段类型编码，再查 metadata_component_field_type 获取 data_type
     *
     * @param reqDTO 字段ID列表请求
     * @return 字段JDBC类型和字段类型信息列表，包含字段ID、字段名称、JDBC类型和字段类型
     */
    @Operation(summary = "根据字段ID列表返回JDBC数据类型和字段类型")
    List<EntityFieldJdbcTypeRespDTO> getFieldJdbcTypes(@Valid @RequestBody EntityFieldJdbcTypeReqDTO reqDTO);
}
