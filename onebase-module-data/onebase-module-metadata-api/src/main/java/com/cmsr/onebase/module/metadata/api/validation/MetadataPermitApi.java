package com.cmsr.onebase.module.metadata.api.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.api.validation.dto.PermitRefOtftRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * 权限管理 sdk
 *
 * @author matianyu
 * @date 2025-09-10
 */
@Tag(name = "权限管理 sdk")
public interface MetadataPermitApi {

    /**
     * 获取权限参考操作类型列表
     *
     * @return 权限参考操作类型列表
     */
    @Operation(summary = "获取权限参考操作类型列表")
    List<PermitRefOtftRespDTO> getPermitRefOtftList();
}
