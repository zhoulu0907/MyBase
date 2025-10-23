package com.cmsr.onebase.module.metadata.api.validation;

import com.cmsr.onebase.module.metadata.api.validation.MetadataPermitApi;
import com.cmsr.onebase.module.metadata.api.validation.dto.PermitRefOtftRespDTO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataComponentFieldTypeDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataPermitRefOtftDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationTypeDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataSet;
import org.anyline.entity.Order;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限管理 API 实现类
 *
 * @author matianyu
 * @date 2025-09-12
 */
@Service
@Slf4j
public class MetadataPermitApiImpl implements MetadataPermitApi {

    @Resource
    private AnylineService<?> anylineService;

    @Override
    public List<PermitRefOtftRespDTO> getPermitRefOtftList() {
        try {
            // 1. 查询所有启用的字段类型
            DefaultConfigStore fieldTypeCs = new DefaultConfigStore();
            fieldTypeCs.and("status", 1); // 启用状态
            fieldTypeCs.and("deleted", 0);
            fieldTypeCs.order("sort_order", Order.TYPE.ASC);
            DataSet fieldTypeDataSet = anylineService.querys("metadata_component_field_type", fieldTypeCs);
            List<MetadataComponentFieldTypeDO> fieldTypes = fieldTypeDataSet.entity(MetadataComponentFieldTypeDO.class);

            if (fieldTypes == null || fieldTypes.isEmpty()) {
                log.warn("没有找到字段类型配置");
                return new ArrayList<>();
            }

            // 2. 创建字段类型ID到字段类型的映射
            Map<Long, MetadataComponentFieldTypeDO> fieldTypeMap = new HashMap<>();
            for (MetadataComponentFieldTypeDO fieldType : fieldTypes) {
                fieldTypeMap.put(fieldType.getId(), fieldType);
            }

            // 3. 查询权限参考操作类型关联配置
            DefaultConfigStore permitCs = new DefaultConfigStore();
            permitCs.and("deleted", 0);
            permitCs.order("field_type_id", Order.TYPE.ASC);
            permitCs.order("sort_order", Order.TYPE.ASC);
            DataSet permitDataSet = anylineService.querys("metadata_permit_ref_otft", permitCs);
            List<MetadataPermitRefOtftDO> permitRefOtftList = permitDataSet.entity(MetadataPermitRefOtftDO.class);

            if (permitRefOtftList == null || permitRefOtftList.isEmpty()) {
                log.warn("没有找到权限参考操作类型关联配置");
                return new ArrayList<>();
            }

            // 4. 查询校验类型信息
            DefaultConfigStore validationTypeCs = new DefaultConfigStore();
            validationTypeCs.and("status", 1); // 启用状态
            validationTypeCs.and("deleted", 0);
            validationTypeCs.order("sort_order", Order.TYPE.ASC);
            DataSet validationTypeDataSet = anylineService.querys("metadata_validation_type", validationTypeCs);
            List<MetadataValidationTypeDO> validationTypes = validationTypeDataSet.entity(MetadataValidationTypeDO.class);

            // 5. 创建校验类型ID到校验类型的映射
            Map<Long, MetadataValidationTypeDO> validationTypeMap = new HashMap<>();
            for (MetadataValidationTypeDO validationType : validationTypes) {
                validationTypeMap.put(validationType.getId(), validationType);
            }

            // 6. 组装结果数据
            List<PermitRefOtftRespDTO> result = new ArrayList<>();
            for (MetadataPermitRefOtftDO permitRef : permitRefOtftList) {
                MetadataComponentFieldTypeDO fieldType = fieldTypeMap.get(permitRef.getFieldTypeId());
                MetadataValidationTypeDO validationType = validationTypeMap.get(permitRef.getValidationTypeId());

                if (fieldType != null && validationType != null) {
                    PermitRefOtftRespDTO dto = new PermitRefOtftRespDTO();
                    dto.setId(validationType.getId());
                    dto.setOperationTypeCode(validationType.getValidationCode());
                    dto.setOperationTypeName(validationType.getValidationName());
                    dto.setFieldTypeCode(fieldType.getFieldTypeCode());
                    dto.setSort(permitRef.getSortOrder());
                    dto.setRemark(validationType.getValidationDesc());
                    result.add(dto);
                }
            }

            return result;
        } catch (Exception e) {
            log.error("获取权限参考操作类型列表失败", e);
            return new ArrayList<>();
        }
    }
}
