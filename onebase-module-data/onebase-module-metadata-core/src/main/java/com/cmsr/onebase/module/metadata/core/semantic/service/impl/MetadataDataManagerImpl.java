package com.cmsr.onebase.module.metadata.core.semantic.service.impl;

import com.cmsr.onebase.module.metadata.core.dal.database.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.cmsr.onebase.module.metadata.api.version.MetadataDataManagerApi;
import org.springframework.stereotype.Service;

@Service
public class MetadataDataManagerImpl implements MetadataDataManagerApi {

    @Autowired
    private MetadataAppAndDatasourceRepository metadataAppAndDatasourceRepository;

    @Autowired
    private MetadataAutoNumberConfigRepository metadataAutoNumberConfigRepository;

    @Autowired
    private MetadataAutoNumberRuleItemRepository metadataAutoNumberRuleItemRepository;

    @Autowired
    private MetadataAutoNumberStateRepository metadataAutoNumberStateRepository;

    @Autowired
    private MetadataBusinessEntityRepository metadataBusinessEntityRepository;

    @Autowired
    private MetadataDatasourceRepository metadataDatasourceRepository;

    @Autowired
    private MetadataEntityFieldOptionRepository metadataEntityFieldOptionRepository;

    @Autowired
    private MetadataEntityFieldRepository metadataEntityFieldRepository;

    @Autowired
    private MetadataEntityRelationshipRepository metadataEntityRelationshipRepository;

    @Autowired
    private MetadataValidationChildNotEmptyRepository metadataValidationChildNotEmptyRepository;

    @Autowired
    private MetadataValidationFormatRepository metadataValidationFormatRepository;

    @Autowired
    private MetadataValidationLengthRepository metadataValidationLengthRepository;

    @Autowired
    private MetadataValidationRangeRepository metadataValidationRangeRepository;

    @Autowired
    private MetadataValidationRequiredRepository metadataValidationRequiredRepository;

    @Autowired
    private MetadataValidationRuleDefinitionRepository metadataValidationRuleDefinitionRepository;

    @Autowired
    private MetadataValidationRuleGroupRepository metadataValidationRuleGroupRepository;

    @Autowired
    private MetadataValidationRuleRepository metadataValidationRuleRepository;

    @Autowired
    private MetadataValidationUniqueRepository metadataValidationUniqueRepository;


    @Override
    public void moveMetaDataRuntimeToHistory(Long applicationId, Long versionTag) {
        metadataAppAndDatasourceRepository.moveRuntimeToHistory(applicationId, versionTag);
        metadataAutoNumberConfigRepository.moveRuntimeToHistory(applicationId, versionTag);
        metadataAutoNumberRuleItemRepository.moveRuntimeToHistory(applicationId, versionTag);
        metadataAutoNumberStateRepository.moveRuntimeToHistory(applicationId,versionTag);
        metadataBusinessEntityRepository.moveRuntimeToHistory(applicationId,versionTag);
        metadataDatasourceRepository.moveRuntimeToHistory(applicationId,versionTag);
        metadataEntityFieldOptionRepository.moveRuntimeToHistory(applicationId,versionTag);
        metadataEntityFieldRepository.moveRuntimeToHistory(applicationId,versionTag);
        metadataEntityRelationshipRepository.moveRuntimeToHistory(applicationId,versionTag);
        metadataValidationChildNotEmptyRepository.moveRuntimeToHistory(applicationId,versionTag);
        metadataValidationFormatRepository.moveRuntimeToHistory(applicationId,versionTag);
        metadataValidationLengthRepository.moveRuntimeToHistory(applicationId,versionTag);
        metadataValidationRangeRepository.moveRuntimeToHistory(applicationId,versionTag);
        metadataValidationRequiredRepository.moveRuntimeToHistory(applicationId,versionTag);
        metadataValidationRuleDefinitionRepository.moveRuntimeToHistory(applicationId,versionTag);
        metadataValidationRuleGroupRepository.moveRuntimeToHistory(applicationId,versionTag);
        metadataValidationRuleRepository.moveRuntimeToHistory(applicationId,versionTag);
        metadataValidationUniqueRepository.moveRuntimeToHistory(applicationId,versionTag);
    }

    @Override
    public void copyMetaDataEditToRuntime(Long applicationId) {
        metadataAppAndDatasourceRepository.copyEditToRuntime(applicationId);
        metadataAutoNumberConfigRepository.copyEditToRuntime(applicationId);
        metadataAutoNumberRuleItemRepository.copyEditToRuntime(applicationId);
        metadataAutoNumberStateRepository.copyEditToRuntime(applicationId) ;
        metadataBusinessEntityRepository.copyEditToRuntime(applicationId) ;
        metadataDatasourceRepository.copyEditToRuntime(applicationId) ;
        metadataEntityFieldOptionRepository.copyEditToRuntime(applicationId) ;
        metadataEntityFieldRepository.copyEditToRuntime(applicationId) ;
        metadataEntityRelationshipRepository.copyEditToRuntime(applicationId) ;
        metadataValidationChildNotEmptyRepository.copyEditToRuntime(applicationId) ;
        metadataValidationFormatRepository.copyEditToRuntime(applicationId) ;
        metadataValidationLengthRepository.copyEditToRuntime(applicationId) ;
        metadataValidationRangeRepository.copyEditToRuntime(applicationId) ;
        metadataValidationRequiredRepository.copyEditToRuntime(applicationId) ;
        metadataValidationRuleDefinitionRepository.copyEditToRuntime(applicationId) ;
        metadataValidationRuleGroupRepository.copyEditToRuntime(applicationId) ;
        metadataValidationRuleRepository.copyEditToRuntime(applicationId) ;
        metadataValidationUniqueRepository.copyEditToRuntime(applicationId) ;
    }

    @Override
    public void deleteApplicationVersionData(Long applicationId, Long versionTag){
        metadataAppAndDatasourceRepository.deleteApplicationVersionData(applicationId, versionTag);
        metadataAutoNumberConfigRepository.deleteApplicationVersionData(applicationId, versionTag);
        metadataAutoNumberRuleItemRepository.deleteApplicationVersionData(applicationId, versionTag);
        metadataAutoNumberStateRepository.deleteApplicationVersionData(applicationId,versionTag);
        metadataBusinessEntityRepository.deleteApplicationVersionData(applicationId,versionTag);
        metadataDatasourceRepository.deleteApplicationVersionData(applicationId,versionTag);
        metadataEntityFieldOptionRepository.deleteApplicationVersionData(applicationId,versionTag);
        metadataEntityFieldRepository.deleteApplicationVersionData(applicationId,versionTag);
        metadataEntityRelationshipRepository.deleteApplicationVersionData(applicationId,versionTag);
        metadataValidationChildNotEmptyRepository.deleteApplicationVersionData(applicationId,versionTag);
        metadataValidationFormatRepository.deleteApplicationVersionData(applicationId,versionTag);
        metadataValidationLengthRepository.deleteApplicationVersionData(applicationId,versionTag);
        metadataValidationRangeRepository.deleteApplicationVersionData(applicationId,versionTag);
        metadataValidationRequiredRepository.deleteApplicationVersionData(applicationId,versionTag);
        metadataValidationRuleDefinitionRepository.deleteApplicationVersionData(applicationId,versionTag);
        metadataValidationRuleGroupRepository.deleteApplicationVersionData(applicationId,versionTag);
        metadataValidationRuleRepository.deleteApplicationVersionData(applicationId,versionTag);
        metadataValidationUniqueRepository.moveRuntimeToHistory(applicationId,versionTag);
    }

    @Override
    public void deleteAllApplicationData(Long applicationId) {
        metadataAppAndDatasourceRepository.deleteAllApplicationData(applicationId);
        metadataAutoNumberConfigRepository.deleteAllApplicationData(applicationId);
        metadataAutoNumberRuleItemRepository.deleteAllApplicationData(applicationId);
        metadataAutoNumberStateRepository.deleteAllApplicationData(applicationId) ;
        metadataBusinessEntityRepository.deleteAllApplicationData(applicationId) ;
        metadataDatasourceRepository.deleteAllApplicationData(applicationId) ;
        metadataEntityFieldOptionRepository.deleteAllApplicationData(applicationId) ;
        metadataEntityFieldRepository.deleteAllApplicationData(applicationId) ;
        metadataEntityRelationshipRepository.deleteAllApplicationData(applicationId) ;
        metadataValidationChildNotEmptyRepository.deleteAllApplicationData(applicationId) ;
        metadataValidationFormatRepository.deleteAllApplicationData(applicationId) ;
        metadataValidationLengthRepository.deleteAllApplicationData(applicationId) ;
        metadataValidationRangeRepository.deleteAllApplicationData(applicationId) ;
        metadataValidationRequiredRepository.deleteAllApplicationData(applicationId) ;
        metadataValidationRuleDefinitionRepository.deleteAllApplicationData(applicationId) ;
        metadataValidationRuleGroupRepository.deleteAllApplicationData(applicationId) ;
        metadataValidationRuleRepository.deleteAllApplicationData(applicationId) ;
        metadataValidationUniqueRepository.deleteAllApplicationData(applicationId) ;
    }

}
