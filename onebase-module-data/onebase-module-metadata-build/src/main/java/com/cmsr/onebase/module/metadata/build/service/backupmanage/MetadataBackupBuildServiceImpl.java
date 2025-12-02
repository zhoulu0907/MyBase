package com.cmsr.onebase.module.metadata.build.service.backupmanage;

import com.cmsr.onebase.module.metadata.build.controller.admin.backupmanage.vo.MetadataBackupRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.backupmanage.vo.MetadataRestoreReqVO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataDatasourceRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataBusinessEntityRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRuleRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 元数据备份恢复 Service 实现类
 *
 * @author matianyu
 * @date 2025-08-12
 */
@Service
@Slf4j
public class MetadataBackupBuildServiceImpl implements MetadataBackupBuildService {

    // 备份恢复功能需要直接操作数据库，所以注入Repository
    @Resource
    private MetadataDatasourceRepository datasourceRepository;

    @Resource
    private MetadataBusinessEntityRepository businessEntityRepository;

    @Resource
    private MetadataEntityFieldRepository entityFieldRepository;

    @Resource
    private MetadataEntityRelationshipRepository entityRelationshipRepository;

    @Resource
    private MetadataValidationRuleRepository validationRuleRepository;

    /**
     * 根据应用ID备份元数据
     *
     * @param appId 应用ID
     * @return 备份的元数据JSON
     */
    @Override
    public MetadataBackupRespVO backupMetadata(Long appId) {
        log.info("开始备份应用ID为 {} 的元数据", appId);

        MetadataBackupRespVO backupRespVO = new MetadataBackupRespVO();
        backupRespVO.setApplicationId(appId);
        backupRespVO.setBackupTime(LocalDateTime.now());

        // 1. 备份数据源
        List<MetadataDatasourceDO> datasourceList = datasourceRepository.getDatasourceListByAppId(appId);
        backupRespVO.setDatasourceList(datasourceList);
        log.info("备份数据源数量: {}", datasourceList.size());

        // 2. 备份业务实体
        List<MetadataBusinessEntityDO> businessEntityList = businessEntityRepository.getSimpleEntityListByAppId(appId);
        backupRespVO.setBusinessEntityList(businessEntityList);
        log.info("备份业务实体数量: {}", businessEntityList.size());

        // 3. 备份实体字段
        List<MetadataEntityFieldDO> entityFieldList = entityFieldRepository.getEntityFieldListByAppId(appId);
        backupRespVO.setEntityFieldList(entityFieldList);
        log.info("备份实体字段数量: {}", entityFieldList.size());

        // 4. 备份实体关系
        List<MetadataEntityRelationshipDO> entityRelationshipList = entityRelationshipRepository.getRelationshipsByAppId(appId);
        backupRespVO.setEntityRelationshipList(entityRelationshipList);
        log.info("备份实体关系数量: {}", entityRelationshipList.size());

        // 5. 备份校验规则（校验规则没有app_id字段，通过关联entity_id和field_id间接关联）
        List<MetadataValidationRuleDefinitionDO> validationRuleList = validationRuleRepository.getAllValidationRules();
        backupRespVO.setValidationRuleList(validationRuleList);
        log.info("备份校验规则数量: {}", validationRuleList.size());

        log.info("完成备份应用ID为 {} 的元数据", appId);
        return backupRespVO;
    }

    /**
     * 根据应用ID恢复元数据
     *
     * @param restoreReqVO 恢复请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreMetadata(MetadataRestoreReqVO restoreReqVO) {
        Long targetApplicationId = restoreReqVO.getTargetApplicationId();
        log.info("开始恢复元数据到应用ID为 {} 的应用", targetApplicationId);

        // 1. 软删除现有的元数据
        softDeleteExistingMetadata(targetApplicationId);

        // 2. 恢复数据源
        restoreDatasources(restoreReqVO.getDatasourceList(), targetApplicationId);

        // 3. 恢复业务实体
        restoreBusinessEntities(restoreReqVO.getBusinessEntityList(), targetApplicationId);

        // 4. 恢复实体字段
        restoreEntityFields(restoreReqVO.getEntityFieldList(), targetApplicationId);

        // 5. 恢复实体关系
        restoreEntityRelationships(restoreReqVO.getEntityRelationshipList(), targetApplicationId);

        // 6. 恢复校验规则
        restoreValidationRules(restoreReqVO.getValidationRuleList(), targetApplicationId);

        log.info("完成恢复元数据到应用ID为 {} 的应用", targetApplicationId);
    }

    /**
     * 软删除现有的元数据
     *
     * @param appId 应用ID
     */
    private void softDeleteExistingMetadata(Long appId) {
        log.info("开始软删除应用ID为 {} 的现有元数据", appId);

        // 软删除校验规则（通过关联的entity_id和field_id处理）
        List<MetadataValidationRuleDefinitionDO> existingValidationRules = validationRuleRepository.getAllValidationRules();
        for (MetadataValidationRuleDefinitionDO rule : existingValidationRules) {
            rule.setDeleted(1L);
            validationRuleRepository.updateById(rule);
        }

        // 软删除实体关系
        List<MetadataEntityRelationshipDO> existingRelationships = entityRelationshipRepository.getRelationshipsByAppId(appId);
        for (MetadataEntityRelationshipDO relationship : existingRelationships) {
            relationship.setDeleted(1L);
            entityRelationshipRepository.updateById(relationship);
        }

        // 软删除实体字段
        List<MetadataEntityFieldDO> existingFields = entityFieldRepository.getEntityFieldListByAppId(appId);
        for (MetadataEntityFieldDO field : existingFields) {
            field.setDeleted(1L);
            entityFieldRepository.updateById(field);
        }

        // 软删除业务实体
        List<MetadataBusinessEntityDO> existingEntities = businessEntityRepository.getSimpleEntityListByAppId(appId);
        for (MetadataBusinessEntityDO entity : existingEntities) {
            entity.setDeleted(1L);
            businessEntityRepository.updateById(entity);
        }

        // 软删除数据源
        List<MetadataDatasourceDO> existingDatasources = datasourceRepository.getDatasourceListByAppId(appId);
        for (MetadataDatasourceDO datasource : existingDatasources) {
            datasource.setDeleted(1L);
            datasourceRepository.updateById(datasource);
        }

        log.info("完成软删除应用ID为 {} 的现有元数据", appId);
    }

    /**
     * 恢复数据源
     *
     * @param datasourceList 数据源列表
     * @param targetApplicationId    目标应用ID
     */
    private void restoreDatasources(List<MetadataDatasourceDO> datasourceList, Long targetApplicationId) {
        log.info("开始恢复数据源，数量: {}", datasourceList.size());
        for (MetadataDatasourceDO datasource : datasourceList) {
            // 重置ID，让数据库自动生成新ID
            datasource.setId(null);
            datasource.setApplicationId(targetApplicationId);
            datasource.setDeleted(0L);
            datasource.setCreateTime(LocalDateTime.now());
            datasource.setUpdateTime(LocalDateTime.now());
            datasourceRepository.save(datasource);
        }
        log.info("完成恢复数据源");
    }

    /**
     * 恢复业务实体
     *
     * @param businessEntityList 业务实体列表
     * @param targetApplicationId        目标应用ID
     */
    private void restoreBusinessEntities(List<MetadataBusinessEntityDO> businessEntityList, Long targetApplicationId) {
        log.info("开始恢复业务实体，数量: {}", businessEntityList.size());
        for (MetadataBusinessEntityDO entity : businessEntityList) {
            // 重置ID，让数据库自动生成新ID
            entity.setId(null);
            entity.setApplicationId(targetApplicationId);
            entity.setDeleted(0L);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            businessEntityRepository.save(entity);
        }
        log.info("完成恢复业务实体");
    }

    /**
     * 恢复实体字段
     *
     * @param entityFieldList 实体字段列表
     * @param targetApplicationId     目标应用ID
     */
    private void restoreEntityFields(List<MetadataEntityFieldDO> entityFieldList, Long targetApplicationId) {
        log.info("开始恢复实体字段，数量: {}", entityFieldList.size());
        for (MetadataEntityFieldDO field : entityFieldList) {
            // 重置ID，让数据库自动生成新ID
            field.setId(null);
            field.setApplicationId(targetApplicationId);
            field.setDeleted(0L);
            field.setCreateTime(LocalDateTime.now());
            field.setUpdateTime(LocalDateTime.now());
            entityFieldRepository.save(field);
        }
        log.info("完成恢复实体字段");
    }

    /**
     * 恢复实体关系
     *
     * @param entityRelationshipList 实体关系列表
     * @param targetApplicationId            目标应用ID
     */
    private void restoreEntityRelationships(List<MetadataEntityRelationshipDO> entityRelationshipList, Long targetApplicationId) {
        log.info("开始恢复实体关系，数量: {}", entityRelationshipList.size());
        for (MetadataEntityRelationshipDO relationship : entityRelationshipList) {
            // 重置ID，让数据库自动生成新ID
            relationship.setId(null);
            relationship.setApplicationId(targetApplicationId);
            relationship.setDeleted(0L);
            relationship.setCreateTime(LocalDateTime.now());
            relationship.setUpdateTime(LocalDateTime.now());
            entityRelationshipRepository.save(relationship);
        }
        log.info("完成恢复实体关系");
    }

    /**
     * 恢复校验规则
     *
     * @param validationRuleList 校验规则列表
     * @param targetApplicationId        目标应用ID
     */
    private void restoreValidationRules(List<MetadataValidationRuleDefinitionDO> validationRuleList, Long targetApplicationId) {
        log.info("开始恢复校验规则，数量: {}", validationRuleList.size());
        for (MetadataValidationRuleDefinitionDO rule : validationRuleList) {
            // 重置ID，让数据库自动生成新ID
            rule.setId(null);
            // 注意：校验规则定义表没有app_id字段，所以不需要设置
            rule.setDeleted(0L);
            rule.setCreateTime(LocalDateTime.now());
            rule.setUpdateTime(LocalDateTime.now());
            validationRuleRepository.save(rule);
        }
        log.info("完成恢复校验规则");
    }

}
