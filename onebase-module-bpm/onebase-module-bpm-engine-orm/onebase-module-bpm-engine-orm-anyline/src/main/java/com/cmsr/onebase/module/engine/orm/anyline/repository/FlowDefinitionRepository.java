package com.cmsr.onebase.module.engine.orm.anyline.repository;

import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowDefinition;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * 流程定义 仓储
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowDefinitionRepository extends DataRepository<FlowDefinition> {

    public FlowDefinitionRepository() {
        super(FlowDefinition.class);
    }

    @Override
    public FlowDefinition insert(FlowDefinition entity) {
        // 如果 definitionUuid 为空，则生成新的 UUID
        if (StringUtils.isEmpty(entity.getDefinitionUuid())) {
            entity.setDefinitionUuid(UuidUtils.getUuid());
        }
        return super.insert(entity);
    }
}


