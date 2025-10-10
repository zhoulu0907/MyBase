package com.cmsr.onebase.module.bpm.core.warmflow.anyline.dao.impl;

import com.cmsr.onebase.module.bpm.core.warmflow.anyline.repository.FlowUserRepository;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject.BpmFlowUserDO;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.entity.WfFlowUser;
import jakarta.annotation.Resource;
import org.anyline.data.param.init.DefaultConfigStore;
import org.dromara.warm.flow.core.orm.agent.WarmQuery;
import org.dromara.warm.flow.core.orm.dao.FlowUserDao;
import org.dromara.warm.flow.core.utils.page.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class WfFlowUserDaoImpl implements FlowUserDao<WfFlowUser> {

    @Resource
    private FlowUserRepository flowUserRepository;

    @Override
    public WfFlowUser newEntity() {
        WfFlowUser wf = new WfFlowUser();
        wf.setFlowUserDO(new BpmFlowUserDO());
        return wf;
    }

    @Override
    public WfFlowUser selectById(Serializable id) {
        Long longId = convertToLong(id);
        if (longId == null) {
            return null;
        }
        BpmFlowUserDO data = flowUserRepository.findById(longId);
        return data == null ? null : convert(data);
    }

    @Override
    public List<WfFlowUser> selectByIds(Collection<? extends Serializable> ids) {
        return ids.stream().map(this::selectById).filter(v -> v != null).collect(Collectors.toList());
    }

    @Override
    public Page<WfFlowUser> selectPage(WfFlowUser entity, Page<WfFlowUser> page) {
        return page; // TODO 按需实现分页
    }

    @Override
    public List<WfFlowUser> selectList(WfFlowUser entity, WarmQuery<WfFlowUser> query) {
        DefaultConfigStore cfg = buildConfig(entity);
        List<BpmFlowUserDO> list = flowUserRepository.findAllByConfig(cfg);
        return list.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public long selectCount(WfFlowUser entity) {
        DefaultConfigStore cfg = buildConfig(entity);
        return flowUserRepository.countByConfig(cfg);
    }

    @Override
    public int save(WfFlowUser entity) {
        BpmFlowUserDO data = entity.getFlowUserDO();
        flowUserRepository.insert(data);
        entity.setId(data.getId());
        return 1;
    }

    @Override
    public int updateById(WfFlowUser entity) {
        flowUserRepository.update(entity.getFlowUserDO());
        return 1;
    }

    @Override
    public int delete(WfFlowUser entity) {
        if (entity.getId() != null) {
            flowUserRepository.deleteById(entity.getId());
            return 1;
        }
        return 0;
    }

    @Override
    public int deleteById(Serializable id) {
        Long longId = convertToLong(id);
        if (longId == null) return 0;
        flowUserRepository.deleteById(longId);
        return 1;
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        int c = 0;
        for (Serializable id : ids) {
            Long v = convertToLong(id);
            if (v != null) { flowUserRepository.deleteById(v); c++; }
        }
        return c;
    }

    @Override
    public void saveBatch(List<WfFlowUser> list) {
        for (WfFlowUser e : list) save(e);
    }

    @Override
    public void updateBatch(List<WfFlowUser> list) {
        for (WfFlowUser e : list) updateById(e);
    }

    @Override
    public List<WfFlowUser> listByProcessedBys(Long associated, List<String> processedBys, String[] types) {
        DefaultConfigStore c = new DefaultConfigStore();
        if (associated != null) c.eq("associated", associated);
        if (processedBys != null && !processedBys.isEmpty()) c.in("processed_by", processedBys);
        if (types != null && types.length > 0) c.in("type", types);
        List<BpmFlowUserDO> list = flowUserRepository.findAllByConfig(c);
        return list.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public List<WfFlowUser> listByAssociatedAndTypes(List<Long> associatedList, String[] types) {
        DefaultConfigStore c = new DefaultConfigStore();
        if (associatedList != null && !associatedList.isEmpty()) c.in("associated", associatedList);
        if (types != null && types.length > 0) c.in("type", types);
        List<BpmFlowUserDO> list = flowUserRepository.findAllByConfig(c);
        return list.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public int deleteByTaskIds(List<Long> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) return 0;
        DefaultConfigStore c = new DefaultConfigStore();
        c.in("associated", taskIds);
        List<BpmFlowUserDO> list = flowUserRepository.findAllByConfig(c);
        int count = 0;
        for (BpmFlowUserDO u : list) {
            if (u.getId() != null) {
                flowUserRepository.deleteById(u.getId());
                count++;
            }
        }
        return count;
    }

    private DefaultConfigStore buildConfig(WfFlowUser e) {
        DefaultConfigStore c = new DefaultConfigStore();
        if (e == null) return c;
        if (e.getType() != null) c.eq("type", e.getType());
        if (e.getProcessedBy() != null) c.eq("processed_by", e.getProcessedBy());
        if (e.getAssociated() != null) c.eq("associated", e.getAssociated());
        return c;
    }

    private WfFlowUser convert(BpmFlowUserDO data) {
        WfFlowUser wf = new WfFlowUser();
        wf.setFlowUserDO(data);
        return wf;
    }

    private Long convertToLong(Serializable id) {
        if (id == null) return null;
        if (id instanceof Long) return (Long) id;
        if (id instanceof String) try { return Long.parseLong((String) id); } catch (NumberFormatException ignored) { return null; }
        if (id instanceof Number) return ((Number) id).longValue();
        return null;
    }
}


