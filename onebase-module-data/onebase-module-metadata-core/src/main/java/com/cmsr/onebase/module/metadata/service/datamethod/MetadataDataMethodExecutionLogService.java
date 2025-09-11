package com.cmsr.onebase.module.metadata.service.datamethod;

import com.cmsr.onebase.module.metadata.dal.dataobject.method.MetadataDataMethodExecutionLogDO;

/**
 * 数据方法执行日志 Service
 *
 * 负责为读/写引擎或服务落库执行日志，后续可扩展查询接口。
 *
 * @author bty418
 * @date 2025-08-22
 */
public interface MetadataDataMethodExecutionLogService {

    /**
     * 记录执行日志
     *
     * @param logDO 日志实体
     */
    void record(MetadataDataMethodExecutionLogDO logDO);
}
