package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.app.api.appresource.AppResourceApi;
import com.cmsr.onebase.module.app.api.appresource.dto.AppMenuRespDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.AppPagesetRespDTO;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowInsBizExtRepository;
import com.cmsr.onebase.module.bpm.core.dal.database.ext.BpmFlowDefinitionRepositoryExt;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowInsBizExtDO;
import com.cmsr.onebase.module.bpm.core.dal.mapper.BpmInstanceMapper;
import com.cmsr.onebase.module.bpm.core.dto.BpmDefinitionExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.BpmGlobalConfigDTO;
import com.cmsr.onebase.module.bpm.core.dto.PageViewGroupDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.*;
import com.cmsr.onebase.module.bpm.core.validator.BpmAppResourceValidator;
import com.cmsr.onebase.module.bpm.core.vo.BpmFormDataPageReqVO;
import com.cmsr.onebase.module.bpm.core.vo.design.BpmDefJsonVO;
import com.cmsr.onebase.module.bpm.core.vo.design.edge.base.BaseEdgeVO;
import com.cmsr.onebase.module.bpm.runtime.helper.BpmEntityHelper;
import com.cmsr.onebase.module.bpm.runtime.service.BpmInstanceService;
import com.cmsr.onebase.module.bpm.runtime.service.instance.detail.BpmDetailService;
import com.cmsr.onebase.module.bpm.runtime.service.instance.exec.impl.BpmExecServiceImpl;
import com.cmsr.onebase.module.bpm.runtime.service.instance.operator.BpmOperatorRecordService;
import com.cmsr.onebase.module.bpm.runtime.service.instance.predict.BpmPredictService;
import com.cmsr.onebase.module.bpm.runtime.utils.PageViewUtil;
import com.cmsr.onebase.module.bpm.runtime.vo.*;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowInstance;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowInstanceRepository;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.*;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticConditionNodeTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticOperatorEnum;
import com.cmsr.onebase.module.metadata.core.semantic.type.BpmSystemFieldEnum;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticPageConditionVO;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.dto.NodeJson;
import org.dromara.warm.flow.core.dto.SkipJson;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.service.InsService;
import org.dromara.warm.flow.core.service.TaskService;
import org.jetbrains.annotations.NotNull;
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
    @Resource
    private BpmFlowDefinitionRepositoryExt defExtService;

    @Resource(name = "bpmDefService")
    private DefService defService;

    @Resource(name = "bpmInsService")
    private InsService insService;

    @Resource(name = "bpmTaskService")
    private TaskService taskService;

    @Resource
    private FlowInstanceRepository flowInstanceRepository;

    @Resource
    private BpmFlowInsBizExtRepository flowInsExtRepository;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private PageViewUtil pageViewUtil;

    @Resource
    private BpmDetailService detailService;

    @Resource
    private BpmOperatorRecordService operatorRecordService;

    @Resource
    private BpmPredictService predictService;

    @Resource
    private BpmExecServiceImpl bpmExecService;

    @Resource
    private AppResourceApi appResourceApi;

    @Resource
    private BpmAppResourceValidator bpmAppResourceValidator;

    @Resource
    private SemanticDynamicDataApi semanticDynamicDataApi;

    @Resource
    private BpmEntityHelper bpmEntityHelper;

    @Resource
    private BpmInstanceMapper  bpmInstanceMapper;

    private String buildFormSummary(EntityVO entityVO, BpmDefinitionExtDTO defExtDTO, SemanticEntitySchemaDTO entitySchemaDTO) {
        StringBuilder sb = new StringBuilder();
        int count = 0;

        Map<String, SemanticFieldSchemaDTO> mainTableFieldMap = new HashMap<>();

        for (SemanticFieldSchemaDTO field : entitySchemaDTO.getFields()) {
            mainTableFieldMap.put(field.getFieldName(), field);
        }

        // 拿到所有的实体字段
        Map<String, Set<String>> nonSystemFields = bpmEntityHelper.getNonSystemFields(entitySchemaDTO);

        // 拿主表的字段名
        Set<String> mainTableFieldNames = nonSystemFields.get(entitySchemaDTO.getTableName());

        BpmGlobalConfigDTO.FormSummaryConfig formSummaryCfg = null;

        if (defExtDTO.getGlobalConfig() != null) {
            formSummaryCfg = defExtDTO.getGlobalConfig().getFormSummaryCfg();
        }

        Set<String> formSummaryFieldNames = new HashSet<>();

        if (formSummaryCfg != null && CollectionUtils.isNotEmpty(formSummaryCfg.getFieldConfigs())) {
            for (BpmGlobalConfigDTO.FieldConfigDTO fieldConfig : formSummaryCfg.getFieldConfigs()) {
                // 只取主表的字段，todo：待完善多表的情况
                if (!Objects.equals(fieldConfig.getTableName(), entitySchemaDTO.getTableName())) {
                    continue;
                }

                formSummaryFieldNames.add(fieldConfig.getFieldName());
            }
        }

        if (CollectionUtils.isEmpty(formSummaryFieldNames)) {
            entityVO.getData().keySet().forEach(fieldName -> {
                if (mainTableFieldNames.contains(fieldName)) {
                    formSummaryFieldNames.add(fieldName);
                }
            });
        }

        for (String name : formSummaryFieldNames) {
            SemanticFieldSchemaDTO fieldDto = mainTableFieldMap.get(name);

            if (fieldDto == null) {
                continue;
            }

            String displayName = fieldDto.getDisplayName();
            String fieldName = fieldDto.getFieldName();
            Object fieldValue = entityVO.getData().get(fieldName);

            if (fieldValue == null) {
                continue;
            }

            // 超过3个字段，只取前3个
            if (count >= 3) {
                break;
            }

            // 处理复杂组件类型，转换为SemanticFieldValueDTO获取实际存储值
            SemanticFieldTypeEnum fieldType = bpmEntityHelper.findFieldType(entitySchemaDTO, entitySchemaDTO.getTableName(), fieldName);
            if (fieldType != null) {
                SemanticFieldValueDTO<Object> semanticFieldValue = SemanticFieldValueDTO.ofType(fieldType);
                semanticFieldValue.setRawValue(fieldValue);
                Object storeValue = semanticFieldValue.getStoreValue();
                if (storeValue != null) {
                    fieldValue = storeValue;
                }
            }

            sb.append(displayName).append(":").append(fieldValue).append(" ");

            count++;
        }

        return sb.toString();
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public BpmSubmitRespVO submit(BpmSubmitReqVO reqVO) {
        BpmSubmitRespVO respVO = new BpmSubmitRespVO();
        String entityDataId = null;
        Long applicationId = ApplicationManager.getApplicationId();

        if (applicationId == null) {
            throw exception(ErrorCodeConstants.MISSING_APPLICATION_ID);
        }

        // 和更新数据公用了字段，需要手动校验
        if (MapUtils.isEmpty(reqVO.getEntity().getData())) {
            throw exception(ErrorCodeConstants.FLOW_ENTITY_DATA_NOT_EXISTS.getCode(), "实体数据内容不能为空");
        }

        String businessUuid = reqVO.getBusinessUuid();

        // 校验菜单
        AppMenuRespDTO appMenuRespDTO = appResourceApi.getAppMenuByUuidAndAppId(businessUuid, applicationId);
        bpmAppResourceValidator.validateMenu(appMenuRespDTO, applicationId);

        // 校验页面集
        AppPagesetRespDTO appPagesetRespDTO = appResourceApi.getPageSetByMenuUuidAndAppId(businessUuid, applicationId);
        bpmAppResourceValidator.validatePageset(appPagesetRespDTO, applicationId);

        String tableName = reqVO.getEntity().getTableName();

        // 校验tableName
        SemanticEntitySchemaDTO entitySchemaDTO = semanticDynamicDataApi.buildEntitySchemaByTableName(tableName);

        if (!Objects.equals(entitySchemaDTO.getEntityUuid(), appMenuRespDTO.getEntityUuid())) {
            throw exception(ErrorCodeConstants.INVALID_ENTITY_TABLE_NAME);
        }

        Definition def = defExtService.getByFormPathAndStatus(businessUuid, PublishStatus.PUBLISHED.getKey());
        if (def == null) {
            throw exception(ErrorCodeConstants.PUBLISHED_FLOW_NOT_EXISTS);
        }

        // 详情视图页面和编辑视图页面
        PageViewGroupDTO pageViewGroupDTO = pageViewUtil.findPageViewGroupByPageSetUuid(appPagesetRespDTO.getPageSetUuid());

        if (pageViewGroupDTO == null) {
            throw exception(ErrorCodeConstants.MISSING_EDIT_OR_DETAIL_PAGE_VIEW);
        }

        // 业务状态
        BpmBusinessStatusEnum businessStatus = BpmBusinessStatusEnum.IN_APPROVAL;

        if (reqVO.isDraft()) {
            businessStatus = BpmBusinessStatusEnum.DRAFT;
        }

        EntityVO entityVO = reqVO.getEntity();

        // 先插入数据，todo：这个操作跨库了，待处理回滚问题
        entityDataId = bpmEntityHelper.insertEntityData(entityVO);

        BpmFlowInsBizExtDO flowInsExtDO = new BpmFlowInsBizExtDO();
        Map<String, Object> variables = new HashMap<>();

        // 传应用ID和实体ID
        BpmDefinitionExtDTO extDto = JsonUtils.parseObject(def.getExt(), BpmDefinitionExtDTO.class);
        variables.put(BpmConstants.VAR_ENTITY_TABLE_NAME_KEY, tableName);
        variables.put(BpmConstants.VAR_PAGE_VIEW_GROUP_KEY, JsonUtils.toJsonString(pageViewGroupDTO));

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
                    .message(BpmNodeApproveStatusEnum.POST_SUBMITTED.getName())
                    .flowStatus(businessStatus.getCode())
                    .hisStatus(BpmNodeApproveStatusEnum.POST_SUBMITTED.getCode());
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
        String formSummary = buildFormSummary(entityVO, extDto, entitySchemaDTO);

        if (StringUtils.isBlank(formSummary)) {
            formSummary = reqVO.getFormName();
        }

        // 保存扩展信息
        flowInsExtDO.setBusinessDataId(entityDataId);
        flowInsExtDO.setBindingViewId(def.getFormPath());
        flowInsExtDO.setBpmVersion("V" + def.getVersion());
        flowInsExtDO.setBpmTitle(bpmTitle);
        flowInsExtDO.setInitiatorId(String.valueOf(loginUserId));
        flowInsExtDO.setInitiatorAvatar(userRespDTO.getAvatar());
        flowInsExtDO.setInitiatorName(initiatorName);
        flowInsExtDO.setInitiatorDeptId(userRespDTO.getDeptId());
        flowInsExtDO.setInitiatorDeptName(userRespDTO.getDeptName());
        flowInsExtDO.setFormName(reqVO.getFormName());
        flowInsExtDO.setFormSummary(formSummary);
        flowInsExtDO.setInstanceId(instance.getId());
        flowInsExtDO.setApplicationId(applicationId);

        flowInsExtRepository.save(flowInsExtDO);

        respVO.setInstanceId(instance.getId());
        respVO.setEntityDataId(entityDataId);

        return respVO;
    }

    @Override
    public void execTask(ExecTaskReqVO reqVO) {
        bpmExecService.execTask(reqVO);
    }

    @Override
    public List<BpmOperatorRecordRespVO.OperatorRecord> getOperatorRecord(Long instanceId) {
        return operatorRecordService.getOperatorRecord(instanceId);
    }

    @Override
    public BpmTaskDetailRespVO getFormDetail(BpmTaskDetailReqVO reqVO) {
        return detailService.getFormDetail(reqVO);
    }

    @Override
    public List<BpmPredictRespVO.NodeInfo> flowPredict(BpmPredictReqVO reqVO) {
        return predictService.flowPredict(reqVO);
    }

    @Override
    public BpmPreviewRespVO flowPreview(BpmPreviewReqVO reqVO) {
        Long instanceId = reqVO.getInstanceId();
        String businessUuid = reqVO.getBusinessUuid();

        Long applicationId = ApplicationManager.getApplicationId();
        if (applicationId == null) {
            throw exception(ErrorCodeConstants.MISSING_APPLICATION_ID);
        }

        BpmPreviewRespVO respVO = new BpmPreviewRespVO();

        // 抛参数异常
        if (instanceId == null && businessUuid == null) {
            throw new IllegalArgumentException("业务UUID和流程实例ID不能同时为空");
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
            // 校验菜单
            AppMenuRespDTO appMenuRespDTO = appResourceApi.getAppMenuByUuidAndAppId(businessUuid, applicationId);
            bpmAppResourceValidator.validateMenuAndPageset(appMenuRespDTO, applicationId);

            String menuUuid = appMenuRespDTO.getMenuUuid();

            // 查询已发布的流程定义
            Definition definition = defExtService.getByFormPathAndStatus(menuUuid, PublishStatus.PUBLISHED.getKey());
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

        respVO.setBusinessUuid(defJson.getFormPath());
        respVO.setFlowName(defJson.getFlowName());
        respVO.setBpmVersion("V" + defJson.getVersion());
        respVO.setBpmDefJson(JsonUtils.toJsonString(bpmDefJsonVO));

        return respVO;
    }
   /**
     * 获取流程表单数据
     *
     * @param reqVO 获取流程表单数据请求
     * @return 流程表单数据
     */
    @Override
    public PageResult<Map<String, Object>> formDataPage(BpmFormDataPageReqVO reqVO) {
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        PageResult<Map<String, Object>> response = PageResult.empty();

        // menuId转menuUuid
        AppMenuRespDTO appMenuRespDTO = appResourceApi.getAppMenuById(reqVO.getMenuId());
        bpmAppResourceValidator.validateMenu(appMenuRespDTO, appMenuRespDTO.getApplicationId());

        String menuUuid = appMenuRespDTO.getMenuUuid();

        // todo 增加流程本身的条件筛选，数据量如果太大，可能会导致性能问题，待优化，先限制2000条
        QueryWrapper instanceQuery = QueryWrapper.create();
        instanceQuery.eq(FlowInstance::getFormPath, menuUuid);
        instanceQuery.limit(2000);
        instanceQuery.orderBy(FlowInstance::getCreateTime, false);

        List<FlowInstance> instances = flowInstanceRepository.list(instanceQuery);
        Map<Long, Long> entityDataIdInstanceIdMap = new HashMap<>();

        if (CollectionUtils.isEmpty(instances)) {
            return response;
        }

        Set<Long> entityDataIds = new HashSet<>();

        for (Instance instance : instances) {
            Long entityDataId = Long.valueOf(instance.getBusinessId());
            entityDataIds.add(entityDataId);
            entityDataIdInstanceIdMap.put(entityDataId, instance.getId());
        }

        // 去查询实体的数据
        // 1. 创建 SemanticPageConditionVO 对象
        SemanticPageConditionVO conditionVO = new SemanticPageConditionVO();
        conditionVO.setTableName(reqVO.getTableName());
        conditionVO.setPageNo(reqVO.getPageNo());
        conditionVO.setPageSize(reqVO.getPageSize());

        // 2. 构造查询条件 - 通过 entityId 集合查询
        SemanticConditionDTO idCondition = new SemanticConditionDTO();
        idCondition.setFieldName("id");
        idCondition.setOperator(SemanticOperatorEnum.EXISTS_IN);
        idCondition.setFieldValue(Arrays.asList(entityDataIds.toArray()));
        idCondition.setNodeType(SemanticConditionNodeTypeEnum.CONDITION);

        conditionVO.setSortBy(reqVO.getSortBy());

        if (reqVO.getFilters() != null) {
            if (reqVO.getFilters().getChildren() == null) {
                reqVO.getFilters().setChildren(new ArrayList<>());
            }

            reqVO.getFilters().getChildren().add(idCondition);
            conditionVO.setSemanticConditionDTO(reqVO.getFilters());
        } else {
            conditionVO.setSemanticConditionDTO(idCondition);
        }

        // 3. 调用 getDataByCondition 方法， todo 增加menuId的权限限制
        PageResult<SemanticEntityValueDTO> entityPageResult = semanticDynamicDataApi.getDataByCondition(conditionVO);
        Set<Long> instanceIds = new HashSet<>();

        response.setTotal(entityPageResult.getTotal());

        if (CollectionUtils.isEmpty(entityPageResult.getList())) {
            return response;
        }

        for (SemanticEntityValueDTO entityValueDTO : entityPageResult.getList()) {
             Long instanceId = entityDataIdInstanceIdMap.get(entityValueDTO.getId());

             if (instanceId == null) {
                 log.warn("未匹配到对应的实例ID");
                 continue;
             }

            instanceIds.add(instanceId);
        }

        // 查询流程数据，todo 使用关联查询

        List<FlowInstance> instanceResults =  flowInstanceRepository.listByIds(instanceIds);

        QueryWrapper instanceExtResultQuery = QueryWrapper.create();
        instanceExtResultQuery.in(BpmFlowInsBizExtDO::getInstanceId, instanceIds);
        List<BpmFlowInsBizExtDO> instanceExtResults = flowInsExtRepository.list(instanceExtResultQuery);

        Map<Long, FlowInstance> instanceResultMap = new HashMap<>();
        Map<Long, BpmFlowInsBizExtDO> instanceExtResultMap = new HashMap<>();
        Set<Long> userIds = new HashSet<>();

        for (FlowInstance instanceResult : instanceResults) {
            Long instanceId = instanceResult.getId();
            Long entityDataId = Long.valueOf(instanceResult.getBusinessId());

            instanceResultMap.put(entityDataId, instanceResult);

            for (BpmFlowInsBizExtDO instanceExtResult : instanceExtResults) {
                Long extInstanceId = instanceExtResult.getInstanceId();
                if (Objects.equals(extInstanceId, instanceId)) {
                    instanceExtResultMap.put(entityDataId, instanceExtResult);

                    if (instanceExtResult.getInitiatorId() != null) {
                        userIds.add(Long.valueOf(instanceExtResult.getInitiatorId()));
                    }

                    break;
                }
            }
        }

        // 查出所有的用户信息
        Map<Long, AdminUserRespDTO> userMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userIds)) {
            CommonResult<List<AdminUserRespDTO>> userResult = adminUserApi.getUserList(userIds);

            if (userResult.isSuccess() && CollectionUtils.isNotEmpty(userResult.getData())) {
                userMap = userResult.getData().stream().collect(Collectors.toMap(AdminUserRespDTO::getId, v -> v));
            }
        }

        for (SemanticEntityValueDTO entityValueDTO : entityPageResult.getList()) {
            Long entityValueId = (Long) entityValueDTO.getId();

            FlowInstance matchedInstance = instanceResultMap.get(entityValueId);
            BpmFlowInsBizExtDO matchedInstanceExt = instanceExtResultMap.get(entityValueId);

            if (matchedInstance == null && matchedInstanceExt == null) {
                log.warn("无匹配的流程实例信息 entityDataId = {}", entityValueId);
                continue;
            }

            //  填充BPM相关的值
            for (BpmSystemFieldEnum bpmFieldEnum : BpmSystemFieldEnum.values()) {
                SemanticFieldTypeEnum fieldType = SemanticFieldTypeEnum.ofCode(bpmFieldEnum.getFieldTypeCode());

                if (fieldType == null) {
                    log.warn("实体字段类型为空");
                    continue;
                }

                SemanticFieldValueDTO<Object> semanticFieldValue = SemanticFieldValueDTO.ofType(fieldType);
                semanticFieldValue.setFieldName(bpmFieldEnum.getFieldName());
                Object rawValue = null;

                if (matchedInstanceExt != null) {
                    if (bpmFieldEnum == BpmSystemFieldEnum.BPM_TITLE) {
                        rawValue = matchedInstanceExt.getBpmTitle();
                    } else if (bpmFieldEnum == BpmSystemFieldEnum.BPM_INITIATOR_ID) {
                        if (matchedInstanceExt.getInitiatorId() != null) {
                            Map<String, Object> mapValue = new HashMap<>();
                            mapValue.put("id", matchedInstanceExt.getInitiatorId());

                            AdminUserRespDTO matchedUser = userMap.get(Long.valueOf(matchedInstanceExt.getInitiatorId()));

                            if (matchedUser != null) {
                                mapValue.put("name", matchedUser.getNickname());
                            }

                            rawValue = mapValue;
                        }
                    } else if (bpmFieldEnum == BpmSystemFieldEnum.BPM_SUBMIT_TIME) {
                        rawValue = matchedInstanceExt.getSubmitTime();
                    }
                }

                if (matchedInstance != null) {
                    if (bpmFieldEnum == BpmSystemFieldEnum.BPM_CURRENT_NODE) {
                        Map<String, Object> mapValue = new HashMap<>();
                        mapValue.put("id", matchedInstance.getNodeCode());
                        mapValue.put("name", matchedInstance.getNodeName());

                        rawValue = mapValue;
                    } else if (bpmFieldEnum == BpmSystemFieldEnum.BPM_STATUS) {
                        Map<String, Object> mapValue = new HashMap<>();
                        mapValue.put("id", matchedInstance.getFlowStatus());

                        BpmBusinessStatusEnum bpmStatusEnum = BpmBusinessStatusEnum.getByCode(matchedInstance.getFlowStatus());

                        if (bpmStatusEnum != null) {
                            mapValue.put("name", bpmStatusEnum.getDesc());
                        }

                        rawValue = mapValue;
                    } else if (bpmFieldEnum == BpmSystemFieldEnum.BPM_INSTANCE_ID) {
                        rawValue = matchedInstance.getId();
                    }
                }

                semanticFieldValue.setRawValue(rawValue);

                entityValueDTO.getFieldValueMap().put(bpmFieldEnum.getFieldName(), semanticFieldValue);
            }

            response.getList().add(entityValueDTO.getGlobalRawMap());
        }

        return response;


//        // 第一次查询：获取流程实例基础信息
//        com.github.pagehelper.Page<BpmInstanceDTO> pageResult = PageHelper
//                .startPage(reqVO.getPageNo(), reqVO.getPageSize())
//                .doSelectPage(() -> bpmInstanceMapper.getFormDataPage(reqVO, loginUserId));
//
//        // 提取所有 businessDataId 用于第二次查询
//        List<String> businessDataIdList = pageResult.getResult().stream()
//                .map(BpmInstanceDTO::getBusinessDataId)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//        // 第二次查询：根据 businessDataIdList 获取详细的实体数据
//        PageResult<SemanticEntityValueDTO> entityPageResult = new PageResult<>(new ArrayList<>(), 0L);
//        Map<String, BpmInstanceDTO> instanceMap;

//        if (!businessDataIdList.isEmpty()) {
//            entityPageResult = getEntityDataByCondition(
//                    businessDataIdList, reqVO.getTableName(), reqVO.getPageNo(), reqVO.getPageSize(), reqVO.getEntityFilters());
//
//            // 建立 businessDataId 到 BpmInstanceDTO 的映射，便于后续查找
//            instanceMap = pageResult.getResult().stream()
//                    .filter(item -> item.getBusinessDataId() != null)
//                    .collect(Collectors.toMap(BpmInstanceDTO::getBusinessDataId, item -> item));
//        } else {
//            instanceMap = new HashMap<>();
//        }

        // 以第二次查询结果为主构建最终响应
//        List<BpmFormDataPageRespVO> list = entityPageResult.getList().stream().map(entityValue -> {
//            BpmFormDataPageRespVO vo = new BpmFormDataPageRespVO();
//
//            // 从映射中获取对应的流程实例数据
//            String businessDataId = String.valueOf(entityValue.getGlobalRawMap().get("id"));
//            BpmInstanceDTO instance = instanceMap.get(businessDataId);
//
//            if (instance != null) {
//                // 拼接第一次查询的流程实例数据
//                vo.setId(instance.getId());
//                vo.setProcessTitle(instance.getBpmTitle());
//                UserBasicInfoVO initiator = new UserBasicInfoVO();
//                initiator.setUserId(String.valueOf(instance.getInitiatorId()))
//                        .setName(instance.getInitiatorName())
//                        .setAvatar(instance.getInitiatorAvatar());
//                vo.setInitiator(initiator);
//                vo.setFlowStatus(instance.getFlowStatus());
//                vo.setSubmitTime(instance.getSubmitTime());
//                vo.setNodeCode(instance.getNodeCode());
//                vo.setNodeName(instance.getNodeName());
//            }
//
//            // 使用第二次查询的实体数据
//            vo.setData(entityValue.getGlobalRawMap());
//
//            return vo;
//        }).toList();

        // return new PageResult<>(list, entityPageResult.getTotal());
    }

   /**
     * 获取实体数据
     *
     * @return 获取流程表单数据响应
     */
   private PageResult<SemanticEntityValueDTO> getEntityDataByCondition(List businessDataIdList, String tableName,int pageNo, int pageSize,SemanticPageConditionVO entityFilters) {
       // 1. 创建 SemanticPageConditionVO 对象
       SemanticPageConditionVO conditionVO = new SemanticPageConditionVO();
       conditionVO.setTableName(tableName); // 设置表名

       // 2. 构造查询条件 - 通过 entityId 集合查询
       SemanticConditionDTO idCondition = new SemanticConditionDTO();
       idCondition.setFieldName("id"); // 假设主键字段名为"id"
       idCondition.setOperator(SemanticOperatorEnum.EXISTS_IN); // 使用IN操作符
       idCondition.setFieldValue(businessDataIdList); // entityIdList
       idCondition.setNodeType(SemanticConditionNodeTypeEnum.CONDITION);

       // 3. 处理 entityFilters 中的条件，并与ID条件组合
       if (entityFilters != null && entityFilters.getSemanticConditionDTO() != null) {
           entityFilters.setTableName(tableName);
           entityFilters.getSemanticConditionDTO().getChildren().add(idCondition);
           SemanticConditionDTO combinedCondition = getSemanticConditionDTO(entityFilters, idCondition);
           conditionVO.setSemanticConditionDTO(combinedCondition);

           // 设置 entityFilters 中的排序条件
           if (entityFilters.getSortBy() != null) {
               conditionVO.setSortBy(entityFilters.getSortBy());
           }
       } else {
           // 如果没有 entityFilters 条件，只使用ID条件
           conditionVO.setSemanticConditionDTO(idCondition);
       }

       // 4. 设置分页参数
       conditionVO.setPageNo(pageNo);    // 页码
       conditionVO.setPageSize(pageSize); // 每页大小

       // 5. 调用 getDataByCondition 方法
       PageResult<SemanticEntityValueDTO> result = semanticDynamicDataApi.getDataByCondition(conditionVO);
       return result;
   }

   /**
   * 处理 entityFilters 中的条件，并与ID条件组合
   *
   * @param entityFilters entityFilters
   * @param idCondition   ID条件
   * @return 组合后的条件
   */
    @NotNull
    private static SemanticConditionDTO getSemanticConditionDTO(SemanticPageConditionVO entityFilters, SemanticConditionDTO idCondition) {
        SemanticConditionDTO filterConditions = entityFilters.getSemanticConditionDTO();

        // 创建组合条件，将ID条件和entityFilters条件组合在一起
        SemanticConditionDTO combinedCondition = new SemanticConditionDTO();
        combinedCondition.setNodeType(entityFilters.getSemanticConditionDTO().getNodeType());
        combinedCondition.setCombinator(entityFilters.getSemanticConditionDTO().getCombinator());
        combinedCondition.setConditionType(entityFilters.getSemanticConditionDTO().getConditionType());
        combinedCondition.setFieldName(entityFilters.getSemanticConditionDTO().getFieldName());
        combinedCondition.setOperator(entityFilters.getSemanticConditionDTO().getOperator());
        combinedCondition.setFieldValue(entityFilters.getSemanticConditionDTO().getFieldValue());
        combinedCondition.setFieldUuid(entityFilters.getSemanticConditionDTO().getFieldUuid());
        combinedCondition.setChildren(Arrays.asList(idCondition, filterConditions));
        return combinedCondition;
    }


    private List<String> splitToList(String str) {
        if (StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }

        return Arrays.stream(str.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }
    private List<String> splitToFlowStatusList(String str) {
        List<String> flowStatusList = splitToList(str);

        if (CollectionUtils.isEmpty(flowStatusList)) {
            return flowStatusList;
        }

        return flowStatusList.stream()
                .filter(s -> {
                    if (BpmBusinessStatusEnum.getByCode(s) == null) {
                        log.warn("忽略不支持的流程状态：{}", s);
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }
//    private void validateBusinessUuid(BpmFormDataPageReqVO queryPageVO) {
//        String businessUuid = queryPageVO.getBusinessUuid();
//        AppMenuRespDTO appMenuRespDTO = appResourceApi.getAppMenuByUuidAndAppId(businessUuid, queryPageVO.getAppId());
//        bpmAppResourceValidator.validateMenuAndPageset(appMenuRespDTO, queryPageVO.getAppId());
//    }
//
//    private void fillAppId(BpmFormDataPageReqVO queryPageVO) {
//        // todo: 后续放到全局的Repository中
//        Long appId = ApplicationManager.getApplicationId();
//
//        if (appId == null) {
//            throw exception(ErrorCodeConstants.MISSING_APPLICATION_ID);
//        }
//        queryPageVO.setAppId(appId);
//    }
}
