package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataPermitRefOtftDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.entity.Order;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 关联查询 Service 实现
 *
 * @author GitHub Copilot
 * @date 2025-09-11
 */
@Service
@Slf4j
public class MetadataPermitRefOtftServiceImpl implements MetadataPermitRefOtftService {

    @Resource
    private AnylineService<?> anylineService;

    @Override
    public List<MetadataPermitRefOtftDO> listByFieldTypeIds(Set<Long> fieldTypeIds) {
        List<MetadataPermitRefOtftDO> list = new ArrayList<>();
        if (fieldTypeIds == null || fieldTypeIds.isEmpty()) {
            return list;
        }
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and("deleted", 0);
        cs.and(Compare.IN, "field_type_id", new HashSet<>(fieldTypeIds));
        cs.order("field_type_id", Order.TYPE.ASC);
        cs.order("sort_order", Order.TYPE.ASC);
        DataSet ds = anylineService.querys("metadata_permit_ref_otft", cs);
        for (DataRow row : ds) {
            MetadataPermitRefOtftDO vo = row.entity(MetadataPermitRefOtftDO.class);
            if (vo != null) {
                list.add(vo);
            }
        }
        return list;
    }
}
