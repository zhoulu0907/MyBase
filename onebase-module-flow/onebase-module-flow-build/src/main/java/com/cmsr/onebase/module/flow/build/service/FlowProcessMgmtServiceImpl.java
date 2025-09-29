package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.flow.build.graph.Graph;
import com.cmsr.onebase.module.flow.build.vo.*;
import com.cmsr.onebase.module.flow.core.dal.database.*;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDateFieldDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessEntityDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessFormDO;
import com.cmsr.onebase.module.flow.core.enums.FlowEnableStatusEnum;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.core.enums.FlowTriggerTypeEnum;
import com.cmsr.onebase.module.flow.core.enums.JsonGraphConstant;
import com.cmsr.onebase.module.flow.core.vo.PageFlowProcessReqVO;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeRespDTO;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private FlowProcessFormRepository flowProcessFormRepository;

    @Autowired
    private FlowProcessEntityRepository flowProcessEntityRepository;

    @Autowired
    private FlowProcessDateFieldRepository flowProcessDateFieldRepository;

    @Autowired
    private FlowProcessTimeRepository flowProcessTimeRepository;

    @Autowired
    private MetadataEntityFieldApi metadataEntityFieldApi;

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
        FlowTriggerTypeEnum triggerTypeEnum = FlowTriggerTypeEnum.getByType(flowProcessDO.getTriggerType());
        if (triggerTypeEnum == FlowTriggerTypeEnum.FORM) {
            FlowProcessFormDO flowProcessFormDO = flowProcessFormRepository.findByProcessId(id);
            flowProcessVO.setTriggerConfig(Map.of(JsonGraphConstant.PAGE_ID, flowProcessFormDO.getPageId()));
        } else if (triggerTypeEnum == FlowTriggerTypeEnum.ENTITY) {
            FlowProcessEntityDO flowProcessEntityDO = flowProcessEntityRepository.findByProcessId(id);
            flowProcessVO.setTriggerConfig(Map.of(JsonGraphConstant.ENTITY_ID, flowProcessEntityDO.getEntityId()));
        } else if (triggerTypeEnum == FlowTriggerTypeEnum.DATE_FIELD) {
            FlowProcessDateFieldDO flowProcessDateFieldDO = flowProcessDateFieldRepository.findByProcessId(id);
            flowProcessVO.setTriggerConfig(Map.of(JsonGraphConstant.ENTITY_ID, flowProcessDateFieldDO.getEntityId()));
        }
        return flowProcessVO;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CreateFlowProcessReqVO reqVO) {
        // 转换为DO对象
        FlowProcessDO flowProcessDO = new FlowProcessDO();
        BeanUtils.copyProperties(reqVO, flowProcessDO);
        // 禁用
        flowProcessDO.setEnableStatus(FlowEnableStatusEnum.DISABLE.getStatus());
        // 保存到数据库
        FlowProcessDO saved = flowProcessRepository.insert(flowProcessDO);
        saveAdditional(flowProcessDO, reqVO.getTriggerConfig());
        return saved.getId();
    }

    private void saveAdditional(FlowProcessDO flowProcessDO, Map<String, Object> triggerConfig) {
        FlowTriggerTypeEnum triggerTypeEnum = FlowTriggerTypeEnum.getByType(flowProcessDO.getTriggerType());
        if (triggerTypeEnum == FlowTriggerTypeEnum.FORM) {
            Long pageId = MapUtils.getLong(triggerConfig, JsonGraphConstant.PAGE_ID);
            FlowProcessFormDO flowProcessFormDO = new FlowProcessFormDO();
            flowProcessFormDO.setProcessId(flowProcessDO.getId());
            flowProcessFormDO.setPageId(pageId);
            flowProcessFormRepository.insert(flowProcessFormDO);
        } else if (triggerTypeEnum == FlowTriggerTypeEnum.ENTITY) {
            Long entityId = MapUtils.getLong(triggerConfig, JsonGraphConstant.ENTITY_ID);
            FlowProcessEntityDO flowProcessEntityDO = new FlowProcessEntityDO();
            flowProcessEntityDO.setProcessId(flowProcessDO.getId());
            flowProcessEntityDO.setEntityId(entityId);
            flowProcessEntityRepository.insert(flowProcessEntityDO);
        } else if (triggerTypeEnum == FlowTriggerTypeEnum.DATE_FIELD) {
            Long entityId = MapUtils.getLong(triggerConfig, JsonGraphConstant.ENTITY_ID);
            FlowProcessDateFieldDO flowProcessDateFieldDO = new FlowProcessDateFieldDO();
            flowProcessDateFieldDO.setProcessId(flowProcessDO.getId());
            flowProcessDateFieldDO.setEntityId(entityId);
            flowProcessDateFieldRepository.insert(flowProcessDateFieldDO);
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
        // 检查流程是否存在
        FlowProcessDO flowProcessDO = validateFlowProcessExist(reqVO.getId());
        flowProcessDO.setProcessDefinition(complicateFieldDataType(reqVO.getProcessDefinition()));
        // 更新流程定义
        flowProcessDO.setProcessDefinition(reqVO.getProcessDefinition());
        if (reqVO.getProcessStatus() != null && reqVO.getProcessStatus().intValue() >= 0) {
            flowProcessDO.setEnableStatus(reqVO.getProcessStatus());
        }
        // 保存更新
        flowProcessRepository.update(flowProcessDO);
    }

    private String complicateFieldDataType(String processDefinition) {
        Graph graph = JsonUtils.parseObject(processDefinition, Graph.class);
        List<Long> allFieldId = graph.findAllFieldId();
        Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap = selectFieldInfoMap(allFieldId);
        graph.updateFieldDataType(fieldInfoMap);
        processDefinition = JsonUtils.toJsonString(graph);
        return processDefinition;
    }

    private Map<Long, EntityFieldJdbcTypeRespDTO> selectFieldInfoMap(List<Long> fieldIds) {
        EntityFieldJdbcTypeReqDTO reqDTO = new EntityFieldJdbcTypeReqDTO();
        reqDTO.setFieldIds(fieldIds);

        List<EntityFieldJdbcTypeRespDTO> fieldJdbcTypes = metadataEntityFieldApi.getFieldJdbcTypes(reqDTO);

        return fieldJdbcTypes.stream()
                .collect(Collectors.toMap(EntityFieldJdbcTypeRespDTO::getFieldId, info -> info));
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
        flowProcessDO.setEnableStatus(FlowEnableStatusEnum.ENABLE.getStatus());
        flowProcessRepository.update(flowProcessDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableFlowProcess(Long id) {
        // 检查流程是否存在
        FlowProcessDO flowProcessDO = validateFlowProcessExist(id);
        // 关闭流程
        flowProcessDO.setEnableStatus(FlowEnableStatusEnum.DISABLE.getStatus());
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
