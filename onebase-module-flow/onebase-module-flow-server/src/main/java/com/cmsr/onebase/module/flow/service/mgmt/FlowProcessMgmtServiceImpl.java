package com.cmsr.onebase.module.flow.service.mgmt;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.flow.controller.admin.mgmt.vo.*;
import com.cmsr.onebase.module.flow.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.enums.mgmt.FlowStatusEnum;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程管理服务实现类
 */
@Setter
@Service
public class FlowProcessMgmtServiceImpl implements FlowProcessMgmtService {

    @Autowired
    private FlowProcessRepository flowProcessRepository;

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
        return convertToVO(flowProcessDO);
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
        return saved.getId();
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
        // 更新流程定义
        flowProcessDO.setProcessDefinition(reqVO.getProcessDefinition());
        // 保存更新
        flowProcessRepository.update(flowProcessDO);
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
