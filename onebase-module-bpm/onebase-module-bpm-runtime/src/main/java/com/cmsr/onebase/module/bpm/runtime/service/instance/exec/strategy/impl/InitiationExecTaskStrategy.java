package com.cmsr.onebase.module.bpm.runtime.service.instance.exec.strategy.impl;

import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowAgentInsDO;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowInsBizExtDO;
import com.cmsr.onebase.module.bpm.core.dto.node.InitiationNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeBtnCfgDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmActionButtonEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmBusinessStatusEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeApproveStatusEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.runtime.vo.EntityVO;
import com.cmsr.onebase.module.bpm.runtime.vo.ExecTaskReqVO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanicMergeConditionVO;
import org.apache.commons.collections4.MapUtils;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.enums.SkipType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 发起节点策略
 *
 * @author liyang
 * @date 2025-11-03
 */
@Component
public class InitiationExecTaskStrategy extends AbstractExecTaskStrategy<InitiationNodeExtDTO> {
    @Override
    public boolean supports(String bizNodeType) {
        return Objects.equals(bizNodeType, BpmNodeTypeEnum.INITIATION.getCode());
    }

    @Override
    public void execute(User matchedUser, BpmFlowAgentInsDO agentInsDO, Task task, InitiationNodeExtDTO extDTO, ExecTaskReqVO reqVO) {
        String buttonType = reqVO.getButtonType();

        // 获取按钮权限
        List<BaseNodeBtnCfgDTO> buttonConfigs = extDTO.getButtonConfigs();
        BaseNodeBtnCfgDTO matchButtonConfig = null;

        for (BaseNodeBtnCfgDTO buttonConfig : buttonConfigs) {
            if (buttonConfig.getEnabled() && Objects.equals(buttonConfig.getButtonType(), buttonType)) {
                matchButtonConfig = buttonConfig;
                break;
            }
        }

        // 未设置按钮，无权限
        if (matchButtonConfig == null) {
            throw exception(ErrorCodeConstants.NO_BUTTON_PERMISSION);
        }

        EntityVO entityVO = reqVO.getEntity();

        if (Objects.equals(buttonType, BpmActionButtonEnum.SAVE.getCode())) {
            // 只更新数据，状态不变更
        } else if (Objects.equals(buttonType, BpmActionButtonEnum.SUBMIT.getCode())) {
            // 在执行 skip 之前判断是否是首次提交（避免状态变更后判断不准）
            boolean isFirst = Objects.equals(task.getFlowStatus(), BpmBusinessStatusEnum.DRAFT.getCode());
            
            FlowParams skipParams = FlowParams.build()
                    .skipType(SkipType.PASS.getKey())
                    .message(BpmNodeApproveStatusEnum.POST_SUBMITTED.getName())
                    .flowStatus(BpmBusinessStatusEnum.IN_APPROVAL.getCode())
                    .hisStatus(BpmNodeApproveStatusEnum.POST_SUBMITTED.getCode());
            taskService.skip(skipParams, task);

            // 只更新首次提交时间
            if (isFirst) {
                // todo: 不应该为空
                BpmFlowInsBizExtDO insBizExtDO = insBizExtRepository.findOneByInstanceId(task.getInstanceId());

                if (insBizExtDO != null) {
                    insBizExtDO.setSubmitTime(LocalDateTime.now());
                    insBizExtRepository.updateById(insBizExtDO);
                }
            }
        } else {
            throw exception(ErrorCodeConstants.UNSUPPORT_ACTION_BUTTON_TYPE);
        }

        // 实体数据更新，发起节点默认有编辑权限
        if (entityVO != null && MapUtils.isNotEmpty(entityVO.getData())) {
            SemanicMergeConditionVO updateDataReqVO = new SemanicMergeConditionVO();
            updateDataReqVO.setData(entityVO.getData());
            updateDataReqVO.setTableName(entityVO.getTableName());
            semanticDynamicDataApi.updateDataById(updateDataReqVO);
        }
    }
}


