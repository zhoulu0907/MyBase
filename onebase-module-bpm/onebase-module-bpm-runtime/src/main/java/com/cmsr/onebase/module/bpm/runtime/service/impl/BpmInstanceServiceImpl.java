package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.app.api.auth.AppAuthRoleUser;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowInsBizExtRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowInsBizExtDO;
import com.cmsr.onebase.module.bpm.core.dto.BpmDefinitionExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.BpmGlobalConfigDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.ApproverNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.NodePermFlagDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.*;
import com.cmsr.onebase.module.bpm.core.service.BpmEngineDefExtService;
import com.cmsr.onebase.module.bpm.core.vo.UserBasicInfoVO;
import com.cmsr.onebase.module.bpm.core.vo.design.BpmDefJsonVO;
import com.cmsr.onebase.module.bpm.core.vo.design.node.base.BaseEdgeVO;
import com.cmsr.onebase.module.bpm.runtime.service.BpmInstanceService;
import com.cmsr.onebase.module.bpm.runtime.service.detail.strategy.InstanceDetailStrategyManager;
import com.cmsr.onebase.module.bpm.runtime.service.exec.strategy.ExecTaskStrategyManager;
import com.cmsr.onebase.module.bpm.runtime.vo.*;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.InsertDataReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataMethodCoreService;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.dto.NodeJson;
import org.dromara.warm.flow.core.dto.SkipJson;
import org.dromara.warm.flow.core.entity.*;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.service.*;
import org.dromara.warm.flow.core.service.impl.BpmConstants;
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
    private AdminUserApi adminUserApi;

    @Resource
    private AppAuthRoleUser appAuthRoleUser;

    @Resource
    private NodeService nodeService;

    @Resource
    private MetadataDataMethodCoreService metadataDataMethodCoreService;

    @Resource
    private ExecTaskStrategyManager execTaskStrategyManager;

    @Resource
    private InstanceDetailStrategyManager instanceDetailStrategyManager;

    @Resource
    protected MetadataEntityFieldApi metadataEntityFieldApi;

    /**
     * 根据节点编码获取节点扩展DTO
     *
     * @param nodeCode 节点编码
     * @param defJsonStr 流程定义JSON字符串
     * @return 节点扩展DTO，如果不存在则返回null
     */
    private BaseNodeExtDTO getNodeExtDTOByNodeCode(String nodeCode, String defJsonStr) {
        if (StringUtils.isBlank(defJsonStr)) {
            return null;
        }

        DefJson defJson = JsonUtils.parseObject(defJsonStr, DefJson.class);
        return getNodeExtDTOByNodeCode(nodeCode, defJson);
    }

    /**
     * 根据节点编码获取节点扩展DTO
     *
     * @param nodeCode 节点编码
     * @param defJson 流程定义JSON对象
     * @return 节点扩展DTO，如果不存在则返回null
     */
    private BaseNodeExtDTO getNodeExtDTOByNodeCode(String nodeCode, DefJson defJson) {
        if (defJson == null || defJson.getNodeList() == null) {
            return null;
        }

        NodeJson currNodeJson = null;

        for (NodeJson nodeJson : defJson.getNodeList()) {
            if (Objects.equals(nodeJson.getNodeCode(), nodeCode)) {
                currNodeJson = nodeJson;
                break;
            }
        }

        if (currNodeJson == null) {
            return null;
        }

        return JsonUtils.parseObject(currNodeJson.getExt(), BaseNodeExtDTO.class);
    }

    private String buildFormSummary(EntityVO entityVO, BpmDefinitionExtDTO defExtDTO) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        Long entityId = entityVO.getEntityId();

        // 拿到所有的实体字段
        EntityFieldQueryReqDTO queryReqDTO = new EntityFieldQueryReqDTO();
        queryReqDTO.setEntityId(entityId);
        List<EntityFieldRespDTO> fieldList = metadataEntityFieldApi.getEntityFieldList(queryReqDTO);

        Map<Long, EntityFieldRespDTO> fieldMap = new HashMap<>();

        for (EntityFieldRespDTO fieldDto : fieldList) {
            fieldMap.put(fieldDto.getId(), fieldDto);
        }

        BpmGlobalConfigDTO.FormSummaryConfig formSummaryCfg = null;

        if (defExtDTO.getGlobalConfig() != null) {
            formSummaryCfg = defExtDTO.getGlobalConfig().getFormSummaryCfg();
        }

        Set<Long> formSummaryFieldIds = new HashSet<>();
        if (formSummaryCfg != null && CollectionUtils.isNotEmpty(formSummaryCfg.getFieldConfigs())) {
            for (BpmGlobalConfigDTO.FieldConfigDTO fieldConfig : formSummaryCfg.getFieldConfigs()) {
                formSummaryFieldIds.add(fieldConfig.getFieldId());
            }
        } else {
            formSummaryFieldIds = entityVO.getData().keySet();
        }

        for (Long id : formSummaryFieldIds) {
            EntityFieldRespDTO fieldDto = fieldMap.get(id);
            Object fieldValue = entityVO.getData().get(id);

            if (fieldDto == null || fieldValue == null) {
                continue;
            }

            // 超过3个字段，只取前3个
            if (count >= 3) {
                break;
            }

            sb.append(fieldDto.getDisplayName()).append(":").append(fieldValue).append(" ");

            count++;
        }

        return sb.toString();
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

        // 和更新数据公用了字段，需要手动校验
        if (MapUtils.isEmpty(reqVO.getEntity().getData())) {
            throw exception(ErrorCodeConstants.FLOW_ENTITY_DATA_NOT_EXISTS.getCode(), "实体数据内容不能为空");
        }

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

        String bpmTitle = String.format("%s发起的%s", initiatorName, reqVO.getFormName());

        // 构建表单摘要
        String formSummary = buildFormSummary(entityVO, extDto);

        if (StringUtils.isBlank(formSummary)) {
            formSummary = reqVO.getFormName();
        }

        // 保存扩展信息
        flowInsExtDO.setBusinessDataId(entityDataId);
        flowInsExtDO.setBindingViewId(def.getFormPath());
        flowInsExtDO.setBpmVersion("V" + def.getVersion());
        flowInsExtDO.setBpmTitle(bpmTitle);
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

        if (instance.getBusinessId() == null) {
            throw exception(ErrorCodeConstants.FLOW_NOT_BIND_ENTITY_ID);
        }

        Long entityDataId = Long.parseLong(instance.getBusinessId());

        // 忽略前端传的entityDataId，使用流程实例绑定的实体数据ID
        if (reqVO.getEntity() != null) {
            reqVO.getEntity().setId(entityDataId);
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
        Long loginUserId = WebFrameworkUtils.getLoginUserId();

        // 查询流程实例
        Instance instance = insService.getById(instanceId);
        if (instance == null) {
            throw exception(ErrorCodeConstants.FLOW_INSTANCE_NOT_EXISTS);
        }

        // 设置流程状态
        vo.setCurrentStatus(instance.getFlowStatus());
        vo.setInstanceId(instanceId);

        // 获取实体ID
        Long entityId = (Long) instance.getVariableMap().get("entityId");
        if (entityId == null) {
            throw exception(ErrorCodeConstants.FLOW_NOT_BIND_ENTITY_ID);
        }

        // 填充业务扩展信息（与节点类型无关的通用逻辑）
        fillBpmBizExt(vo, instanceId);

        // 填充表单数据（与节点类型无关的通用逻辑）
        fillFormData(vo, instance, entityId);

        // 获取节点配置
        String nodeCode = instance.getNodeCode();
        BaseNodeExtDTO nodeExtDTO = getNodeExtDTOByNodeCode(nodeCode, instance.getDefJson());

        // 使用策略处理节点类型相关的逻辑（按钮配置、字段权限配置）
        if (nodeExtDTO != null) {
            instanceDetailStrategyManager.processInstanceDetail(vo, nodeExtDTO, instance, loginUserId);
        } else {
            log.warn("未找到节点配置，nodeCode: {}", nodeCode);
        }

        return vo;
    }

    /**
     * 填充业务扩展信息
     *
     * @param vo 详情VO
     * @param instanceId 流程实例ID
     */
    private void fillBpmBizExt(BpmFlowTaskDetailVO vo, Long instanceId) {
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
    private void fillFormData(BpmFlowTaskDetailVO vo, Instance instance, Long entityId) {
        String entityDataId = instance.getBusinessId();
        if (entityDataId == null) {
            throw exception(ErrorCodeConstants.FLOW_ENTITY_DATA_ID_NOT_EXISTS);
        }

        Map<String, Object> data = metadataDataMethodCoreService.getData(entityId, entityDataId, null);
        if (data != null && !data.isEmpty()) {
            vo.setFormData(data);
        }
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
                    List<Long> userIds = appAuthRoleUser.findUserIdsByRoleIds(permFlagDTO.getRoleIds());

                    if (CollectionUtils.isNotEmpty(userIds)) {
                        approverUserIds.addAll(userIds);
                    }
                } else {
                    // todo: 支持更多类型的权限
                }

                if (!approverUserIds.isEmpty()) {
                    // 限制最多100个用户
                    approverUserIds = new HashSet<>(approverUserIds.stream().limit( BpmConstants.MAX_NODE_APPROVER_USERS).toList());
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

    @Override
    public BpmPreviewRespVO flowPreview(BpmPreviewReqVO reqVO) {
        Long instanceId = reqVO.getInstanceId();
        Long businessId = reqVO.getBusinessId();

        BpmPreviewRespVO respVO = new BpmPreviewRespVO();

        // 抛参数异常
        if (instanceId == null && businessId == null) {
            throw new IllegalArgumentException("业务ID和流程实例ID不能同时为空");
        }

        DefJson defJson;

        // 优先处理流程实例ID
        if (instanceId != null) {
            // 获取流程实例
            Instance instance = insService.getById(reqVO.getInstanceId());

            if (instance == null) {
                throw exception(ErrorCodeConstants.FLOW_INSTANCE_NOT_EXISTS);
            }

            respVO.setInstanceId(instance.getId());

            String defJsonStr = instance.getDefJson();

            // 解析流程定义JSON
            defJson = FlowEngine.jsonConvert.strToBean(defJsonStr, DefJson.class);
        } else {
            // 查询已发布的流程定义
            Definition definition = defExtService.getByFormPathAndStatus(String.valueOf(businessId), PublishStatus.PUBLISHED.getKey());
            if (definition == null) {
                log.error(ErrorCodeConstants.PUBLISHED_FLOW_NOT_EXISTS.getMsg());
                throw exception(ErrorCodeConstants.PUBLISHED_FLOW_NOT_EXISTS);
            }

            defJson = defService.queryDesign(definition.getId());
        }

        BpmDefJsonVO bpmDefJsonVO = new BpmDefJsonVO();

        for (NodeJson nodeJson : defJson.getNodeList()) {
            String ext = nodeJson.getExt();
            if (ext == null) {
                continue;
            }

            BaseNodeExtDTO extDTO = JsonUtils.parseObject(ext, BaseNodeExtDTO.class);

            // 节点扩展信息/配置 - 现在返回的是BaseNodeVO
            CommonNodeVO nodeVO = new CommonNodeVO();

            // 设置节点信息
            nodeVO.setId(nodeJson.getNodeCode());
            nodeVO.setName(nodeJson.getNodeName());
            nodeVO.setType(extDTO.getNodeType());
            nodeVO.setMeta(extDTO.getMeta());
            nodeVO.setData(new CommonNodeVO.DataVO());

            // 设置状态
            BpmEleRunStatusEnum eleRunStatus = BpmEleRunStatusEnum.chartStatusToEleRunStatus(nodeJson.getStatus());

            if (eleRunStatus == null) {
                nodeVO.getData().setRunStatus(BpmEleRunStatusEnum.PENDING.getCode());
            } else {
                nodeVO.getData().setRunStatus(eleRunStatus.getCode());
            }

            if (bpmDefJsonVO.getNodes() == null) {
                bpmDefJsonVO.setNodes(new ArrayList<>());
            }

            bpmDefJsonVO.getNodes().add(nodeVO);
        }

        for (NodeJson nodeJson : defJson.getNodeList()) {
            for (SkipJson skipJson : nodeJson.getSkipList()) {
                BaseEdgeVO edgeVO = new BaseEdgeVO();
                edgeVO.setSourceNodeId(skipJson.getNowNodeCode());
                edgeVO.setTargetNodeId(skipJson.getNextNodeCode());
                edgeVO.setName(skipJson.getSkipName());
                edgeVO.setType(skipJson.getSkipType());
                edgeVO.setSkipCondition(skipJson.getSkipCondition());

                // 设置状态
                BpmEleRunStatusEnum eleRunStatus = BpmEleRunStatusEnum.chartStatusToEleRunStatus(skipJson.getStatus());

                if (eleRunStatus == null) {
                    edgeVO.setRunStatus(BpmEleRunStatusEnum.PENDING.getCode());
                } else {
                    edgeVO.setRunStatus(eleRunStatus.getCode());
                }

                // 添加边视图到列表
                if (bpmDefJsonVO.getEdges() == null) {
                    bpmDefJsonVO.setEdges(new ArrayList<>());
                }

                bpmDefJsonVO.getEdges().add(edgeVO);
            }
        }

        respVO.setBusinessId(Long.valueOf(defJson.getFormPath()));
        respVO.setFlowName(defJson.getFlowName());
        respVO.setVersion("V" + defJson.getVersion());
        respVO.setBpmDefJson(JsonUtils.toJsonString(bpmDefJsonVO));

        return respVO;
    }
}
