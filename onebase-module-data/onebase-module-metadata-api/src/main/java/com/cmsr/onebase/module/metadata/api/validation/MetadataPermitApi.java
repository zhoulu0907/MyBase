package com.cmsr.onebase.module.metadata.api.validation;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.metadata.api.validation.dto.PermitRefOtftRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 权限管理 API
 *
 * @author matianyu
 * @date 2025-09-10
 */
@FeignClient(name = "onebase-module-metadata-build", path = "/metadata")
@Tag(name = "权限管理 API")
public interface MetadataPermitApi {

    /**
     * 获取权限参考操作类型列表
     *
     * @return 权限参考操作类型列表
     */
    @GetMapping("/permit/ref-otft-list")
    @Operation(summary = "获取权限参考操作类型列表")
    CommonResult<List<PermitRefOtftRespDTO>> getPermitRefOtftList();
}