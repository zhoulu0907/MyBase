package com.cmsr.onebase.module.app.build.service.custombutton;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.module.app.build.vo.custombutton.*;
import com.cmsr.onebase.module.app.core.dal.database.custombutton.AppCustomButtonActionConfigRepository;
import com.cmsr.onebase.module.app.core.dal.database.custombutton.AppCustomButtonConditionGroupRepository;
import com.cmsr.onebase.module.app.core.dal.database.custombutton.AppCustomButtonConditionItemRepository;
import com.cmsr.onebase.module.app.core.dal.database.custombutton.AppCustomButtonRepository;
import com.cmsr.onebase.module.app.core.dal.database.custombutton.AppCustomButtonUpdateFieldRepository;
import com.cmsr.onebase.module.app.core.dal.database.resource.AppPageSetRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppCustomButtonActionConfigDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppCustomButtonConditionGroupDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppCustomButtonConditionItemDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppCustomButtonDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppCustomButtonUpdateFieldDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.enums.custombutton.AppCustomButtonErrorCodeConstants;
import com.cmsr.onebase.module.app.core.enums.custombutton.CustomButtonActionTypeEnum;
import com.cmsr.onebase.module.app.core.enums.custombutton.CustomButtonOperationScopeEnum;
import com.cmsr.onebase.module.app.core.enums.custombutton.CustomButtonStatusEnum;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
public class AppCustomButtonServiceImpl implements AppCustomButtonService {

    @Resource
    private AppCustomButtonRepository customButtonRepository;

    @Resource
    private AppCustomButtonActionConfigRepository actionConfigRepository;

    @Resource
    private AppCustomButtonUpdateFieldRepository updateFieldRepository;

    @Resource
    private AppCustomButtonConditionGroupRepository conditionGroupRepository;

    @Resource
    private AppCustomButtonConditionItemRepository conditionItemRepository;

    @Resource
    private AppPageSetRepository pageSetRepository;

    @Override
    public CustomButtonPageRespVO page(CustomButtonPageReqVO reqVO) {
        AppResourcePagesetDO pageSetDO = getAndValidatePageSet(reqVO.getPageSetId());
        List<AppCustomButtonDO> buttonDOS = customButtonRepository.findByPageSetUuid(pageSetDO.getPageSetUuid());
        List<CustomButtonListItemRespVO> items = new ArrayList<>();
        for (AppCustomButtonDO buttonDO : buttonDOS) {
            CustomButtonListItemRespVO item = new CustomButtonListItemRespVO();
            item.setId(buttonDO.getId());
            item.setButtonCode(buttonDO.getButtonCode());
            item.setButtonName(buttonDO.getButtonName());
            item.setOperationScope(buttonDO.getOperationScope());
            item.setActionType(buttonDO.getActionType());
            item.setStatus(buttonDO.getStatus());
            item.setSortNo(buttonDO.getSortNo());
            items.add(item);
        }
        CustomButtonPageRespVO respVO = new CustomButtonPageRespVO();
        respVO.setList(items);
        return respVO;
    }

    @Override
    public CustomButtonDetailRespVO get(Long id) {
        AppCustomButtonDO buttonDO = customButtonRepository.getById(id);
        if (buttonDO == null) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_NOT_EXISTS);
        }
        CustomButtonDetailRespVO respVO = new CustomButtonDetailRespVO();
        respVO.setId(buttonDO.getId());
        respVO.setButtonCode(buttonDO.getButtonCode());
        respVO.setPageId(safeLong(buttonDO.getPageUuid()));
        AppResourcePagesetDO pageSetDO = pageSetRepository.findByUuidInApplication(
                ApplicationManager.getRequiredApplicationId(), buttonDO.getPageSetUuid());
        if (pageSetDO != null) {
            respVO.setPageSetId(pageSetDO.getId());
        }
        respVO.setButtonName(buttonDO.getButtonName());
        respVO.setButtonDesc(buttonDO.getButtonDesc());
        respVO.setShowDesc(buttonDO.getShowDesc());
        respVO.setStyleType(buttonDO.getStyleType());
        respVO.setColorHex(buttonDO.getColorHex());
        respVO.setColorAlpha(buttonDO.getColorAlpha());
        respVO.setIconCode(buttonDO.getIconCode());
        respVO.setOperationScope(buttonDO.getOperationScope());
        respVO.setShowInForm(buttonDO.getShowInForm());
        respVO.setShowInRowAction(buttonDO.getShowInRowAction());
        respVO.setShowInBatchAction(buttonDO.getShowInBatchAction());
        respVO.setActionType(buttonDO.getActionType());
        respVO.setSortNo(buttonDO.getSortNo());
        respVO.setStatus(buttonDO.getStatus());
        AppCustomButtonActionConfigDO actionConfigDO = actionConfigRepository.findByButtonUuid(buttonDO.getButtonUuid());
        if (actionConfigDO != null) {
            CustomButtonActionConfigReqVO actionConfigVO = toActionConfigReqVO(actionConfigDO);
            respVO.setActionConfig(actionConfigVO);
            respVO.setFlowAction(toFlowActionReqVO(actionConfigDO));
        }
        List<CustomButtonUpdateFieldReqVO> updateFields = toUpdateFieldReqVOList(
                updateFieldRepository.findByButtonUuid(buttonDO.getButtonUuid()));
        if (CustomButtonActionTypeEnum.TRIGGER_FLOW.getCode().equalsIgnoreCase(buttonDO.getActionType())) {
            respVO.setTriggerFlowAction(toTriggerFlowActionReqVO(actionConfigDO));
            respVO.setFlowAction(toFlowActionReqVO(actionConfigDO));
        }
        if (CustomButtonActionTypeEnum.UPDATE_FORM.getCode().equalsIgnoreCase(buttonDO.getActionType())) {
            respVO.setUpdateFormAction(toUpdateFormActionReqVO(actionConfigDO, updateFields));
            respVO.setUpdateFields(updateFields);
        }
        if (CustomButtonActionTypeEnum.CREATE_RELATED_RECORD.getCode().equalsIgnoreCase(buttonDO.getActionType())) {
            respVO.setCreateRelatedAction(toCreateRelatedActionReqVO(actionConfigDO));
        }
        if (CustomButtonActionTypeEnum.OPEN_PAGE.getCode().equalsIgnoreCase(buttonDO.getActionType())) {
            respVO.setOpenPageAction(toOpenPageActionReqVO(actionConfigDO));
        }
        respVO.setAvailableCondition(toAvailableConditionReqVO(buttonDO.getButtonUuid()));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CustomButtonSaveReqVO reqVO) {
        AppResourcePagesetDO pageSetDO = getAndValidatePageSet(reqVO.getPageSetId());
        validateSaveReq(reqVO, pageSetDO.getPageSetUuid(), null);
        Long currentCount = customButtonRepository.countByPageSetUuid(pageSetDO.getPageSetUuid());
        if (currentCount != null && currentCount >= 10L) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_COUNT_EXCEED_LIMIT);
        }
        AppCustomButtonDO buttonDO = new AppCustomButtonDO();
        buttonDO.setButtonUuid(UuidUtils.getUuid());
        buttonDO.setButtonCode(generateButtonCode());
        buttonDO.setMenuUuid(pageSetDO.getMenuUuid());
        buttonDO.setPageSetUuid(pageSetDO.getPageSetUuid());
        buttonDO.setPageUuid(reqVO.getPageId() == null ? null : String.valueOf(reqVO.getPageId()));
        applySaveFields(buttonDO, reqVO);
        customButtonRepository.save(buttonDO);
        upsertActionConfig(buttonDO, reqVO);
        replaceUpdateFields(buttonDO, reqVO);
        replaceConditions(buttonDO, reqVO);
        return buttonDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(CustomButtonSaveReqVO reqVO) {
        if (reqVO.getId() == null) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_NOT_EXISTS);
        }
        AppCustomButtonDO buttonDO = customButtonRepository.getById(reqVO.getId());
        if (buttonDO == null) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_NOT_EXISTS);
        }
        AppResourcePagesetDO pageSetDO = getAndValidatePageSet(reqVO.getPageSetId());
        validateSaveReq(reqVO, pageSetDO.getPageSetUuid(), reqVO.getId());
        buttonDO.setMenuUuid(pageSetDO.getMenuUuid());
        buttonDO.setPageSetUuid(pageSetDO.getPageSetUuid());
        buttonDO.setPageUuid(reqVO.getPageId() == null ? null : String.valueOf(reqVO.getPageId()));
        applySaveFields(buttonDO, reqVO);
        customButtonRepository.updateById(buttonDO);
        upsertActionConfig(buttonDO, reqVO);
        replaceUpdateFields(buttonDO, reqVO);
        replaceConditions(buttonDO, reqVO);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        AppCustomButtonDO buttonDO = customButtonRepository.getById(id);
        if (buttonDO == null) {
            return true;
        }
        actionConfigRepository.removeByButtonUuid(buttonDO.getButtonUuid());
        updateFieldRepository.removeByButtonUuid(buttonDO.getButtonUuid());
        conditionItemRepository.removeByButtonUuid(buttonDO.getButtonUuid());
        conditionGroupRepository.removeByButtonUuid(buttonDO.getButtonUuid());
        customButtonRepository.removeById(id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateStatus(CustomButtonStatusReqVO reqVO) {
        AppCustomButtonDO buttonDO = customButtonRepository.getById(reqVO.getId());
        if (buttonDO == null) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_NOT_EXISTS);
        }
        buttonDO.setStatus(reqVO.getStatus());
        customButtonRepository.updateById(buttonDO);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean sort(CustomButtonSortReqVO reqVO) {
        getAndValidatePageSet(reqVO.getPageSetId());
        if (CollectionUtils.isEmpty(reqVO.getItems())) {
            return true;
        }
        for (CustomButtonSortReqVO.Item item : reqVO.getItems()) {
            AppCustomButtonDO buttonDO = customButtonRepository.getById(item.getId());
            if (buttonDO == null) {
                continue;
            }
            buttonDO.setSortNo(item.getSortNo());
            customButtonRepository.updateById(buttonDO);
        }
        return true;
    }

    private void validateSaveReq(CustomButtonSaveReqVO reqVO, String pageSetUuid, Long excludeId) {
        if (!CustomButtonActionTypeEnum.exists(reqVO.getActionType())) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_ACTION_TYPE_INVALID);
        }
        if (!CustomButtonOperationScopeEnum.exists(reqVO.getOperationScope())) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_SCOPE_INVALID);
        }
        if (customButtonRepository.existsByPageSetUuidAndButtonName(pageSetUuid, reqVO.getButtonName(), excludeId)) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_NAME_DUPLICATE);
        }
        validateDisplayPosition(reqVO);
        validateActionConfig(reqVO);
    }

    private void validateActionConfig(CustomButtonSaveReqVO reqVO) {
        if (CustomButtonActionTypeEnum.TRIGGER_FLOW.getCode().equalsIgnoreCase(reqVO.getActionType())) {
            if (resolveFlowProcessId(reqVO) == null) {
                throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_FLOW_CONFIG_REQUIRED);
            }
        }
        if (CustomButtonActionTypeEnum.CREATE_RELATED_RECORD.getCode().equalsIgnoreCase(reqVO.getActionType())) {
            CustomButtonCreateRelatedActionReqVO action = resolveCreateRelatedAction(reqVO);
            if (action == null || StringUtils.isAnyBlank(action.getTargetPageSetUuid(), action.getTargetPageUuid(),
                    action.getTargetRelationFieldUuid())) {
                throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_ACTION_CONFIG_REQUIRED);
            }
        }
        if (CustomButtonActionTypeEnum.OPEN_PAGE.getCode().equalsIgnoreCase(reqVO.getActionType())) {
            CustomButtonOpenPageActionReqVO action = resolveOpenPageAction(reqVO);
            if (action == null || StringUtils.isBlank(action.getTargetType())) {
                throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_ACTION_CONFIG_REQUIRED);
            }
            if ("INNER_PAGE".equalsIgnoreCase(action.getTargetType())
                    && StringUtils.isAnyBlank(action.getTargetPageSetUuid(), action.getTargetPageUuid())) {
                throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_ACTION_CONFIG_REQUIRED);
            }
            if ("OUTER_URL".equalsIgnoreCase(action.getTargetType()) && StringUtils.isBlank(action.getTargetUrl())) {
                throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_ACTION_CONFIG_REQUIRED);
            }
        }
    }

    private void validateDisplayPosition(CustomButtonSaveReqVO reqVO) {
        boolean batch = CustomButtonOperationScopeEnum.BATCH.getCode().equalsIgnoreCase(reqVO.getOperationScope());
        if (batch && (CustomButtonActionTypeEnum.UPDATE_FORM.getCode().equalsIgnoreCase(reqVO.getActionType())
                || CustomButtonActionTypeEnum.OPEN_PAGE.getCode().equalsIgnoreCase(reqVO.getActionType()))) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_DISPLAY_POSITION_INVALID);
        }
        if (batch) {
            reqVO.setShowInForm(0);
            reqVO.setShowInRowAction(0);
            reqVO.setShowInBatchAction(1);
            return;
        }
        if (reqVO.getShowInForm() == null) {
            reqVO.setShowInForm(1);
        }
        if (reqVO.getShowInRowAction() == null) {
            reqVO.setShowInRowAction(0);
        }
        if (reqVO.getShowInBatchAction() == null) {
            reqVO.setShowInBatchAction(0);
        }
    }

    private void applySaveFields(AppCustomButtonDO buttonDO, CustomButtonSaveReqVO reqVO) {
        buttonDO.setButtonName(reqVO.getButtonName());
        buttonDO.setButtonDesc(reqVO.getButtonDesc());
        buttonDO.setShowDesc(reqVO.getShowDesc() == null ? 1 : reqVO.getShowDesc());
        buttonDO.setStyleType(reqVO.getStyleType());
        buttonDO.setColorHex(reqVO.getColorHex());
        buttonDO.setColorAlpha(reqVO.getColorAlpha());
        buttonDO.setIconCode(reqVO.getIconCode());
        buttonDO.setOperationScope(reqVO.getOperationScope());
        buttonDO.setShowInForm(reqVO.getShowInForm() == null ? 0 : reqVO.getShowInForm());
        buttonDO.setShowInRowAction(reqVO.getShowInRowAction() == null ? 0 : reqVO.getShowInRowAction());
        buttonDO.setShowInBatchAction(reqVO.getShowInBatchAction() == null ? 0 : reqVO.getShowInBatchAction());
        buttonDO.setActionType(reqVO.getActionType());
        buttonDO.setSortNo(reqVO.getSortNo() == null ? 0 : reqVO.getSortNo());
        buttonDO.setStatus(StringUtils.isBlank(reqVO.getStatus()) ? CustomButtonStatusEnum.ENABLE.getCode() : reqVO.getStatus());
    }

    private void upsertActionConfig(AppCustomButtonDO buttonDO, CustomButtonSaveReqVO reqVO) {
        AppCustomButtonActionConfigDO configDO = actionConfigRepository.findByButtonUuid(buttonDO.getButtonUuid());
        if (configDO == null) {
            configDO = new AppCustomButtonActionConfigDO();
            configDO.setButtonUuid(buttonDO.getButtonUuid());
            configDO.setApplicationId(buttonDO.getApplicationId());
        }
        configDO.setActionType(buttonDO.getActionType());
        clearActionConfigFields(configDO);
        applyActionConfigFields(configDO, reqVO);
        if (configDO.getId() == null) {
            actionConfigRepository.save(configDO);
        } else {
            actionConfigRepository.updateById(configDO);
        }
    }

    private void replaceUpdateFields(AppCustomButtonDO buttonDO, CustomButtonSaveReqVO reqVO) {
        updateFieldRepository.removeByButtonUuid(buttonDO.getButtonUuid());
        List<CustomButtonUpdateFieldReqVO> updateFields = resolveUpdateFields(reqVO);
        if (!CustomButtonActionTypeEnum.UPDATE_FORM.getCode().equalsIgnoreCase(buttonDO.getActionType())
                || CollectionUtils.isEmpty(updateFields)) {
            return;
        }
        List<AppCustomButtonUpdateFieldDO> fieldDOS = new ArrayList<>();
        for (CustomButtonUpdateFieldReqVO fieldReqVO : updateFields) {
            AppCustomButtonUpdateFieldDO fieldDO = new AppCustomButtonUpdateFieldDO();
            fieldDO.setApplicationId(buttonDO.getApplicationId());
            fieldDO.setButtonUuid(buttonDO.getButtonUuid());
            fieldDO.setFieldMode(fieldReqVO.getFieldMode());
            fieldDO.setFieldUuid(fieldReqVO.getFieldUuid());
            fieldDO.setFieldCode(fieldReqVO.getFieldCode());
            fieldDO.setRequiredFlag(fieldReqVO.getRequiredFlag() == null ? 0 : fieldReqVO.getRequiredFlag());
            fieldDO.setValueType(fieldReqVO.getValueType());
            fieldDO.setValueConfig(fieldReqVO.getValueConfig());
            fieldDO.setSortNo(fieldReqVO.getSortNo() == null ? 0 : fieldReqVO.getSortNo());
            fieldDOS.add(fieldDO);
        }
        updateFieldRepository.saveBatch(fieldDOS);
    }

    private void applyActionConfigFields(AppCustomButtonActionConfigDO configDO, CustomButtonSaveReqVO reqVO) {
        CustomButtonUpdateFormActionReqVO updateFormActionReqVO = reqVO.getUpdateFormAction();
        if (updateFormActionReqVO != null) {
            configDO.setOpenMode(updateFormActionReqVO.getOpenMode());
            configDO.setSubmitSuccessText(updateFormActionReqVO.getSubmitSuccessText());
        }
        CustomButtonCreateRelatedActionReqVO createRelatedReqVO = reqVO.getCreateRelatedAction();
        if (createRelatedReqVO != null) {
            configDO.setOpenMode(createRelatedReqVO.getOpenMode());
            configDO.setTargetPageSetUuid(createRelatedReqVO.getTargetPageSetUuid());
            configDO.setTargetPageUuid(createRelatedReqVO.getTargetPageUuid());
            configDO.setTargetEntityUuid(createRelatedReqVO.getTargetEntityUuid());
            configDO.setTargetRelationFieldUuid(createRelatedReqVO.getTargetRelationFieldUuid());
            configDO.setTargetRelationScope(createRelatedReqVO.getTargetRelationScope());
        }
        CustomButtonTriggerFlowActionReqVO triggerFlowReqVO = reqVO.getTriggerFlowAction();
        if (triggerFlowReqVO != null) {
            configDO.setFlowProcessId(triggerFlowReqVO.getFlowProcessId());
            configDO.setFlowProcessUuid(triggerFlowReqVO.getFlowProcessUuid());
            configDO.setConfirmRequired(triggerFlowReqVO.getConfirmRequired());
            configDO.setConfirmText(triggerFlowReqVO.getConfirmText());
        }
        CustomButtonOpenPageActionReqVO openPageReqVO = reqVO.getOpenPageAction();
        if (openPageReqVO != null) {
            configDO.setOpenMode(openPageReqVO.getOpenMode());
            configDO.setTargetType(openPageReqVO.getTargetType());
            configDO.setTargetPageSetUuid(openPageReqVO.getTargetPageSetUuid());
            configDO.setTargetPageUuid(openPageReqVO.getTargetPageUuid());
            configDO.setTargetUrl(openPageReqVO.getTargetUrl());
            if (CollectionUtils.isNotEmpty(openPageReqVO.getParams())) {
                configDO.setConfigJson(JsonUtils.toJsonString(openPageReqVO.getParams()));
            }
        }
        CustomButtonActionConfigReqVO actionConfigReqVO = reqVO.getActionConfig();
        if (actionConfigReqVO != null) {
            configDO.setOpenMode(actionConfigReqVO.getOpenMode());
            configDO.setSubmitSuccessText(actionConfigReqVO.getSubmitSuccessText());
            configDO.setTargetType(actionConfigReqVO.getTargetType());
            configDO.setTargetPageSetUuid(actionConfigReqVO.getTargetPageSetUuid());
            configDO.setTargetPageUuid(actionConfigReqVO.getTargetPageUuid());
            configDO.setTargetUrl(actionConfigReqVO.getTargetUrl());
            configDO.setTargetEntityUuid(actionConfigReqVO.getTargetEntityUuid());
            configDO.setTargetRelationFieldUuid(actionConfigReqVO.getTargetRelationFieldUuid());
            configDO.setTargetRelationScope(actionConfigReqVO.getTargetRelationScope());
            configDO.setFlowProcessId(actionConfigReqVO.getFlowProcessId());
            configDO.setFlowProcessUuid(actionConfigReqVO.getFlowProcessUuid());
            configDO.setConfirmRequired(actionConfigReqVO.getConfirmRequired());
            configDO.setConfirmText(actionConfigReqVO.getConfirmText());
            configDO.setConfigJson(actionConfigReqVO.getConfigJson());
        }
        CustomButtonFlowActionReqVO flowReqVO = reqVO.getFlowAction();
        if (flowReqVO != null) {
            configDO.setFlowProcessId(flowReqVO.getFlowProcessId());
            configDO.setFlowProcessUuid(flowReqVO.getFlowProcessUuid());
            configDO.setConfirmRequired(flowReqVO.getConfirmRequired());
            configDO.setConfirmText(flowReqVO.getConfirmText());
        }
        if (CustomButtonActionTypeEnum.TRIGGER_FLOW.getCode().equalsIgnoreCase(reqVO.getActionType())
                && configDO.getConfirmRequired() == null) {
            configDO.setConfirmRequired(1);
        }
    }

    private void clearActionConfigFields(AppCustomButtonActionConfigDO configDO) {
        configDO.setOpenMode(null);
        configDO.setSubmitSuccessText(null);
        configDO.setTargetType(null);
        configDO.setTargetPageSetUuid(null);
        configDO.setTargetPageUuid(null);
        configDO.setTargetUrl(null);
        configDO.setTargetEntityUuid(null);
        configDO.setTargetRelationFieldUuid(null);
        configDO.setTargetRelationScope(null);
        configDO.setFlowProcessId(null);
        configDO.setFlowProcessUuid(null);
        configDO.setConfirmRequired(null);
        configDO.setConfirmText(null);
        configDO.setConfigJson(null);
    }

    private Long resolveFlowProcessId(CustomButtonSaveReqVO reqVO) {
        if (reqVO.getTriggerFlowAction() != null && reqVO.getTriggerFlowAction().getFlowProcessId() != null) {
            return reqVO.getTriggerFlowAction().getFlowProcessId();
        }
        if (reqVO.getActionConfig() != null && reqVO.getActionConfig().getFlowProcessId() != null) {
            return reqVO.getActionConfig().getFlowProcessId();
        }
        return reqVO.getFlowAction() == null ? null : reqVO.getFlowAction().getFlowProcessId();
    }

    private List<CustomButtonUpdateFieldReqVO> resolveUpdateFields(CustomButtonSaveReqVO reqVO) {
        if (reqVO.getUpdateFormAction() != null && reqVO.getUpdateFormAction().getUpdateFields() != null) {
            return reqVO.getUpdateFormAction().getUpdateFields();
        }
        return reqVO.getUpdateFields();
    }

    private CustomButtonCreateRelatedActionReqVO resolveCreateRelatedAction(CustomButtonSaveReqVO reqVO) {
        if (reqVO.getCreateRelatedAction() != null) {
            return reqVO.getCreateRelatedAction();
        }
        if (reqVO.getActionConfig() == null) {
            return null;
        }
        CustomButtonCreateRelatedActionReqVO action = new CustomButtonCreateRelatedActionReqVO();
        action.setOpenMode(reqVO.getActionConfig().getOpenMode());
        action.setTargetPageSetUuid(reqVO.getActionConfig().getTargetPageSetUuid());
        action.setTargetPageUuid(reqVO.getActionConfig().getTargetPageUuid());
        action.setTargetEntityUuid(reqVO.getActionConfig().getTargetEntityUuid());
        action.setTargetRelationFieldUuid(reqVO.getActionConfig().getTargetRelationFieldUuid());
        action.setTargetRelationScope(reqVO.getActionConfig().getTargetRelationScope());
        return action;
    }

    private CustomButtonOpenPageActionReqVO resolveOpenPageAction(CustomButtonSaveReqVO reqVO) {
        if (reqVO.getOpenPageAction() != null) {
            return reqVO.getOpenPageAction();
        }
        if (reqVO.getActionConfig() == null) {
            return null;
        }
        CustomButtonOpenPageActionReqVO action = new CustomButtonOpenPageActionReqVO();
        action.setOpenMode(reqVO.getActionConfig().getOpenMode());
        action.setTargetType(reqVO.getActionConfig().getTargetType());
        action.setTargetPageSetUuid(reqVO.getActionConfig().getTargetPageSetUuid());
        action.setTargetPageUuid(reqVO.getActionConfig().getTargetPageUuid());
        action.setTargetUrl(reqVO.getActionConfig().getTargetUrl());
        return action;
    }

    private CustomButtonActionConfigReqVO toActionConfigReqVO(AppCustomButtonActionConfigDO configDO) {
        if (configDO == null) {
            return null;
        }
        CustomButtonActionConfigReqVO reqVO = new CustomButtonActionConfigReqVO();
        reqVO.setOpenMode(configDO.getOpenMode());
        reqVO.setSubmitSuccessText(configDO.getSubmitSuccessText());
        reqVO.setTargetType(configDO.getTargetType());
        reqVO.setTargetPageSetUuid(configDO.getTargetPageSetUuid());
        reqVO.setTargetPageUuid(configDO.getTargetPageUuid());
        reqVO.setTargetUrl(configDO.getTargetUrl());
        reqVO.setTargetEntityUuid(configDO.getTargetEntityUuid());
        reqVO.setTargetRelationFieldUuid(configDO.getTargetRelationFieldUuid());
        reqVO.setTargetRelationScope(configDO.getTargetRelationScope());
        reqVO.setFlowProcessId(configDO.getFlowProcessId());
        reqVO.setFlowProcessUuid(configDO.getFlowProcessUuid());
        reqVO.setConfirmRequired(configDO.getConfirmRequired());
        reqVO.setConfirmText(configDO.getConfirmText());
        reqVO.setConfigJson(configDO.getConfigJson());
        return reqVO;
    }

    private CustomButtonFlowActionReqVO toFlowActionReqVO(AppCustomButtonActionConfigDO configDO) {
        if (configDO == null || configDO.getFlowProcessId() == null) {
            return null;
        }
        CustomButtonFlowActionReqVO flowReqVO = new CustomButtonFlowActionReqVO();
        flowReqVO.setFlowProcessId(configDO.getFlowProcessId());
        flowReqVO.setFlowProcessUuid(configDO.getFlowProcessUuid());
        flowReqVO.setConfirmRequired(configDO.getConfirmRequired());
        flowReqVO.setConfirmText(configDO.getConfirmText());
        return flowReqVO;
    }

    private CustomButtonTriggerFlowActionReqVO toTriggerFlowActionReqVO(AppCustomButtonActionConfigDO configDO) {
        if (configDO == null || configDO.getFlowProcessId() == null) {
            return null;
        }
        CustomButtonTriggerFlowActionReqVO reqVO = new CustomButtonTriggerFlowActionReqVO();
        reqVO.setFlowProcessId(configDO.getFlowProcessId());
        reqVO.setFlowProcessUuid(configDO.getFlowProcessUuid());
        reqVO.setConfirmRequired(configDO.getConfirmRequired());
        reqVO.setConfirmText(configDO.getConfirmText());
        return reqVO;
    }

    private CustomButtonUpdateFormActionReqVO toUpdateFormActionReqVO(AppCustomButtonActionConfigDO configDO,
                                                                       List<CustomButtonUpdateFieldReqVO> updateFields) {
        CustomButtonUpdateFormActionReqVO reqVO = new CustomButtonUpdateFormActionReqVO();
        if (configDO != null) {
            reqVO.setOpenMode(configDO.getOpenMode());
            reqVO.setSubmitSuccessText(configDO.getSubmitSuccessText());
        }
        reqVO.setUpdateFields(updateFields);
        return reqVO;
    }

    private CustomButtonCreateRelatedActionReqVO toCreateRelatedActionReqVO(AppCustomButtonActionConfigDO configDO) {
        if (configDO == null) {
            return null;
        }
        CustomButtonCreateRelatedActionReqVO reqVO = new CustomButtonCreateRelatedActionReqVO();
        reqVO.setOpenMode(configDO.getOpenMode());
        reqVO.setTargetPageSetUuid(configDO.getTargetPageSetUuid());
        reqVO.setTargetPageUuid(configDO.getTargetPageUuid());
        reqVO.setTargetEntityUuid(configDO.getTargetEntityUuid());
        reqVO.setTargetRelationFieldUuid(configDO.getTargetRelationFieldUuid());
        reqVO.setTargetRelationScope(configDO.getTargetRelationScope());
        return reqVO;
    }

    private CustomButtonOpenPageActionReqVO toOpenPageActionReqVO(AppCustomButtonActionConfigDO configDO) {
        if (configDO == null) {
            return null;
        }
        CustomButtonOpenPageActionReqVO reqVO = new CustomButtonOpenPageActionReqVO();
        reqVO.setOpenMode(configDO.getOpenMode());
        reqVO.setTargetType(configDO.getTargetType());
        reqVO.setTargetPageSetUuid(configDO.getTargetPageSetUuid());
        reqVO.setTargetPageUuid(configDO.getTargetPageUuid());
        reqVO.setTargetUrl(configDO.getTargetUrl());
        if (StringUtils.isNotBlank(configDO.getConfigJson())) {
            reqVO.setParams(JsonUtils.parseObject(configDO.getConfigJson(),
                    new TypeReference<List<CustomButtonOpenPageParamReqVO>>() {}));
        }
        return reqVO;
    }

    private List<CustomButtonUpdateFieldReqVO> toUpdateFieldReqVOList(List<AppCustomButtonUpdateFieldDO> fieldDOS) {
        List<CustomButtonUpdateFieldReqVO> result = new ArrayList<>();
        for (AppCustomButtonUpdateFieldDO fieldDO : fieldDOS) {
            CustomButtonUpdateFieldReqVO reqVO = new CustomButtonUpdateFieldReqVO();
            reqVO.setFieldMode(fieldDO.getFieldMode());
            reqVO.setFieldUuid(fieldDO.getFieldUuid());
            reqVO.setFieldCode(fieldDO.getFieldCode());
            reqVO.setRequiredFlag(fieldDO.getRequiredFlag());
            reqVO.setValueType(fieldDO.getValueType());
            reqVO.setValueConfig(fieldDO.getValueConfig());
            reqVO.setSortNo(fieldDO.getSortNo());
            result.add(reqVO);
        }
        return result;
    }

    private void replaceConditions(AppCustomButtonDO buttonDO, CustomButtonSaveReqVO reqVO) {
        conditionItemRepository.removeByButtonUuid(buttonDO.getButtonUuid());
        conditionGroupRepository.removeByButtonUuid(buttonDO.getButtonUuid());
        CustomButtonAvailableConditionReqVO conditionReqVO = reqVO.getAvailableCondition();
        if (conditionReqVO == null || CollectionUtils.isEmpty(conditionReqVO.getValueRules())) {
            return;
        }
        int groupNo = 0;
        for (List<CustomButtonConditionItemReqVO> group : conditionReqVO.getValueRules()) {
            if (CollectionUtils.isEmpty(group)) {
                continue;
            }
            AppCustomButtonConditionGroupDO groupDO = new AppCustomButtonConditionGroupDO();
            groupDO.setApplicationId(buttonDO.getApplicationId());
            groupDO.setButtonUuid(buttonDO.getButtonUuid());
            groupDO.setGroupNo(groupNo);
            groupDO.setLogicType("AND");
            groupDO.setSortNo(groupNo);
            conditionGroupRepository.save(groupDO);

            List<AppCustomButtonConditionItemDO> itemDOS = new ArrayList<>();
            int sortNo = 0;
            for (CustomButtonConditionItemReqVO itemReqVO : group) {
                AppCustomButtonConditionItemDO itemDO = new AppCustomButtonConditionItemDO();
                itemDO.setApplicationId(buttonDO.getApplicationId());
                itemDO.setButtonUuid(buttonDO.getButtonUuid());
                itemDO.setGroupId(groupDO.getId());
                itemDO.setFieldUuid(itemReqVO.getFieldUuid());
                itemDO.setFieldCode(itemReqVO.getFieldCode());
                itemDO.setOperator(itemReqVO.getOperator());
                itemDO.setValueType(itemReqVO.getValueType());
                itemDO.setCompareValue(itemReqVO.getCompareValue());
                itemDO.setSortNo(itemReqVO.getSortNo() == null ? sortNo : itemReqVO.getSortNo());
                itemDOS.add(itemDO);
                sortNo++;
            }
            if (CollectionUtils.isNotEmpty(itemDOS)) {
                conditionItemRepository.saveBatch(itemDOS);
            }
            groupNo++;
        }
    }

    private CustomButtonAvailableConditionReqVO toAvailableConditionReqVO(String buttonUuid) {
        List<AppCustomButtonConditionGroupDO> groupDOS = conditionGroupRepository.findByButtonUuid(buttonUuid);
        if (CollectionUtils.isEmpty(groupDOS)) {
            return null;
        }
        List<Long> groupIds = groupDOS.stream().map(AppCustomButtonConditionGroupDO::getId).collect(Collectors.toList());
        List<AppCustomButtonConditionItemDO> itemDOS = conditionItemRepository.findByGroupIds(groupIds);
        Map<Long, List<AppCustomButtonConditionItemDO>> itemMap = itemDOS.stream()
                .collect(Collectors.groupingBy(AppCustomButtonConditionItemDO::getGroupId, LinkedHashMap::new, Collectors.toList()));
        List<List<CustomButtonConditionItemReqVO>> valueRules = new ArrayList<>();
        for (AppCustomButtonConditionGroupDO groupDO : groupDOS) {
            List<CustomButtonConditionItemReqVO> groupItems = new ArrayList<>();
            for (AppCustomButtonConditionItemDO itemDO : itemMap.getOrDefault(groupDO.getId(), Collections.emptyList())) {
                CustomButtonConditionItemReqVO itemReqVO = new CustomButtonConditionItemReqVO();
                itemReqVO.setFieldUuid(itemDO.getFieldUuid());
                itemReqVO.setFieldCode(itemDO.getFieldCode());
                itemReqVO.setOperator(itemDO.getOperator());
                itemReqVO.setValueType(itemDO.getValueType());
                itemReqVO.setCompareValue(itemDO.getCompareValue());
                itemReqVO.setSortNo(itemDO.getSortNo());
                groupItems.add(itemReqVO);
            }
            if (CollectionUtils.isNotEmpty(groupItems)) {
                valueRules.add(groupItems);
            }
        }
        CustomButtonAvailableConditionReqVO conditionReqVO = new CustomButtonAvailableConditionReqVO();
        conditionReqVO.setValueRules(valueRules);
        return conditionReqVO;
    }

    private AppResourcePagesetDO getAndValidatePageSet(Long pageSetId) {
        AppResourcePagesetDO pageSetDO = pageSetRepository.getById(pageSetId);
        if (pageSetDO == null) {
            throw ServiceExceptionUtil.exception(AppCustomButtonErrorCodeConstants.CUSTOM_BUTTON_PAGESET_NOT_EXISTS);
        }
        return pageSetDO;
    }

    private Long safeLong(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (Exception ex) {
            return null;
        }
    }

    private String generateButtonCode() {
        String uuid = UuidUtils.getUuid().replace("-", "");
        int codeLength = Math.min(16, uuid.length());
        return "cb_" + uuid.substring(0, codeLength);
    }
}
