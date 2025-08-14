package com.cmsr.onebase.module.metadata.api.entity;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import com.cmsr.onebase.module.metadata.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * RPC 服务 - 实体字段管理
 *
 * @author matianyu
 * @date 2025-08-14
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 实体字段管理")
public interface MetadataEntityFieldApi {

    String PREFIX = ApiConstants.PREFIX + "/entity-field";

    @PostMapping(PREFIX + "/list")
    @Operation(summary = "查询指定实体的字段列表", description = "根据查询条件获取指定实体的字段列表")
    CommonResult<List<EntityFieldRespDTO>> getEntityFieldList(@RequestBody EntityFieldQueryReqDTO reqDTO);

}
