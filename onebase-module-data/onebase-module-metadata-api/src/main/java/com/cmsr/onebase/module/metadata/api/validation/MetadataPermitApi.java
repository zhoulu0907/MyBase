package com.cmsr.onebase.module.metadata.api.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.api.validation.dto.PermitRefOtftRespDTO;
import com.cmsr.onebase.module.metadata.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 数据权限 - 操作符与字段类型关联 查询 API
 *
 * 无入参，返回关联关系列表
 *
 * 对应 SQL 结构：
 * select mpo.id, mcft.field_type_code, mcft.field_type_name, mvt.validation_code, mvt.validation_name
 * from metadata_permit_ref_otft mpo, metadata_component_field_type mcft, metadata_validation_type mvt
 * where mpo.field_type_id = mcft.id and mpo.validation_type_id = mvt.id
 * order by mpo.sort_order
 *
 * @author bty418
 * @date 2025-08-14
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 数据权限关联")
public interface MetadataPermitApi {

    String PREFIX = ApiConstants.PREFIX + "/permit";

    @GetMapping(PREFIX + "/ref-otft/list")
    @Operation(summary = "查询操作符与字段类型关联列表", description = "无入参，返回关系列表")
    CommonResult<List<PermitRefOtftRespDTO>> getPermitRefOtftList();
}


