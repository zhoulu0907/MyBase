package com.cmsr.onebase.module.bpm.runtime.service.impl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.dal.database.BpmFlowInsBizExtRepository;
import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowInsBizExtDO;
import com.cmsr.onebase.module.bpm.core.dto.BpmDefinitionExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.BpmGlobalConfigDTO;
import com.cmsr.onebase.module.bpm.core.dto.PageViewGroupDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmBusinessStatusEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmEleRunStatusEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeApproveStatusEnum;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.core.service.BpmEngineDefExtService;
import com.cmsr.onebase.module.bpm.core.vo.design.BpmDefJsonVO;
import com.cmsr.onebase.module.bpm.core.vo.design.node.base.BaseEdgeVO;
import com.cmsr.onebase.module.bpm.runtime.service.BpmInstanceService;
import com.cmsr.onebase.module.bpm.runtime.service.instance.detail.BpmDetailService;
import com.cmsr.onebase.module.bpm.runtime.service.instance.exec.impl.BpmExecServiceImpl;
import com.cmsr.onebase.module.bpm.runtime.service.instance.operator.BpmOperatorRecordService;
import com.cmsr.onebase.module.bpm.runtime.service.instance.predict.BpmPredictService;
import com.cmsr.onebase.module.bpm.runtime.utils.PageViewUtil;
import com.cmsr.onebase.module.bpm.runtime.vo.*;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.InsertDataReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import com.cmsr.onebase.module.system.api.user.AdminUserApi;
import com.cmsr.onebase.module.system.api.user.dto.AdminUserRespDTO;
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
import org.dromara.warm.flow.core.service.impl.BpmConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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
    private BpmEngineDefExtService defExtService;

    @Resource(name = "bpmDefService")
    private DefService defService;

    @Resource(name = "bpmInsService")
    private InsService insService;

    @Resource(name = "bpmTaskService")
    private TaskService taskService;

    @Resource
    private DataMethodApi dataMethodApi;

    @Resource
    private BpmFlowInsBizExtRepository flowInsExtRepository;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    protected MetadataEntityFieldApi metadataEntityFieldApi;

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

        Definition def = defExtService.getByFormPathAndStatus(String.valueOf(reqVO.getBusinessId()), PublishStatus.PUBLISHED.getKey());
        if (def == null) {
            throw exception(ErrorCodeConstants.PUBLISHED_FLOW_NOT_EXISTS);
        }

        // 实体ID todo：校验，应该和businessID有关联关系
        Long entityId = reqVO.getEntity().getEntityId();

        // 和更新数据公用了字段，需要手动校验
        if (MapUtils.isEmpty(reqVO.getEntity().getData())) {
            throw exception(ErrorCodeConstants.FLOW_ENTITY_DATA_NOT_EXISTS.getCode(), "实体数据内容不能为空");
        }

        // 详情视图页面和编辑视图页面
        PageViewGroupDTO pageViewGroupDTO = pageViewUtil.findPageViewGroup(reqVO.getBusinessId());

        if (pageViewGroupDTO == null) {
            throw exception(ErrorCodeConstants.MISSING_EDIT_OR_DETAIL_PAGE_VIEW);
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
        variables.put(BpmConstants.VAR_APP_ID_KEY, extDto.getAppId());
        variables.put(BpmConstants.VAR_ENTITY_ID_KEY, entityId);
        variables.put(BpmConstants.VAR_BINDING_VIEW_ID_KEY, reqVO.getBusinessId());
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
