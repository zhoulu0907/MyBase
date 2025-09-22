package com.cmsr.onebase.module.metadata.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberResetLogDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 自动编号-重置日志 仓储
 */
@Repository
public class MetadataAutoNumberResetLogRepository extends DataRepository<MetadataAutoNumberResetLogDO> {
    public MetadataAutoNumberResetLogRepository() { super(MetadataAutoNumberResetLogDO.class); }

    public List<MetadataAutoNumberResetLogDO> listByConfig(Long configId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataAutoNumberResetLogDO.CONFIG_ID, configId);
        cs.and("deleted", 0);
        cs.order(MetadataAutoNumberResetLogDO.RESET_TIME, Order.TYPE.DESC);
        return findAllByConfig(cs);
    }

    public void deleteByConfigId(Long configId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataAutoNumberResetLogDO.CONFIG_ID, configId);
        deleteByConfig(cs);
    }
}


