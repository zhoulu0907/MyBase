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
import com.cmsr.onebase.module.bpm.core.enums.BpmConstants;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 抄送节点监听器
 *
 * @author liyang
 * @date 2025-11-18
 */
@Slf4j
@Service("ccNodeListener")
public class BpmCcNodeListener {
    @Lazy
    @Resource
    private BpmCcNodeListener self;

    @Resource(name = "bpmTaskService")
    private TaskService taskService;

    @Resource
    private BpmFlowCcRecordRepository ccRecordRepository;

    public void handleCreate(ListenerVariable listenerVariable) {
        Map<String, Object> flowVariable = listenerVariable.getVariable();
        Task currTask = listenerVariable.getTask();
        String currHandler = BpmConstants.SYS_USER_ID;

        // 使用系统用户进行跳转
        FlowParams skipParams = FlowParams.build()
                .variable(flowVariable)
                .message("自动抄送")
                .skipType(SkipType.PASS.getKey())
                .hisStatus(BpmNodeApproveStatusEnum.POST_AUTO_CC.getCode())
                .handler(currHandler);

        log.info("自动抄送任务开始执行..., nodeCode：{} nodeName：{}", currTask.getNodeCode(), currTask.getNodeName());

        taskService.skip(skipParams, currTask);
    }

    public void handleCcUsers(Task currTask, Map<String, Object> flowVariable) {
        handleCcUsers(currTask, flowVariable, BpmConstants.MAX_NODE_CC_USERS);
    }

    public void handleCcUsers(Task currTask, Map<String, Object> flowVariable, Integer maxUsers) {
        if (currTask == null) {
            return;
        }

        String ccUsersKey = BpmConstants.VAR_CC_USERS_KEY + "_" + currTask.getNodeCode();
        String ccUsersStr = MapUtils.getString(flowVariable, ccUsersKey);

        if (StringUtils.isBlank(ccUsersStr)) {
            flowVariable.remove(ccUsersKey);
            return;
        }

        Set<String> ccUsers = JsonUtils.parseObject(ccUsersStr, new TypeReference<>() {});

        if (CollectionUtils.isEmpty(ccUsers)) {
            log.warn("无可抄送的人员信息");
            return;
        }

        // 限制用户上限
        if (ccUsers.size() > maxUsers) {
            log.warn("抄送人列表最多{}个用户 当前为：{}", maxUsers, ccUsers.size());
            ccUsers = new HashSet<>(ccUsers.stream().limit(maxUsers).toList());
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

        ccRecordRepository.saveBatch(ccRecords);

        // 移除抄送用户信息
        flowVariable.remove(ccUsersKey);
    }

    public void handleFinish(ListenerVariable listenerVariable) {
        Map<String, Object> flowVariable = listenerVariable.getVariable();
        Task currTask = listenerVariable.getTask();

        self.handleCcUsers(currTask, flowVariable);
    }
}
