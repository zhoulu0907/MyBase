package com.cmsr.onebase.module.metadata.convert.backupmanage;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.api.backupmanage.dto.MetadataBackupReqDTO;
import com.cmsr.onebase.module.metadata.api.backupmanage.dto.MetadataBackupRespDTO;
import com.cmsr.onebase.module.metadata.api.backupmanage.dto.MetadataBusinessEntityDTO;
import com.cmsr.onebase.module.metadata.api.backupmanage.dto.MetadataDatasourceDTO;
import com.cmsr.onebase.module.metadata.api.backupmanage.dto.MetadataEntityFieldDTO;
import com.cmsr.onebase.module.metadata.api.backupmanage.dto.MetadataEntityRelationshipDTO;
import com.cmsr.onebase.module.metadata.api.backupmanage.dto.MetadataRestoreReqDTO;
import com.cmsr.onebase.module.metadata.api.backupmanage.dto.MetadataValidationRuleDTO;
import com.cmsr.onebase.module.metadata.controller.admin.backupmanage.vo.MetadataBackupReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.backupmanage.vo.MetadataBackupRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.backupmanage.vo.MetadataRestoreReqVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;

import java.util.List;

/**
 * 元数据备份恢复转换器
 *
 * @author matianyu
 * @date 2025-08-12
 */
public class MetadataBackupConvert {

    public static final MetadataBackupConvert INSTANCE = new MetadataBackupConvert();

    // DTO 与 VO 之间的转换
    public MetadataBackupReqVO convert(MetadataBackupReqDTO dto) {
        return BeanUtils.toBean(dto, MetadataBackupReqVO.class);
    }

    public MetadataBackupRespDTO convert(MetadataBackupRespVO vo) {
        return BeanUtils.toBean(vo, MetadataBackupRespDTO.class, dto -> {
            dto.setDatasourceList(convertDatasourceList(vo.getDatasourceList()));
            dto.setBusinessEntityList(convertEntityList(vo.getBusinessEntityList()));
            dto.setEntityFieldList(convertFieldList(vo.getEntityFieldList()));
            dto.setEntityRelationshipList(convertRelationshipList(vo.getEntityRelationshipList()));
            dto.setValidationRuleList(convertRuleList(vo.getValidationRuleList()));
        });
    }

    public MetadataRestoreReqVO convert(MetadataRestoreReqDTO dto) {
        return BeanUtils.toBean(dto, MetadataRestoreReqVO.class, vo -> {
            vo.setDatasourceList(convertDatasourceDOList(dto.getDatasourceList()));
            vo.setBusinessEntityList(convertEntityDOList(dto.getBusinessEntityList()));
            vo.setEntityFieldList(convertFieldDOList(dto.getEntityFieldList()));
            vo.setEntityRelationshipList(convertRelationshipDOList(dto.getEntityRelationshipList()));
            vo.setValidationRuleList(convertRuleDOList(dto.getValidationRuleList()));
        });
    }

    // DO 与 DTO 之间的转换
    public MetadataDatasourceDTO convert(MetadataDatasourceDO datasource) {
        return BeanUtils.toBean(datasource, MetadataDatasourceDTO.class);
    }

    public List<MetadataDatasourceDTO> convertDatasourceList(List<MetadataDatasourceDO> list) {
        return BeanUtils.toBean(list, MetadataDatasourceDTO.class);
    }

    public MetadataDatasourceDO convert(MetadataDatasourceDTO dto) {
        return BeanUtils.toBean(dto, MetadataDatasourceDO.class);
    }

    public List<MetadataDatasourceDO> convertDatasourceDOList(List<MetadataDatasourceDTO> list) {
        return BeanUtils.toBean(list, MetadataDatasourceDO.class);
    }

    public MetadataBusinessEntityDTO convert(MetadataBusinessEntityDO entity) {
        return BeanUtils.toBean(entity, MetadataBusinessEntityDTO.class);
    }

    public List<MetadataBusinessEntityDTO> convertEntityList(List<MetadataBusinessEntityDO> list) {
        return BeanUtils.toBean(list, MetadataBusinessEntityDTO.class);
    }

    public MetadataBusinessEntityDO convert(MetadataBusinessEntityDTO dto) {
        return BeanUtils.toBean(dto, MetadataBusinessEntityDO.class);
    }

    public List<MetadataBusinessEntityDO> convertEntityDOList(List<MetadataBusinessEntityDTO> list) {
        return BeanUtils.toBean(list, MetadataBusinessEntityDO.class);
    }

    public MetadataEntityFieldDTO convert(MetadataEntityFieldDO field) {
        return BeanUtils.toBean(field, MetadataEntityFieldDTO.class);
    }

    public List<MetadataEntityFieldDTO> convertFieldList(List<MetadataEntityFieldDO> list) {
        return BeanUtils.toBean(list, MetadataEntityFieldDTO.class);
    }

    public MetadataEntityFieldDO convert(MetadataEntityFieldDTO dto) {
        return BeanUtils.toBean(dto, MetadataEntityFieldDO.class);
    }

    public List<MetadataEntityFieldDO> convertFieldDOList(List<MetadataEntityFieldDTO> list) {
        return BeanUtils.toBean(list, MetadataEntityFieldDO.class);
    }

    public MetadataEntityRelationshipDTO convert(MetadataEntityRelationshipDO relationship) {
        return BeanUtils.toBean(relationship, MetadataEntityRelationshipDTO.class);
    }

    public List<MetadataEntityRelationshipDTO> convertRelationshipList(List<MetadataEntityRelationshipDO> list) {
        return BeanUtils.toBean(list, MetadataEntityRelationshipDTO.class);
    }

    public MetadataEntityRelationshipDO convert(MetadataEntityRelationshipDTO dto) {
        return BeanUtils.toBean(dto, MetadataEntityRelationshipDO.class);
    }

    public List<MetadataEntityRelationshipDO> convertRelationshipDOList(List<MetadataEntityRelationshipDTO> list) {
        return BeanUtils.toBean(list, MetadataEntityRelationshipDO.class);
    }

    public MetadataValidationRuleDTO convert(MetadataValidationRuleDefinitionDO rule) {
        return BeanUtils.toBean(rule, MetadataValidationRuleDTO.class);
    }

    public List<MetadataValidationRuleDTO> convertRuleList(List<MetadataValidationRuleDefinitionDO> list) {
        return BeanUtils.toBean(list, MetadataValidationRuleDTO.class);
    }

    public MetadataValidationRuleDefinitionDO convert(MetadataValidationRuleDTO dto) {
        return BeanUtils.toBean(dto, MetadataValidationRuleDefinitionDO.class);
    }

    public List<MetadataValidationRuleDefinitionDO> convertRuleDOList(List<MetadataValidationRuleDTO> list) {
        return BeanUtils.toBean(list, MetadataValidationRuleDefinitionDO.class);
    }

}
