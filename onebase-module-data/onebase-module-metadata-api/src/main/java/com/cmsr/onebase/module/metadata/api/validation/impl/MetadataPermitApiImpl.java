// package com.cmsr.onebase.module.metadata.api.validation.impl;

// import com.cmsr.onebase.module.metadata.api.validation.MetadataPermitApi;
// import com.cmsr.onebase.module.metadata.api.validation.dto.PermitRefOtftRespDTO;
// import com.cmsr.onebase.module.metadata.core.dal.database.MetadataComponentFieldTypeRepository;
// import com.cmsr.onebase.module.metadata.core.dal.database.MetadataPermitRefOtftRepository;
// import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationTypeRepository;
// import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataComponentFieldTypeDO;
// import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataPermitRefOtftDO;
// import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationTypeDO;
// import jakarta.annotation.Resource;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

// /**
//  * 权限管理 API 实现类
//  *
//  * @author matianyu
//  * @date 2025-09-12
//  */
// @Service
// @Slf4j
// public class MetadataPermitApiImpl implements MetadataPermitApi {

//     @Resource
//     private MetadataComponentFieldTypeRepository componentFieldTypeRepository;

//     @Resource
//     private MetadataPermitRefOtftRepository permitRefOtftRepository;

//     @Resource
//     private MetadataValidationTypeRepository validationTypeRepository;

//     @Override
//     public List<PermitRefOtftRespDTO> getPermitRefOtftList() {
//         try {
//             // 1. 查询所有启用的字段类型
//             List<MetadataComponentFieldTypeDO> fieldTypes = componentFieldTypeRepository.findAllEnabled();
//             if (fieldTypes == null || fieldTypes.isEmpty()) {
//                 log.warn("没有找到字段类型配置");
//                 return new ArrayList<>();
//             }

//             // 2. 创建字段类型ID到字段类型的映射
//             Map<Long, MetadataComponentFieldTypeDO> fieldTypeMap = fieldTypes.stream()
//                     .collect(Collectors.toMap(MetadataComponentFieldTypeDO::getId, ft -> ft));

//             // 3. 查询权限参考操作类型关联配置
//             List<MetadataPermitRefOtftDO> permitRefOtftList = permitRefOtftRepository.findAll();
//             if (permitRefOtftList == null || permitRefOtftList.isEmpty()) {
//                 log.warn("没有找到权限参考操作类型关联配置");
//                 return new ArrayList<>();
//             }

//             // 4. 查询校验类型信息
//             List<MetadataValidationTypeDO> validationTypes = validationTypeRepository.findAllEnabled();

//             // 5. 创建校验类型ID到校验类型的映射
//             Map<Long, MetadataValidationTypeDO> validationTypeMap = validationTypes.stream()
//                     .collect(Collectors.toMap(MetadataValidationTypeDO::getId, vt -> vt));

//             // 6. 组装结果数据
//             return permitRefOtftList.stream()
//                     .map(permitRef -> {
//                         MetadataComponentFieldTypeDO fieldType = fieldTypeMap.get(permitRef.getFieldTypeId());
//                         MetadataValidationTypeDO validationType = validationTypeMap.get(permitRef.getValidationTypeId());

//                         if (fieldType != null && validationType != null) {
//                             PermitRefOtftRespDTO dto = new PermitRefOtftRespDTO();
//                             dto.setId(validationType.getId());
//                             dto.setOperationTypeCode(validationType.getValidationCode());
//                             dto.setOperationTypeName(validationType.getValidationName());
//                             dto.setFieldTypeCode(fieldType.getFieldTypeCode());
//                             dto.setSort(permitRef.getSortOrder());
//                             dto.setRemark(validationType.getValidationDesc());
//                             return dto;
//                         }
//                         return null;
//                     })
//                     .filter(dto -> dto != null)
//                     .collect(Collectors.toList());
//         } catch (Exception e) {
//             log.error("获取权限参考操作类型列表失败", e);
//             return new ArrayList<>();
//         }
//     }
// }
