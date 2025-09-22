package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.method.MetadataDataMethodExecutionLogDO;
import org.springframework.stereotype.Repository;

/**
 * 数据方法执行日志 Repository 实现
 *
 * 通过继承通用 DataRepository，提供基础的插入能力；实现接口以便被 Service 以接口方式注入。
 *
 * @author bty418
 * @date 2025-08-25
 */
@Repository
public class MetadataDataMethodExecutionLogRepositoryImpl
        extends DataRepository<MetadataDataMethodExecutionLogDO>
        implements MetadataDataMethodExecutionLogRepository {

    /**
     * 指定默认实体类
     */
    public MetadataDataMethodExecutionLogRepositoryImpl() {
        super(MetadataDataMethodExecutionLogDO.class);
    }

    /**
     * 新增执行日志
     *
     * @param logDO 日志对象
     */
    @Override
    public MetadataDataMethodExecutionLogDO insert(MetadataDataMethodExecutionLogDO logDO) {
        // 复用通用仓储的插入能力
        return super.insert(logDO);
    }
}
