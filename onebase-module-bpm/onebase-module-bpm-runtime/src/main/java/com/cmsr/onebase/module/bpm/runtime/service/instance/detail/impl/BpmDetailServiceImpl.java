package com.cmsr.onebase.module.bpm.runtime.service.instance.detail.impl;

import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowAgentInsRepository;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowCcRecordRepository;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowInsBizExtRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowAgentInsDO;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowCcRecordDO;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowInsBizExtDO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmViewSourceEnum;
import com.cmsr.onebase.module.bpm.core.utils.BpmUtil;
import com.cmsr.onebase.module.bpm.core.vo.UserBasicInfoVO;
import com.cmsr.onebase.module.bpm.runtime.service.context.BpmPermissionUserContext;
import com.cmsr.onebase.module.bpm.runtime.service.instance.detail.BpmDetailService;
import com.cmsr.onebase.module.bpm.runtime.service.instance.detail.strategy.InstanceDetailStrategyManager;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailRespVO;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowHisTask;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataMethodCoreService;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.dromara.warm.flow.core.entity.HisTask;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.service.HisTaskService;
import org.dromara.warm.flow.core.service.InsService;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.service.UserService;
import org.dromara.warm.flow.core.service.impl.BpmConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 流程详情服务实现
 *
 * @author liyang
 * @date 2025-11-17
 */
@Slf4j
@Service
public class BpmDetailServiceImpl implements BpmDetailService {
    @Resource(name = "bpmInsService")
    private InsService insService;

    @Resource(name = "bpmTaskService")
    private TaskService taskService;

    @Resource(name = "bpmHisTaskService")
    private HisTaskService hisTaskService;

    @Resource(name = "bpmUserService")
    private UserService userService;

    @Resource
    private BpmFlowInsBizExtRepository flowInsExtRepository;

    @Resource
    private BpmFlowCcRecordRepository ccRecordRepository;

    @Resource
    private BpmFlowAgentInsRepository agentInsRepository;

    @Resource
    private MetadataDataMethodCoreService metadataDataMethodCoreService;

    @Resource
    private InstanceDetailStrategyManager instanceDetailStrategyManager;

    @Data
    private static class InsDetailContext {
        /**
         * 流程实例 必须存在
         */
        private Instance instance;

        /**
         * 登录用户ID 必须存在
         */
        private Long loginUserId;

        private Task task;

        private HisTask hisTask;

        /**
         * 匹配的用户（审批人/转办人/委派人）
         */
        private User matchedUser;

        /**
         * 代理人记录（如果当前用户是代理人，则不为null）
         */
        private BpmFlowAgentInsDO agentIns;
    }

    @Transactional(rollbackFor = Exception.class)
    public BpmTaskDetailRespVO getFormDetail(BpmTaskDetailReqVO reqVO) {
        BpmTaskDetailRespVO respVO = new BpmTaskDetailRespVO();
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        Long instanceId = reqVO.getInstanceId();

        // 查询流程实例
        Instance instance = insService.getById(instanceId);
        if (instance == null) {
            throw exception(ErrorCodeConstants.FLOW_INSTANCE_NOT_EXISTS);
        }

        BpmViewSourceEnum source = BpmViewSourceEnum.getByCode(reqVO.getFrom());
        if (source == null) {
            throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "无效的来源类型");
        }

        // 设置流程状态
        respVO.setCurrentStatus(instance.getFlowStatus());
        respVO.setInstanceId(instanceId);

        // 获取实体ID
        Long entityId = MapUtils.getLong(instance.getVariableMap(), BpmConstants.VAR_ENTITY_ID_KEY);
        if (entityId == null) {
            throw exception(ErrorCodeConstants.FLOW_NOT_BIND_ENTITY_ID);
        }

        InsDetailContext context = new InsDetailContext();
        context.setInstance(instance);

        // 当前待办
        getLastTodoTask(reqVO, context, loginUserId, source);

        // 存在最新待办，已办就没必要查了
        if (context.getTask() == null) {
            getDoneTask(reqVO, context, loginUserId, source);
        }

        // 填充业务扩展信息（与节点类型无关的通用逻辑）
        fillBpmBizExt(respVO, instanceId);

        // 填充表单数据（与节点类型无关的通用逻辑）
        fillFormData(respVO, instance, entityId);

        BaseNodeExtDTO nodeExtDTO = null;
        Task currTask = context.getTask();
        HisTask hisTask = context.getHisTask();

        if (currTask != null) {
            respVO.setTaskId(currTask.getId());
            nodeExtDTO = BpmUtil.getNodeExtDTOByNodeCode(currTask.getNodeCode(), instance.getDefJson());
        } else if (hisTask != null) {
            nodeExtDTO = BpmUtil.getNodeExtDTOByNodeCode(hisTask.getNodeCode(), instance.getDefJson());
        }

        // 填充其他流程详情
        instanceDetailStrategyManager.processInstanceDetail(respVO, nodeExtDTO, instance, loginUserId, currTask != null);

        // 标记已读状态
        markAsRead(reqVO, context);

        // 处理代理信息
        if (context.getAgentIns() != null) {
            respVO.setProcessTitle(BpmConstants.AGENT_TITLE_PREFIX + respVO.getProcessTitle());
        }

        return respVO;
    }

    private void getLastTodoTask(BpmTaskDetailReqVO reqVO, InsDetailContext context, Long loginUserId, BpmViewSourceEnum sourceEnum) {
        Long instanceId = context.getInstance().getId();

        if (sourceEnum == BpmViewSourceEnum.TODO) {
            Long taskId = reqVO.getTaskId();

            if (taskId == null) {
                throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "待办任务ID不能为空");
            }

            Task task = taskService.getById(taskId);

            if (task == null) {
                throw exception(ErrorCodeConstants.FLOW_TASK_NOT_EXISTS);
            }

            if (!Objects.equals(task.getInstanceId(), instanceId)) {
                throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "待办任务ID与实例ID不匹配");
            }

            // 权限判断
            BpmPermissionUserContext userContext = findPermissionUserContext(taskId, String.valueOf(loginUserId));

            if (userContext == null) {
                throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY);
            }

            context.setMatchedUser(userContext.getMatchedUser());
            context.setAgentIns(userContext.getAgentIns());
            context.setTask(task);

            return;
        }

        getLastTodoTask(instanceId, context, loginUserId);
    }

    private void getDoneTask(BpmTaskDetailReqVO reqVO, InsDetailContext context, Long loginUserId, BpmViewSourceEnum sourceEnum) {
        Instance instance = context.getInstance();
        Long instanceId = instance.getId();

        if (sourceEnum == BpmViewSourceEnum.TODO) {
            // 暂无已办任务
        } else if (sourceEnum == BpmViewSourceEnum.CREATED) {
            // 我的创建无需已办任务，但需要校验是否为创建人，todo：是否增加统一校验逻辑
            if (!Objects.equals(String.valueOf(loginUserId), instance.getCreateBy())) {
                log.error("用户 {} 无权限访问我的创建实例 {}", loginUserId, instanceId);
                throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY);
            }
        } else if (sourceEnum == BpmViewSourceEnum.CC) {
            Long taskId = reqVO.getTaskId();

            if (taskId == null) {
                throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "待办任务ID不能为空");
            }

            // 权限校验：查询抄送记录
            ConfigStore configStore = new DefaultConfigStore();
            configStore.and(BpmFlowCcRecordDO.TASK_ID, taskId);
            configStore.and(BpmFlowCcRecordDO.USER_ID, loginUserId);

            BpmFlowCcRecordDO ccRecordDO = ccRecordRepository.findOne(configStore);

            // 当前用户没有查看此抄送的任务权限
            if (ccRecordDO == null) {
                // 查出代理人权限
                List<BpmFlowAgentInsDO> agentInsList = agentInsRepository.findAllByTaskIdAndAgentId(taskId, String.valueOf(loginUserId));

                if (CollectionUtils.isEmpty(agentInsList)) {
                    throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "您没有查看此抄送的任务权限");
                }

                Set<Long> approveUserIds = new HashSet<>();
                Map<Long, BpmFlowAgentInsDO> agentInsMap = new HashMap<>();

                for (BpmFlowAgentInsDO agentInsDO : agentInsList) {
                    approveUserIds.add(agentInsDO.getPrincipalId());
                    agentInsMap.put(agentInsDO.getPrincipalId(), agentInsDO);
                }

                ConfigStore agentCcQuery = new DefaultConfigStore();
                agentCcQuery.and(BpmFlowCcRecordDO.TASK_ID, taskId);
                agentCcQuery.in(BpmFlowCcRecordDO.USER_ID, approveUserIds);

                ccRecordDO = ccRecordRepository.findOne(agentCcQuery);

                // 当前用户没有查看此抄送的任务权限
                if (ccRecordDO == null) {
                    throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "您没有查看此抄送的任务权限");
                }

                context.setAgentIns(agentInsMap.get(ccRecordDO.getUserId()));
            }

            // 查询已办任务
            HisTask hisTaskQuery = new FlowHisTask();
            hisTaskQuery.setTaskId(taskId);
            hisTaskQuery.setInstanceId(instanceId);

            HisTask hisTask = hisTaskService.getOne(hisTaskQuery);

            if (hisTask == null) {
                throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "未查询到已办任务");
            }

            context.setHisTask(hisTask);
        } else {
            Long taskId = reqVO.getTaskId();

            if (taskId == null) {
                throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "待办任务ID不能为空");
            }

            // 查询已办任务
            HisTask hisTaskQuery = new FlowHisTask();
            hisTaskQuery.setTaskId(taskId);
            hisTaskQuery.setInstanceId(instanceId);

            List<HisTask> hisTasks = hisTaskService.list(hisTaskQuery);

            if (CollectionUtils.isEmpty(hisTasks)) {
                log.error("已办任务 {} 不存在", taskId);
                throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "未查询到已办任务");
            }

            Map<String, HisTask> hisTaskMap = new HashMap<>();
            for (HisTask hisTask : hisTasks) {
                hisTaskMap.put(hisTask.getApprover(), hisTask);
            }

            HisTask hisTask = hisTaskMap.get(String.valueOf(loginUserId));
            if (hisTask != null) {
                // 审批人权限，直接返回
                context.setHisTask(hisTask);
                return;
            }

            // 查出代理人权限
            List<BpmFlowAgentInsDO> agentInsList = agentInsRepository.findAllByTaskIdAndAgentId(taskId, String.valueOf(loginUserId));

            if (CollectionUtils.isEmpty(agentInsList)) {
                throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "没有已办任务权限");
            }

            for (BpmFlowAgentInsDO agentInsDO : agentInsList) {
                hisTask = hisTaskMap.get(agentInsDO.getPrincipalId());

                if (hisTask != null) {
                    // 代理人权限，直接返回
                    context.setAgentIns(agentInsDO);
                    context.setHisTask(hisTask);
                    return;
                }
            }

            log.error("用户 {} 无权限访问已办任务 {}", loginUserId, taskId);
            throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "未查询到已办任务");
        }
    }

    /**
     * 标记已读状态
     *
     * @param reqVO       请求VO
     * @param loginUserId 登录用户ID
     */
    private void markAsRead(BpmTaskDetailReqVO reqVO, InsDetailContext context) {
        Task currTask = context.getTask();

        // 查看匹配的用户
        User matchedUser = context.getMatchedUser();

        // 当前待办标记为已读
        if (currTask != null) {
            // 待办必定有匹配的用户
            String processedBy = matchedUser.getProcessedBy();

            List<User> users = userService.listByProcessedBys(currTask.getId(), processedBy);
            if (CollectionUtils.isNotEmpty(users)) {
                LocalDateTime now = LocalDateTime.now();

                for (User user : users) {
                    user.setUpdateTime(now);
                }

                userService.updateBatch(users);
            }
        }

        if (Objects.equals(reqVO.getFrom(), BpmViewSourceEnum.CC.getCode())) {
            String processedBy = matchedUser.getProcessedBy();

            ConfigStore configStore = new DefaultConfigStore();
            configStore.and(BpmFlowCcRecordDO.TASK_ID, reqVO.getTaskId());
            configStore.and(BpmFlowCcRecordDO.USER_ID, processedBy);

            BpmFlowCcRecordDO ccRecord = ccRecordRepository.findOne(configStore);

            if (ccRecord == null) {
                return;
            }

            if (!BooleanUtils.toBoolean(ccRecord.getViewed())) {
                ccRecord.setViewed(BooleanUtils.toInteger(true));
                ccRecord.setViewedTime(LocalDateTime.now());
                ccRecordRepository.update(ccRecord);
            }
        }
    }

    /**
     * 获取最新的待办任务
     *
     * @param instanceId  流程实例ID
     * @param loginUserId 登录用户ID
     * @return 待办任务，如果无权限则返回null
     */
    private void getLastTodoTask(Long instanceId, InsDetailContext context, Long loginUserId) {
        // 查询该实例的待办任务
        List<Task> tasks = taskService.getByInsId(instanceId);
        if (tasks == null || tasks.isEmpty()) {
            return;
        }

        List<Long> taskIds = new ArrayList<>();
        Map<Long, Task> taskMap = new HashMap<>();

        for (Task task : tasks) {
            taskIds.add(task.getId());
            taskMap.put(task.getId(), task);
        }

        BpmPermissionUserContext userContext = findPermissionUserContext(taskIds, String.valueOf(loginUserId));

        if (userContext == null) {
            return;
        }

        context.setMatchedUser(userContext.getMatchedUser());
        context.setAgentIns(userContext.getAgentIns());
        context.setTask(taskMap.get(userContext.getMatchedUser().getAssociated()));
    }

    /**
     * 填充业务扩展信息
     *
     * @param vo         详情VO
     * @param instanceId 流程实例ID
     */
    private void fillBpmBizExt(BpmTaskDetailRespVO vo, Long instanceId) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.and(BpmFlowInsBizExtDO.INSTANCE_ID, instanceId);
        BpmFlowInsBizExtDO flowInsExtDO = flowInsExtRepository.findOne(configStore);

        if (flowInsExtDO == null) {
            throw exception(ErrorCodeConstants.BPM_BIZ_EXT_NOT_EXIST);
        }

        vo.setProcessTitle(flowInsExtDO.getBpmTitle());
        vo.setBpmVersion(flowInsExtDO.getBpmVersion());
        vo.setSubmitTime(flowInsExtDO.getSubmitTime());
        vo.setInitiatorDeptId(flowInsExtDO.getInitiatorDeptId());
        vo.setInitiatorDeptName(flowInsExtDO.getInitiatorDeptName());

        vo.setInitiator(new UserBasicInfoVO());
        vo.getInitiator().setUserId(flowInsExtDO.getInitiatorId());
        vo.getInitiator().setName(flowInsExtDO.getInitiatorName());
        vo.getInitiator().setAvatar(flowInsExtDO.getInitiatorAvatar());

        // todo: 待删除
        vo.setInitiatorId(flowInsExtDO.getInitiatorId());
        vo.setInitiatorName(flowInsExtDO.getInitiatorName());
    }

    /**
     * 填充表单数据
     *
     * @param vo       详情VO
     * @param instance 流程实例
     * @param entityId 实体ID
     */
    private void fillFormData(BpmTaskDetailRespVO vo, Instance instance, Long entityId) {
        String entityDataId = instance.getBusinessId();
        if (entityDataId == null) {
            throw exception(ErrorCodeConstants.FLOW_ENTITY_DATA_ID_NOT_EXISTS);
        }

        Map<String, Object> data = metadataDataMethodCoreService.getData(entityId, entityDataId, null, null);
        if (data != null && !data.isEmpty()) {
            vo.setFormData(data);
        }
    }

    private BpmPermissionUserContext findPermissionUserContext(Long taskId, String loginUserId) {
        return findPermissionUserContext(List.of(taskId), loginUserId);
    }

    private BpmPermissionUserContext findPermissionUserContext(List<Long> taskIds, String loginUserId) {
        List<User> users = userService.getByAssociateds(taskIds);
        BpmPermissionUserContext userContext = null;

        if (CollectionUtils.isEmpty(users)) {
            return userContext;
        }

        Map<String, User> userMap = users.stream().collect(Collectors.toMap(User::getProcessedBy, user -> user));
        User matchedUser = userMap.get(loginUserId);

        if (matchedUser != null) {
            userContext = new BpmPermissionUserContext(matchedUser, null);
            return userContext;
        }

        // 找出节点关联的代理人
        List<BpmFlowAgentInsDO> agentInsDOList = agentInsRepository.findAllByTaskIdsAndAgentId(taskIds, loginUserId);

        if (CollectionUtils.isEmpty(agentInsDOList)) {
            return userContext;
        }

        for (BpmFlowAgentInsDO agentInsDO : agentInsDOList) {
            Long principalId = agentInsDO.getPrincipalId();

            User agentUser = userMap.get(String.valueOf(principalId));

            if (agentUser != null) {
                userContext = new BpmPermissionUserContext(agentUser, agentInsDO);
                break;
            }
        }

        return userContext;
    }
}
