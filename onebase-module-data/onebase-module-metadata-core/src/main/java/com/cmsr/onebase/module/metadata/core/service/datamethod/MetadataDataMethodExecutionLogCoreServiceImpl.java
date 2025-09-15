package com.cmsr.onebase.module.metadata.core.service.datamethod;

import com.cmsr.onebase.module.metadata.core.dal.database.MetadataDataMethodExecutionLogRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.method.MetadataDataMethodExecutionLogDO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 数据方法执行日志 Service 实现
 *
 * 简单封装 repository，后续可增加异步落库、限流、脱敏等能力。
 *
 * @author bty418
 * @date 2025-08-22
 */
@Service
public class MetadataDataMethodExecutionLogCoreServiceImpl implements MetadataDataMethodExecutionLogCoreService {

    @Resource
    private MetadataDataMethodExecutionLogRepository repository;

    @Override
    public void record(MetadataDataMethodExecutionLogDO logDO) {
        repository.insert(logDO);
    }
}
