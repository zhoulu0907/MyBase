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
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * 任务中心转换类
 *
 * @author liyang
 * @date 2025-01-27
 */
@Mapper
public interface BpmTaskCenterConvert {

    BpmTaskCenterConvert INSTANCE = Mappers.getMapper(BpmTaskCenterConvert.class);

    /**
     * 将 BpmTodoTaskDTO 转换为 BpmFlowTodoTaskVO
     *
     * @param todoTaskDTO 待办任务DTO
     * @return 待办任务VO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "taskId", source = "id")
    @Mapping(target = "instanceId", source = "instanceId")
    @Mapping(target = "flowStatus", source = "flowStatus")
    @Mapping(target = "nodeCode", source = "nodeCode")
    @Mapping(target = "processTitle", source = "bpmTitle")
    @Mapping(target = "submitTime", source = "submitTime")
    @Mapping(target = "formSummary", source = "formSummary")
    @Mapping(target = "arrivalTime", source = "createTime")
    @Mapping(target = "businessUuid", source = "bindingViewId")
    @Mapping(target = "initiator", ignore = true)
    BpmFlowTodoTaskVO toTodoTaskVO(BpmTodoTaskDTO todoTaskDTO);

    /**
     * 将 BpmDoneTaskDTO 转换为 BpmFlowDoneTaskVO
     *
     * @param doneTaskDTO 已办任务DTO
     * @return 已办任务VO
     */
    @Mapping(target = "taskId", source = "taskId")
    @Mapping(target = "hisTaskId", source = "id")
    @Mapping(target = "instanceId", source = "instanceId")
    @Mapping(target = "processTitle", source = "bpmTitle")
    @Mapping(target = "formSummary", source = "formSummary")
    @Mapping(target = "handleTime", source = "updateTime")
    @Mapping(target = "taskStatus", source = "taskFlowStatus")
    @Mapping(target = "businessUuid", source = "bindingViewId")
    @Mapping(target = "initiator", ignore = true)
    BpmFlowDoneTaskVO toDoneTaskVO(BpmDoneTaskDTO doneTaskDTO);

    /**
     * 将 BpmMyInstanceDTO 转换为 BpmMyCreatedVO
     *
     * @param myInstanceDTO 我创建的实例DTO
     * @return 我创建的流程VO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "processTitle", source = "bpmTitle")
    @Mapping(target = "flowStatus", source = "flowStatus")
    @Mapping(target = "formSummary", source = "formSummary")
    @Mapping(target = "submitTime", source = "submitTime")
    @Mapping(target = "createTime", source = "createTime")
    @Mapping(target = "updateTime", source = "updateTime")
    @Mapping(target = "instanceId", source = "id")
    @Mapping(target = "businessUuid", source = "bindingViewId")
    BpmMyCreatedVO toMyCreatedVO(BpmMyInstanceDTO myInstanceDTO);

    /**
     * 将 BpmCcRecordDTO 转换为 BpmCcTaskPageResVO
     *
     * @param ccRecord 抄送记录DTO
     * @return 抄送任务VO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "processTitle", source = "bpmTitle")
    @Mapping(target = "flowStatus", source = "flowStatus")
    @Mapping(target = "arrivalTime", source = "createTime")
    @Mapping(target = "taskId", source = "taskId")
    @Mapping(target = "instanceId", source = "instanceId")
    @Mapping(target = "businessUuid", source = "bindingViewId")
    @Mapping(target = "viewed", source = "viewed", qualifiedByName = "intToBoolean")
    @Mapping(target = "initiator", ignore = true)
    BpmCcTaskPageResVO toCcTaskVO(BpmCcRecordDTO ccRecord);

    /**
     * 将整数转换为布尔值
     *
     * @param value 整数值
     * @return 布尔值
     */
    @Named("intToBoolean")
    default Boolean intToBoolean(Integer value) {
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
    default UserBasicInfoVO createInitiator(Long initiatorId, String initiatorName, String initiatorAvatar) {
        UserBasicInfoVO initiator = new UserBasicInfoVO();
        initiator.setUserId(String.valueOf(initiatorId));
        initiator.setName(initiatorName);
        initiator.setAvatar(initiatorAvatar);
        return initiator;
    }

    /**
     * 设置发起人信息 - 待办任务
     */
    @AfterMapping
    default void setTodoTaskInitiator(@MappingTarget BpmFlowTodoTaskVO vo, BpmTodoTaskDTO dto) {
        vo.setInitiator(createInitiator(dto.getInitiatorId(), dto.getInitiatorName(), dto.getInitiatorAvatar()));
    }

    /**
     * 设置发起人信息 - 已办任务
     */
    @AfterMapping
    default void setDoneTaskInitiator(@MappingTarget BpmFlowDoneTaskVO vo, BpmDoneTaskDTO dto) {
        vo.setInitiator(createInitiator(dto.getInitiatorId(), dto.getInitiatorName(), dto.getInitiatorAvatar()));
    }

    /**
     * 设置发起人信息 - 抄送任务
     */
    @AfterMapping
    default void setCcTaskInitiator(@MappingTarget BpmCcTaskPageResVO vo, BpmCcRecordDTO dto) {
        vo.setInitiator(createInitiator(dto.getInitiatorId(), dto.getInitiatorName(), dto.getInitiatorAvatar()));
    }
}
