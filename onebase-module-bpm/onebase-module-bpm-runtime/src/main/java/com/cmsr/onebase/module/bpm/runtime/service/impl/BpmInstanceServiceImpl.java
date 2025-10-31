package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.api.dto.BpmDefinitionExtDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.ApproverNodeExtDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.InitiationNodeExtDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.StartNodeExtDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.base.BaseNodeBtnCfgDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.api.enums.BpmActionButtonEnum;
import com.cmsr.onebase.module.bpm.api.enums.BpmBusinessStatusEnum;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowInsExtRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowInsBizExtDO;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.core.service.BpmEngineDefExtService;
import com.cmsr.onebase.module.bpm.runtime.service.BpmInstanceService;
import com.cmsr.onebase.module.bpm.runtime.vo.*;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.InsertDataReqDTO;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataMethodCoreService;
import com.cmsr.onebase.module.system.api.dept.DeptApi;
import com.cmsr.onebase.module.system.api.dept.dto.DeptRespDTO;
import com.cmsr.onebase.module.system.api.permission.PermissionApi;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.dto.NodeJson;
import org.dromara.warm.flow.core.entity.*;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.service.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.dromara.warm.flow.core.FlowEngine.nodeService;
import static org.dromara.warm.flow.core.enums.FlowStatus.PASS;

/**
 *
 * 流程实例服务实现
 *
 * @author liyang
 * @date 2025-10-27
 */
@Slf4j
@Service
public class BpmInstanceServiceImpl implements BpmInstanceService {
    // 自注入
    @Lazy
    @Resource
    private BpmInstanceServiceImpl self;

    @Resource
    private BpmEngineDefExtService defExtService;

    @Resource
    private DefService defService;

    @Resource
    private InsService insService;

    @Resource
    private TaskService taskService;

    @Resource
    private UserService userService;

    @Resource
    private DataMethodApi dataMethodApi;

    @Resource
    private HisTaskService hisTaskService;

    @Resource
    private BpmFlowInsExtRepository flowInsExtRepository;

    @Resource
    private DeptApi deptApi;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private NodeService nodeService;

    @Resource
    private MetadataDataMethodCoreService metadataDataMethodCoreService;

    @Resource
    private PermissionApi permissionApi;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ListActButtonRespVO getActButtons(String taskId, String businessId) {
        ListActButtonRespVO respVO = new ListActButtonRespVO();
        NodeJson currNodeJson = null;
        Instance instance = null;

        respVO.setButtons(new ArrayList<>());

        // businessId 业务ID是dataSetId，通过该ID能查到对应的流程定义信息
        // dataId是数据ID，通过该ID能查到对应的流程实例信息
        log.info("获取流程实例的操作按钮: {}, {}", taskId, businessId);

        // 查询已经发布的业务流程
        // 先判断taskId是否为空
        if (StringUtils.isBlank(taskId)) {
            // 为空则代表尚未发起流程，查询业务流程定义是否存在
            Definition flowDefinition = defExtService.getByFormPathAndStatus(businessId, PublishStatus.PUBLISHED.getKey());

            // 不存在已经发布的流程定义
            if (flowDefinition == null) {
                throw exception(ErrorCodeConstants.PUBLISHED_FLOW_NOT_EXISTS);
            }

            // 获取开始节点对应的按钮
            DefJson defJson = defService.queryDesign(flowDefinition.getId());
            for (NodeJson nodeJson : defJson.getNodeList()) {
                if (Objects.equals(nodeJson.getNodeType(), NodeType.START.getKey())) {
                    currNodeJson = nodeJson;
                    break;
                }
            }
        } else {
            Task task = taskService.getById(taskId);

            if (task == null) {
                log.error("获取流程实例的操作按钮: {} 不存在", taskId);
                throw exception(ErrorCodeConstants.FLOW_TASK_NOT_EXISTS);
            }

            // todo：判断task的权限
            String nodeCode = task.getNodeCode();
            Long instanceId = task.getInstanceId();
            Integer nodeType = task.getNodeType();

            // 流程节点类型不存在操作按钮
            if (!NodeType.isBetween(nodeType)) {
                throw exception(ErrorCodeConstants.FLOW_NODE_NOT_EXISTS);
            }

            // 数据存在则说明已经发起过流程，查询流程实例表
            instance = insService.getById(instanceId);

            // instance为空说明流程未发起/已完成，不存在流程实例
            if (instance == null) {
                throw exception(ErrorCodeConstants.FLOW_INSTANCE_NOT_EXISTS);
            }

            // 找到流程定义
            String defJsonStr = instance.getDefJson();
            DefJson defJson = FlowEngine.jsonConvert.strToBean(defJsonStr, DefJson.class);

            // 找到对应节点的配置
            for (NodeJson nodeJson : defJson.getNodeList()) {
                if (nodeJson.getNodeCode().equals(nodeCode)) {
                    currNodeJson = nodeJson;
                    break;
                }
            }
        }

        // 节点不存在
        if (currNodeJson == null) {
            throw exception(ErrorCodeConstants.FLOW_NODE_NOT_EXISTS);
        }

        BaseNodeExtDTO nodeExtDTO = JsonUtils.parseObject(currNodeJson.getExt(), BaseNodeExtDTO.class);
        String userId = String.valueOf(WebFrameworkUtils.getLoginUserId());
        List<BaseNodeBtnCfgDTO> buttonConfigs = new ArrayList<>();

        if (nodeExtDTO instanceof ApproverNodeExtDTO approverNodeExtDTO) {
            // todo：判断审批节点的权限

            // 审批节点
            if (approverNodeExtDTO.getButtonConfigs() != null) {
                buttonConfigs.addAll(approverNodeExtDTO.getButtonConfigs());
            }
        } else if (nodeExtDTO instanceof StartNodeExtDTO startNodeExtDTO) {
            // 开始节点
            if (startNodeExtDTO.getButtonConfigs() != null) {
                buttonConfigs.addAll(startNodeExtDTO.getButtonConfigs());
            }
        } else if (nodeExtDTO instanceof InitiationNodeExtDTO initiationNodeExtDTO) {
            // 发起节点
            // 判断是否是创建人
            if (!Objects.equals(instance.getCreateBy(), userId)) {
                throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY);
            }

            if (initiationNodeExtDTO.getButtonConfigs() != null) {
                buttonConfigs.addAll(initiationNodeExtDTO.getButtonConfigs());
            }
        } else {
            // 未知节点
            throw exception(ErrorCodeConstants.UNSUPPORT_NODE_TYPE);
        }

        for (BaseNodeBtnCfgDTO buttonConfig : buttonConfigs) {
            if (!buttonConfig.getEnabled()) {
                continue;
            }

            ListActButtonRespVO.ActionButton buttonVO = new ListActButtonRespVO.ActionButton();
            buttonVO.setButtonName(buttonConfig.getButtonName());
            buttonVO.setButtonType(buttonConfig.getButtonType());
            buttonVO.setDisplayName(buttonConfig.getDisplayName());
            respVO.getButtons().add(buttonVO);
        }

        respVO.setBusinessId(businessId);

        return respVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BpmSubmitRespVO submit(BpmSubmitReqVO reqVO) {
        BpmSubmitRespVO respVO = new BpmSubmitRespVO();
        String entityDataId = null;

        Definition def = defExtService.getByFormPathAndStatus(reqVO.getBusinessId(), PublishStatus.PUBLISHED.getKey());
        if (def == null) {
            throw exception(ErrorCodeConstants.PUBLISHED_FLOW_NOT_EXISTS);
        }

        // 实体ID todo：校验，应该和businessID有关联关系
        Long entityId = reqVO.getEntity().getEntityId();

        // 业务状态
        BpmBusinessStatusEnum businessStatus = BpmBusinessStatusEnum.IN_APPROVAL;

        if (reqVO.isDraft()) {
            businessStatus = BpmBusinessStatusEnum.DRAFT;
        }

        EntityVO entityVO = reqVO.getEntity();

        InsertDataReqDTO insertDataReqDTO = new InsertDataReqDTO();
        insertDataReqDTO.setEntityId(entityId);
        insertDataReqDTO.setData(new ArrayList<>());
        insertDataReqDTO.getData().add(entityVO.getData());

        // 先插入数据
        List<List<EntityFieldDataRespDTO>> insertedData = dataMethodApi.insertData(insertDataReqDTO);

        for (EntityFieldDataRespDTO respDTO : insertedData.get(0)) {
            if (Objects.equals(respDTO.getFieldName(), "id")) {
                entityDataId = String.valueOf(respDTO.getFieldValue());
                break;
            }
        }

        if (StringUtils.isBlank(entityDataId)) {
            throw exception(ErrorCodeConstants.FLOW_ENTITY_DATA_ID_NOT_EXISTS);
        }

        BpmFlowInsBizExtDO flowInsExtDO = new BpmFlowInsBizExtDO();
        Map<String, Object> variables = new HashMap<>();

        // 传应用ID和实体ID
        BpmDefinitionExtDTO extDto = JsonUtils.parseObject(def.getExt(), BpmDefinitionExtDTO.class);
        variables.put("appId", extDto.getAppId());
        variables.put("entityId", entityId);

        entityVO.getData().forEach((key, value) -> variables.put(String.valueOf(key), value));

        // 开启流程
        FlowParams flowParams = FlowParams.build()
                //.handler(startProcessBo.getHandler())
                .flowCode(def.getFlowCode())
                .variable(variables)
                .flowStatus(businessStatus.getCode());

        Instance instance = insService.start(entityDataId, flowParams);

        // 提交请求 自动往下走一个节点
        if (!reqVO.isDraft()) {
            List<Task> tasks = taskService.getByInsId(instance.getId());
            Task task = tasks.get(0);
            String taskNodeCode = task.getNodeCode();

            String defJsonStr = instance.getDefJson();
            DefJson defJson = FlowEngine.jsonConvert.strToBean(defJsonStr, DefJson.class);

            for (NodeJson nodeJson : defJson.getNodeList()) {
                if (Objects.equals(nodeJson.getNodeCode(), taskNodeCode)) {
                    // 判断节点类型，必须是发起节点
                    BaseNodeExtDTO extDTO = JsonUtils.parseObject(nodeJson.getExt(), BaseNodeExtDTO.class);

                    if (!Objects.equals(extDTO.getNodeType(), BpmNodeTypeEnum.INITIATION.getCode())) {
                        throw exception(ErrorCodeConstants.FLOW_NODE_TYPE_MUST_BE_INITIATION);
                    }

                    break;
                }
            }

            // 自动跳到下一个节点
            FlowParams skipParams = FlowParams.build()
//                    .handler(completeTaskBo.getHandler())
                    .variable(variables)
                    .skipType(SkipType.PASS.getKey())
                    .message("已提交")
                    .flowStatus(businessStatus.getCode())
                    .hisStatus("已提交");
//                    .hisTaskExt(completeTaskBo.getFileId());
            taskService.skip(skipParams, task);

            // 设置发起时间
            flowInsExtDO.setSubmitTime(LocalDateTime.now());
        }

        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        String initiatorName = SecurityFrameworkUtils.getLoginUserNickname();
        Long deptId = SecurityFrameworkUtils.getLoginUserDeptId();
        String businessTitle = String.format("%s发起的%s", initiatorName, reqVO.getFormName());

        // 任选3个字段，todo 从全局配置里选择，关联实体查询
        String formSummary = reqVO.getEntity().getData().entrySet().stream()
                .limit(3)
                .map(Map.Entry::getValue)
                .map(Object::toString)
                .collect(Collectors.joining(" "));

        if (StringUtils.isBlank(formSummary)) {
            formSummary = reqVO.getFormName();
        }

        CommonResult<DeptRespDTO> deptRespDTO = deptApi.getDept(deptId);
        if (!deptRespDTO.isSuccess()) {
            throw exception(ErrorCodeConstants.DEPT_API_CALL_FAILED);
        }

        // 保存扩展信息
        flowInsExtDO.setBusinessId(entityDataId);
        flowInsExtDO.setBpmVersion("V" + def.getVersion());
        flowInsExtDO.setBusinessTitle(businessTitle);
        flowInsExtDO.setInitiatorId(loginUserId);
        flowInsExtDO.setInitiatorName(initiatorName);
        flowInsExtDO.setInitiatorDeptId(deptId);
        flowInsExtDO.setInitiatorDeptName(deptRespDTO.getData().getName());
        flowInsExtDO.setFormName(reqVO.getFormName());
        flowInsExtDO.setFormSummary(formSummary);
        flowInsExtDO.setInstanceId(instance.getId());
        flowInsExtDO.setAppId(extDto.getAppId());

        flowInsExtRepository.insert(flowInsExtDO);

        respVO.setInstanceId(instance.getId());
        respVO.setEntityDataId(entityDataId);

        return respVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execTask(ExecTaskReqVO reqVO) {
        String taskId = reqVO.getTaskId();

        BpmActionButtonEnum buttonEnum = BpmActionButtonEnum.getByCode(reqVO.getButtonType());
        if (buttonEnum == null) {
            throw exception(ErrorCodeConstants.UNSUPPORT_ACTION_BUTTON_TYPE);
        }

        // 暂时只支持同意和拒绝和保存 todo：判断当前节点是否支持该按钮
        if (!BpmActionButtonEnum.APPROVE.equals(buttonEnum)
                && !BpmActionButtonEnum.REJECT.equals(buttonEnum)
                && !BpmActionButtonEnum.SAVE.equals(buttonEnum)
                && !BpmActionButtonEnum.SUBMIT.equals(buttonEnum)) {
            throw exception(ErrorCodeConstants.UNSUPPORT_ACTION_BUTTON_TYPE);
        }

        // 查找task是否存在
        Task task = taskService.getById(taskId);
        if (task == null) {
            throw exception(ErrorCodeConstants.FLOW_TASK_NOT_EXISTS);
        }

        // 校验实体ID
        if (reqVO.getEntity() != null) {
            Instance instance = insService.getById(task.getInstanceId());

            if (instance == null) {
                throw exception(ErrorCodeConstants.FLOW_INSTANCE_NOT_EXISTS);
            }

            Long entityId = (Long) instance.getVariableMap().get("entityId");

            if (entityId == null) {
                throw exception(ErrorCodeConstants.FLOW_NOT_BIND_ENTITY_ID);
            }

            if (!entityId.equals(reqVO.getEntity().getEntityId())) {
                throw exception(ErrorCodeConstants.INVALID_ENTITY_ID);
            }
        }

        List<User> users = userService.getByAssociateds(List.of(task.getId()));
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        boolean hasPermission = false;

        for (User user : users) {
            if (user.getProcessedBy().equals(String.valueOf(loginUserId))) {
                // 说明是当前登录用户拥有权限
                hasPermission = true;
                break;
            }
        }

        if (!hasPermission) {
            throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY);
        }

        // todo： 判断字段读写权限

        Map<String, Object> variables = new HashMap<>();

        EntityVO entityVO = reqVO.getEntity();

        if (entityVO != null) {
            entityVO.getData().forEach((key, value) -> variables.put(String.valueOf(key), value));
        }

        // 自动跳到下一个节点
        FlowParams skipParams = FlowParams.build()
//                    .handler(completeTaskBo.getHandler())
                .variable(variables);
//                    .hisStatus(TaskStatusEnum.PASS.getStatus())
//                    .hisTaskExt(completeTaskBo.getFileId());

        // todo 查按钮的默认审批意见
        String comment = reqVO.getComment();

        if (StringUtils.isBlank(comment)) {
            comment = buttonEnum.getName();
        }

        if (buttonEnum == BpmActionButtonEnum.APPROVE) {
            skipParams = skipParams.message(comment)
                    .skipType(SkipType.PASS.getKey())
                    .flowStatus(BpmBusinessStatusEnum.IN_APPROVAL.getCode())
                    .hisStatus("已" + buttonEnum.getName());

            taskService.skip(skipParams, task);
        } else if (buttonEnum == BpmActionButtonEnum.REJECT) {
            String nodeCode = task.getNodeCode();
            boolean hasRejectNode = false;


            List<Skip> skipList = FlowEngine.skipService().getByDefIdAndNowNodeCode(task.getDefinitionId(), nodeCode);
            for (Skip skip : skipList) {
                if (skip.getSkipType().equals(SkipType.REJECT.getKey())) {
                    hasRejectNode = true;
                    break;
                }
            }

            skipParams = skipParams.message(comment)
                    .skipType(SkipType.REJECT.getKey())
                    .flowStatus(BpmBusinessStatusEnum.REJECTED.getCode())
                    .hisStatus("已" + buttonEnum.getName());

            if (hasRejectNode) {
                taskService.skip(skipParams, task);
            } else {
                taskService.rejectLast(task, skipParams);
            }
        }

        // todo：更新实体数据
    }

    @Override
    public List<BpmOperatorRecordRespVO.OperatorRecord> getOperatorRecord(Long instanceId) {
        List<BpmOperatorRecordRespVO.OperatorRecord> operatorRecords = new ArrayList<>();

        Instance instance = insService.getById(instanceId);

        if (instance == null) {
            throw exception(ErrorCodeConstants.FLOW_INSTANCE_NOT_EXISTS);
        }

        String defJsonStr = instance.getDefJson();
        DefJson defJson = FlowEngine.jsonConvert.strToBean(defJsonStr, DefJson.class);

        // 查出已办
        List<HisTask> hisTasks = hisTaskService.getByInsId(instanceId);

        // 查出待办
        List<Task> tasks = taskService.getByInsId(instanceId);

        // todo： 按照时间排序，已办、待办按照时间排序
        Map<String, BaseNodeExtDTO> nodeDtoMap = new HashMap<>();
        Map<Long, BpmOperatorRecordRespVO.OperatorRecord> recordMap = new HashMap<>();

        for (NodeJson nodeJson : defJson.getNodeList()) {
            BaseNodeExtDTO extDTO = JsonUtils.parseObject(nodeJson.getExt(), BaseNodeExtDTO.class);
            nodeDtoMap.put(nodeJson.getNodeCode(), extDTO);
        }

        // 进行组装
        for (HisTask hisTask : hisTasks) {
            Long taskId = hisTask.getTaskId();

            // 跳过非中间节点
            if (!NodeType.isBetween(hisTask.getNodeType())) {
                continue;
            }

            BpmOperatorRecordRespVO.OperatorRecord record = recordMap.get(taskId);

            if (record == null) {
                BaseNodeExtDTO extDTO = nodeDtoMap.get(hisTask.getNodeCode());
                record = new BpmOperatorRecordRespVO.OperatorRecord();
                record.setNodeName(hisTask.getNodeName());
                record.setNodeType(extDTO.getNodeType());

                if (extDTO instanceof ApproverNodeExtDTO approverNodeExtDTO) {
                    record.setApproveMode(approverNodeExtDTO.getApproverConfig().getApprovalMode());
                }

                operatorRecords.add(record);
                recordMap.put(taskId, record);
            }

            if (record.getOperators() == null) {
                record.setOperators(new ArrayList<>());
            }

            BpmOperatorRecordRespVO.OperatorInfo operatorInfo = new BpmOperatorRecordRespVO.OperatorInfo();
            operatorInfo.setOperator(hisTask.getApprover());
            operatorInfo.setOperatorTime(hisTask.getUpdateTime());
            operatorInfo.setComment(hisTask.getMessage());
            operatorInfo.setTaskStatus(hisTask.getFlowStatus());

            record.setDisplayStatus(operatorInfo.getTaskStatus());

            record.getOperators().add(operatorInfo);
        }

        // 处理待办数据
        for (Task task : tasks) {
            Long taskId = task.getId();
            BpmOperatorRecordRespVO.OperatorRecord record = recordMap.get(taskId);
            BaseNodeExtDTO extDTO = nodeDtoMap.get(task.getNodeCode());
            String nodeType = extDTO.getNodeType();

            if (record == null) {
                record = new BpmOperatorRecordRespVO.OperatorRecord();
                record.setNodeName(task.getNodeName());
                record.setNodeType(nodeType);

                if (extDTO instanceof ApproverNodeExtDTO approverNodeExtDTO) {
                    record.setApproveMode(approverNodeExtDTO.getApproverConfig().getApprovalMode());
                }

                operatorRecords.add(record);
                recordMap.put(taskId, record);
            }

            // 查找有权限的用户
            List<User> users = userService.getByAssociateds(List.of(task.getId()));

            if (CollectionUtils.isNotEmpty(users)) {
                if (record.getOperators() == null) {
                    record.setOperators(new ArrayList<>());
                }

                for (User user : users) {
                    BpmOperatorRecordRespVO.OperatorInfo operatorInfo = new BpmOperatorRecordRespVO.OperatorInfo();
                    operatorInfo.setOperator(user.getProcessedBy());
                    operatorInfo.setOperatorTime(task.getUpdateTime());

                    // 判断下节点类型
                    if (Objects.equals(nodeType, BpmNodeTypeEnum.INITIATION.getCode())) {
                        operatorInfo.setTaskStatus("待提交");
                    } else if (Objects.equals(nodeType, BpmNodeTypeEnum.APPROVER.getCode())) {
                        operatorInfo.setTaskStatus("审批中");
                    } else {
                        // todo
                        operatorInfo.setTaskStatus("待处理");
                    }

                    // 只要有待办，展示状态与任务状态一致
                    record.setDisplayStatus(operatorInfo.getTaskStatus());

                    record.getOperators().add(operatorInfo);
                }
            }
        }

        // 只运行一次的循环，为了让代码看起来层级简单些
        do {
            if (CollectionUtils.isEmpty(operatorRecords)) {
                break;
            }

            Set<Long> userIds = new HashSet<>();

            for (BpmOperatorRecordRespVO.OperatorRecord operatorRecord : operatorRecords) {
                if (CollectionUtils.isNotEmpty(operatorRecord.getOperators())) {
                    for (BpmOperatorRecordRespVO.OperatorInfo operatorInfo : operatorRecord.getOperators()) {
                        userIds.add(Long.parseLong(operatorInfo.getOperator()));
                    }
                }
            }

            if (CollectionUtils.isEmpty(userIds)) {
                break;
            }

            CommonResult<List<AdminUserRespDTO>> result = adminUserApi.getUserList(userIds);

            // todo: 抛出异常？
            if (!result.isSuccess()) {
                log.warn("获取用户列表失败，userIds: {}", userIds);
                break;
            }

            if (CollectionUtils.isEmpty(result.getData())) {
                break;
            }

            List<AdminUserRespDTO> users = result.getData();


            Map<Long, String> userMap = new HashMap<>();

            for (AdminUserRespDTO user : users) {
                userMap.put(user.getId(), user.getNickname());
            }

            for (BpmOperatorRecordRespVO.OperatorRecord operatorRecord : operatorRecords) {
                if (CollectionUtils.isNotEmpty(operatorRecord.getOperators())) {
                    for (BpmOperatorRecordRespVO.OperatorInfo operatorInfo : operatorRecord.getOperators()) {
                        String nickName = userMap.get(Long.parseLong(operatorInfo.getOperator()));

                        if (nickName != null) {
                            operatorInfo.setOperator(nickName);
                        }
                    }
                }
            }

            break;
        } while (false);

        return operatorRecords;
    }

    @Override
    public BpmFlowTaskDetailVO getFormDetail(String taskId, Long instanceId) {
        BpmFlowTaskDetailVO vo = new BpmFlowTaskDetailVO();
        NodeJson currNodeJson = null;
        Long loginUserId = WebFrameworkUtils.getLoginUserId();

        Task task = taskService.getById(taskId);

        // 任务不存在
        if (task == null) {
           throw exception(ErrorCodeConstants.FLOW_TASK_NOT_EXISTS);
        }

        // 以任务里的数据为准
        instanceId = task.getInstanceId();
        Instance instance = insService.getById(instanceId);

        if (instance == null) {
            throw exception(ErrorCodeConstants.FLOW_INSTANCE_NOT_EXISTS);
        }

        Long entityId = (Long) instance.getVariableMap().get("entityId");

        if (entityId == null) {
            throw exception(ErrorCodeConstants.FLOW_NOT_BIND_ENTITY_ID);
        }

        // 查询下是否是当前用户的待办任务，如果不是则不显示按钮
        List<User> users = userService.getByProcessedBys(task.getId(), List.of(String.valueOf(loginUserId)));

        // 测试按钮显示
        if (CollectionUtils.isNotEmpty(users)) {
            String nodeCode = task.getNodeCode();
            DefJson defJson = FlowEngine.jsonConvert.strToBean(instance.getDefJson(), DefJson.class);
            // 找到对应节点的配置
            for (NodeJson nodeJson : defJson.getNodeList()) {
                if (nodeJson.getNodeCode().equals(nodeCode)) {
                    currNodeJson = nodeJson;
                    break;
                }
            }

            BaseNodeExtDTO nodeExtDTO = JsonUtils.parseObject(currNodeJson.getExt(), BaseNodeExtDTO.class);
            String userId = String.valueOf(WebFrameworkUtils.getLoginUserId());
            List<BaseNodeBtnCfgDTO> buttonConfigs = new ArrayList<>();

            if (nodeExtDTO instanceof ApproverNodeExtDTO approverNodeExtDTO) {
                // todo：判断审批节点的权限

                // 审批节点
                if (approverNodeExtDTO.getButtonConfigs() != null) {
                    buttonConfigs.addAll(approverNodeExtDTO.getButtonConfigs());
                }
            } else if (nodeExtDTO instanceof InitiationNodeExtDTO initiationNodeExtDTO) {
                // 发起节点
                // 判断是否是创建人
                if (!Objects.equals(instance.getCreateBy(), userId)) {
                    throw exception(ErrorCodeConstants.FLOW_PERMISSION_DENY);
                }

                if (initiationNodeExtDTO.getButtonConfigs() != null) {
                    buttonConfigs.addAll(initiationNodeExtDTO.getButtonConfigs());
                }
            } else {
                // 未知节点
                log.info("未知节点类型，nodeCode: {}", nodeCode);
            }

            for (BaseNodeBtnCfgDTO buttonConfig : buttonConfigs) {
                if (!buttonConfig.getEnabled()) {
                    continue;
                }

                if (vo.getButtonConfigs() == null) {
                    vo.setButtonConfigs(new ArrayList<>());
                }

                vo.getButtonConfigs().add(buttonConfig);
            }
        }

        vo.setCurrentStatus(instance.getFlowStatus());

        //查询流程扩展信息
        ConfigStore configStore= new DefaultConfigStore();
        configStore.and("instance_id", instanceId);
        BpmFlowInsBizExtDO flowInsExtDO = flowInsExtRepository.findOne(configStore);

        if (flowInsExtDO != null) {
            vo.setBpmVersion(flowInsExtDO.getBpmVersion());
            vo.setInitiatorId(flowInsExtDO.getInitiatorId());
            vo.setInitiatorName(flowInsExtDO.getInitiatorName());
            vo.setSubmitTime(flowInsExtDO.getSubmitTime());
            vo.setInitiatorDeptId(flowInsExtDO.getInitiatorDeptId());
            vo.setInitiatorDeptName(flowInsExtDO.getInitiatorDeptName());
            vo.setInitiatorId(flowInsExtDO.getInitiatorId());
            vo.setInitiatorName(flowInsExtDO.getInitiatorName());
        }

        String entityDataId = instance.getBusinessId();

        //查询form信息
        Map<String, Object> data = metadataDataMethodCoreService.getData(entityId, entityDataId,null);
        if (data != null && !data.isEmpty()){
            vo.setFormData(data);
        }
        return vo;

    }

    @Override
    public List<BpmFlowPreviewVO> flowPreview(String flowCode) {
        // 获取流程节点
        Definition definition = defService.getPublishByFlowCode(flowCode);
        if (definition == null) {
            log.error(ErrorCodeConstants.FLOW_NOT_EXISTS.getMsg());
            throw exception(ErrorCodeConstants.FLOW_NOT_EXISTS);
        }

        Node startNode = nodeService.getStartNode(definition.getId());
        Node endNode = nodeService.getEndNode(definition.getId());

        if (startNode == null || endNode == null) {
            log.error("流程定义缺少开始节点或结束节点");
            throw exception(ErrorCodeConstants.FLOW_NODE_NOT_EXISTS);
        }

        // 获取开始节点的下一个节点作为当前节点
        List<Node> nextNodesFromStart = nodeService.getNextNodeList(definition.getId(), startNode.getNodeCode(), null, "PASS", null);
        String currentNodeCode = null;
        if (!nextNodesFromStart.isEmpty()) {
            currentNodeCode = nextNodesFromStart.get(0).getNodeCode();
        }

        // 递归获取所有中间节点
        List<BpmFlowPreviewVO> result = new ArrayList<>();
        traverseFlowNodes(definition.getId(), startNode.getNodeCode(), result, new HashSet<>(), endNode.getNodeCode(), currentNodeCode);
        return result;

    }
     /**
     * 递归遍历流程节点
     */
    private void traverseFlowNodes(Long definitionId, String currentNodeCode,
                                   List<BpmFlowPreviewVO> result, Set<String> visitedNodes,
                                   String endNodeCode, String targetCurrentNodeCode) {
        // 递归出口：如果是结束节点或者已经访问过的节点
        if (endNodeCode.equals(currentNodeCode) || visitedNodes.contains(currentNodeCode)) {
            return;
        }

        // 标记当前节点为已访问
        visitedNodes.add(currentNodeCode);

        // 获取当前节点信息（排除开始节点和结束节点）
        Node currentNode = nodeService.getByDefIdAndNodeCode(definitionId, currentNodeCode);
        if (currentNode != null) {
            // 不添加开始节点和结束节点到结果中
            boolean isStartNode = nodeService.getStartNode(definitionId).getNodeCode().equals(currentNodeCode);
            boolean isEndNode = nodeService.getEndNode(definitionId).getNodeCode().equals(currentNodeCode);

            if (!isStartNode && !isEndNode) {
                BpmFlowPreviewVO previewVO = new BpmFlowPreviewVO();
                previewVO.setNodeCode(currentNode.getNodeCode());
                previewVO.setNodeName(currentNode.getNodeName());
                // 设置是否为当前节点
                previewVO.setCurrentNode(currentNodeCode.equals(targetCurrentNodeCode));
                // TODO: 设置审批人信息
                if(StringUtils.isNotEmpty(currentNode.getPermissionFlag())){
                    try {
                        // 直接解析 JSON 字符串为 Map
                        Map<String, Object> permissionMap = JsonUtils.parseObject(currentNode.getPermissionFlag(), Map.class);

                        // 提取 userIds 数组
                        List<String> strUserIds = (List<String>) permissionMap.get("userIds");
                        if (strUserIds != null && !strUserIds.isEmpty()) {
                            List<Long> userIds = strUserIds.stream()
                                    .map(Long::valueOf)
                                    .collect(Collectors.toList());
                            CommonResult<List<AdminUserRespDTO>> admins = adminUserApi.getUserList(userIds);
                            if(admins.isSuccess()) {
                                List<BpmFlowPreviewVO.HandlerInfo> handlers = new ArrayList<>();
                                admins.getData().forEach(user -> {
                                    BpmFlowPreviewVO.HandlerInfo handlerInfo = new BpmFlowPreviewVO.HandlerInfo();
                                    handlerInfo.setUserId(user.getId());
                                    handlerInfo.setUserName(user.getNickname());
                                    handlerInfo.setUserAvatar(user.getAvatar());
                                    handlers.add(handlerInfo);
                                });
                                previewVO.setHandlers(handlers);
                            }

                        }

                        // 提取 roleIds 数组
                        List<String> strRoleIds = (List<String>) permissionMap.get("roleIds");
                        if (strRoleIds != null && !strRoleIds.isEmpty()) {
                                List<Long> roleIds = strRoleIds.stream()
                                        .map(Long::valueOf)
                                        .collect(Collectors.toList());

                            CommonResult<Set<Long>> userIds =   permissionApi.getUserRoleIdListByRoleIds(roleIds);
                            if (userIds.isSuccess()) {
                                Set<Long> idsData = userIds.getData();
                                CommonResult<List<AdminUserRespDTO>> admins = adminUserApi.getUserList(idsData);
                                if(admins.isSuccess()) {
                                    List<BpmFlowPreviewVO.HandlerInfo> handlers = new ArrayList<>();
                                    admins.getData().forEach(user -> {
                                        BpmFlowPreviewVO.HandlerInfo handlerInfo = new BpmFlowPreviewVO.HandlerInfo();
                                        handlerInfo.setUserId(user.getId());
                                        handlerInfo.setUserName(user.getNickname());
                                        handlerInfo.setUserAvatar(user.getAvatar());
                                        handlers.add(handlerInfo);
                                    });
                                    previewVO.setHandlers(handlers);
                                }

                            }
                            log.info("提取到的 Role IDs: {}", roleIds);
                        }
                    } catch (Exception e) {
                        log.error("解析 permissionFlag JSON 失败: {}", currentNode.getPermissionFlag(), e);
                    }

                }

                result.add(previewVO);
            }
        }

        // 获取下一节点列表
        List<Node> nextNodes = nodeService.getNextNodeList(definitionId, currentNodeCode, null, "PASS", null);

        // 递归处理下一节点
        for (Node nextNode : nextNodes) {
            traverseFlowNodes(definitionId, nextNode.getNodeCode(), result, visitedNodes, endNodeCode, targetCurrentNodeCode);
        }
    }


}
