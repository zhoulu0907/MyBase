package com.cmsr.onebase.module.metadata.api.versionmanage.impl;

import com.cmsr.onebase.module.metadata.core.dal.database.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.cmsr.onebase.module.metadata.api.versionmanage.MetaDataVersionManageApi;

public class MetaDataVersionManageImplService implements MetaDataVersionManageApi {

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


}
