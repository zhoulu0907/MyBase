package com.cmsr.onebase.module.engine.orm.anyline.dao;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowTask;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowTaskRepository;
import jakarta.annotation.Resource;
import org.dromara.warm.flow.core.orm.dao.FlowTaskDao;

import java.util.List;

/**
 * WarmFlow 待办任务 DAO 实现
 * 使用 WfFlowTaskDo 直接实现
 *
 * @author liyang
 * @date 2025-01-27
 */
public class FlowTaskDaoImpl extends WarmDaoImpl<FlowTask> implements FlowTaskDao<FlowTask> {

    @Resource
    private FlowTaskRepository flowTaskRepository;

    @Override
    public DataRepository<FlowTask> getRepository() {
        return flowTaskRepository;
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        // todo 待处理
        return 0;
    }

    @Override
    public List<FlowTask> getByInsIdAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        // todo 待处理
        return null;
    }

    @Override
    public FlowTask newEntity() {
        return newEntity();
    }
}
