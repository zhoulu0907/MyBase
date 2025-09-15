package com.cmsr.onebase.module.metadata.api.entity;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeReqDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

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
     * 根据字段ID列表返回对应的JDBC数据类型
     * 先查 metadata_entity_field 获取字段类型编码，再查 metadata_component_field_type 获取 data_type
     *
     * @param reqDTO 字段ID列表请求
     * @return 字段ID到JDBC类型的映射
     */
    @Operation(summary = "根据字段ID列表返回JDBC数据类型")
    Map<Long, String> getFieldJdbcTypes(@Valid @RequestBody EntityFieldJdbcTypeReqDTO reqDTO);
}
