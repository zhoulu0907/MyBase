package com.cmsr.onebase.module.bpm.runtime.convert;

import com.cmsr.onebase.module.bpm.core.dto.BpmCcRecordDTO;
import com.cmsr.onebase.module.bpm.core.dto.BpmDoneTaskDTO;
import com.cmsr.onebase.module.bpm.core.dto.BpmMyInstanceDTO;
import com.cmsr.onebase.module.bpm.core.dto.BpmTodoTaskDTO;
import com.cmsr.onebase.module.bpm.core.vo.UserBasicInfoVO;
import com.cmsr.onebase.module.bpm.runtime.vo.taskcenter.BpmCcTaskPageResVO;
import com.cmsr.onebase.module.bpm.runtime.vo.taskcenter.BpmFlowDoneTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.taskcenter.BpmFlowTodoTaskVO;
import com.cmsr.onebase.module.bpm.runtime.vo.taskcenter.BpmMyCreatedVO;
import org.apache.commons.lang3.BooleanUtils;

/**
 * 任务中心转换类
 *
 * @author liyang
 * @date 2025-01-27
 */
public class BpmTaskCenterConvert {

    /**
     * 将 BpmTodoTaskDTO 转换为 BpmFlowTodoTaskVO
     *
     * @param todoTaskDTO 待办任务DTO
     * @return 待办任务VO
     */
    public static BpmFlowTodoTaskVO toTodoTaskVO(BpmTodoTaskDTO todoTaskDTO) {
        if (todoTaskDTO == null) {
            return null;
        }

        BpmFlowTodoTaskVO vo = new BpmFlowTodoTaskVO();
        vo.setId(todoTaskDTO.getId());
        vo.setTaskId(todoTaskDTO.getId());
        vo.setInstanceId(todoTaskDTO.getInstanceId());
        vo.setFlowStatus(todoTaskDTO.getFlowStatus());
        vo.setNodeCode(todoTaskDTO.getNodeCode());
        vo.setProcessTitle(todoTaskDTO.getBpmTitle());
        vo.setSubmitTime(todoTaskDTO.getSubmitTime());
        vo.setFormSummary(todoTaskDTO.getFormSummary());
        vo.setArrivalTime(todoTaskDTO.getCreateTime());
        vo.setBusinessUuid(todoTaskDTO.getBindingViewId());
        vo.setInitiator(createInitiator(todoTaskDTO.getInitiatorId(), todoTaskDTO.getInitiatorName(), todoTaskDTO.getInitiatorAvatar()));
        // pageSetId 忽略，保持为 null

        return vo;
    }

    /**
     * 将 BpmDoneTaskDTO 转换为 BpmFlowDoneTaskVO
     *
     * @param doneTaskDTO 已办任务DTO
     * @return 已办任务VO
     */
    public static BpmFlowDoneTaskVO toDoneTaskVO(BpmDoneTaskDTO doneTaskDTO) {
        if (doneTaskDTO == null) {
            return null;
        }

        BpmFlowDoneTaskVO vo = new BpmFlowDoneTaskVO();
        vo.setTaskId(doneTaskDTO.getTaskId());
        vo.setHisTaskId(doneTaskDTO.getId());
        vo.setInstanceId(doneTaskDTO.getInstanceId());
        vo.setProcessTitle(doneTaskDTO.getBpmTitle());
        vo.setFormSummary(doneTaskDTO.getFormSummary());
        vo.setHandleTime(doneTaskDTO.getUpdateTime());
        vo.setTaskStatus(doneTaskDTO.getTaskFlowStatus());
        vo.setBusinessUuid(doneTaskDTO.getBindingViewId());
        vo.setInitiator(createInitiator(doneTaskDTO.getInitiatorId(), doneTaskDTO.getInitiatorName(), doneTaskDTO.getInitiatorAvatar()));
        // pageSetId 忽略，保持为 null

        return vo;
    }

    /**
     * 将 BpmMyInstanceDTO 转换为 BpmMyCreatedVO
     *
     * @param myInstanceDTO 我创建的实例DTO
     * @return 我创建的流程VO
     */
    public static BpmMyCreatedVO toMyCreatedVO(BpmMyInstanceDTO myInstanceDTO) {
        if (myInstanceDTO == null) {
            return null;
        }

        BpmMyCreatedVO vo = new BpmMyCreatedVO();
        vo.setId(myInstanceDTO.getId());
        vo.setProcessTitle(myInstanceDTO.getBpmTitle());
        vo.setFlowStatus(myInstanceDTO.getFlowStatus());
        vo.setFormSummary(myInstanceDTO.getFormSummary());
        vo.setSubmitTime(myInstanceDTO.getSubmitTime());
        vo.setCreateTime(myInstanceDTO.getCreateTime());
        vo.setUpdateTime(myInstanceDTO.getUpdateTime());
        vo.setInstanceId(myInstanceDTO.getId());
        vo.setBusinessUuid(myInstanceDTO.getBindingViewId());
        // currentNodeHandler 忽略，保持为 null
        // taskId 忽略，保持为 null
        // pageSetId 忽略，保持为 null

        return vo;
    }

    /**
     * 将 BpmCcRecordDTO 转换为 BpmCcTaskPageResVO
     *
     * @param ccRecord 抄送记录DTO
     * @return 抄送任务VO
     */
    public static BpmCcTaskPageResVO toCcTaskVO(BpmCcRecordDTO ccRecord) {
        if (ccRecord == null) {
            return null;
        }

        BpmCcTaskPageResVO vo = new BpmCcTaskPageResVO();
        vo.setId(ccRecord.getId());
        vo.setProcessTitle(ccRecord.getBpmTitle());
        vo.setFlowStatus(ccRecord.getFlowStatus());
        vo.setArrivalTime(ccRecord.getCreateTime());
        vo.setTaskId(ccRecord.getTaskId());
        vo.setInstanceId(ccRecord.getInstanceId());
        vo.setBusinessUuid(ccRecord.getBindingViewId());
        vo.setViewed(intToBoolean(ccRecord.getViewed()));
        vo.setInitiator(createInitiator(ccRecord.getInitiatorId(), ccRecord.getInitiatorName(), ccRecord.getInitiatorAvatar()));
        // pageSetId 忽略，保持为 null

        return vo;
    }

    /**
     * 将整数转换为布尔值
     *
     * @param value 整数值
     * @return 布尔值
     */
    private static Boolean intToBoolean(Integer value) {
        return BooleanUtils.toBoolean(value);
    }

    /**
     * 创建发起人信息
     *
     * @param initiatorId 发起人ID
     * @param initiatorName 发起人名称
     * @param initiatorAvatar 发起人头像
     * @return 发起人信息VO
     */
    private static UserBasicInfoVO createInitiator(Long initiatorId, String initiatorName, String initiatorAvatar) {
        if (initiatorId == null) {
            return null;
        }

        UserBasicInfoVO initiator = new UserBasicInfoVO();
        initiator.setUserId(String.valueOf(initiatorId));
        initiator.setName(initiatorName);
        initiator.setAvatar(initiatorAvatar);
        return initiator;
    }
}
