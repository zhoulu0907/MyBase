package com.cmsr.onebase.module.flow.service.mgmt;

import com.alibaba.druid.util.StringUtils;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.flow.controller.admin.mgmt.vo.*;
import com.cmsr.onebase.module.flow.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.dal.database.FlowProcessTriggerEntityRepository;
import com.cmsr.onebase.module.flow.dal.database.FlowProcessTriggerFormRepository;
import com.cmsr.onebase.module.flow.dal.database.FlowProcessTriggerTimeRepository;
import com.cmsr.onebase.module.flow.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.dal.dataobject.FlowProcessTriggerEntityDO;
import com.cmsr.onebase.module.flow.dal.dataobject.FlowProcessTriggerFormDO;
import com.cmsr.onebase.module.flow.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.enums.mgmt.FlowStatusEnum;
import com.cmsr.onebase.module.flow.enums.mgmt.FlowTriggerTypeEnum;
import com.cmsr.onebase.module.flow.graph.JsonGraph;
import com.cmsr.onebase.module.flow.graph.JsonGraphNode;
import com.cmsr.onebase.module.flow.graph.data.StartEntityNodeData;
import com.cmsr.onebase.module.flow.graph.data.StartFormNodeData;
import com.cmsr.onebase.module.flow.utils.JsonGraphConstant;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程管理服务实现类
 */
@Setter
@Service
public class FlowProcessMgmtServiceImpl implements FlowProcessMgmtService {

    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Autowired
    private FlowProcessTriggerFormRepository flowProcessTriggerFormRepository;

    @Autowired
    private FlowProcessTriggerEntityRepository flowProcessTriggerEntityRepository;

    @Autowired
    private FlowProcessTriggerTimeRepository flowProcessTriggerTimeRepository;

    @Override
    public PageResult<FlowProcessVO> pageList(PageFlowProcessReqVO reqVO) {
        // 分页查询
        PageResult<FlowProcessDO> pageResult = flowProcessRepository.findPageByQuery(reqVO);
        // DO转换为VO
        List<FlowProcessVO> voList = pageResult.getList().stream()
                .map(v -> {
                    FlowProcessVO vo = convertToVO(v);
                    vo.setProcessDefinition(null);
                    return vo;
                })
                .collect(Collectors.toList());
        return new PageResult<>(voList, pageResult.getTotal());
    }

    @Override
    public FlowProcessVO getDetail(Long id) {
        FlowProcessDO flowProcessDO = flowProcessRepository.findById(id);
        if (flowProcessDO == null) {
            return null;
        }
        FlowProcessVO flowProcessVO = convertToVO(flowProcessDO);
        Object triggerConfig = getTriggerConfig(flowProcessDO.getId(), flowProcessDO.getTriggerType());
        flowProcessVO.setTriggerConfig(triggerConfig);
        return flowProcessVO;
    }

    private Object getTriggerConfig(Long processId, String triggerType) {
        FlowTriggerTypeEnum flowTriggerTypeEnum = FlowTriggerTypeEnum.getByType(triggerType);
        if (flowTriggerTypeEnum == FlowTriggerTypeEnum.FORM) {
            return flowProcessTriggerFormRepository.findByProcessId(processId);
        } else if (flowTriggerTypeEnum == FlowTriggerTypeEnum.ENTITY) {
            return flowProcessTriggerEntityRepository.findByProcessId(processId);
        } else {
            return Collections.EMPTY_MAP;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CreateFlowProcessReqVO reqVO) {
        // 转换为DO对象
        FlowProcessDO flowProcessDO = new FlowProcessDO();
        BeanUtils.copyProperties(reqVO, flowProcessDO);
        flowProcessDO.setProcessStatus(FlowStatusEnum.DISABLE.getStatus());
        // 保存到数据库
        FlowProcessDO saved = flowProcessRepository.insert(flowProcessDO);
        saveTriggerConfig(flowProcessDO.getId(), reqVO.getTriggerType(), reqVO.getTriggerConfig());
        return saved.getId();
    }

    private void saveTriggerConfig(Long processId, String triggerType, Map<String, Object> triggerConfig) {
        if (MapUtils.isEmpty(triggerConfig)) {
            return;
        }
        if (FlowTriggerTypeEnum.getByType(triggerType) == FlowTriggerTypeEnum.FORM) {
            Long pageId = MapUtils.getLong(triggerConfig, JsonGraphConstant.PAGE_ID);
            Long fieldId = MapUtils.getLong(triggerConfig, JsonGraphConstant.FIELD_ID);
            FlowProcessTriggerFormDO flowProcessTriggerFormDO = new FlowProcessTriggerFormDO();
            flowProcessTriggerFormDO.setProcessId(processId);
            flowProcessTriggerFormDO.setPageId(pageId);
            flowProcessTriggerFormDO.setFieldId(fieldId);
            flowProcessTriggerFormRepository.insert(flowProcessTriggerFormDO);
        } else if (FlowTriggerTypeEnum.getByType(triggerType) == FlowTriggerTypeEnum.ENTITY) {
            Long entityId = MapUtils.getLong(triggerConfig, JsonGraphConstant.ENTITY_ID);
            FlowProcessTriggerEntityDO flowProcessTriggerEntityDO = new FlowProcessTriggerEntityDO();
            flowProcessTriggerEntityDO.setProcessId(processId);
            flowProcessTriggerEntityDO.setEntityId(entityId);
            flowProcessTriggerEntityRepository.insert(flowProcessTriggerEntityDO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateFlowProcessReqVO reqVO) {
        // 检查流程是否存在
        FlowProcessDO flowProcessDO = validateFlowProcessExist(reqVO.getId());
        // 更新字段
        BeanUtils.copyProperties(reqVO, flowProcessDO);
        // 保存更新
        flowProcessRepository.update(flowProcessDO);
    }

    @Override
    public void updateProcessDefinition(UpdateProcessDefinitionReqVO reqVO) {
        JsonGraph jsonGraph = JsonGraph.of(reqVO.getProcessDefinition());
        // 检查流程是否存在
        FlowProcessDO flowProcessDO = validateFlowProcessExist(reqVO.getId());
        // 更新流程定义
        flowProcessDO.setProcessDefinition(reqVO.getProcessDefinition());
        flowProcessDO.setProcessStatus(reqVO.getProcessStatus());
        // 保存更新
        flowProcessRepository.update(flowProcessDO);
        JsonGraphNode startNode = jsonGraph.getStartNode();
        updateProcessTriggerConfig(flowProcessDO.getId(), startNode);
    }

    private void updateProcessTriggerConfig(Long processId, JsonGraphNode startNode) {
        if (StringUtils.equals(JsonGraphConstant.START_FORM, startNode.getType())) {
            StartFormNodeData startFormNodeData = JsonUtils.treeToObject(startNode.getData(), StartFormNodeData.class);
            FlowProcessTriggerFormDO triggerFormDO = flowProcessTriggerFormRepository.findByProcessId(processId);

            boolean isNewRecord = (triggerFormDO == null);
            if (isNewRecord) {
                triggerFormDO = new FlowProcessTriggerFormDO();
            }

            // 设置触发器配置属性
            populateTriggerFormDO(triggerFormDO, processId, startFormNodeData);

            // 保存或更新记录
            if (isNewRecord) {
                flowProcessTriggerFormRepository.insert(triggerFormDO);
            } else {
                flowProcessTriggerFormRepository.update(triggerFormDO);
            }
        } else if (StringUtils.equals(JsonGraphConstant.START_ENTITY, startNode.getType())) {
            StartEntityNodeData startEntityNodeData = JsonUtils.treeToObject(startNode.getData(), StartEntityNodeData.class);
            FlowProcessTriggerEntityDO triggerEntityDO = flowProcessTriggerEntityRepository.findByProcessId(processId);

            boolean isNewRecord = (triggerEntityDO == null);
            if (isNewRecord) {
                triggerEntityDO = new FlowProcessTriggerEntityDO();
            }
            // 填充对象属性
            populateTriggerEntityDO(triggerEntityDO, processId, startEntityNodeData);
            // 保存或更新记录
            if (isNewRecord) {
                flowProcessTriggerEntityRepository.insert(triggerEntityDO);
            } else {
                flowProcessTriggerEntityRepository.update(triggerEntityDO);
            }
        } else if (StringUtils.equals(JsonGraphConstant.START_TIME, startNode.getType())) {

        }
    }


    /**
     * 填充FlowProcessTriggerFormDO对象属性
     */
    private void populateTriggerFormDO(FlowProcessTriggerFormDO triggerFormDO, Long processId, StartFormNodeData startFormNodeData) {
        triggerFormDO.setProcessId(processId);
        triggerFormDO.setPageId(startFormNodeData.getPageId());
        triggerFormDO.setFieldId(startFormNodeData.getFieldId());
        triggerFormDO.setTriggerScope(startFormNodeData.getTriggerScope());
        triggerFormDO.setTriggerUserType(startFormNodeData.getTriggerUserType());
        triggerFormDO.setTriggerUserValue(startFormNodeData.getTriggerUserValue());
        triggerFormDO.setTriggerEvents(JsonUtils.toJsonString(startFormNodeData.getTriggerEvents()));
        triggerFormDO.setFilterCondition(JsonUtils.toJsonString(startFormNodeData.getFilterCondition()));
        triggerFormDO.setIsChildTriggerAllowed(startFormNodeData.getIsChildTriggerAllowed());
    }

    private void populateTriggerEntityDO(FlowProcessTriggerEntityDO triggerEntityDO, Long processId, StartEntityNodeData startEntityNodeData) {
        triggerEntityDO.setProcessId(processId);
        triggerEntityDO.setEntityId(startEntityNodeData.getEntityId());
        triggerEntityDO.setTriggerEvents(JsonUtils.toJsonString(startEntityNodeData.getTriggerEvents()));
        triggerEntityDO.setTriggerFieldIds(JsonUtils.toJsonString(startEntityNodeData.getTriggerFieldIds()));
        triggerEntityDO.setFilterCondition(JsonUtils.toJsonString(startEntityNodeData.getFilterCondition()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void renameFlowProcess(RenameFlowProcessReqVO reqVO) {
        // 检查流程是否存在
        FlowProcessDO flowProcessDO = validateFlowProcessExist(reqVO.getId());
        // 更新流程名称
        flowProcessDO.setProcessName(reqVO.getProcessName());
        flowProcessRepository.update(flowProcessDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableFlowProcess(Long id) {
        // 检查流程是否存在
        FlowProcessDO flowProcessDO = validateFlowProcessExist(id);
        // 启用流程
        flowProcessDO.setProcessStatus(FlowStatusEnum.ENABLE.getStatus());
        flowProcessRepository.update(flowProcessDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableFlowProcess(Long id) {
        // 检查流程是否存在
        FlowProcessDO flowProcessDO = validateFlowProcessExist(id);
        // 关闭流程
        flowProcessDO.setProcessStatus(FlowStatusEnum.DISABLE.getStatus());
        flowProcessRepository.update(flowProcessDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 检查流程是否存在
        validateFlowProcessExist(id);
        // 删除流程
        flowProcessRepository.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> ids) {
        for (Long id : ids) {
            validateFlowProcessExist(id);
        }
        flowProcessRepository.deleteByIds(ids);
    }

    private FlowProcessDO validateFlowProcessExist(Long id) {
        FlowProcessDO flowProcessDO = flowProcessRepository.findById(id);
        if (flowProcessDO == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.FLOW_NOT_EXIST);
        }
        return flowProcessDO;
    }

    /**
     * DO转换为VO
     */
    private FlowProcessVO convertToVO(FlowProcessDO flowProcessDO) {
        return BeanUtils.toBean(flowProcessDO, FlowProcessVO.class);
    }
}
