package com.cmsr.onebase.module.bpm.core.warmflow.anyline.dao.impl;

import com.cmsr.onebase.module.bpm.core.warmflow.anyline.repository.FlowInstanceRepository;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowInstanceDO;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.entity.WfFlowInstance;
import jakarta.annotation.Resource;
import org.anyline.data.param.init.DefaultConfigStore;
import org.dromara.warm.flow.core.orm.agent.WarmQuery;
import org.dromara.warm.flow.core.orm.dao.FlowInstanceDao;
import org.dromara.warm.flow.core.utils.page.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WarmFlow 流程实例 DAO 实现
 * 使用 WfFlowInstanceDo 直接实现
 *
 * @author liyang
 * @date 2025-01-27
 */
public class WfFlowInstanceDaoImpl implements FlowInstanceDao<WfFlowInstance> {

    @Resource
    private FlowInstanceRepository flowInstanceRepository;

    @Override
    public WfFlowInstance newEntity() {
        WfFlowInstance wfFlowInstanceDo = new WfFlowInstance();
        return wfFlowInstanceDo;
    }

    @Override
    public WfFlowInstance selectById(Serializable id) {
        Long longId = convertToLong(id);
        if (longId == null) {
            return null;
        }
        BpmFlowInstanceDO flowInstanceDO = flowInstanceRepository.findById(longId);
        return flowInstanceDO == null ? null : convertToWfFlowInstanceDo(flowInstanceDO);
    }

    @Override
    public List<WfFlowInstance> selectByIds(Collection<? extends Serializable> ids) {
        return ids.stream()
                .map(this::selectById)
                .filter(instance -> instance != null)
                .collect(Collectors.toList());
    }

    @Override
    public Page<WfFlowInstance> selectPage(WfFlowInstance entity, Page<WfFlowInstance> page) {
        // TODO: 实现分页逻辑
        return page;
    }

    @Override
    public List<WfFlowInstance> selectList(WfFlowInstance entity, WarmQuery<WfFlowInstance> query) {
        DefaultConfigStore configStore = buildConfigStore(entity);
        List<BpmFlowInstanceDO> flowInstanceDOs = flowInstanceRepository.findAllByConfig(configStore);
        return flowInstanceDOs.stream()
                .map(this::convertToWfFlowInstanceDo)
                .collect(Collectors.toList());
    }

    @Override
    public long selectCount(WfFlowInstance entity) {
        DefaultConfigStore configStore = buildConfigStore(entity);
        return flowInstanceRepository.countByConfig(configStore);
    }

    @Override
    public int save(WfFlowInstance entity) {
        BpmFlowInstanceDO flowInstanceDO = convertToFlowInstanceDO(entity);
        flowInstanceRepository.insert(flowInstanceDO);
        // 更新ID
        entity.setId(flowInstanceDO.getId());
        return 1;
    }

    @Override
    public int updateById(WfFlowInstance entity) {
        BpmFlowInstanceDO flowInstanceDO = convertToFlowInstanceDO(entity);
        flowInstanceRepository.update(flowInstanceDO);
        return 1;
    }

    @Override
    public int delete(WfFlowInstance entity) {
        if (entity.getId() != null) {
            flowInstanceRepository.deleteById(entity.getId());
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
        flowInstanceRepository.deleteById(longId);
        return 1;
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        int count = 0;
        for (Serializable id : ids) {
            Long longId = convertToLong(id);
            if (longId != null) {
                flowInstanceRepository.deleteById(longId);
                count++;
            }
        }
        return count;
    }

    @Override
    public void saveBatch(List<WfFlowInstance> list) {
        for (WfFlowInstance entity : list) {
            save(entity);
        }
    }

    @Override
    public void updateBatch(List<WfFlowInstance> list) {
        for (WfFlowInstance entity : list) {
            updateById(entity);
        }
    }

    @Override
    public List<WfFlowInstance> getByDefIds(List<Long> defIds) {
        if (defIds == null || defIds.isEmpty()) {
            return null;
        }

        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in("definition_id", defIds);
        List<BpmFlowInstanceDO> flowInstanceDOs = flowInstanceRepository.findAllByConfig(configStore);
        return flowInstanceDOs.stream()
                .map(this::convertToWfFlowInstanceDo)
                .collect(Collectors.toList());
    }

    /**
     * 构建查询条件
     */
    private DefaultConfigStore buildConfigStore(WfFlowInstance entity) {
      return null;
    }

    /**
     * 将 FlowInstanceDO 转换为 WfFlowInstanceDo
     */
    private WfFlowInstance convertToWfFlowInstanceDo(BpmFlowInstanceDO flowInstanceDO) {
        WfFlowInstance wfFlowInstanceDo = new WfFlowInstance();
        return wfFlowInstanceDo;
    }

    /**
     * 将 WfFlowInstanceDo 转换为 FlowInstanceDO
     */
    private BpmFlowInstanceDO convertToFlowInstanceDO(WfFlowInstance wfFlowInstanceDo) {
        return wfFlowInstanceDo;
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
}
