package com.cmsr.onebase.module.engine.orm.anyline.dao;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowSkip;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowSkipRepository;
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
public class FlowSkipDaoImpl extends WarmDaoImpl<FlowSkip> implements FlowSkipDao<FlowSkip> {

    @Resource
    private FlowSkipRepository flowSkipRepository;

    @Override
    public DataRepository<FlowSkip> getRepository() {
        return flowSkipRepository;
    }

    @Override
    public int deleteSkipByDefIds(Collection<? extends Serializable> defIds) {
        // todo 待处理
        return 0;
    }

    @Override
    public FlowSkip newEntity() {
        return new FlowSkip();
    }
}


