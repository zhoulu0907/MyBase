package com.cmsr.onebase.module.bpm.runtime.listener;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowCcRecordRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowCcRecordDO;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeApproveStatusEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.service.impl.BpmConstants;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 抄送节点监听器
 *
 * @author liyang
 * @date 2025-11-18
 */
@Slf4j
@Service("ccNodeListener")
public class BpmCcNodeListener {
    @Resource
    private TaskService taskService;

    @Resource
    private BpmFlowCcRecordRepository ccRecordRepository;

    public void handleCreate(ListenerVariable listenerVariable) {
        FlowParams flowParams = listenerVariable.getFlowParams();
        Map<String, Object> flowVariable = listenerVariable.getVariable();
        Task currTask = listenerVariable.getTask();
        String currHandler = flowParams.getHandler();

        // 基础 FlowParams
        FlowParams skipParams = FlowParams.build()
                .variable(flowVariable)
                .message("自动抄送")
                .skipType(SkipType.PASS.getKey())
                .hisStatus(BpmNodeApproveStatusEnum.POST_AUTO_CC.getCode())
                .handler(currHandler);

        log.info("自动抄送任务开始执行..., nodeCode：{} nodeName：{}", currTask.getNodeCode(), currTask.getNodeName());

        taskService.skip(skipParams, currTask);
    }

    public void handleFinish(ListenerVariable listenerVariable) {
        Map<String, Object> flowVariable = listenerVariable.getVariable();
        Task currTask = listenerVariable.getTask();
        String ccUsersKey = BpmConstants.VAR_CC_USERS_KEY + "_" + currTask.getNodeCode();

        String ccUsersStr = MapUtils.getString(flowVariable, ccUsersKey);

        if (StringUtils.isBlank(ccUsersStr)) {
            flowVariable.remove(ccUsersKey);
            return;
        }

        List<String> ccUsers = JsonUtils.parseObject(ccUsersStr, new TypeReference<>() {});

        if (CollectionUtils.isEmpty(ccUsers)) {
            log.warn("无可抄送的人员信息");
            return;
        }

        // 限制用户上限
        if (ccUsers.size() > BpmConstants.MAX_NODE_CC_USERS) {
            log.warn("审批人列表最多{}个用户 当前为：{}",BpmConstants.MAX_NODE_CC_USERS, ccUsers.size());
            ccUsers = new ArrayList<>(ccUsers.stream().limit(BpmConstants.MAX_NODE_CC_USERS).toList());
        }

        List<BpmFlowCcRecordDO> ccRecords = new ArrayList<>();

        // 增加抄送用户信息
        for (String userId : ccUsers) {
            BpmFlowCcRecordDO ccRecordDO = new BpmFlowCcRecordDO();
            ccRecordDO.setInstanceId(currTask.getInstanceId());
            ccRecordDO.setTaskId(currTask.getId());
            ccRecordDO.setUserId(userId);
            ccRecordDO.setViewed(BooleanUtils.toInteger(false));

            ccRecords.add(ccRecordDO);
        }

        ccRecordRepository.insertBatch(ccRecords);

        // 移除抄送用户信息
        flowVariable.remove(ccUsersKey);
    }
}
