package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldOptionDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 字段选项 仓储
 *
 * @author bty418
 * @date 2025-08-18
 */
@Repository
@Slf4j
public class MetadataEntityFieldOptionRepository extends DataRepository<MetadataEntityFieldOptionDO> {

    public MetadataEntityFieldOptionRepository() {
        super(MetadataEntityFieldOptionDO.class);
    }

    public List<MetadataEntityFieldOptionDO> findAllByFieldId(Long fieldId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataEntityFieldOptionDO.FIELD_ID, fieldId);
        cs.and("deleted", 0);
        cs.order(MetadataEntityFieldOptionDO.OPTION_ORDER, Order.TYPE.ASC);
        cs.order("create_time", Order.TYPE.ASC);
        return findAllByConfig(cs);
    }

    public void deleteByFieldId(Long fieldId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataEntityFieldOptionDO.FIELD_ID, fieldId);
        List<MetadataEntityFieldOptionDO> list = findAllByConfig(cs);
        for (MetadataEntityFieldOptionDO item : list) {
            deleteById(item.getId());
        }
    }
}


