package com.cmsr.onebase.module.metadata.api.entity;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 实体字段管理 API
 *
 * @author matianyu
 * @date 2025-09-10
 */
//@FeignClient(name = "onebase-module-metadata-build", path = "/metadata")
@Tag(name = "实体字段管理 API")
public interface MetadataEntityFieldApi {

    /**
     * 查询指定实体的字段列表
     *
     * @param reqDTO 查询请求参数
     * @return 字段列表
     */
    @PostMapping("/entity-field/list")
    @Operation(summary = "查询指定实体的字段列表")
    List<EntityFieldRespDTO> getEntityFieldList(@Valid @RequestBody EntityFieldQueryReqDTO reqDTO);
}
