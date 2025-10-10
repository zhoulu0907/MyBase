package com.cmsr.onebase.module.bpm.core.warmflow.anyline.dao.impl;

import com.cmsr.onebase.module.bpm.core.warmflow.anyline.entity.WfFlowDefinition;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.repository.FlowDefinitionRepository;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowDefinitionDO;
import jakarta.annotation.Resource;
import org.anyline.data.param.init.DefaultConfigStore;
import org.dromara.warm.flow.core.orm.agent.WarmQuery;
import org.dromara.warm.flow.core.orm.dao.FlowDefinitionDao;
import org.dromara.warm.flow.core.utils.page.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WarmFlow 流程定义 DAO 实现 V2
 * 使用 WfFlowDefinitionDov2 直接实现
 *
 * @author liyang
 * @date 2025-01-27
 */
public class WfFlowDefinitionDaoImpl implements FlowDefinitionDao<WfFlowDefinition> {

    @Resource
    private FlowDefinitionRepository flowDefinitionRepository;

    @Override
    public List<WfFlowDefinition> queryByCodeList(List<String> flowCodes) {
        if (flowCodes == null || flowCodes.isEmpty()) {
            return null;
        }

        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in("flow_code", flowCodes);
        List<BpmFlowDefinitionDO> bpmFlowDefinitionDOS = flowDefinitionRepository.findAllByConfig(configStore);
        return bpmFlowDefinitionDOS.stream()
                .map(this::convertToWfFlowDefinitionDov2)
                .collect(Collectors.toList());
    }

    @Override
    public void updatePublishStatus(List<Long> ids, Integer isPublish) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        for (Long id : ids) {
            BpmFlowDefinitionDO bpmFlowDefinitionDO = flowDefinitionRepository.findById(id);
            if (bpmFlowDefinitionDO != null) {
                bpmFlowDefinitionDO.setIsPublish(isPublish);
                flowDefinitionRepository.update(bpmFlowDefinitionDO);
            }
        }
    }

    @Override
    public WfFlowDefinition newEntity() {
        return new WfFlowDefinition();
    }

    @Override
    public WfFlowDeffinition selectById(Serializable id) {
        Long longId = convertToLong(id);
        if (longId == null) {
            return null;
        }
        BpmFlowDefinitionDO bpmFlowDefinitionDO = flowDefinitionRepository.findById(longId);
        return bpmFlowDefinitionDO == null ? null : convertToWfFlowDefinitionDov2(bpmFlowDefinitionDO);
    }

    @Override
    public List<WfFlowDefinition> selectByIds(Collection<? extends Serializable> ids) {
        return ids.stream()
                .map(id -> selectById(id))
                .filter(def -> def != null)
                .collect(Collectors.toList());
    }

    @Override
    public Page<WfFlowDefinition> selectPage(WfFlowDefinition definition, Page<WfFlowDefinition> page) {
        // 简化分页实现，直接返回传入的 page 对象
        return page;
    }

    @Override
    public List<WfFlowDefinition> selectList(WfFlowDefinition definition, WarmQuery<WfFlowDefinition> warmQuery) {
        DefaultConfigStore configStore = buildConfigStore(definition);
        List<BpmFlowDefinitionDO> bpmFlowDefinitionDOS = flowDefinitionRepository.findAllByConfig(configStore);
        return bpmFlowDefinitionDOS.stream()
                .map(this::convertToWfFlowDefinitionDov2)
                .collect(Collectors.toList());
    }

    @Override
    public long selectCount(WfFlowDefinition definition) {
        DefaultConfigStore configStore = buildConfigStore(definition);
        return flowDefinitionRepository.countByConfig(configStore);
    }

    @Override
    public int save(WfFlowDefinition definition) {
        BpmFlowDefinitionDO bpmFlowDefinitionDO = convertToFlowDefinitionDO(definition);
        flowDefinitionRepository.insert(bpmFlowDefinitionDO);
        // 更新ID
        definition.setId(bpmFlowDefinitionDO.getId());
        return 1;
    }

    @Override
    public int updateById(WfFlowDefinition definition) {
        BpmFlowDefinitionDO bpmFlowDefinitionDO = convertToFlowDefinitionDO(definition);
        flowDefinitionRepository.update(bpmFlowDefinitionDO);
        return 1;
    }

    @Override
    public int delete(WfFlowDefinition definition) {
        if (definition.getId() != null) {
            flowDefinitionRepository.deleteById(definition.getId());
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
        flowDefinitionRepository.deleteById(longId);
        return 1;
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        int count = 0;
        for (Serializable id : ids) {
            Long longId = convertToLong(id);
            if (longId != null) {
                flowDefinitionRepository.deleteById(longId);
                count++;
            }
        }
        return count;
    }

    @Override
    public void saveBatch(List<WfFlowDefinition> list) {
        for (WfFlowDefinition definition : list) {
            save(definition);
        }
    }

    @Override
    public void updateBatch(List<WfFlowDefinition> list) {
        for (WfFlowDefinition definition : list) {
            updateById(definition);
        }
    }

    /**
     * 构建查询条件
     */
    private DefaultConfigStore buildConfigStore(WfFlowDefinition definition) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (definition != null) {
            if (definition.getFlowCode() != null) {
                configStore.eq("flow_code", definition.getFlowCode());
            }
            if (definition.getFlowName() != null) {
                configStore.like("flow_name", definition.getFlowName());
            }
            if (definition.getVersion() != null) {
                configStore.eq("version", definition.getVersion());
            }
            if (definition.getIsPublish() != null) {
                configStore.eq("is_publish", definition.getIsPublish());
            }
            if (definition.getActivityStatus() != null) {
                configStore.eq("activity_status", definition.getActivityStatus());
            }
        }
        return configStore;
    }

    /**
     * 将 FlowDefinitionDO 转换为 WfFlowDefinitionDov2
     */
    private WfFlowDefinition convertToWfFlowDefinitionDov2(BpmFlowDefinitionDO bpmFlowDefinitionDO) {
        WfFlowDefinition wfFlowDefinitionDov2 = new WfFlowDefinition();
        wfFlowDefinitionDov2.setFlowDefinitionDO(bpmFlowDefinitionDO);
        return wfFlowDefinitionDov2;
    }

    /**
     * 将 WfFlowDefinitionDov2 转换为 FlowDefinitionDO
     */
    private BpmFlowDefinitionDO convertToFlowDefinitionDO(WfFlowDefinition wfFlowDefinitionDov2) {
        return wfFlowDefinitionDov2.getFlowDefinitionDO();
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