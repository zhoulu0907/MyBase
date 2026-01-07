package com.cmsr.onebase.module.flow.core.impl;

import com.cmsr.onebase.module.flow.api.FlowDataManager;
import com.cmsr.onebase.module.flow.core.dal.database.*;
import com.cmsr.onebase.module.flow.core.handler.FlowChangeClient;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author：huangjie
 * @Date：2025/12/8 16:10
 */
@Setter
@Service
public class FlowDataManagerImpl implements FlowDataManager {

    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Autowired
    private FlowExecutionLogRepository flowExecutionLogRepository;

    @Autowired
    private FlowProcessDateFieldRepository flowProcessDateFieldRepository;

    @Autowired
    private FlowProcessTimeRepository flowProcessTimeRepository;

    @Autowired
    private FlowConnectorRepository flowConnectorRepository;

    @Autowired
    private FlowConnectorScriptRepository flowConnectorScriptRepository;

    @Autowired
    private FlowChangeClient flowChangeClient;

    @Override
    public void moveRuntimeToHistory(Long applicationId, Long versionTag) {
        flowProcessRepository.moveRuntimeToHistory(applicationId, versionTag);
    }

    @Override
    public void copyEditToRuntime(Long applicationId) {
        flowProcessRepository.copyEditToRuntime(applicationId);
    }

    @Override
    public void deleteRuntimeData(Long applicationId) {
        flowChangeClient.applicationDelete(applicationId);
    }

    @Override
    public void updateRuntimeData(Long applicationId) {
        flowChangeClient.applicationUpdate(applicationId);
    }

    @Override
    public void deleteAllApplicationData(Long applicationId) {
        flowProcessRepository.deleteAllApplicationData(applicationId);
        flowExecutionLogRepository.deleteAllApplicationData(applicationId);
        flowProcessDateFieldRepository.deleteAllApplicationData(applicationId);
        flowProcessTimeRepository.deleteAllApplicationData(applicationId);
        flowConnectorRepository.deleteAllApplicationData(applicationId);
        flowConnectorScriptRepository.deleteAllApplicationData(applicationId);
    }

    @Override
    public void deleteApplicationVersionData(Long applicationId, Long versionId) {
        flowProcessRepository.deleteApplicationVersionData(applicationId, versionId);
    }

}
