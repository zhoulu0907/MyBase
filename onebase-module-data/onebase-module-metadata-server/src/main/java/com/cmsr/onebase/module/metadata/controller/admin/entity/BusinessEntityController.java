package com.cmsr.onebase.module.metadata.controller.admin.entity;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.BusinessEntityPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.BusinessEntityRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.BusinessEntitySaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.ERDiagramRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.ERRelationshipVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.SimpleEntityRespVO;
import com.cmsr.onebase.module.metadata.convert.entity.BusinessEntityConvert;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.service.entity.MetadataBusinessEntityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 业务实体管理
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Tag(name = "管理后台 - 业务实体管理")
@RestController
@RequestMapping("/metadata/business-entity")
@Validated
@Slf4j
public class BusinessEntityController {

    @Resource
    private MetadataBusinessEntityService businessEntityService;

    @PostMapping("/create")
    @Operation(summary = "创建业务实体")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:create')")
    public CommonResult<BusinessEntityRespVO> createBusinessEntity(@Valid @RequestBody BusinessEntitySaveReqVO reqVO) {
        Long id = businessEntityService.createBusinessEntity(reqVO);
        MetadataBusinessEntityDO businessEntity = businessEntityService.getBusinessEntity(id);
        return success(BusinessEntityConvert.INSTANCE.convert(businessEntity));
    }

    @PostMapping("/update")
    @Operation(summary = "更新业务实体信息")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:update')")
    public CommonResult<Boolean> updateBusinessEntity(@Valid @RequestBody BusinessEntitySaveReqVO reqVO) {
        businessEntityService.updateBusinessEntity(reqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "软删除业务实体")
    @Parameter(name = "id", description = "业务实体ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:delete')")
    public CommonResult<Boolean> deleteBusinessEntity(@RequestParam("id") Long id) {
        businessEntityService.deleteBusinessEntity(id);
        return success(true);
    }

    @PostMapping("/get")
    @Operation(summary = "根据ID获取业务实体详细信息")
    @Parameter(name = "id", description = "业务实体ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:query')")
    public CommonResult<BusinessEntityRespVO> getBusinessEntity(@RequestParam("id") Long id) {
        MetadataBusinessEntityDO businessEntity = businessEntityService.getBusinessEntity(id);
        return success(BusinessEntityConvert.INSTANCE.convert(businessEntity));
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询业务实体列表")
    @PreAuthorize("@ss.hasPermission('metadata:business-entity:query')")
    public CommonResult<PageResult<BusinessEntityRespVO>> getBusinessEntityPage(@Valid BusinessEntityPageReqVO pageReqVO) {
        PageResult<MetadataBusinessEntityDO> pageResult = businessEntityService.getBusinessEntityPage(pageReqVO);
        return success(BusinessEntityConvert.INSTANCE.convertPage(pageResult));
    }

    @PostMapping("/list-by-datasource")
    @Operation(summary = "根据数据源获得业务实体列表")
    @Parameter(name = "datasourceId", description = "数据源ID", required = true, example = "1024")
    //@PreAuthorize("@ss.hasPermission('metadata:business-entity:query')")
    public CommonResult<List<BusinessEntityRespVO>> getBusinessEntityListByDatasourceId(@RequestParam("datasourceId") Long datasourceId) {
        // 1. 获取业务实体列表
        List<MetadataBusinessEntityDO> list = businessEntityService.getBusinessEntityListByDatasourceId(datasourceId);
        
        // 2. 转换为 VO
        List<BusinessEntityRespVO> result = BusinessEntityConvert.INSTANCE.convertList(list);
        
        // 3. 参考 getERDiagramByDatasourceId 的实现，填充 relationType 字段
        // relationType 用于标识实体在关系中的角色：PARENT(主表/父表) 或 CHILD(子表)
        if (!result.isEmpty()) {
            // 复用 ER 图服务获取关系信息，保持逻辑一致性
            ERDiagramRespVO erDiagram = businessEntityService.getERDiagramByDatasourceId(datasourceId);
            List<ERRelationshipVO> relationships = erDiagram.getRelationships();
            
            // 收集所有作为源实体(主表)和目标实体(子表)的ID
            Set<String> sourceIds = relationships.stream()
                    .map(ERRelationshipVO::getSourceEntityId)
                    .collect(Collectors.toSet());
            Set<String> targetIds = relationships.stream()
                    .map(ERRelationshipVO::getTargetEntityId)  
                    .collect(Collectors.toSet());
                    
            // 为每个实体设置关系类型
            for (BusinessEntityRespVO entity : result) {
                if (sourceIds.contains(entity.getId())) {
                    entity.setRelationType("PARENT");  // 主表：其他表引用此表
                }
                if (targetIds.contains(entity.getId())) {
                    entity.setRelationType("CHILD");   // 子表：引用其他表的外键
                }
                // 注意：一个实体可能既是某些关系的主表，又是其他关系的子表
                // 在这种情况下，最后设置的值会覆盖前面的值
                // 如果既不是源实体也不是目标实体，relationType 保持 null
            }
        }
        
        return success(result);
    }

    @PostMapping("/er-diagram")
    @Operation(summary = "根据数据源ID获取ER图数据", description = "获取指定数据源下所有实体信息、字段信息以及实体间的关联关系，用于前端绘制ER图")
    @Parameter(name = "datasourceId", description = "数据源ID", required = true, example = "1024")
    //@PreAuthorize("@ss.hasPermission('metadata:business-entity:query')")
    public CommonResult<ERDiagramRespVO> getERDiagramByDatasourceId(@RequestParam("datasourceId") Long datasourceId) {
        ERDiagramRespVO result = businessEntityService.getERDiagramByDatasourceId(datasourceId);
        return success(result);
    }

    @PostMapping("/list-by-app")
    @Operation(summary = "根据应用ID获取实体列表", description = "返回实体ID和名称，用于下拉选择等场景")
    @Parameter(name = "appId", description = "应用ID", required = true, example = "1024")
    //@PreAuthorize("@ss.hasPermission('metadata:business-entity:query')")
    public CommonResult<List<SimpleEntityRespVO>> getSimpleEntityListByAppId(@RequestParam("appId") Long appId) {
        List<SimpleEntityRespVO> result = businessEntityService.getSimpleEntityListByAppId(appId);
        return success(result);
    }

}
