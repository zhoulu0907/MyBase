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

    /**
     * 流程详情上下文
     * 用于在方法间传递流程详情相关的数据
     */
    @Data
    private static class InsDetailContext {
        /**
         * 流程实例（必须存在）
         */
        private Instance instance;

        /**
         * 登录用户ID（必须存在）
         */
        private Long loginUserId;

        /**
         * 来源类型
         */
        private BpmViewSourceEnum source;

        /**
         * 实体ID
         */
        private Long entityId;

        /**
         * 当前待办任务
         */
        private Task task;

        /**
         * 历史已办任务
         */
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


    /**
     * 获取流程实例的表单详情
     *
     * @param reqVO 流程详情请求VO
     * @return 流程实例的表单详情VO
     */
    @Transactional(rollbackFor = Exception.class)
    public BpmTaskDetailRespVO getFormDetail(BpmTaskDetailReqVO reqVO) {
        // 1. 构建上下文并校验基础权限
        InsDetailContext context = buildContext(reqVO);

        // 2. 根据来源类型加载任务（任务加载时会进行权限校验）
        loadTaskBySource(reqVO, context);

        // 3. 构建响应数据
        BpmTaskDetailRespVO respVO = buildResponse(context);

        // 4. 标记已读状态
        markAsRead(reqVO, context);

        return respVO;
    }

    /**
     * 构建流程详情上下文
     *
     * @param reqVO 请求VO
     * @return 上下文对象
     */
    private InsDetailContext buildContext(BpmTaskDetailReqVO reqVO) {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        Long instanceId = reqVO.getInstanceId();

        // 查询流程实例
        Instance instance = insService.getById(instanceId);
        if (instance == null) {
            throw exception(ErrorCodeConstants.FLOW_INSTANCE_NOT_EXISTS);
        }

        // 校验来源类型
        BpmViewSourceEnum source = BpmViewSourceEnum.getByCode(reqVO.getFrom());
        if (source == null) {
            throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "无效的来源类型");
        }

        // 获取实体ID
        Long entityId = MapUtils.getLong(instance.getVariableMap(), BpmConstants.VAR_ENTITY_ID_KEY);
        if (entityId == null) {
            throw exception(ErrorCodeConstants.FLOW_NOT_BIND_ENTITY_ID);
        }

        InsDetailContext context = new InsDetailContext();
        context.setInstance(instance);
        context.setLoginUserId(loginUserId);
        context.setSource(source);
        context.setEntityId(entityId);

        // CREATED 来源需要校验创建人权限（基于实例信息，无需任务信息）
        if (source == BpmViewSourceEnum.CREATED) {
            validateCreatorPermission(context);
        }

        return context;
    }

    /**
     * 根据来源类型加载任务
     *
     * @param reqVO   请求VO
     * @param context 上下文
     */
    private void loadTaskBySource(BpmTaskDetailReqVO reqVO, InsDetailContext context) {
        BpmViewSourceEnum source = context.getSource();

        switch (source) {
            case TODO:
                // 待办来源：加载待办任务
                loadTodoTask(reqVO, context);
                break;
            case CREATED:
                // 我的创建来源：无需加载任务，权限已在 buildContext 中校验
                // CREATED 来源只查看流程实例信息，不涉及任务
                break;
            case CC:
                // 抄送来源：先尝试查找待办任务，如果没有则加载抄送任务（已办任务）
                loadTodoTaskFromInstance(context);
                if (context.getTask() == null) {
                    loadCcTask(reqVO, context);
                }
                break;
            case DONE:
                // 已办来源：先尝试查找待办任务，如果没有则加载已办任务
                loadTodoTaskFromInstance(context);
                if (context.getTask() == null) {
                    loadDoneTask(reqVO, context);
                }
                break;
            default:
                // 其他来源：尝试从实例中查找待办任务
                loadTodoTaskFromInstance(context);
                break;
        }
    }

    /**
     * 构建响应数据
     *
     * @param context 上下文
     * @return 响应VO
     */
    private BpmTaskDetailRespVO buildResponse(InsDetailContext context) {
        BpmTaskDetailRespVO respVO = new BpmTaskDetailRespVO();
        Instance instance = context.getInstance();

        // 设置基础信息
        respVO.setCurrentStatus(instance.getFlowStatus());
        respVO.setInstanceId(instance.getId());

        // 填充业务扩展信息
        fillBpmBizExt(respVO, instance.getId());

        // 填充表单数据
        fillFormData(respVO, instance, context.getEntityId());

        // 设置任务ID和节点扩展信息
        Task task = context.getTask();
        HisTask hisTask = context.getHisTask();
        BaseNodeExtDTO nodeExtDTO = null;

        if (task != null) {
            respVO.setTaskId(task.getId());
            nodeExtDTO = BpmUtil.getNodeExtDTOByNodeCode(task.getNodeCode(), instance.getDefJson());
        } else if (hisTask != null) {
            nodeExtDTO = BpmUtil.getNodeExtDTOByNodeCode(hisTask.getNodeCode(), instance.getDefJson());
        }

        // 填充其他流程详情
        instanceDetailStrategyManager.processInstanceDetail(
                respVO, nodeExtDTO, instance, context.getLoginUserId(), task != null);

        // 处理代理信息
        if (context.getAgentIns() != null) {
            respVO.setProcessTitle(BpmConstants.AGENT_TITLE_PREFIX + respVO.getProcessTitle());
        }

        return respVO;
    }

    /**
     * 加载待办任务（从请求参数中）
     *
     * @param reqVO   请求VO
     * @param context 上下文
     */
    private void loadTodoTask(BpmTaskDetailReqVO reqVO, InsDetailContext context) {
        Long taskId = reqVO.getTaskId();
        if (taskId == null) {
            throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "待办任务ID不能为空");
        }

        Task task = taskService.getById(taskId);
        if (task == null) {
            throw exception(ErrorCodeConstants.FLOW_TASK_NOT_EXISTS);
        }

        // 校验任务与实例是否匹配
        Long instanceId = context.getInstance().getId();
        if (!Objects.equals(task.getInstanceId(), instanceId)) {
            throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "待办任务ID与实例ID不匹配");
        }

        // 权限校验
        BpmPermissionUserContext userContext = findPermissionUserContext(
                taskId, String.valueOf(context.getLoginUserId()));
        if (userContext == null) {
            throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY);
        }

        context.setTask(task);
        context.setMatchedUser(userContext.getMatchedUser());
        context.setAgentIns(userContext.getAgentIns());
    }

    /**
     * 从实例中加载待办任务
     *
     * @param context 上下文
     */
    private void loadTodoTaskFromInstance(InsDetailContext context) {
        Long instanceId = context.getInstance().getId();
        Long loginUserId = context.getLoginUserId();

        // 查询该实例的待办任务
        List<Task> tasks = taskService.getByInsId(instanceId);
        if (CollectionUtils.isEmpty(tasks)) {
            return;
        }

        // 构建任务ID列表和映射
        List<Long> taskIds = tasks.stream().map(Task::getId).collect(Collectors.toList());
        Map<Long, Task> taskMap = tasks.stream()
                .collect(Collectors.toMap(Task::getId, task -> task));

        // 查找权限匹配的用户
        BpmPermissionUserContext userContext = findPermissionUserContext(
                taskIds, String.valueOf(loginUserId));
        if (userContext == null) {
            return;
        }

        // 设置上下文
        context.setMatchedUser(userContext.getMatchedUser());
        context.setAgentIns(userContext.getAgentIns());
        context.setTask(taskMap.get(userContext.getMatchedUser().getAssociated()));
    }

    /**
     * 校验创建人权限
     *
     * @param context 上下文
     */
    private void validateCreatorPermission(InsDetailContext context) {
        Instance instance = context.getInstance();
        Long loginUserId = context.getLoginUserId();

        if (!Objects.equals(String.valueOf(loginUserId), instance.getCreateBy())) {
            log.error("用户 {} 无权限访问我的创建实例 {}", loginUserId, instance.getId());
            throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY);
        }
    }

    /**
     * 加载抄送任务
     *
     * @param reqVO   请求VO
     * @param context 上下文
     */
    private void loadCcTask(BpmTaskDetailReqVO reqVO, InsDetailContext context) {
        Long taskId = reqVO.getTaskId();
        if (taskId == null) {
            throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "待办任务ID不能为空");
        }

        Long instanceId = context.getInstance().getId();

        // 权限校验：查询抄送记录（支持代理），并设置代理信息到 context
        validateCcRecordAndSetAgent(taskId, context);

        // 查询已办任务
        HisTask hisTask = findHisTask(taskId, instanceId);
        if (hisTask == null) {
            throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "未查询到已办任务");
        }

        context.setHisTask(hisTask);
    }

    /**
     * 加载已办任务
     *
     * @param reqVO   请求VO
     * @param context 上下文
     */
    private void loadDoneTask(BpmTaskDetailReqVO reqVO, InsDetailContext context) {
        Long taskId = reqVO.getTaskId();
        if (taskId == null) {
            throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "待办任务ID不能为空");
        }

        Long loginUserId = context.getLoginUserId();
        Long instanceId = context.getInstance().getId();

        // 查询已办任务列表
        List<HisTask> hisTasks = findHisTasks(taskId, instanceId);
        if (CollectionUtils.isEmpty(hisTasks)) {
            log.error("已办任务 {} 不存在", taskId);
            throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "未查询到已办任务");
        }

        // 构建已办任务映射
        Map<String, HisTask> hisTaskMap = buildHisTaskMap(hisTasks);

        // 查找匹配的已办任务（支持代理）
        HisTask matchedHisTask = findMatchedHisTask(hisTaskMap, taskId, loginUserId, context);
        if (matchedHisTask == null) {
            log.error("用户 {} 无权限访问已办任务 {}", loginUserId, taskId);
            throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "未查询到已办任务");
        }

        context.setHisTask(matchedHisTask);
    }

    /**
     * 校验抄送记录权限并设置代理信息（支持代理）
     *
     * @param taskId  任务ID
     * @param context 上下文（包含登录用户ID，用于设置代理信息）
     */
    private void validateCcRecordAndSetAgent(Long taskId, InsDetailContext context) {
        Long loginUserId = context.getLoginUserId();

        // 先查询直接权限
        ConfigStore configStore = new DefaultConfigStore();
        configStore.and(BpmFlowCcRecordDO.TASK_ID, taskId);
        configStore.and(BpmFlowCcRecordDO.USER_ID, loginUserId);
        BpmFlowCcRecordDO ccRecord = ccRecordRepository.findOne(configStore);

        if (ccRecord != null) {
            // 直接权限，没有代理记录
            return;
        }

        // 查询代理权限
        List<BpmFlowAgentInsDO> agentInsList = agentInsRepository.findAllByTaskIdAndAgentId(
                taskId, String.valueOf(loginUserId));
        if (CollectionUtils.isEmpty(agentInsList)) {
            throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "您没有查看此抄送的任务权限");
        }

        Set<Long> principalIds = agentInsList.stream()
                .map(BpmFlowAgentInsDO::getPrincipalId)
                .collect(Collectors.toSet());

        // 查找被代理人的抄送记录
        ConfigStore agentCcQuery = new DefaultConfigStore();
        agentCcQuery.and(BpmFlowCcRecordDO.TASK_ID, taskId);
        agentCcQuery.in(BpmFlowCcRecordDO.USER_ID, principalIds);

        BpmFlowCcRecordDO agentCcRecord = ccRecordRepository.findOne(agentCcQuery);
        if (agentCcRecord == null) {
            throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "您没有查看此抄送的任务权限");
        }

        // 找到代理的抄送记录，设置对应的代理记录到 context
        Long principalId = Long.valueOf(agentCcRecord.getUserId());
        BpmFlowAgentInsDO agentIns = agentInsList.stream()
                .filter(agent -> Objects.equals(agent.getPrincipalId(), principalId))
                .findFirst()
                .orElse(null);

        context.setAgentIns(agentIns);
    }


    /**
     * 查找已办任务
     *
     * @param taskId      任务ID
     * @param instanceId  实例ID
     * @return 已办任务
     */
    private HisTask findHisTask(Long taskId, Long instanceId) {
        HisTask hisTaskQuery = new FlowHisTask();
        hisTaskQuery.setTaskId(taskId);
        hisTaskQuery.setInstanceId(instanceId);
        return hisTaskService.getOne(hisTaskQuery);
    }

    /**
     * 查找已办任务列表
     *
     * @param taskId      任务ID
     * @param instanceId  实例ID
     * @return 已办任务列表
     */
    private List<HisTask> findHisTasks(Long taskId, Long instanceId) {
        HisTask hisTaskQuery = new FlowHisTask();
        hisTaskQuery.setTaskId(taskId);
        hisTaskQuery.setInstanceId(instanceId);
        return hisTaskService.list(hisTaskQuery);
    }

    /**
     * 构建已办任务映射（key: approver, value: HisTask）
     *
     * @param hisTasks 已办任务列表
     * @return 已办任务映射
     */
    private Map<String, HisTask> buildHisTaskMap(List<HisTask> hisTasks) {
        Map<String, HisTask> hisTaskMap = new HashMap<>();
        for (HisTask hisTask : hisTasks) {
            hisTaskMap.put(hisTask.getApprover(), hisTask);
        }
        return hisTaskMap;
    }

    /**
     * 查找匹配的已办任务（支持代理）
     *
     * @param hisTaskMap  已办任务映射
     * @param taskId      任务ID
     * @param loginUserId 登录用户ID
     * @param context     上下文（用于设置代理信息）
     * @return 匹配的已办任务，如果不存在则返回null
     */
    private HisTask findMatchedHisTask(Map<String, HisTask> hisTaskMap, Long taskId,
                                      Long loginUserId, InsDetailContext context) {
        // 先查找直接权限
        HisTask hisTask = hisTaskMap.get(String.valueOf(loginUserId));
        if (hisTask != null) {
            return hisTask;
        }

        // 查找代理权限
        List<BpmFlowAgentInsDO> agentInsList = agentInsRepository.findAllByTaskIdAndAgentId(
                taskId, String.valueOf(loginUserId));
        if (CollectionUtils.isEmpty(agentInsList)) {
            return null;
        }

        for (BpmFlowAgentInsDO agentInsDO : agentInsList) {
            hisTask = hisTaskMap.get(String.valueOf(agentInsDO.getPrincipalId()));
            if (hisTask != null) {
                context.setAgentIns(agentInsDO);
                return hisTask;
            }
        }

        return null;
    }

    /**
     * 标记已读状态
     *
     * @param reqVO   请求VO
     * @param context 上下文
     */
    private void markAsRead(BpmTaskDetailReqVO reqVO, InsDetailContext context) {
        // 标记待办任务为已读
        markTodoTaskAsRead(context);

        // 标记抄送任务为已读
        if (Objects.equals(reqVO.getFrom(), BpmViewSourceEnum.CC.getCode())) {
            markCcTaskAsRead(reqVO, context);
        }
    }

    /**
     * 标记待办任务为已读
     *
     * @param context 上下文
     */
    private void markTodoTaskAsRead(InsDetailContext context) {
        Task task = context.getTask();
        if (task == null) {
            return;
        }

        User matchedUser = context.getMatchedUser();
        if (matchedUser == null) {
            return;
        }

        String processedBy = matchedUser.getProcessedBy();
        List<User> users = userService.listByProcessedBys(task.getId(), processedBy);
        if (CollectionUtils.isEmpty(users)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        for (User user : users) {
            user.setUpdateTime(now);
        }
        userService.updateBatch(users);
    }

    /**
     * 标记抄送任务为已读
     *
     * @param reqVO   请求VO
     * @param context 上下文
     */
    private void markCcTaskAsRead(BpmTaskDetailReqVO reqVO, InsDetailContext context) {
        Long taskId = reqVO.getTaskId();
        if (taskId == null) {
            return;
        }

        // 确定要标记的用户ID（如果是代理，使用被代理人ID）
        Long userId = context.getLoginUserId();
        if (context.getAgentIns() != null) {
            userId = context.getAgentIns().getPrincipalId();
        }

        ConfigStore configStore = new DefaultConfigStore();
        configStore.and(BpmFlowCcRecordDO.TASK_ID, taskId);
        configStore.and(BpmFlowCcRecordDO.USER_ID, userId);

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
