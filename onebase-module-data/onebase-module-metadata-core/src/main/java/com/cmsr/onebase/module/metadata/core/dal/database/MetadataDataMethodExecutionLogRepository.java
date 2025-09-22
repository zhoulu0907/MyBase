package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.method.MetadataDataMethodExecutionLogDO;

/**
 * 数据方法执行日志 Repository
 *
 * 提供基本的插入与简单查询接口，以便在引擎或服务层落日志。
 *
 * @author bty418
 * @date 2025-08-22
 */
public interface MetadataDataMethodExecutionLogRepository {

    /**
     * 新增执行日志
     *
     * @param logDO 日志对象
     */
    MetadataDataMethodExecutionLogDO insert(MetadataDataMethodExecutionLogDO logDO);

}
