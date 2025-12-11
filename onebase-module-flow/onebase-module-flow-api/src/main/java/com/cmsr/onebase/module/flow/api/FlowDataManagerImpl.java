package com.cmsr.onebase.module.flow.api;

import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.handler.FlowCacheHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author：huangjie
 * @Date：2025/12/8 16:10
 */
@Service
public class FlowDataManagerImpl implements FlowDataManager {

    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Autowired
    private FlowCacheHandler flowCacheHandler;

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
        flowCacheHandler.onApplicationDelete(applicationId);
    }

    @Override
    public void onlineRuntimeData(Long applicationId) {
        flowCacheHandler.onApplicationChange(applicationId);
    }
}
