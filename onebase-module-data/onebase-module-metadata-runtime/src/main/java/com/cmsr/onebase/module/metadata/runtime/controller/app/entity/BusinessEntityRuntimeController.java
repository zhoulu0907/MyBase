package com.cmsr.onebase.module.metadata.runtime.controller.app.entity;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.EntityWithFieldsBatchQueryReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.EntityWithFieldsRespVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.SimpleEntityRespVO;
import com.cmsr.onebase.module.metadata.runtime.service.entity.MetadataBusinessEntityRuntimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 运行态 - 业务实体管理
 *
 * @author matianyu
 * @date 2025-12-15
 */
@Tag(name = "运行态 - 业务实体管理")
@RestController
@RequestMapping("/metadata/business-entity")
@Validated
@Slf4j
public class BusinessEntityRuntimeController {

    @Resource
    private MetadataBusinessEntityRuntimeService businessEntityRuntimeService;

    /**
     * 根据应用ID获取实体列表
     *
     * @param appId 应用ID（可选，不传则从请求头获取）
     * @return 简单实体信息列表
     */
    @PostMapping("/list-by-app")
    @Operation(summary = "根据应用ID获取实体列表", description = "返回实体ID和名称，用于下拉选择等场景")
    @Parameter(name = "appId", description = "应用ID", required = false, example = "1024")
    public CommonResult<List<SimpleEntityRespVO>> getSimpleEntityListByAppId(
            @RequestParam(value = "appId", required = false) Long appId) {
        appId = ApplicationManager.getApplicationId();
        List<SimpleEntityRespVO> result = businessEntityRuntimeService.getSimpleEntityListByAppId(appId);
        return success(result);
    }

    /**
     * 批量查询实体及完整字段信息（包含一级子表）
     *
     * @param reqVO 批量查询请求VO（entityUuids和tableNames二选一）
     * @return 实体及字段信息列表
     */
    @PostMapping("/list-with-fields")
    @Operation(summary = "批量查询实体及完整字段信息", 
               description = "根据实体UUID列表或表名列表批量查询实体及其完整字段信息，包含一级子表")
    public CommonResult<List<EntityWithFieldsRespVO>> getEntitiesWithFullFields(
            @RequestBody @Valid EntityWithFieldsBatchQueryReqVO reqVO) {
        List<EntityWithFieldsRespVO> result = businessEntityRuntimeService.getEntitiesWithFullFields(reqVO);
        return success(result);
    }
}
