package com.cmsr.onebase.module.metadata.service.backupmanage;

import com.cmsr.onebase.module.metadata.controller.admin.backupmanage.vo.MetadataBackupRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.backupmanage.vo.MetadataRestoreReqVO;
import com.cmsr.onebase.module.metadata.service.entity.MetadataBusinessEntityService;
import com.cmsr.onebase.module.metadata.service.datasource.MetadataDatasourceBuildService;
import com.cmsr.onebase.module.metadata.service.entity.MetadataEntityFieldService;
import com.cmsr.onebase.module.metadata.service.relationship.MetadataEntityRelationshipService;
import com.cmsr.onebase.module.metadata.service.validation.MetadataValidationRuleService;
import com.cmsr.onebase.module.metadata.dal.database.MetadataDatasourceRepository;
import com.cmsr.onebase.module.metadata.dal.database.MetadataBusinessEntityRepository;
import com.cmsr.onebase.module.metadata.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.dal.database.MetadataValidationRuleRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
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
public class MetadataBackupServiceImpl implements MetadataBackupService {

    @Resource
    private MetadataDatasourceBuildService datasourceBuildService;

    @Resource
    private MetadataBusinessEntityService businessEntityService;

    @Resource
    private MetadataEntityFieldService entityFieldService;

    @Resource
    private MetadataEntityRelationshipService entityRelationshipService;

    @Resource
    private MetadataValidationRuleService validationRuleService;

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
        backupRespVO.setAppId(appId);
        backupRespVO.setBackupTime(LocalDateTime.now());

        // 1. 备份数据源
        DefaultConfigStore datasourceCondition = new DefaultConfigStore();
        datasourceCondition.and(MetadataDatasourceDO.APP_ID, appId);
        datasourceCondition.and("deleted", 0L);
        List<MetadataDatasourceDO> datasourceList = datasourceBuildService.findAllByConfig(datasourceCondition);
        backupRespVO.setDatasourceList(datasourceList);
        log.info("备份数据源数量: {}", datasourceList.size());

        // 2. 备份业务实体
        DefaultConfigStore entityCondition = new DefaultConfigStore();
        entityCondition.and(MetadataBusinessEntityDO.APP_ID, appId);
        entityCondition.and("deleted", 0L);
        List<MetadataBusinessEntityDO> businessEntityList = businessEntityService.findAllByConfig(entityCondition);
        backupRespVO.setBusinessEntityList(businessEntityList);
        log.info("备份业务实体数量: {}", businessEntityList.size());

        // 3. 备份实体字段
        DefaultConfigStore fieldCondition = new DefaultConfigStore();
        fieldCondition.and(MetadataEntityFieldDO.APP_ID, appId);
        fieldCondition.and("deleted", 0L);
        List<MetadataEntityFieldDO> entityFieldList = entityFieldService.findAllByConfig(fieldCondition);
        backupRespVO.setEntityFieldList(entityFieldList);
        log.info("备份实体字段数量: {}", entityFieldList.size());

        // 4. 备份实体关系
        DefaultConfigStore relationshipCondition = new DefaultConfigStore();
        relationshipCondition.and(MetadataEntityRelationshipDO.APP_ID, appId);
        relationshipCondition.and("deleted", 0L);
        List<MetadataEntityRelationshipDO> entityRelationshipList = entityRelationshipService.findAllByConfig(relationshipCondition);
        backupRespVO.setEntityRelationshipList(entityRelationshipList);
        log.info("备份实体关系数量: {}", entityRelationshipList.size());

        // 5. 备份校验规则
        DefaultConfigStore validationCondition = new DefaultConfigStore();
        validationCondition.and("app_id", appId);
        validationCondition.and("deleted", 0L);
        List<MetadataValidationRuleDefinitionDO> validationRuleList = validationRuleService.findAllByConfig(validationCondition);
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
        Long targetAppId = restoreReqVO.getTargetAppId();
        log.info("开始恢复元数据到应用ID为 {} 的应用", targetAppId);

        // 1. 软删除现有的元数据
        softDeleteExistingMetadata(targetAppId);

        // 2. 恢复数据源
        restoreDatasources(restoreReqVO.getDatasourceList(), targetAppId);

        // 3. 恢复业务实体
        restoreBusinessEntities(restoreReqVO.getBusinessEntityList(), targetAppId);

        // 4. 恢复实体字段
        restoreEntityFields(restoreReqVO.getEntityFieldList(), targetAppId);

        // 5. 恢复实体关系
        restoreEntityRelationships(restoreReqVO.getEntityRelationshipList(), targetAppId);

        // 6. 恢复校验规则
        restoreValidationRules(restoreReqVO.getValidationRuleList(), targetAppId);

        log.info("完成恢复元数据到应用ID为 {} 的应用", targetAppId);
    }

    /**
     * 软删除现有的元数据
     *
     * @param appId 应用ID
     */
    private void softDeleteExistingMetadata(Long appId) {
        log.info("开始软删除应用ID为 {} 的现有元数据", appId);

        // 软删除校验规则
        DefaultConfigStore validationCondition = new DefaultConfigStore();
        validationCondition.and("app_id", appId);
        validationCondition.and("deleted", 0L);
        List<MetadataValidationRuleDefinitionDO> existingValidationRules = validationRuleRepository.findAllByConfig(validationCondition);
        for (MetadataValidationRuleDefinitionDO rule : existingValidationRules) {
            rule.setDeleted(1L);
            validationRuleRepository.update(rule);
        }

        // 软删除实体关系
        DefaultConfigStore relationshipCondition = new DefaultConfigStore();
        relationshipCondition.and(MetadataEntityRelationshipDO.APP_ID, appId);
        relationshipCondition.and("deleted", 0L);
        List<MetadataEntityRelationshipDO> existingRelationships = entityRelationshipRepository.findAllByConfig(relationshipCondition);
        for (MetadataEntityRelationshipDO relationship : existingRelationships) {
            relationship.setDeleted(1L);
            entityRelationshipRepository.update(relationship);
        }

        // 软删除实体字段
        DefaultConfigStore fieldCondition = new DefaultConfigStore();
        fieldCondition.and(MetadataEntityFieldDO.APP_ID, appId);
        fieldCondition.and("deleted", 0L);
        List<MetadataEntityFieldDO> existingFields = entityFieldRepository.findAllByConfig(fieldCondition);
        for (MetadataEntityFieldDO field : existingFields) {
            field.setDeleted(1L);
            entityFieldRepository.update(field);
        }

        // 软删除业务实体
        DefaultConfigStore entityCondition = new DefaultConfigStore();
        entityCondition.and(MetadataBusinessEntityDO.APP_ID, appId);
        entityCondition.and("deleted", 0L);
        List<MetadataBusinessEntityDO> existingEntities = businessEntityRepository.findAllByConfig(entityCondition);
        for (MetadataBusinessEntityDO entity : existingEntities) {
            entity.setDeleted(1L);
            businessEntityRepository.update(entity);
        }

        // 软删除数据源
        DefaultConfigStore datasourceCondition = new DefaultConfigStore();
        datasourceCondition.and(MetadataDatasourceDO.APP_ID, appId);
        datasourceCondition.and("deleted", 0L);
        List<MetadataDatasourceDO> existingDatasources = datasourceRepository.findAllByConfig(datasourceCondition);
        for (MetadataDatasourceDO datasource : existingDatasources) {
            datasource.setDeleted(1L);
            datasourceRepository.update(datasource);
        }

        log.info("完成软删除应用ID为 {} 的现有元数据", appId);
    }

    /**
     * 恢复数据源
     *
     * @param datasourceList 数据源列表
     * @param targetAppId    目标应用ID
     */
    private void restoreDatasources(List<MetadataDatasourceDO> datasourceList, Long targetAppId) {
        log.info("开始恢复数据源，数量: {}", datasourceList.size());
        for (MetadataDatasourceDO datasource : datasourceList) {
            // 重置ID，让数据库自动生成新ID
            datasource.setId(null);
            datasource.setAppId(targetAppId);
            datasource.setDeleted(0L);
            datasource.setCreateTime(LocalDateTime.now());
            datasource.setUpdateTime(LocalDateTime.now());
            datasourceRepository.insert(datasource);
        }
        log.info("完成恢复数据源");
    }

    /**
     * 恢复业务实体
     *
     * @param businessEntityList 业务实体列表
     * @param targetAppId        目标应用ID
     */
    private void restoreBusinessEntities(List<MetadataBusinessEntityDO> businessEntityList, Long targetAppId) {
        log.info("开始恢复业务实体，数量: {}", businessEntityList.size());
        for (MetadataBusinessEntityDO entity : businessEntityList) {
            // 重置ID，让数据库自动生成新ID
            entity.setId(null);
            entity.setAppId(targetAppId);
            entity.setDeleted(0L);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            businessEntityRepository.insert(entity);
        }
        log.info("完成恢复业务实体");
    }

    /**
     * 恢复实体字段
     *
     * @param entityFieldList 实体字段列表
     * @param targetAppId     目标应用ID
     */
    private void restoreEntityFields(List<MetadataEntityFieldDO> entityFieldList, Long targetAppId) {
        log.info("开始恢复实体字段，数量: {}", entityFieldList.size());
        for (MetadataEntityFieldDO field : entityFieldList) {
            // 重置ID，让数据库自动生成新ID
            field.setId(null);
            field.setAppId(targetAppId);
            field.setDeleted(0L);
            field.setCreateTime(LocalDateTime.now());
            field.setUpdateTime(LocalDateTime.now());
            entityFieldRepository.insert(field);
        }
        log.info("完成恢复实体字段");
    }

    /**
     * 恢复实体关系
     *
     * @param entityRelationshipList 实体关系列表
     * @param targetAppId            目标应用ID
     */
    private void restoreEntityRelationships(List<MetadataEntityRelationshipDO> entityRelationshipList, Long targetAppId) {
        log.info("开始恢复实体关系，数量: {}", entityRelationshipList.size());
        for (MetadataEntityRelationshipDO relationship : entityRelationshipList) {
            // 重置ID，让数据库自动生成新ID
            relationship.setId(null);
            relationship.setAppId(targetAppId);
            relationship.setDeleted(0L);
            relationship.setCreateTime(LocalDateTime.now());
            relationship.setUpdateTime(LocalDateTime.now());
            entityRelationshipRepository.insert(relationship);
        }
        log.info("完成恢复实体关系");
    }

    /**
     * 恢复校验规则
     *
     * @param validationRuleList 校验规则列表
     * @param targetAppId        目标应用ID
     */
    private void restoreValidationRules(List<MetadataValidationRuleDefinitionDO> validationRuleList, Long targetAppId) {
        log.info("开始恢复校验规则，数量: {}", validationRuleList.size());
        for (MetadataValidationRuleDefinitionDO rule : validationRuleList) {
            // 重置ID，让数据库自动生成新ID
            rule.setId(null);
            // 注意：校验规则定义表没有app_id字段，所以不需要设置
            rule.setDeleted(0L);
            rule.setCreateTime(LocalDateTime.now());
            rule.setUpdateTime(LocalDateTime.now());
            validationRuleRepository.insert(rule);
        }
        log.info("完成恢复校验规则");
    }

}
