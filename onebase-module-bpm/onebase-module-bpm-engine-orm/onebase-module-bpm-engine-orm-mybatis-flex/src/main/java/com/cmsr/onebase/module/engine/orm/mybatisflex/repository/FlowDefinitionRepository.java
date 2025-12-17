package com.cmsr.onebase.module.engine.orm.mybatisflex.repository;

import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.framework.orm.repo.WarmFlowBaseBizRepository;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowDefinition;
import com.cmsr.onebase.module.engine.orm.mybatisflex.mapper.FlowDefinitionMapper;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * 流程定义 仓储
 * @author liyang
 * @date 2025-09-29
 */
@Repository
public class FlowDefinitionRepository extends WarmFlowBaseBizRepository<FlowDefinitionMapper, FlowDefinition> {

    @Override
    public boolean save(FlowDefinition entity) {
        // 如果 definitionUuid 为空，则生成新的 UUID
        if (StringUtils.isEmpty(entity.getDefinitionUuid())) {
            entity.setDefinitionUuid(UuidUtils.getUuid());
        }

        return super.save(entity);
    }
}


