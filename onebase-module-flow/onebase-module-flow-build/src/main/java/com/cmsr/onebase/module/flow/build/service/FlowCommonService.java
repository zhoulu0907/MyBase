package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @Author：huangjie
 * @Date：2025/10/21 15:59
 */
@Slf4j
@Setter
@Service
public class FlowCommonService {

    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Cacheable(value = "flow:process:name:cache", key = "#processId")
    public String getProcessName(Long processId) {
        return flowProcessRepository.findProcessNameById(processId);
    }

    @CacheEvict(value = "flow:process:name:cache", key = "#processId")
    public void clearProcessNameCache(Long processId) {
        log.debug("清除缓存: {}", processId);
    }

}
