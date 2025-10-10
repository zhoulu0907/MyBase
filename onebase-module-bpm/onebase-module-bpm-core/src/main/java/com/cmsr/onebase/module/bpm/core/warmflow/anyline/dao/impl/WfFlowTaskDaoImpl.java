package com.cmsr.onebase.module.bpm.core.warmflow.anyline.dao.impl;

import com.cmsr.onebase.module.bpm.core.warmflow.anyline.repository.FlowTaskRepository;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowTaskDO;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.entity.WfFlowTask;
import jakarta.annotation.Resource;
import org.anyline.data.param.init.DefaultConfigStore;
import org.dromara.warm.flow.core.orm.agent.WarmQuery;
import org.dromara.warm.flow.core.orm.dao.FlowTaskDao;
import org.dromara.warm.flow.core.utils.page.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WarmFlow 待办任务 DAO 实现
 * 使用 WfFlowTaskDo 直接实现
 *
 * @author liyang
 * @date 2025-01-27
 */
public class WfFlowTaskDaoImpl implements FlowTaskDao<WfFlowTask> {

    @Resource
    private FlowTaskRepository flowTaskRepository;

    @Override
    public WfFlowTask newEntity() {
        WfFlowTask wfFlowTaskDo = new WfFlowTask();
        wfFlowTaskDo.setFlowTaskDO(new BpmFlowTaskDO());
        return wfFlowTaskDo;
    }

    @Override
    public WfFlowTask selectById(Serializable id) {
        Long longId = convertToLong(id);
        if (longId == null) {
            return null;
        }
        BpmFlowTaskDO flowTaskDO = flowTaskRepository.findById(longId);
        return flowTaskDO == null ? null : convertToWfFlowTaskDo(flowTaskDO);
    }

    @Override
    public List<WfFlowTask> selectByIds(Collection<? extends Serializable> ids) {
        return ids.stream()
                .map(this::selectById)
                .filter(task -> task != null)
                .collect(Collectors.toList());
    }

    @Override
    public Page<WfFlowTask> selectPage(WfFlowTask entity, Page<WfFlowTask> page) {
        // TODO: 实现分页逻辑
        return page;
    }

    @Override
    public List<WfFlowTask> selectList(WfFlowTask entity, WarmQuery<WfFlowTask> query) {
        DefaultConfigStore configStore = buildConfigStore(entity);
        List<BpmFlowTaskDO> flowTaskDOs = flowTaskRepository.findAllByConfig(configStore);
        return flowTaskDOs.stream()
                .map(this::convertToWfFlowTaskDo)
                .collect(Collectors.toList());
    }

    @Override
    public long selectCount(WfFlowTask entity) {
        DefaultConfigStore configStore = buildConfigStore(entity);
        return flowTaskRepository.countByConfig(configStore);
    }

    @Override
    public int save(WfFlowTask entity) {
        BpmFlowTaskDO flowTaskDO = convertToFlowTaskDO(entity);
        flowTaskRepository.insert(flowTaskDO);
        // 更新ID
        entity.setId(flowTaskDO.getId());
        return 1;
    }

    @Override
    public int updateById(WfFlowTask entity) {
        BpmFlowTaskDO flowTaskDO = convertToFlowTaskDO(entity);
        flowTaskRepository.update(flowTaskDO);
        return 1;
    }

    @Override
    public int delete(WfFlowTask entity) {
        if (entity.getId() != null) {
            flowTaskRepository.deleteById(entity.getId());
            return 1;
        }
        return 0;
    }

    @Override
    public int deleteById(Serializable id) {
        Long longId = convertToLong(id);
        if (longId == null) {
            return 0;
        }
        flowTaskRepository.deleteById(longId);
        return 1;
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        int count = 0;
        for (Serializable id : ids) {
            Long longId = convertToLong(id);
            if (longId != null) {
                flowTaskRepository.deleteById(longId);
                count++;
            }
        }
        return count;
    }

    @Override
    public void saveBatch(List<WfFlowTask> list) {
        for (WfFlowTask entity : list) {
            save(entity);
        }
    }

    @Override
    public void updateBatch(List<WfFlowTask> list) {
        for (WfFlowTask entity : list) {
            updateById(entity);
        }
    }

    @Override
    public List<WfFlowTask> getByInsIdAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        if (instanceId == null || nodeCodes == null || nodeCodes.isEmpty()) {
            return null;
        }

        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq("instance_id", instanceId);
        configStore.in("node_code", nodeCodes);
        List<BpmFlowTaskDO> flowTaskDOs = flowTaskRepository.findAllByConfig(configStore);
        return flowTaskDOs.stream()
                .map(this::convertToWfFlowTaskDo)
                .collect(Collectors.toList());
    }

    /**
     * 构建查询条件
     */
    private DefaultConfigStore buildConfigStore(WfFlowTask entity) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (entity != null) {
            if (entity.getDefinitionId() != null) {
                configStore.eq("definition_id", entity.getDefinitionId());
            }
            if (entity.getInstanceId() != null) {
                configStore.eq("instance_id", entity.getInstanceId());
            }
            if (entity.getNodeCode() != null) {
                configStore.eq("node_code", entity.getNodeCode());
            }
            if (entity.getFlowStatus() != null) {
                configStore.eq("flow_status", entity.getFlowStatus());
            }
        }
        return configStore;
    }

    /**
     * 将 FlowTaskDO 转换为 WfFlowTaskDo
     */
    private WfFlowTask convertToWfFlowTaskDo(BpmFlowTaskDO flowTaskDO) {
        WfFlowTask wfFlowTaskDo = new WfFlowTask();
        wfFlowTaskDo.setFlowTaskDO(flowTaskDO);
        return wfFlowTaskDo;
    }

    /**
     * 将 WfFlowTaskDo 转换为 FlowTaskDO
     */
    private BpmFlowTaskDO convertToFlowTaskDO(WfFlowTask wfFlowTaskDo) {
        return wfFlowTaskDo.getFlowTaskDO();
    }

    /**
     * 将 Serializable id 转换为 Long
     */
    private Long convertToLong(Serializable id) {
        if (id == null) {
            return null;
        }
        if (id instanceof Long) {
            return (Long) id;
        }
        if (id instanceof String) {
            try {
                return Long.parseLong((String) id);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        if (id instanceof Number) {
            return ((Number) id).longValue();
        }
        return null;
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        return 0;
    }
}
