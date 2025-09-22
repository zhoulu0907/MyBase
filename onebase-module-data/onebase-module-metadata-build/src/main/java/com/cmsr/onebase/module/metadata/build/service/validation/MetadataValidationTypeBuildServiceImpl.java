package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationTypeDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 校验类型查询 Service 实现
 *
 * @author GitHub Copilot
 * @date 2025-09-11
 */
@Service
@Slf4j
public class MetadataValidationTypeBuildServiceImpl implements MetadataValidationTypeBuildService {

    @Resource
    private AnylineService<?> anylineService;

    @Override
    public Map<Long, MetadataValidationTypeDO> getByIds(Set<Long> ids) {
        Map<Long, MetadataValidationTypeDO> map = new HashMap<>();
        if (ids == null || ids.isEmpty()) {
            return map;
        }
        // 拷贝到可修改集合，避免 Compare.IN 接收不可变集合导致潜在问题
        Set<Long> idSet = new HashSet<>(ids);
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and("deleted", 0);
        cs.and(Compare.IN, "id", idSet);
        DataSet ds = anylineService.querys("metadata_validation_type", cs);
        for (DataRow row : ds) {
            MetadataValidationTypeDO vo = row.entity(MetadataValidationTypeDO.class);
            if (vo != null && vo.getId() != null) {
                map.put(vo.getId(), vo);
            }
        }
        return map;
    }
}
