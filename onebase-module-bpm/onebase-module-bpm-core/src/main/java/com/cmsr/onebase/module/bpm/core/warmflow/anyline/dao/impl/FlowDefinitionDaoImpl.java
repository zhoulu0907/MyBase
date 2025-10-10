package com.cmsr.onebase.module.bpm.core.warmflow.orm.dao.impl;//package com.cmsr.onebase.module.bpm.core.dal.dao.impl;
//
//import com.cmsr.onebase.module.bpm.core.dal.database.FlowDefinitionRepository;
//import com.cmsr.onebase.module.bpm.core.dal.database.WfFlowDefinitionRepository;
//import com.cmsr.onebase.module.bpm.core.dal.dataobject.WfFlowDefinitionDo;
//import com.cmsr.onebase.module.bpm.core.dal.dataobject.WfFlowDefinitionDo;
//import jakarta.annotation.Resource;
//import org.anyline.data.param.init.DefaultConfigStore;
//import org.dromara.warm.flow.core.orm.agent.WarmQuery;
//import org.dromara.warm.flow.core.orm.dao.FlowDefinitionDao;
//import org.dromara.warm.flow.core.utils.page.Page;
//import org.springframework.stereotype.Component;
//
//import java.io.Serializable;
//import java.util.Collection;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * WarmFlow 流程定义 DAO 实现
// * 使用 WfFlowDefinitionDo 直接实现
// *
// * @author liyang
// * @date 2025-09-29
// */
//@Component
//public class FlowDefinitionDaoImpl implements FlowDefinitionDao<WfFlowDefinitionDo> {
//
//    @Resource
//    private WfFlowDefinitionRepository wfFlowDefinitionRepository;
//
//    @Resource
//    private FlowDefinitionRepository flowDefinitionRepository;
//
//    @Override
//    public List<WfFlowDefinitionDo> queryByCodeList(List<String> flowCodes) {
//        if (flowCodes == null || flowCodes.isEmpty()) {
//            return null;
//        }
//
//        DefaultConfigStore configStore = new DefaultConfigStore();
//        configStore.in("flow_code", flowCodes);
//        return wfFlowDefinitionRepository.findAllByConfig(configStore);
//    }
//
//    @Override
//    public void updatePublishStatus(List<Long> ids, Integer isPublish) {
//        if (ids == null || ids.isEmpty()) {
//            return;
//        }
//
//        for (Long id : ids) {
//            WfFlowDefinitionDo WfFlowDefinitionDo = wfFlowDefinitionRepository.findById(id);
//            if (WfFlowDefinitionDo != null) {
//                WfFlowDefinitionDo.setIsPublish(isPublish);
//                wfFlowDefinitionRepository.update(WfFlowDefinitionDo);
//            }
//        }
//    }
//
//    @Override
//    public WfFlowDefinitionDo newEntity() {
//        return new WfFlowDefinitionDo();
//    }
//
//    @Override
//    public WfFlowDefinitionDo selectById(Serializable id) {
//        return wfFlowDefinitionRepository.findById((Long) id);
//    }
//
//    @Override
//    public List<WfFlowDefinitionDo> selectByIds(Collection<? extends Serializable> ids) {
//        return ids.stream()
//                .map(id -> selectById(id))
//                .filter(def -> def != null)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public Page<WfFlowDefinitionDo> selectPage(WfFlowDefinitionDo definition, Page<WfFlowDefinitionDo> page) {
//        // 简化分页实现，直接返回传入的 page 对象
//        return page;
//    }
//
//    @Override
//    public List<WfFlowDefinitionDo> selectList(WfFlowDefinitionDo definition, WarmQuery<WfFlowDefinitionDo> warmQuery) {
//        DefaultConfigStore configStore = buildConfigStore(definition);
//        return wfFlowDefinitionRepository.findAllByConfig(configStore);
//    }
//
//    @Override
//    public long selectCount(WfFlowDefinitionDo definition) {
//        DefaultConfigStore configStore = buildConfigStore(definition);
//        return wfFlowDefinitionRepository.countByConfig(configStore);
//    }
//
//    @Override
//    public int save(WfFlowDefinitionDo definition) {
//        wfFlowDefinitionRepository.insert(definition);
//        return 1;
//    }
//
//    @Override
//    public int updateById(WfFlowDefinitionDo definition) {
//        wfFlowDefinitionRepository.update(definition);
//        return 1;
//    }
//
//    @Override
//    public int delete(WfFlowDefinitionDo definition) {
//        if (definition.getId() != null) {
//            wfFlowDefinitionRepository.deleteById(definition.getId());
//            return 1;
//        }
//        return 0;
//    }
//
//    @Override
//    public int deleteById(Serializable id) {
//        wfFlowDefinitionRepository.deleteById((Long) id);
//        return 1;
//    }
//
//    @Override
//    public int deleteByIds(Collection<? extends Serializable> ids) {
//        for (Serializable id : ids) {
//            wfFlowDefinitionRepository.deleteById((Long) id);
//        }
//        return ids.size();
//    }
//
//    @Override
//    public void saveBatch(List<WfFlowDefinitionDo> list) {
//        for (WfFlowDefinitionDo definition : list) {
//            save(definition);
//        }
//    }
//
//    @Override
//    public void updateBatch(List<WfFlowDefinitionDo> list) {
//        for (WfFlowDefinitionDo definition : list) {
//            updateById(definition);
//        }
//    }
//
//    /**
//     * 构建查询条件
//     */
//    private DefaultConfigStore buildConfigStore(WfFlowDefinitionDo definition) {
//        DefaultConfigStore configStore = new DefaultConfigStore();
//
//        if (definition != null) {
//            if (definition.getFlowCode() != null) {
//                configStore.eq("flow_code", definition.getFlowCode());
//            }
//            if (definition.getFlowName() != null) {
//                configStore.like("flow_name", definition.getFlowName());
//            }
//            if (definition.getVersion() != null) {
//                configStore.eq("version", definition.getVersion());
//            }
//            if (definition.getIsPublish() != null) {
//                configStore.eq("is_publish", definition.getIsPublish());
//            }
//            if (definition.getActivityStatus() != null) {
//                configStore.eq("activity_status", definition.getActivityStatus());
//            }
//        }
//        return configStore;
//    }
//}