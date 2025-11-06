package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.api.dto.BpmDefinitionExtDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.ApproverNodeExtDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.InitiationNodeExtDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.NodePermFlagDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.StartNodeExtDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.base.BaseNodeBtnCfgDTO;
import com.cmsr.onebase.module.bpm.api.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.api.enums.BpmActionButtonEnum;
import com.cmsr.onebase.module.bpm.api.enums.BpmBusinessStatusEnum;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowInsBizExtRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowInsBizExtDO;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmUserTypeEnum;
import com.cmsr.onebase.module.bpm.core.service.BpmEngineDefExtService;
import com.cmsr.onebase.module.bpm.runtime.service.BpmInstanceService;
import com.cmsr.onebase.module.bpm.runtime.vo.*;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.InsertDataReqDTO;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataMethodCoreService;
import com.cmsr.onebase.module.system.api.dept.DeptApi;
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
    private BpmFlowInsBizExtRepository flowInsExtRepository;

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


    @Resource
    private com.cmsr.onebase.module.bpm.runtime.service.exec.strategy.ExecTaskStrategyManager execTaskStrategyManager;

    private BaseNodeExtDTO getNodeExtDTOByNodeCode(String nodeCode, String defJsonStr) {
        if (StringUtils.isBlank(defJsonStr)) {
            return null;
        }

        DefJson defJson = JsonUtils.parseObject(defJsonStr, DefJson.class);
        if (defJson == null) {
            return null;
        }

        return getNodeExtDTOByNodeCode(nodeCode, defJson);
    }

    private BaseNodeExtDTO getNodeExtDTOByNodeCode(String nodeCode, DefJson defJson) {
        if (defJson == null) {
            return null;
        }

        NodeJson currNodeJson = null;

        for (NodeJson nodeJson : defJson.getNodeList()) {
            if (Objects.equals(nodeJson.getNodeCode(), nodeCode)) {
                currNodeJson = nodeJson;
                break;
            }
        }

        if (currNodeJson != null) {
            BaseNodeExtDTO baseNodeExtDTO = JsonUtils.parseObject(currNodeJson.getExt(), BaseNodeExtDTO.class);
            if (baseNodeExtDTO != null) {
                return baseNodeExtDTO;
            }
        }

        return null;
    }

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

        // 先插入数据，todo：这个操作跨库了，待处理回滚问题
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

            // 获取待办任务，必须为发起节点
            if (CollectionUtils.isEmpty(tasks)) {
                throw exception(ErrorCodeConstants.FLOW_TASK_NOT_EXISTS);
            }

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

        // todo: getUser接口暂时拿不到部门信息，先暂时使用getUserList
        // CommonResult<AdminUserRespDTO> result = adminUserApi.getUser(loginUserId);
        CommonResult<List<AdminUserRespDTO>> result = adminUserApi.getUserList(List.of(loginUserId));
        if (!result.isSuccess() || CollectionUtils.isEmpty(result.getData())) {
            throw exception(ErrorCodeConstants.USER_API_CALL_FAILED);
        }

        AdminUserRespDTO userRespDTO = result.getData().get(0);
        String initiatorName = userRespDTO.getNickname();

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

        // 保存扩展信息
        flowInsExtDO.setBusinessId(entityDataId);
        flowInsExtDO.setBpmVersion("V" + def.getVersion());
        flowInsExtDO.setBusinessTitle(businessTitle);
        flowInsExtDO.setInitiatorId(loginUserId);
        flowInsExtDO.setInitiatorAvatar(userRespDTO.getAvatar());
        flowInsExtDO.setInitiatorName(initiatorName);
        flowInsExtDO.setInitiatorDeptId(userRespDTO.getDeptId());
        flowInsExtDO.setInitiatorDeptName(userRespDTO.getDeptName());
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

        // 查找task是否存在
        Task task = taskService.getById(taskId);
        if (task == null) {
            throw exception(ErrorCodeConstants.FLOW_TASK_NOT_EXISTS);
        }

        Instance instance = insService.getById(task.getInstanceId());

        if (instance == null) {
            throw exception(ErrorCodeConstants.FLOW_INSTANCE_NOT_EXISTS);
        }

        String taskNodeCode = task.getNodeCode();
        BaseNodeExtDTO extDTO = getNodeExtDTOByNodeCode(taskNodeCode, instance.getDefJson());

        if (extDTO == null) {
            throw exception(ErrorCodeConstants.FLOW_NODE_NOT_EXISTS);
        }

        // 校验实体ID
        if (reqVO.getEntity() != null) {
            Long entityId = (Long) instance.getVariableMap().get("entityId");

            if (entityId == null) {
                throw exception(ErrorCodeConstants.FLOW_NOT_BIND_ENTITY_ID);
            }

            if (!entityId.equals(reqVO.getEntity().getEntityId())) {
                throw exception(ErrorCodeConstants.INVALID_ENTITY_ID);
            }
        }

        List<User> users = userService.getByAssociateds(List.of(task.getId()),
                BpmUserTypeEnum.APPROVAL.getCode(),
                BpmUserTypeEnum.TRANSFER.getCode(),
                BpmUserTypeEnum.DEPUTE.getCode());
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

        // 执行
        execTaskStrategyManager.execute(task, extDTO, reqVO);
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
            List<User> users = userService.getByAssociateds(List.of(task.getId()),
                    BpmUserTypeEnum.APPROVAL.getCode(),
                    BpmUserTypeEnum.TRANSFER.getCode(),
                    BpmUserTypeEnum.DEPUTE.getCode());

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


            Map<Long, AdminUserRespDTO> userMap = new HashMap<>();

            for (AdminUserRespDTO user : users) {
                userMap.put(user.getId(), user);
            }

            for (BpmOperatorRecordRespVO.OperatorRecord operatorRecord : operatorRecords) {
                if (CollectionUtils.isNotEmpty(operatorRecord.getOperators())) {
                    for (BpmOperatorRecordRespVO.OperatorInfo operatorInfo : operatorRecord.getOperators()) {
                        AdminUserRespDTO user = userMap.get(Long.parseLong(operatorInfo.getOperator()));

                        if (user != null) {
                            operatorInfo.setOperator(user.getNickname());
                            operatorInfo.setAvatar(user.getAvatar());
                        } else {
                            // todo: 用户不存在
                            operatorInfo.setOperator("-");
                        }
                    }
                }
            }

            break;
        } while (false);

        return operatorRecords;
    }

    @Override
    public BpmFlowTaskDetailVO getFormDetail(Long instanceId) {
        BpmFlowTaskDetailVO vo = new BpmFlowTaskDetailVO();
        NodeJson currNodeJson = null;
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        boolean hasPermission = false;

        Instance instance = insService.getById(instanceId);

        if (instance == null) {
            throw exception(ErrorCodeConstants.FLOW_INSTANCE_NOT_EXISTS);
        }

        Long entityId = (Long) instance.getVariableMap().get("entityId");

        if (entityId == null) {
            throw exception(ErrorCodeConstants.FLOW_NOT_BIND_ENTITY_ID);
        }

        // 查询该实例的待办任务, todo: 优化这段的代码写法
        List<Task> tasks = taskService.getByInsId(instanceId);
        Task todoTask = null;

        if (CollectionUtils.isNotEmpty(tasks)) {
            List<Long> taskIds = new ArrayList<>();
            Map<Long, Task> taskMap = new HashMap<>();

            for (Task task : tasks) {
              taskIds.add(task.getId());
              taskMap.put(task.getId(), task);
            }

            List<User> users = userService.getByAssociateds(taskIds,
                    BpmUserTypeEnum.APPROVAL.getCode(),
                    BpmUserTypeEnum.TRANSFER.getCode(),
                    BpmUserTypeEnum.DEPUTE.getCode());

            if (CollectionUtils.isNotEmpty(users)) {
                for (User user : users) {
                    if (Objects.equals(user.getProcessedBy(), String.valueOf(loginUserId))) {
                        hasPermission = true;
                        todoTask = taskMap.get(user.getAssociated());
                        break;
                    }
                }
            }
        }

        // 按钮显示
        if (hasPermission && todoTask != null) {
            String nodeCode = todoTask.getNodeCode();
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

            vo.setTaskId(todoTask.getId());
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
        }

        String entityDataId = instance.getBusinessId();

        //查询form信息
        Map<String, Object> data = metadataDataMethodCoreService.getData(entityId, entityDataId,null);
        if (data != null && !data.isEmpty()){
            vo.setFormData(data);
        }

        vo.setInstanceId(instanceId);

        return vo;
    }

    @Override
    public List<BpmPredictRespVO.NodeInfo> flowPredict(BpmPredictReqVO reqVO) {
        Long businessId = reqVO.getBusinessId();
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        List<BpmPredictRespVO.NodeInfo> nodes = new ArrayList<>();

        Definition definition = defExtService.getByFormPathAndStatus(String.valueOf(businessId), PublishStatus.PUBLISHED.getKey());
        if (definition == null) {
            log.error(ErrorCodeConstants.PUBLISHED_FLOW_NOT_EXISTS.getMsg());
            throw exception(ErrorCodeConstants.PUBLISHED_FLOW_NOT_EXISTS);
        }

        Node startNode = nodeService.getStartNode(definition.getId());

        if (startNode == null) {
            log.error("流程定义缺少开始节点");
            throw exception(ErrorCodeConstants.FLOW_NODE_NOT_EXISTS.getCode(), "获取开始节点失败");
        }

        Node currentNode = startNode;
        Set<Long> allUserIds = new HashSet<>();

        while (true) {
            // todo：理论上只会有一条路径，需要结合实体信息预测下一步走向，主要是涉及到条件分支的
            Node nextNode = nodeService.getNextNode(definition.getId(), currentNode.getNodeCode(), null, SkipType.PASS.getKey());

            if (nextNode == null) {
                // 找不到下一个节点，结束，todo 是否应该抛出异常
                log.warn("没找到下一个节点");
                break;
            }

            if (NodeType.isEnd(nextNode.getNodeType())) {
                break;
            }

            currentNode = nextNode;

            // 设置预测节点信息
            BpmPredictRespVO.NodeInfo nodeInfo = new BpmPredictRespVO.NodeInfo();
            nodeInfo.setHandlers(new ArrayList<>());
            nodeInfo.setNodeCode(nextNode.getNodeCode());
            nodeInfo.setNodeName(nextNode.getNodeName());

            // 设置节点信息
            BaseNodeExtDTO nodeExtDTO = JsonUtils.parseObject(nextNode.getExt(), BaseNodeExtDTO.class);

            // 获取业务节点类型
            String bizNodeType = nodeExtDTO.getNodeType();

            if (Objects.equals(bizNodeType, BpmNodeTypeEnum.INITIATION.getCode())) {
                // 发起节点的处理人是当前登录用户
                BpmPredictRespVO.HandlerInfo handlerInfo = new BpmPredictRespVO.HandlerInfo();
                handlerInfo.setHandlerId(loginUserId);
                nodeInfo.getHandlers().add(handlerInfo);
                allUserIds.add(loginUserId);
            } else if (Objects.equals(bizNodeType, BpmNodeTypeEnum.APPROVER.getCode())) {
                // 解析权限标志
                NodePermFlagDTO permFlagDTO = JsonUtils.parseObject(currentNode.getPermissionFlag(), NodePermFlagDTO.class);

                // 用户ID去重
                Set<Long> approverUserIds = new HashSet<>();

                if (CollectionUtils.isNotEmpty(permFlagDTO.getUserIds())) {
                    // 处理用户列表
                    approverUserIds.addAll(permFlagDTO.getUserIds());
                } else if (CollectionUtils.isNotEmpty(permFlagDTO.getRoleIds())) {
                    // 处理角色列表
                    CommonResult<Set<Long>> result = permissionApi.getUserRoleIdListByRoleIds(permFlagDTO.getRoleIds());

                    if (result.isSuccess()) {
                        approverUserIds.addAll(result.getData());
                    }
                } else {
                    // todo: 支持更多类型的权限
                }

                if (!approverUserIds.isEmpty()) {
                    allUserIds.addAll(approverUserIds);

                    for (Long userId : approverUserIds) {
                        BpmPredictRespVO.HandlerInfo handlerInfo = new BpmPredictRespVO.HandlerInfo();
                        handlerInfo.setHandlerId(userId);
                        nodeInfo.getHandlers().add(handlerInfo);
                    }
                }
            } else {
                // todo: 支持更多类型的节点
                log.warn("未知节点类型，bizNodeType: {}", bizNodeType);
                continue;
            }

            nodes.add(nodeInfo);
        }

        // 获取用户的名称和头像
        CommonResult<List<AdminUserRespDTO>> userResult = adminUserApi.getUserList(allUserIds);

        if (userResult.isSuccess()) {
            // 构建用户ID到用户信息的映射
            Map<Long, AdminUserRespDTO> userMap = userResult.getData().stream()
                    .collect(Collectors.toMap(AdminUserRespDTO::getId, v -> v));

            for (BpmPredictRespVO.NodeInfo node : nodes) {
                for (BpmPredictRespVO.HandlerInfo handler : node.getHandlers()) {
                    AdminUserRespDTO user = userMap.get(handler.getHandlerId());
                    if (user != null) {
                        handler.setHandlerName(user.getNickname());
                        handler.setUserAvatar(user.getAvatar());
                    } else {
                        // todo：处理用户不存在的情况，名称先设置成 "-"
                        handler.setHandlerName("-");
                        log.warn("用户不存在，userId: {}", handler.getHandlerId());
                    }
                }
            }
        } else {
            log.warn("获取用户信息失败，userIds: {}", allUserIds);
            throw exception(ErrorCodeConstants.USER_API_CALL_FAILED);
        }

        return nodes;
    }
}
