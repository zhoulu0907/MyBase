package com.cmsr.onebase.module.engine.orm.mybatisflex.dao;

import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowSkip;
import com.cmsr.onebase.module.engine.orm.mybatisflex.mapper.FlowSkipMapper;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowSkipRepository;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.dromara.warm.flow.core.orm.dao.FlowSkipDao;

import java.io.Serializable;
import java.util.Collection;

/**
 * 流程跳转 DAO 实现
 *
 * @author liyang
 * @date 2025-10-11
 */
public class FlowSkipDaoImpl extends WarmDaoImpl<FlowSkipMapper, FlowSkip> implements FlowSkipDao<FlowSkip> {
    @Resource
    private FlowSkipRepository flowSkipRepository;

    @Override
    public FlowSkipRepository getRepository() {
        return flowSkipRepository;
    }

    @Override
    public int deleteSkipByDefIds(Collection<? extends Serializable> defIds) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in(FlowSkip::getDefinitionId, defIds);

        return getRepository().remove(queryWrapper) ? 1 : 0;
    }

    @Override
    public FlowSkip newEntity() {
        return new FlowSkip();
    }
}


