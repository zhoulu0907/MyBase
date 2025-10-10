package com.cmsr.onebase.module.bpm.core.warmflow.anyline.dao.impl;

import com.cmsr.onebase.module.bpm.core.warmflow.anyline.repository.FlowSkipRepository;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowSkipDO;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.entity.WfFlowSkip;
import jakarta.annotation.Resource;
import org.anyline.data.param.init.DefaultConfigStore;
import org.dromara.warm.flow.core.orm.agent.WarmQuery;
import org.dromara.warm.flow.core.orm.dao.FlowSkipDao;
import org.dromara.warm.flow.core.utils.page.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class WfFlowSkipDaoImpl implements FlowSkipDao<WfFlowSkip> {

    @Resource
    private FlowSkipRepository flowSkipRepository;

    @Override
    public WfFlowSkip newEntity() {
        WfFlowSkip wf = new WfFlowSkip();
        return wf;
    }

    @Override
    public WfFlowSkip selectById(Serializable id) {
        Long longId = convertToLong(id);
        if (longId == null) return null;
        BpmFlowSkipDO data = flowSkipRepository.findById(longId);
        return data == null ? null : convert(data);
    }

    @Override
    public List<WfFlowSkip> selectByIds(Collection<? extends Serializable> ids) {
        return ids.stream().map(this::selectById).filter(v -> v != null).collect(Collectors.toList());
    }

    @Override
    public Page<WfFlowSkip> selectPage(WfFlowSkip entity, Page<WfFlowSkip> page) {
        return page; // TODO 分页按需实现
    }

    @Override
    public List<WfFlowSkip> selectList(WfFlowSkip entity, WarmQuery<WfFlowSkip> query) {
        DefaultConfigStore cfg = buildConfig(entity);
        List<BpmFlowSkipDO> list = flowSkipRepository.findAllByConfig(cfg);
        return list.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public long selectCount(WfFlowSkip entity) {
        DefaultConfigStore cfg = buildConfig(entity);
        return flowSkipRepository.countByConfig(cfg);
    }

    @Override
    public int save(WfFlowSkip entity) {
        flowSkipRepository.insert(entity);
        entity.setId(entity.getId());
        return 1;
    }

    @Override
    public int updateById(WfFlowSkip entity) {
        flowSkipRepository.update(entity);
        return 1;
    }

    @Override
    public int delete(WfFlowSkip entity) {
        if (entity.getId() != null) {
            flowSkipRepository.deleteById(entity.getId());
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
        flowSkipRepository.deleteById(longId);
        return 1;
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        int c = 0;
        for (Serializable id : ids) {
            Long v = convertToLong(id);
            if (v != null) { flowSkipRepository.deleteById(v); c++; }
        }
        return c;
    }

    @Override
    public void saveBatch(List<WfFlowSkip> list) {
        for (WfFlowSkip e : list) {
            save(e);
        }
    }

    @Override
    public void updateBatch(List<WfFlowSkip> list) {
        for (WfFlowSkip e : list) {
            updateById(e);
        }
    }

    @Override
    public int deleteSkipByDefIds(Collection<? extends Serializable> defIds) {
        int c = 0;
        for (Serializable id : defIds) {
            if (id == null) {
                continue;
            }
            DefaultConfigStore cfg = new DefaultConfigStore();
            cfg.eq("definition_id", id);
            // 简化：按条件查询再逐条删
            List<BpmFlowSkipDO> list = flowSkipRepository.findAllByConfig(cfg);
            for (BpmFlowSkipDO d : list) {
                if (d.getId() != null) { flowSkipRepository.deleteById(d.getId()); c++; }
            }
        }
        return c;
    }

    private DefaultConfigStore buildConfig(WfFlowSkip e) {
        DefaultConfigStore c = new DefaultConfigStore();
        if (e == null) {
            return c;
        }
        if (e.getDefinitionId() != null) {
            c.eq("definition_id", e.getDefinitionId());
        }
        if (e.getNowNodeCode() != null) {
            c.eq("now_node_code", e.getNowNodeCode());
        }
        if (e.getNextNodeCode() != null) {
            c.eq("next_node_code", e.getNextNodeCode());
        }
        if (e.getSkipType() != null) {
            c.eq("skip_type", e.getSkipType());
        }
        return c;
    }

    private WfFlowSkip convert(BpmFlowSkipDO data) {
        WfFlowSkip wf = new WfFlowSkip();
        return wf;
    }

    private Long convertToLong(Serializable id) {
        if (id == null) {
            return null;
        }
        if (id instanceof Long) {
            return (Long) id;
        }
        if (id instanceof String) {
            try { return Long.parseLong((String) id); } catch (NumberFormatException ignored) { return null; }
        }
        if (id instanceof Number) {
            return ((Number) id).longValue();
        }
        return null;
    }
}


