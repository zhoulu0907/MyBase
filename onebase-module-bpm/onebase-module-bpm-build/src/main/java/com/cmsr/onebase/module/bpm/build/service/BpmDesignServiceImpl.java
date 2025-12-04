package com.cmsr.onebase.module.bpm.build.service;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.app.api.appresource.AppResourceApi;
import com.cmsr.onebase.module.app.api.appresource.dto.AppMenuRespDTO;
import com.cmsr.onebase.module.bpm.api.enums.ErrorCodeConstants;
import com.cmsr.onebase.module.bpm.core.validator.BpmAppResourceValidator;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignRespVO;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignSaveReqVO;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmPublishReqVO;
import com.cmsr.onebase.module.bpm.convert.BpmDesignConvert;
import com.cmsr.onebase.module.bpm.core.dal.database.ext.BpmFlowDefinitionRepositoryExt;
import com.cmsr.onebase.module.bpm.core.vo.design.BpmDefJsonVO;
import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowDefinition;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowDefinitionRepository;
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

    private String generateFlowCode() {
        return "fc_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    public BpmDefJsonVO validateBpmDefJsonVO(String bpmDefJson) {
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

        return bpmDefJsonVO;
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
            // 检测flowCode
            if (StringUtils.isBlank(flowCode)) {
                // 前端没传则随机生成一个
                flowCode = generateFlowCode();
                defJson.setFlowCode(flowCode);
            }
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
        Long flowId = flowDesignVO.getId();
        Long applicationId = ApplicationManager.getApplicationId();

        if (applicationId == null) {
            throw exception(ErrorCodeConstants.MISSING_APPLICATION_ID);
        }

        flowDesignVO.setAppId(applicationId);

        // 前端暂时用不到流程名称字段，如果流程名称为空则使用默认名称“业务流程”
        if (StringUtils.isBlank(flowDesignVO.getFlowName())) {
            flowDesignVO.setFlowName("业务流程");
        }

        // 校验流程定义JSON
        BpmDefJsonVO bpmDefJsonVO = validateBpmDefJsonVO(flowDesignVO.getBpmDefJson());
        flowDesignVO.setBpmDefJsonVO(bpmDefJsonVO);

        if (flowId == null) {
            return createBpmFlow(flowDesignVO);
        } else {
            return updateBpmFlow(flowDesignVO);
        }
    }

    @Override
    public BpmDesignRespVO queryById(Long id) {
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
        AppMenuRespDTO menuDTO = appResourceApi.getAppMenuById(businessId);

        bpmAppResourceValidator.validateMenuAndPageset(menuDTO, ApplicationManager.getApplicationId());

        return queryByMenuUuid(menuDTO.getMenuUuid());
    }

    @Override
    public BpmDesignRespVO queryByBusinessUuid(String businessUuid) {
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
