package com.cmsr.onebase.module.bpm.build.service;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.app.api.appresource.AppResourceApi;
import com.cmsr.onebase.module.app.api.appresource.dto.AppMenuRespDTO;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.enums.BpmNodeTypeEnum;
import com.cmsr.onebase.module.bpm.core.validator.BpmAppResourceValidator;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignRespVO;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignSaveReqVO;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmPublishReqVO;
import com.cmsr.onebase.module.bpm.convert.BpmDesignConvert;
import com.cmsr.onebase.module.bpm.core.dal.database.ext.BpmFlowDefinitionRepositoryExt;
import com.cmsr.onebase.module.bpm.core.dto.node.base.FieldPermCfgDTO;
import com.cmsr.onebase.module.bpm.core.vo.design.BpmDefJsonVO;
import com.cmsr.onebase.module.bpm.core.vo.design.edge.base.BaseEdgeVO;
import com.cmsr.onebase.module.bpm.core.vo.design.node.ApproverNodeVO;
import com.cmsr.onebase.module.bpm.core.vo.design.node.base.BaseNodeVO;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowDefinition;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowDefinitionRepository;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRelationSchemaDTO;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.service.DefService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 流程设计服务实现类
 *
 * @author liyang
 * @date 2025-10-20
 */
@Service
@Slf4j
public class BpmDesignServiceImpl implements BpmDesignService {

    @Resource(name = "bpmDefService")
    private DefService defService;

    @Resource
    private BpmFlowDefinitionRepositoryExt defExtService;

    @Resource
    private FlowDefinitionRepository flowDefinitionRepository;

    @Resource
    private BpmDesignConvert bpmDesignConvert;

    @Resource
    private Validator validator;

    @Resource
    private AppResourceApi appResourceApi;

    @Resource
    private BpmAppResourceValidator bpmAppResourceValidator;

    @Resource
    private SemanticDynamicDataApi semanticDynamicDataApi;

    private String generateFlowCode() {
        return "fc_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    public BpmDefJsonVO validateBpmDefJsonVO(String bpmDefJson, String businessUuid) {
        BpmDefJsonVO bpmDefJsonVO = JsonUtils.parseObject(bpmDefJson, BpmDefJsonVO.class);
        if (bpmDefJsonVO == null) {
            log.error("流程定义JSON解析失败");
            throw exception(ErrorCodeConstants.VALIDATE_BPM_DEF_JSON_FAILED);
        }

        // 使用Jakarta Validation进行校验
        Set<ConstraintViolation<BpmDefJsonVO>> violations = validator.validate(bpmDefJsonVO);

        if (!violations.isEmpty()) {
            log.error("BpmDefJsonVO校验失败:");
            for (ConstraintViolation<BpmDefJsonVO> violation : violations) {
                log.error("- {}: {}", violation.getPropertyPath(), violation.getMessage());
                throw exception(ErrorCodeConstants.VALIDATE_BPM_DEF_JSON_FAILED.getCode(), violation.getMessage());
            }
        }
        // 流程校验
        validateBpmDefStructure(bpmDefJsonVO);
        // 权限字段校验
        validateFieldPermConfigFields(bpmDefJsonVO, businessUuid);
        return bpmDefJsonVO;
    }

    private void validateBpmDefStructure(BpmDefJsonVO bpmDefJsonVO) {
        List<BaseNodeVO> nodes = bpmDefJsonVO.getNodes();
        List<BaseEdgeVO> edges = bpmDefJsonVO.getEdges();

        Map<String, BaseNodeVO> nodeMap = new HashMap<>();
        for (BaseNodeVO node : nodes) {
            nodeMap.put(node.getId(), node);
        }

        BaseNodeVO startNode = nodes.stream().filter(node -> BpmNodeTypeEnum.START.getCode().equals(node.getType())).findFirst().orElse(null);
        BaseNodeVO initiationNode = nodes.stream().filter(node -> BpmNodeTypeEnum.INITIATION.getCode().equals(node.getType())).findFirst().orElse(null);

        if (startNode == null || initiationNode == null) {
            throw exception(ErrorCodeConstants.START_OR_INITIATION_NOT_EXISTS);
        }

        long startCount = nodes.stream().filter(node -> BpmNodeTypeEnum.START.getCode().equals(node.getType())).count();
        long initiationCount = nodes.stream().filter(node -> BpmNodeTypeEnum.INITIATION.getCode().equals(node.getType())).count();

        if (startCount != 1) {
            throw exception(ErrorCodeConstants.START_ONLY_ONE);
        }
        if (initiationCount != 1) {
            throw exception(ErrorCodeConstants.INITIATION_ONLY_ONE);
        }

        // 连接开始节点的边，开始节点之后必须是提交节点
        List<BaseEdgeVO> startOutgoing = edges.stream()
                .filter(edge -> edge.getSourceNodeId().equals(startNode.getId()))
                .toList();

        if (startOutgoing.size() != 1 || !startOutgoing.get(0).getTargetNodeId().equals(initiationNode.getId())) {
            throw exception(ErrorCodeConstants.INITIATION_MUST_AFTER_START);
        }

        Map<String, Integer> incomingCount = new HashMap<>();
        Map<String, Integer> outgoingCount = new HashMap<>();
        for (BaseEdgeVO edge : edges) {
            outgoingCount.merge(edge.getSourceNodeId(), 1, Integer::sum);
            incomingCount.merge(edge.getTargetNodeId(), 1, Integer::sum);
        }

        for (BaseNodeVO node : nodes) {
            if (BpmNodeTypeEnum.START.getCode().equals(node.getType()) || BpmNodeTypeEnum.END.getCode().equals(node.getType())) {
                continue;
            }
            int incoming = incomingCount.getOrDefault(node.getId(), 0);
            int outgoing = outgoingCount.getOrDefault(node.getId(), 0);

            if (incoming == 0 || outgoing == 0) {
                throw exception(ErrorCodeConstants.SINGLE_NODE);
            }
        }
    }

    private void validateFieldPermConfigFields(BpmDefJsonVO bpmDefJsonVO, String businessUuid) {
        if (StringUtils.isBlank(businessUuid)) {
            throw exception(ErrorCodeConstants.REQUIRED_BUSINESS_UUID_MISSING);
        }

        List<BaseNodeVO> nodes = bpmDefJsonVO.getNodes();
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        SemanticEntitySchemaDTO entitySchema = getEntitySchemaByBusinessUuid(businessUuid);
        Map<String, Set<String>> entityTableFields = buildEntityTableFields(entitySchema);

        for (BaseNodeVO node : nodes) {
            if (!(node instanceof ApproverNodeVO approverNode)) {
                continue;
            }

            ApproverNodeVO.ApproverNodeDataVO nodeData = approverNode.getData();
            if (nodeData == null) {
                continue;
            }

            FieldPermCfgDTO fieldPermConfig = nodeData.getFieldPermConfig();
            if (fieldPermConfig == null || !Boolean.TRUE.equals(fieldPermConfig.getUseNodeConfig())) {
                continue;
            }

            List<FieldPermCfgDTO.FieldConfigDTO> fieldConfigs = fieldPermConfig.getFieldConfigs();
            if (fieldConfigs == null || fieldConfigs.isEmpty()) {
                continue;
            }

            for (FieldPermCfgDTO.FieldConfigDTO fieldConfig : fieldConfigs) {
                String tableName = fieldConfig.getTableName();
                String fieldName = fieldConfig.getFieldName();
                if (StringUtils.isBlank(tableName) || StringUtils.isBlank(fieldName)) {
                    continue;
                }

                Set<String> fieldSet = entityTableFields.get(tableName);
                if (fieldSet == null) {
                    String message = String.format("实体表不存在: 表名=%s", tableName);
                    throw exception(ErrorCodeConstants.ENTITY_TABLE_NOT_EXISTS.getCode(), message);
                }
                if (!fieldSet.contains(fieldName)) {
                    // 如果fieldConfig.getParentDisplayName()拿不到值，说明是主表的字段，直接用主表的displayName
                    //String displayName = StringUtils.isEmpty(fieldConfig.getParentDisplayName())? entitySchema.getDisplayName() : fieldConfig.getParentDisplayName();
                    String message = String.format("字段不存在: 表名=%s, 字段名=%s", tableName, fieldName);
                    throw exception(ErrorCodeConstants.ENTITY_TABLE_FIELD_NOT_EXISTS.getCode(), message);
                }
            }
        }
    }

    private SemanticEntitySchemaDTO getEntitySchemaByBusinessUuid(String businessUuid) {
        Long applicationId = ApplicationManager.getApplicationId();
        AppMenuRespDTO menuDTO = appResourceApi.getAppMenuByUuidAndAppId(businessUuid, applicationId);
        if (menuDTO == null || StringUtils.isBlank(menuDTO.getEntityUuid())) {
            throw exception(ErrorCodeConstants.FLOW_NOT_BIND_ENTITY);
        }

        return semanticDynamicDataApi.buildEntitySchemaByUuid(menuDTO.getEntityUuid());
    }

    private Map<String, Set<String>> buildEntityTableFields(SemanticEntitySchemaDTO entitySchema) {
        Map<String, Set<String>> entityTableFields = new HashMap<>();

        if (entitySchema == null || StringUtils.isBlank(entitySchema.getTableName())) {
            throw exception(ErrorCodeConstants.FLOW_NOT_BIND_ENTITY);
        }

        entityTableFields.put(entitySchema.getTableName(), collectFieldNames(entitySchema.getFields()));

        List<SemanticRelationSchemaDTO> connectors = entitySchema.getConnectors();
        if (connectors == null || connectors.isEmpty()) {
            return entityTableFields;
        }

        for (SemanticRelationSchemaDTO connector : connectors) {
            String targetTableName = connector.getTargetEntityTableName();
            if (StringUtils.isBlank(targetTableName)) {
                continue;
            }

            Set<String> targetFields = collectFieldNames(connector.getRelationAttributes());
            //这里需要把子表表名加上
            targetFields.add(targetTableName);
            entityTableFields.put(targetTableName, targetFields);
        }

        return entityTableFields;
    }

    private Set<String> collectFieldNames(List<SemanticFieldSchemaDTO> fields) {
        if (fields == null || fields.isEmpty()) {
            return new HashSet<>();
        }

        Set<String> fieldSet = new HashSet<>();
        for (SemanticFieldSchemaDTO field : fields) {
            if (field.getFieldName() != null) {
                fieldSet.add(field.getFieldName());
            }
        }

        return fieldSet;
    }

    private void validateApplicationId() {
        Long applicationId = ApplicationManager.getApplicationId();

        if (applicationId == null) {
            throw exception(ErrorCodeConstants.MISSING_APPLICATION_ID);
        }
    }

    private Long createBpmFlow(BpmDesignSaveReqVO flowDesignVO) {
        Long businessId = flowDesignVO.getBusinessId();
        String businessUuid = flowDesignVO.getBusinessUuid();
        Long applicationId = flowDesignVO.getAppId();

        // 流程编码代表流程唯一标识，流程的多个版本编码也一样；只有多流程的场景才会有不同编码，暂时忽略
        String flowCode = flowDesignVO.getFlowCode();

        // 流程新增才需要业务ID
        if (businessId == null && StringUtils.isBlank(businessUuid)) {
            throw new IllegalArgumentException("业务ID和业务UUID不能同时为空");
        }

        AppMenuRespDTO menuDTO;

        // 校验
        if (StringUtils.isNotBlank(businessUuid)) {
            menuDTO = appResourceApi.getAppMenuByUuidAndAppId(businessUuid, applicationId);
        } else {
            menuDTO = appResourceApi.getAppMenuById(businessId);
        }

        bpmAppResourceValidator.validateMenuAndPageset(menuDTO, applicationId);

        // 设置UUID到Vo
        flowDesignVO.setBusinessUuid(menuDTO.getMenuUuid());

        // 先查询是否存在已经设计中的流程
        Definition existDef = defExtService.getByFormPathAndStatus(businessUuid, PublishStatus.UNPUBLISHED.getKey());

        // 只能存在一个设计态的流程
        if (existDef != null) {
            throw exception(ErrorCodeConstants.DESIGNING_FLOW_EXISTS);
        }

        // 转换JSON
        DefJson defJson = bpmDesignConvert.toDefJson(flowDesignVO);

        // 查询对应表单下任意一个流程
        Definition anyDef = defExtService.getByFormPath(businessUuid);

        if (anyDef == null) {
            // 忽略前端的flowCode，需要保证唯一性
            flowCode = generateFlowCode();
            defJson.setFlowCode(flowCode);
        } else {
            // 使用现有的流程编码
            defJson.setFlowCode(anyDef.getFlowCode());
        }

        // 新增流程
        Definition def = defService.importDef(defJson);
        return def.getId();
    }

    private Long updateBpmFlow(BpmDesignSaveReqVO flowDesignVO) {
        // 流程不存在时，直接查询defJson结构会报错，先查Definition表
        Definition definition = defService.getById(flowDesignVO.getId());

        if (definition == null) {
            throw exception(ErrorCodeConstants.FLOW_NOT_EXISTS);
        }

        if (!Objects.equals(definition.getIsPublish(), PublishStatus.UNPUBLISHED.getKey())) {
            throw exception(ErrorCodeConstants.SAVE_FLOW_FAILED_FOR_NOT_DESIGN_STATUS);
        }

        flowDesignVO.setBusinessUuid(definition.getFormPath());

        // 转换JSON
        DefJson defJson = bpmDesignConvert.toDefJson(flowDesignVO);

        bpmDesignConvert.copyCommonField(defJson, definition);

        // 更新流程
        try {
            defService.saveDef(defJson, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return flowDesignVO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(BpmDesignSaveReqVO flowDesignVO) {
        validateApplicationId();

        Long flowId = flowDesignVO.getId();

        flowDesignVO.setAppId(ApplicationManager.getApplicationId());

        // 前端暂时用不到流程名称字段，如果流程名称为空则使用默认名称“业务流程”
        if (StringUtils.isBlank(flowDesignVO.getFlowName())) {
            flowDesignVO.setFlowName("业务流程");
        }

        // 校验流程定义JSON
        BpmDefJsonVO bpmDefJsonVO = validateBpmDefJsonVO(flowDesignVO.getBpmDefJson(), flowDesignVO.getBusinessUuid());
        flowDesignVO.setBpmDefJsonVO(bpmDefJsonVO);

        if (flowId == null) {
            return createBpmFlow(flowDesignVO);
        } else {
            return updateBpmFlow(flowDesignVO);
        }
    }

    @Override
    public BpmDesignRespVO queryById(Long id) {
        validateApplicationId();

        // 流程不存在时，直接查询defJson结构会报错，先查Definition表
        Definition definition = defService.getById(id);

        if (definition == null) {
            throw exception(ErrorCodeConstants.FLOW_NOT_EXISTS);
        }

        Long applicationId = definition.getApplicationId();

        // 校验应用是否一致
        if (!Objects.equals(applicationId, ApplicationManager.getApplicationId())) {
            throw exception(ErrorCodeConstants.APPLICATION_ID_MISMATCH);
        }

        // 获取defJson结构
        DefJson defJson = defService.queryDesign(id);
        defJson.setId(id);

        return bpmDesignConvert.toDesignRespVO(defJson);
    }

    public BpmDesignRespVO queryByBusinessId(Long businessId) {
        validateApplicationId();

        AppMenuRespDTO menuDTO = appResourceApi.getAppMenuById(businessId);

        bpmAppResourceValidator.validateMenuAndPageset(menuDTO, ApplicationManager.getApplicationId());

        return queryByMenuUuid(menuDTO.getMenuUuid());
    }

    @Override
    public BpmDesignRespVO queryByBusinessUuid(String businessUuid) {
        validateApplicationId();

        AppMenuRespDTO menuDTO = appResourceApi.getAppMenuByUuidAndAppId(businessUuid, ApplicationManager.getApplicationId());

        bpmAppResourceValidator.validateMenuAndPageset(menuDTO, ApplicationManager.getApplicationId());

        return queryByMenuUuid(businessUuid);
    }

    private BpmDesignRespVO queryByMenuUuid(String menuUuid) {
        // 通过业务ID查询流程定义
        // todo：通过业务ID查询流程定义，按创建时间降序查询最新的流程，后续要改成优先查询已发布的流程定义
        // 按创建时间降序排序，获取最新的流程定义
        Definition definition = defExtService.getByFormPath(menuUuid);

        // 不存在则返回一个空的流程定义
        if (definition == null) {
            return new BpmDesignRespVO();
        }

        // 获取defJson结构
        Long flowId = definition.getId();
        DefJson defJson = defService.queryDesign(flowId);
        defJson.setId(flowId);

        return bpmDesignConvert.toDesignRespVO(defJson);
    }

    /**
     *  发布流程
     *
     * - 只会有一个版本是【已发布】，发布新的版本后，之前【已发布】的流程就会状态变更为历史
     *
     * @param reqVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(BpmPublishReqVO reqVo) {
        validateApplicationId();

        Long id = reqVo.getId();

        // 校验流程是否存在
        Definition existDef = defService.getById(id);

        if (existDef == null) {
            throw exception(ErrorCodeConstants.FLOW_NOT_EXISTS);
        }

        Long applicationId = existDef.getApplicationId();

        // 校验应用是否一致
        if (!Objects.equals(applicationId, ApplicationManager.getApplicationId())) {
            throw exception(ErrorCodeConstants.APPLICATION_ID_MISMATCH);
        }

        // 已发布，直接返回
        if (existDef.getIsPublish().equals(PublishStatus.PUBLISHED.getKey())) {
            log.info("流程[{}]已发布，无需重复发布", id);
            return;
        }

        // 此处为自定义业务逻辑，不使用defService.publish，业务场景不同
        // 先更新所有已发布（目前理论上最多只有一个）的流程为历史版本
        String flowCode = existDef.getFlowCode();

        // 条件
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(FlowDefinition::getFlowCode, flowCode);

        // 更新状态
        FlowDefinition updateDef = new FlowDefinition();
        updateDef.setIsPublish(PublishStatus.EXPIRED.getKey());
        flowDefinitionRepository.update(updateDef, queryWrapper);

        // 更新当前状态为已发布
        existDef.setIsPublish(PublishStatus.PUBLISHED.getKey());
        defService.updateById(existDef);
    }
}
