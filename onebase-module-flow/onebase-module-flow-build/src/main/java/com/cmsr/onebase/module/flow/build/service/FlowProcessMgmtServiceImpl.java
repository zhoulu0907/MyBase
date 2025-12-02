package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.flow.build.vo.*;
import com.cmsr.onebase.module.flow.core.dal.database.*;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.enums.FlowEnableStatusEnum;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.core.vo.PageFlowProcessReqVO;
import jakarta.annotation.Resource;
import lombok.Setter;
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


    @Resource
    private AppApplicationApi appApplicationApi;

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
        FlowProcessDO flowProcessDO = flowProcessRepository.getById(id);
        if (flowProcessDO == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.PROCESS_NOT_EXIST);
        }
        FlowProcessVO flowProcessVO = BeanUtils.toBean(flowProcessDO, FlowProcessVO.class);
        flowProcessVO.setTriggerConfig(JsonUtils.parseObject(flowProcessDO.getTriggerConfig(), Map.class));
        return flowProcessVO;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CreateFlowProcessReqVO reqVO) {
        // 转换为DO对象
        FlowProcessDO flowProcessDO = BeanUtils.toBean(reqVO, FlowProcessDO.class);
        flowProcessDO.setProcessUuid(UuidUtils.getUuid());
        // 禁用
        flowProcessDO.setEnableStatus(FlowEnableStatusEnum.DISABLE.getStatus());
        String triggerConfig = JsonUtils.toJsonString(reqVO.getTriggerConfig());
        flowProcessDO.setTriggerConfig(triggerConfig);
        // 保存到数据库
        flowProcessRepository.save(flowProcessDO);
        return flowProcessDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateFlowProcessReqVO reqVO) {
        // 检查流程是否存在
        FlowProcessDO flowProcessDO = validateFlowProcessExist(reqVO.getId());
        // 更新字段
        if (reqVO.getProcessName() != null) {
            flowProcessDO.setProcessName(reqVO.getProcessName());
        }
        if (reqVO.getProcessDescription() != null) {
            flowProcessDO.setProcessDescription(reqVO.getProcessDescription());
        }
        if (reqVO.getProcessStatus() != null) {
            flowProcessDO.setEnableStatus(reqVO.getProcessStatus());
        }
        // 保存更新
        flowProcessRepository.updateById(flowProcessDO);
    }

    @Override
    public void updateProcessDefinition(UpdateProcessDefinitionReqVO reqVO) {
        // 检查流程是否存在
        FlowProcessDO flowProcessDO = validateFlowProcessExist(reqVO.getId());
        // 更新流程定义
        flowProcessDO.setProcessDefinition(reqVO.getProcessDefinition());
        if (reqVO.getProcessStatus() != null && reqVO.getProcessStatus().intValue() >= 0) {
            flowProcessDO.setEnableStatus(reqVO.getProcessStatus());
        }
        // 保存更新
        flowProcessRepository.updateById(flowProcessDO);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void renameFlowProcess(RenameFlowProcessReqVO reqVO) {
        // 检查流程是否存在
        FlowProcessDO flowProcessDO = validateFlowProcessExist(reqVO.getId());
        // 更新流程名称
        flowProcessDO.setProcessName(reqVO.getProcessName());
        flowProcessRepository.updateById(flowProcessDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableFlowProcess(Long id) {
        // 检查流程是否存在
        FlowProcessDO flowProcessDO = validateFlowProcessExist(id);
        // 启用流程
        flowProcessDO.setEnableStatus(FlowEnableStatusEnum.ENABLE.getStatus());
        flowProcessRepository.updateById(flowProcessDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableFlowProcess(Long id) {
        // 检查流程是否存在
        FlowProcessDO flowProcessDO = validateFlowProcessExist(id);
        // 关闭流程
        flowProcessDO.setEnableStatus(FlowEnableStatusEnum.DISABLE.getStatus());
        flowProcessRepository.updateById(flowProcessDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 检查流程是否存在
        validateFlowProcessExist(id);
        // 删除流程
        flowProcessRepository.removeById(id);
        flowProcessDateFieldRepository.deleteByProcessId(id);
        flowProcessEntityRepository.deleteByProcessId(id);
        flowProcessFormRepository.deleteByProcessId(id);
        flowProcessTimeRepository.deleteByProcessId(id);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> ids) {
        for (Long id : ids) {
            validateFlowProcessExist(id);
        }
        flowProcessRepository.removeByIds(ids);
    }

    private FlowProcessDO validateFlowProcessExist(Long id) {
        FlowProcessDO flowProcessDO = flowProcessRepository.getById(id);
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
