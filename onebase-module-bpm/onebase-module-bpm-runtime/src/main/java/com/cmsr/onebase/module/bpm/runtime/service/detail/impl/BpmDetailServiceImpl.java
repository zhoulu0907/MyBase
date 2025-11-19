package com.cmsr.onebase.module.bpm.runtime.service.detail.impl;

import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowCcRecordRepository;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowInsBizExtRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowCcRecordDO;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowInsBizExtDO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmUserTypeEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmViewSourceEnum;
import com.cmsr.onebase.module.bpm.core.utils.BpmUtil;
import com.cmsr.onebase.module.bpm.core.vo.UserBasicInfoVO;
import com.cmsr.onebase.module.bpm.runtime.service.detail.BpmDetailService;
import com.cmsr.onebase.module.bpm.runtime.service.detail.strategy.InstanceDetailStrategyManager;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailRespVO;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowHisTask;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataMethodCoreService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
    @Resource
    private InsService insService;

    @Resource
    private TaskService taskService;

    @Resource
    private HisTaskService hisTaskService;

    @Resource
    private UserService userService;

    @Resource
    private BpmFlowInsBizExtRepository flowInsExtRepository;

    @Resource
    private BpmFlowCcRecordRepository ccRecordRepository;

    @Resource
    private MetadataDataMethodCoreService metadataDataMethodCoreService;

    @Resource
    private InstanceDetailStrategyManager instanceDetailStrategyManager;

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

        // 当前待办
        Task currTask = getLastTodoTask(reqVO, loginUserId, source);

        // 历史已办
        HisTask hisTask = null;

        // 存在最新待办，已办就没必要查了
        if (currTask == null) {
            hisTask = getDoneTask(reqVO, instance, loginUserId, source);
        }

        // 填充业务扩展信息（与节点类型无关的通用逻辑）
        fillBpmBizExt(respVO, instanceId);

        // 填充表单数据（与节点类型无关的通用逻辑）
        fillFormData(respVO, instance, entityId);

        BaseNodeExtDTO nodeExtDTO = null;

        if (currTask != null) {
            respVO.setTaskId(currTask.getId());
            nodeExtDTO = BpmUtil.getNodeExtDTOByNodeCode(currTask.getNodeCode(), instance.getDefJson());
        } else if (hisTask != null) {
            nodeExtDTO = BpmUtil.getNodeExtDTOByNodeCode(hisTask.getNodeCode(), instance.getDefJson());
        }

        // 填充其他流程详情
        instanceDetailStrategyManager.processInstanceDetail(respVO, nodeExtDTO, instance, loginUserId, currTask != null);

        return respVO;
    }

    private Task getLastTodoTask(BpmTaskDetailReqVO reqVO, Long loginUserId, BpmViewSourceEnum sourceEnum) {
        Long instanceId = reqVO.getInstanceId();

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
            List<User> users = userService.listByProcessedBys(taskId, String.valueOf(loginUserId));

            if (CollectionUtils.isEmpty(users)) {
                log.error("用户 {} 无权限访问待办 taskId: {}", loginUserId, taskId);
                throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY);
            }

            return task;
        }

        return getLastTodoTask(instanceId, loginUserId);
    }

    private HisTask getDoneTask(BpmTaskDetailReqVO reqVO, Instance instance, Long loginUserId, BpmViewSourceEnum sourceEnum) {
        Long instanceId = reqVO.getInstanceId();

        if (sourceEnum == BpmViewSourceEnum.TODO) {
            return null;
        } else if (sourceEnum == BpmViewSourceEnum.CREATED) {
            // 我的创建无需已办任务，但需要校验是否为创建人，todo：是否增加统一校验逻辑
            if (!Objects.equals(String.valueOf(loginUserId), instance.getCreateBy())) {
                log.error("用户 {} 无权限访问我的创建实例 {}", loginUserId, instanceId);
                throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY);
            }

            return null;
        } else if (sourceEnum == BpmViewSourceEnum.CC) {
            Long taskId = reqVO.getTaskId();

            if (taskId == null) {
                throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "待办任务ID不能为空");
            }

            ConfigStore configStore = new DefaultConfigStore();
            configStore.and(BpmFlowCcRecordDO.TASK_ID, taskId);
            configStore.and(BpmFlowCcRecordDO.USER_ID, loginUserId);

            BpmFlowCcRecordDO ccRecordDO = ccRecordRepository.findOne(configStore);

            if (ccRecordDO == null) {
                throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "您没有查看此抄送的任务权限");
            }

            // 查询已办任务
            HisTask hisTaskQuery = new FlowHisTask();
            hisTaskQuery.setTaskId(taskId);
            hisTaskQuery.setInstanceId(instanceId);

            HisTask hisTask = hisTaskService.getOne(hisTaskQuery);

            if (hisTask == null) {
                throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "未查询到已办任务");
            }

            return hisTask;
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

            for (HisTask hisTask : hisTasks) {
                if (Objects.equals(hisTask.getApprover(), String.valueOf(loginUserId))) {
                    // 审批人权限，直接返回
                    return hisTask;
                }

                if (Objects.equals(hisTask.getCollaborator(), String.valueOf(loginUserId))) {
                    // 协作者权限，直接返回
                    return hisTask;
                }
            }

            log.error("用户 {} 无权限访问已办任务 {}", loginUserId, taskId);
            throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY.getCode(), "未查询到已办任务");
        }
    }

    /**
     * 获取最新的待办任务
     *
     * @param instanceId 流程实例ID
     * @param loginUserId 登录用户ID
     * @return 待办任务，如果无权限则返回null
     */
    private Task getLastTodoTask(Long instanceId, Long loginUserId) {
        // 查询该实例的待办任务
        List<Task> tasks = taskService.getByInsId(instanceId);
        if (tasks == null || tasks.isEmpty()) {
            return null;
        }

        List<Long> taskIds = new ArrayList<>();
        Map<Long, Task> taskMap = new HashMap<>();

        for (Task task : tasks) {
            taskIds.add(task.getId());
            taskMap.put(task.getId(), task);
        }

        // 查询任务关联的用户（审批人、转交人、委派人）
        List<User> users = userService.getByAssociateds(
                taskIds,
                BpmUserTypeEnum.APPROVAL.getCode(),
                BpmUserTypeEnum.TRANSFER.getCode(),
                BpmUserTypeEnum.DEPUTE.getCode(),
                BpmUserTypeEnum.AGENT.getCode()
        );

        if (CollectionUtils.isEmpty(users)) {
            return null;
        }

        // 检查当前用户是否有权限
        for (User user : users) {
            if (Objects.equals(user.getProcessedBy(), String.valueOf(loginUserId))) {
                return taskMap.get(user.getAssociated());
            }
        }

        return null;
    }

    /**
     * 填充业务扩展信息
     *
     * @param vo 详情VO
     * @param instanceId 流程实例ID
     */
    private void fillBpmBizExt(BpmTaskDetailRespVO vo, Long instanceId) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.and("instance_id", instanceId);
        BpmFlowInsBizExtDO flowInsExtDO = flowInsExtRepository.findOne(configStore);

        if (flowInsExtDO == null) {
            throw exception(ErrorCodeConstants.BPM_BIZ_EXT_NOT_EXIST);
        }

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
     * @param vo 详情VO
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
}
