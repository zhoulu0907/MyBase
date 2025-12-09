package com.cmsr.onebase.module.metadata.api.versionmanage.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cmsr.onebase.module.metadata.api.versionmanage.MetaDataVersionManageApi;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAppAndDatasourceRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberConfigRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataAutoNumberRuleItemRepository;

public class MetaDataVersionManageImplService implements MetaDataVersionManageApi {

    @Autowired
    private MetadataAppAndDatasourceRepository metadataAppAndDatasourceRepository;
    @Autowired
    private MetadataAutoNumberConfigRepository metadataAutoNumberConfigRepository;

    @Autowired
    private MetadataAutoNumberRuleItemRepository metadataAutoNumberRuleItemRepository;  


    @Override
    public void moveMetaDataRuntimeToHistory(Long applicationId, Long versionTag) {
        metadataAppAndDatasourceRepository.moveRuntimeToHistory(applicationId, versionTag);
        metadataAutoNumberConfigRepository.moveRuntimeToHistory(applicationId, versionTag);
        metadataAutoNumberRuleItemRepository.moveRuntimeToHistory(applicationId, versionTag);

    }

    @Override
    public void copyMetaDataEditToRuntime(Long applicationId) {
        metadataAppAndDatasourceRepository.copyEditToRuntime(applicationId);
        metadataAutoNumberConfigRepository.copyEditToRuntime(applicationId);
        metadataAutoNumberConfigRepository.copyEditToRuntime(applicationId);
    }


}
