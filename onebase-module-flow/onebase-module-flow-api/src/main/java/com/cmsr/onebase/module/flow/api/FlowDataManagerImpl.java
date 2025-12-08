package com.cmsr.onebase.module.flow.api;

import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
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

    @Override
    public void runtimeToHistory(Long applicationId, Long versionTag) {
        flowProcessRepository.runtimeToHistory(applicationId, versionTag);
    }

    @Override
    public void editToRuntime(Long applicationId) {

    }

}
